/*
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
package sharknoon.casey.ide;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.application.Preloader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sharknoon.casey.ide.misc.Exitable;
import sharknoon.casey.ide.misc.Initializable;
import sharknoon.casey.ide.ui.dialogs.ExceptionDialog;
import sharknoon.casey.ide.ui.misc.Icon;
import sharknoon.casey.ide.ui.misc.Icons;
import sharknoon.casey.ide.ui.styles.Styles;
import sharknoon.casey.ide.utils.settings.Logger;
import sharknoon.casey.ide.utils.settings.Resources;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author frank
 */
public class MainApplication extends Application {
    
    //Initlialisables and Exitables
    private static final List<Initializable> INITIALIZABLES = new ArrayList<>();
    private static final List<Exitable> EXITABLES = new ArrayList<>();
    private static boolean wasUpdated = false;
    
    public static void registerInitializable(Initializable initializable) {
        INITIALIZABLES.add(initializable);
    }
    
    public static void registerExitable(Exitable exitable) {
        EXITABLES.add(exitable);
    }
    
    /**
     * Fallback for launching this JavaFX Application, please use
     * MainApplication.java instead!
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    public static void stopApp(String message) {
        stopApp(message, true);
    }
    
    public static void stopApp(String message, boolean wasError) {
        if (wasError) {
            showError(Thread.currentThread(), new Exception(message));
        }
        Platform.exit();
    }
    
    //Error handling
    private static void showError(Thread t, Throwable e) {
        System.err.println("An unexpected error occurred in " + t);
        e.printStackTrace();
        ExceptionDialog.show("An unexpected error occurred in " + t, e);
    }
    
    public static boolean wasUpdated() {
        return wasUpdated;
    }
    
    private Scene scene;
    
    @Override
    public void init() throws Exception {
        Thread.setDefaultUncaughtExceptionHandler(MainApplication::showError);
    
        System.setProperty("javafx.animation.framerate", "144");
        //System.setProperty("javafx.preloader", SplashScreen.class.getLanguageDependentName());
        Logger.setLogLevel(Logger.LogLevel.DEBUG);
    
        if (getParameters().getUnnamed().contains("-u")) {
            wasUpdated = true;
        }
        
        var loader = new FXMLLoader();
        var fxmlStream = Resources.createAndGetFileAsStream("sharknoon/casey/ide/ui/MainFXML.fxml", true);

        //Loads the Maincontroller, which loads all the UI Things
        Parent root = loader.load(Objects.requireNonNull(fxmlStream));
        scene = new Scene(root);
        Styles.bindStyleSheets(scene.getStylesheets());
        INITIALIZABLES.forEach(i -> i.init(scene));
    }
    
    @Override
    public void start(Stage stage) {
        Icons.getImage(Icon.LOGO).ifPresent(stage.getIcons()::add);
        stage.setScene(scene);
        stage.setTitle("CaseyIDE");
        stage.setMaximized(true);
        notifyPreloader(new Preloader.ProgressNotification(1.0));
        stage.show();
    }
    
    @Override
    public void stop() {
        EXITABLES.forEach(Exitable::exit);
    }
    
}
