package sharknoon.casey.ide.misc;/*
 * Copyright 2018 Shark Industries.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import org.aeonbits.owner.ConfigFactory;
import sharknoon.casey.ide.MainApplication;
import sharknoon.casey.ide.misc.Executor.ExecutorBuilder;
import sharknoon.casey.ide.ui.dialogs.Dialogs;
import sharknoon.casey.ide.ui.misc.*;
import sharknoon.casey.ide.ui.styles.Styles;
import sharknoon.casey.ide.utils.language.*;
import sharknoon.casey.ide.utils.settings.*;

import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.*;

public class Updater {
    
    private static final VersionProperty UPDATER = ConfigFactory.create(VersionProperty.class);
    private static boolean isCheckingForUpdates = false;
    
    public static void init() {
        ScheduledExecutorService updateCheckingSchedulerService = Executors.newScheduledThreadPool(1);
        updateCheckingSchedulerService.scheduleAtFixedRate(Updater::checkForUpdatesSilently, 20, 3600, TimeUnit.SECONDS);
        MainApplication.registerExitable(() -> {
            if (!updateCheckingSchedulerService.isShutdown()) {
                updateCheckingSchedulerService.shutdown();
            }
        });
    }
    
    public static void checkForUpdates() {
        checkForUpdates(false);
    }
    
    public static void checkForUpdatesSilently() {
        checkForUpdates(true);
    }
    
    private static void checkForUpdates(boolean silent) {
        if (isCheckingForUpdates) {
            return;
        }
        isCheckingForUpdates = true;
        Optional<Path> updater = Resources.getFile("sharknoon/casey/ide/CaseyUPDATER.jar", true);
        if (!updater.isPresent()) {
            Logger.error("Updater not found");
            return;
        }
        CompletableFuture<Integer> integerCompletableFuture = ExecutorBuilder
                .executeJar(updater.get().toAbsolutePath())
                .setArgs("-c", getCurrentVersion().orElse("0.1"))
                .execute();
        integerCompletableFuture.thenAccept(result -> {
            isCheckingForUpdates = false;
            boolean newUpdateAvailable = result == 100;
            Logger.info("Update available: " + newUpdateAvailable);
            if (!newUpdateAvailable) {
                if (!silent) {
                    Platform.runLater(() -> {
                        Alert alert = new Alert(AlertType.INFORMATION);
                        alert.setTitle(Language.get(Word.NO_NEW_UPDATE_AVAILABLE_DIALOG_TITLE));
                        alert.setHeaderText(null);
                        alert.setContentText(Language.get(Word.NO_NEW_UPDATE_AVAILABLE_DIALOG_HEADER_TEXT));
                        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                        Icons.getImage(Icon.UPDATE).ifPresent(stage.getIcons()::add);
                        Styles.bindStyleSheets(alert.getDialogPane().getStylesheets());
                        alert.show();
                    });
                }
                return;
            }
            boolean userWantsUpdate = showUpdateDialog();
            if (!userWantsUpdate) {
                return;
            }
            update();
        }).orTimeout(10, TimeUnit.SECONDS).thenRun(() -> isCheckingForUpdates = false);
        
    }
    
    public static boolean showUpdateDialog() {
        try {
            FutureTask<Boolean> checkIfUserWantsUpdate = new FutureTask<>(() -> Dialogs.showConfirmationDialog(
                    Word.NEW_UPDATE_AVAILABLE_DIALOG_TITLE,
                    Word.NEW_UPDATE_AVAILABLE_DIALOG_HEADER_TEXT,
                    Word.NEW_UPDATE_AVAILABLE_DIALOG_CONTENT_TEXT,
                    Icon.UPDATE,
                    null)
                    .orElse(false));
            Platform.runLater(checkIfUserWantsUpdate);
            return checkIfUserWantsUpdate.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    private static void update() {
        try {
            Optional<Path> updater = Resources.getFile("sharknoon/casey/ide/CaseyUPDATER.jar", true);
            if (!updater.isPresent()) {
                Logger.error("Updater not found");
                return;
            }
            Path path = new java.io.File(Updater.class.getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .getPath())
                    .toPath();
            ExecutorBuilder
                    .executeJar(updater.get().toAbsolutePath())
                    .setArgs("-u", path.toAbsolutePath().toString())
                    .execute();
            MainApplication.stopApp("Updating...", false);
        } catch (Exception e) {
            Logger.error("Could not update Casey", e);
        }
    }
    
    public static Optional<String> getCurrentVersion() {
        String version = UPDATER.version();
        return version == null || version.isEmpty() || !version.contains(".")
                ? Optional.empty()
                : Optional.of(version);
    }
    
}
