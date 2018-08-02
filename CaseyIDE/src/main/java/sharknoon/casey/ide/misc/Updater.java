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

import org.aeonbits.owner.ConfigFactory;
import sharknoon.casey.ide.ui.MainApplication;
import sharknoon.casey.ide.ui.dialogs.Dialogs;
import sharknoon.casey.ide.ui.misc.Icon;
import sharknoon.casey.ide.utils.language.Word;
import sharknoon.casey.ide.utils.settings.Logger;
import sharknoon.casey.ide.utils.settings.Resources;

import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Updater {
    
    private static final VersionProperty UPDATER = ConfigFactory.create(VersionProperty.class);
    private static boolean isCheckingForUpdates = false;
    
    public static void init() {
        ScheduledExecutorService updateCheckingSchedulerService = Executors.newScheduledThreadPool(1);
        updateCheckingSchedulerService.scheduleAtFixedRate(Updater::checkForUpdates, 0, 5, TimeUnit.MINUTES);
        MainApplication.registerExitable(() -> {
            if (!updateCheckingSchedulerService.isShutdown()) {
                updateCheckingSchedulerService.shutdown();
            }
        });
    }
    
    public static void checkForUpdates() {
        if (isCheckingForUpdates) {
            return;
        }
        isCheckingForUpdates = true;
        Optional<Path> updater = Resources.getFile("sharknoon/casey/ide/CaseyUPDATER.jar", true);
        if (!updater.isPresent()) {
            Logger.error("Updater not found");
            return;
        }
        CompletableFuture<Integer> integerCompletableFuture = Executor.runJar(updater.get().toAbsolutePath(), "-c", "-v", getCurrentVersion().orElse("0.1"));//TODO
        integerCompletableFuture.thenAccept(result -> {
            boolean newUpdateAvailable = result == 100;
            showUpdateDialog(newUpdateAvailable);
            isCheckingForUpdates = false;
        });
        
    }
    
    public static boolean showUpdateDialog(boolean newUpdateAvailable) {
        System.out.println("Update Available: " + newUpdateAvailable);
        return Dialogs.showConfirmationDialog(Word.FUNCTION_SITE_FUNCTION_RETURNTYPE, Word.FUNCTION_SITE_FUNCTION_RETURNTYPE, Word.FUNCTION_SITE_FUNCTION_RETURNTYPE, Icon.ERROR, null).orElse(false);
    }
    
    public static Optional<String> getCurrentVersion() {
        String version = UPDATER.version();
        return version == null || version.isEmpty() || !version.contains(".")
                ? Optional.empty()
                : Optional.of(version);
    }
    
}
