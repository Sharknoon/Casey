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
package sharknoon.dualide.ui.sites;

import java.util.Optional;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import sharknoon.dualide.ui.misc.Icon;
import sharknoon.dualide.ui.misc.Icons;
import sharknoon.dualide.utils.language.Language;
import sharknoon.dualide.utils.language.Word;
import static sharknoon.dualide.utils.language.Word.*;

/**
 *
 * @author Josua Frank
 */
public class Dialogs {

    public enum TextInputs {
        NEW_PROJECT_DIALOG,
        NEW_PACKAGE_DIALOG,
        RENAME_PACKAGE_DIALOG
    }

    public enum Confirmations {
        DELETE_PACKAGE_DIALOG,
        DELETE_PROJECT_DIALOG
    }

    public static Optional<String> showTextInputDialog(TextInputs type, String... variables) {
        switch (type) {
            case NEW_PROJECT_DIALOG:
                return showTextInputDialog(
                        NEW_PROJECT_DIALOG_TITLE,
                        NEW_PROJECT_DIALOG_HEADER_TEXT,
                        NEW_PROJECT_DIALOG_CONTENT_TEXT,
                        Icon.PROJECT,
                        variables);
            case NEW_PACKAGE_DIALOG:
                return showTextInputDialog(
                        NEW_PACKAGE_DIALOG_TITLE,
                        NEW_PACKAGE_DIALOG_HEADER_TEXT,
                        NEW_PACKAGE_DIALOG_CONTENT_TEXT,
                        Icon.PACKAGE,
                        variables);
            case RENAME_PACKAGE_DIALOG:
                return showTextInputDialog(
                        RENAME_PACKAGE_DIALOG_TITLE,
                        RENAME_PACKAGE_DIALOG_HEADER_TEXT,
                        RENAME_PACKAGE_DIALOG_CONTENT_TEXT,
                        Icon.RENAME,
                        variables);
        }
        return Optional.empty();
    }

    public static Optional<Boolean> showConfirmationDialog(Confirmations type, String... variables) {
        switch (type) {
            case DELETE_PACKAGE_DIALOG:
                return showConfirmationDialog(
                        DELETE_PACKAGE_DIALOG_TITLE,
                        DELETE_PACKAGE_DIALOG_HEADER_TEXT,
                        DELETE_PACKAGE_DIALOG_CONTENT_TEXT,
                        Icon.TRASH,
                        variables);
            case DELETE_PROJECT_DIALOG:
                return showConfirmationDialog(
                        DELETE_PROJECT_DIALOG_TITLE,
                        DELETE_PROJECT_DIALOG_HEADER_TEXT,
                        DELETE_PROJECT_DIALOG_CONTENT_TEXT,
                        Icon.TRASH,
                        variables);
        }
        return Optional.empty();
    }

    private static Optional<String> showTextInputDialog(Word title, Word headerText, Word conentText, Icon icon, String... variables) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(fill(Language.get(title), variables));
        dialog.setHeaderText(fill(Language.get(headerText), variables));
        dialog.setContentText(fill(Language.get(conentText), variables));
        setIcon(icon, dialog);
        setStyle(dialog);
        return dialog.showAndWait();
    }

    private static Optional<Boolean> showConfirmationDialog(Word title, Word headerText, Word conentText, Icon icon, String... variables) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(fill(Language.get(title), variables));
        alert.setHeaderText(fill(Language.get(headerText), variables));
        alert.setContentText(fill(Language.get(conentText), variables));
        setIcon(icon, alert);
        setStyle(alert);
        Optional<ButtonType> result = alert.showAndWait();
        if (!result.isPresent()) {
            return Optional.empty();
        }
        if (result.get() == ButtonType.OK) {
            return Optional.of(true);
        } else {
            return Optional.of(false);
        }
    }

    private static final String EMPTY = "";

    private static String fill(String toInsert, String... variables) {
        String result = toInsert;
        for (int i = 0; i < variables.length; i += 2) {
            String key = variables[i] != null ? variables[i] : EMPTY;
            String value = variables.length > i + 1 ? variables[i + 1] : EMPTY;
            result = result.replaceAll(key, value);
        }
        return result;
    }

    private static void setIcon(Icon icon, Dialog dialog) {
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(Icons.get(icon).getImage());
    }
    
    private static final String CSSPATH = "sharknoon/dualide/ui/MainCSS.css";
    
    private static void setStyle(Dialog dialog){
        Scene scene = dialog.getDialogPane().getScene();
        scene.getStylesheets().add(CSSPATH);
    }
}
