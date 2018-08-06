package sharknoon.casey.ide.ui.about;

import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import sharknoon.casey.ide.misc.Updater;
import sharknoon.casey.ide.ui.misc.Icon;
import sharknoon.casey.ide.ui.misc.Icons;
import sharknoon.casey.ide.ui.styles.Styles;
import sharknoon.casey.ide.utils.language.Language;
import sharknoon.casey.ide.utils.language.Word;

public class About {
    
    public static void show() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(Language.get(Word.MENUBAR_OPTIONS_ABOUT_TEXT));
        alert.setHeaderText(null);
        alert.setGraphic(null);
        DialogPane dialogPane = alert.getDialogPane();
        GridPane gridPaneRoot = new GridPane();
        gridPaneRoot.setVgap(10);
        gridPaneRoot.setMouseTransparent(true);
        Stage stage = (Stage) dialogPane.getScene().getWindow();
        stage.getIcons().add(Icons.getImage(Icon.LOGO).orElse(null));
        dialogPane.setPrefSize(250, 350);
        Styles.bindStyleSheets(dialogPane.getStylesheets());
        Node logo = Icons.get(Icon.CASEY, 300);
        GridPane.setHalignment(logo, HPos.CENTER);
        Label labelVersion = new Label("Version " + Updater.getCurrentVersion().orElse("null"));
        GridPane.setHalignment(labelVersion, HPos.CENTER);
        gridPaneRoot.addRow(0, logo);
        gridPaneRoot.addRow(1, labelVersion);
        dialogPane.getChildren().add(gridPaneRoot);
        
        alert.show();
    }
    
}
