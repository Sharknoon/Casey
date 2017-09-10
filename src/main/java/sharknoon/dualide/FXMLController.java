/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sharknoon.dualide;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import sharknoon.dualide.utils.language.Language;
import sharknoon.dualide.utils.language.Word;
import sharknoon.dualide.utils.settings.FileUtils;

/**
 * FXML Controller class
 *
 * @author frank
 */
public class FXMLController implements Initializable {

    private static FXMLController controller;

    public FXMLController() {
        controller = this;
    }

    public static FXMLController getInstance() {
        return controller;
    }

    @FXML
    public JFXButton buttonRun;

    @FXML
    public JFXTextArea textAreaLuaCode;

    @FXML
    public TextFlow textFlowConsole;

    @FXML
    public void onRunButton(ActionEvent e) {
        Platform.runLater(() -> {
            setConsoleOutput(Main.getInstance().compileLua(textAreaLuaCode.getText()));
        });
    }

    public void setConsoleOutput(String s) {
        Platform.runLater(() -> {
            textFlowConsole.getChildren().clear();
            textFlowConsole.getChildren().add(new Text(s + "\n"));
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Language.set(Word.RUN, buttonRun);
    }

}
