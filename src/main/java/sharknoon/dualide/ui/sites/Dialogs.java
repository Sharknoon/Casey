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
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextInputDialog;
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
                        variables);
            case NEW_PACKAGE_DIALOG:
                return showTextInputDialog(
                        NEW_PACKAGE_DIALOG_TITLE,
                        NEW_PACKAGE_DIALOG_HEADER_TEXT,
                        NEW_PACKAGE_DIALOG_CONTENT_TEXT,
                        variables);
            case RENAME_PACKAGE_DIALOG:
                return showTextInputDialog(
                        RENAME_PACKAGE_DIALOG_TITLE,
                        RENAME_PACKAGE_DIALOG_HEADER_TEXT,
                        RENAME_PACKAGE_DIALOG_CONTENT_TEXT,
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
                        variables);
            case DELETE_PROJECT_DIALOG:
                return showConfirmationDialog(
                        DELETE_PROJECT_DIALOG_TITLE,
                        DELETE_PROJECT_DIALOG_HEADER_TEXT,
                        DELETE_PROJECT_DIALOG_CONTENT_TEXT,
                        variables);
        }
        return Optional.empty();
    }

    private static Optional<String> showTextInputDialog(Word title, Word headerText, Word conentText, String... variables) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(fill(Language.get(title), variables));
        dialog.setHeaderText(fill(Language.get(headerText), variables));
        dialog.setContentText(fill(Language.get(conentText), variables));
        return dialog.showAndWait();
    }

    private static Optional<Boolean> showConfirmationDialog(Word title, Word headerText, Word conentText, String... variables) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(fill(Language.get(title), variables));
        alert.setHeaderText(fill(Language.get(headerText), variables));
        alert.setContentText(fill(Language.get(conentText), variables));
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

    private static String fill(String toInsert, String... variables) {
        StringBuilder builder = new StringBuilder();
        int counter = 0;
        for (String part : toInsert.split("::")) {
            builder.append(part);
            if (variables.length > counter) {
                builder.append(variables[counter]);
                counter++;
            }
        }
        return builder.toString();
    }
}
