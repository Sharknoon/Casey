/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sharknoon.dualide;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;
import sharknoon.dualide.utils.settings.FileUtils;
import sharknoon.dualide.utils.settings.Props;

/**
 *
 * @author frank
 */
public class Main extends Application {

    private static Main main;

    public Main() {
        main = this;
    }

    @Override
    public void init() {
        FileUtils.init();
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        Parent root = loader.load(getClass().getResource("/fxml/FXMLDocument.fxml").openStream());
        FXMLController controller = loader.getController();

        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/fxml.css");

        primaryStage.setTitle(Props.get("name").orElse("Unnamed Dual Universe IDE"));
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    public static Main getInstance() {
        return main;
    }

    public String compileLua(String lua) {
        Globals globals = JsePlatform.standardGlobals();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        globals.STDOUT = new PrintStream(baos);
        globals.STDERR = new PrintStream(baos);
        try {
            LuaValue chunk = globals.load(lua);
            chunk.call();
            return baos.toString();
        } catch (Exception e) {
            return e.getMessage();
        }
    }

}
