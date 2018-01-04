/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sharknoon.dualide;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import sharknoon.dualide.misc.Exitable;
import sharknoon.dualide.utils.settings.Ressources;
import sharknoon.dualide.utils.settings.Props;

/**
 *
 * @author frank
 */
public class Main extends Application {

    public static Stage stage;

    @Override
    public void start(Stage primaryStage) throws IOException {
        Ressources.resetRessources(true);
        stage = primaryStage;
        FXMLLoader loader = new FXMLLoader();
        Path fxmlPath = Ressources.createAndGetFile("MainFXML.fxml", true);
        Parent root = loader.load(Files.newInputStream(fxmlPath));

        Scene scene = new Scene(root);
        scene.getStylesheets().add("sharknoon/dualide/MainCSS.css");
        
        stage.setTitle(Props.get("name").orElse("Unnamed Dual Universe IDE"));
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    private static final List<Exitable> EXITABLES = new ArrayList<>();

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
