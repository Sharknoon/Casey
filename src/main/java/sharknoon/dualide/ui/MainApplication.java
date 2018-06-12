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
package sharknoon.dualide.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import javafx.application.Preloader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sharknoon.dualide.misc.Exitable;
import sharknoon.dualide.misc.Initializable;
import sharknoon.dualide.utils.settings.Resources;

/**
 *
 * @author frank
 */
public class MainApplication extends Application {

    private Scene scene;

    @Override
    public void init() throws Exception {
          var loader = new FXMLLoader();
          var fxmlStream = Resources.createAndGetFileAsStream("sharknoon/dualide/ui/MainFXML.fxml", true);

        Parent root = loader.load(fxmlStream);
        scene = new Scene(root);
        scene.getStylesheets().add("sharknoon/dualide/ui/MainCSS.css");
        INITIALIZABLES.forEach(i -> i.init(scene));
    }

    @Override
    public void start(Stage stage) throws IOException {
        stage.setScene(scene);
        stage.setTitle("DualIDE");
        stage.setMaximized(true);
        notifyPreloader(new Preloader.ProgressNotification(1.0));
        stage.show();
    }

    //Initlialisables and Exitables
    private static final List<Initializable> INITIALIZABLES = new ArrayList<>();
    private static final List<Exitable> EXITABLES = new ArrayList<>();

    public static void registerInitializable(Initializable initializable) {
        INITIALIZABLES.add(initializable);
    }

    public static void registerExitable(Exitable exitable) {
        EXITABLES.add(exitable);
    }

    @Override
    public void stop() throws Exception {
        EXITABLES.forEach(Exitable::exit);
    }

    /**
     * Fallback for launching this JavaFX Application, please use
     * MainApplication.java instead!
     */
    public static void main(String[] args) {
        launch(args);
    }

}
