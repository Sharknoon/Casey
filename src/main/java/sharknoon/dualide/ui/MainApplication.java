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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sharknoon.dualide.misc.Exitable;
import sharknoon.dualide.misc.Initializable;
import sharknoon.dualide.utils.settings.Ressources;

/**
 *
 * @author frank
 */
public class MainApplication extends Application {

    public static Stage stage;

    @Override
    public void init() throws Exception {
        INITIALIZABLES.forEach(Initializable::init);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Ressources.resetRessources(true);
        stage = primaryStage;
        FXMLLoader loader = new FXMLLoader();
        Path fxmlPath = Ressources.createAndGetFile("sharknoon/dualide/ui/MainFXML.fxml", true);
        Parent root = loader.load(Files.newInputStream(fxmlPath));

        Scene scene = new Scene(root);
        scene.getStylesheets().add("sharknoon/dualide/ui/MainCSS.css");

        stage.setTitle("IDE");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();

    }

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
        EXITABLES.forEach(Exitable::onExit);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
