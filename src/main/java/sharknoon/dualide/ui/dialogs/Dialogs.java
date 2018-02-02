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
package sharknoon.dualide.ui.dialogs;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;
import java.util.Set;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import sharknoon.dualide.logic.statements.values.creations.CreationType;
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

    public interface DialogTypes {
    };

    public enum TextInputs implements DialogTypes {
        NEW_PROJECT_DIALOG,
        NEW_PACKAGE_DIALOG,
        NEW_CLASS_DIALOG,
        NEW_FUNCTION_DIALOG,
        NEW_VARIABLE_DIALOG,
        RENAME_PACKAGE_DIALOG,
        RENAME_CLASS_DIALOG,
        RENAME_PROJECT_DIALOG,
        RENAME_VARIABLE_DIALOG,
        NEW_TEXT_VALUE
    }

    public enum NumberInputs implements DialogTypes {
        NEW_NUMBER_VALUE
    }

    public enum BooleanInputs implements DialogTypes {
        NEW_BOOLEAN_VALUE
    }

    public enum Confirmations implements DialogTypes {
        DELETE_PACKAGE_DIALOG,
        DELETE_PROJECT_DIALOG,
        DELETE_CLASS_DIALOG,
        DELETE_VARIABLE_DIALOG
    }

    public enum TextEditors implements DialogTypes {
        COMMENT_PACKAGE_DIALOG,
        COMMENT_PROJECT_DIALOG,
        COMMENT_CLASS_DIALOG,
        COMMENT_VARIABLE_DIALOG
    }

    public enum Errors implements DialogTypes {
        PROJECT_CORRUPT_DIALOG
    }

    public static Optional<String> showTextInputDialog(TextInputs type, String... variables) {
        return showTextInputDialog(type, null, variables);
    }

    public static Optional<String> showTextInputDialog(TextInputs type, Set<String> forbiddenValues, String... variables) {
        return showTextInputDialog(type, null, forbiddenValues, variables);
    }

    public static Optional<String> showTextInputDialog(TextInputs type, String defaultValue, Set<String> forbiddenValues, String... variables) {
        switch (type) {
            case NEW_PROJECT_DIALOG:
                return showTextInputDialog(
                        NEW_PROJECT_DIALOG_TITLE,
                        NEW_PROJECT_DIALOG_HEADER_TEXT,
                        NEW_PROJECT_DIALOG_CONTENT_TEXT,
                        Icon.PLUSPROJECT,
                        defaultValue,
                        forbiddenValues,
                        variables);
            case NEW_PACKAGE_DIALOG:
                return showTextInputDialog(
                        NEW_PACKAGE_DIALOG_TITLE,
                        NEW_PACKAGE_DIALOG_HEADER_TEXT,
                        NEW_PACKAGE_DIALOG_CONTENT_TEXT,
                        Icon.PLUSPACKAGE,
                        defaultValue,
                        forbiddenValues,
                        variables);
            case NEW_CLASS_DIALOG:
                return showTextInputDialog(
                        NEW_CLASS_DIALOG_TITLE,
                        NEW_CLASS_DIALOG_HEADER_TEXT,
                        NEW_CLASS_DIALOG_CONTENT_TEXT,
                        Icon.PLUSCLASS,
                        defaultValue,
                        forbiddenValues,
                        variables);
            case NEW_FUNCTION_DIALOG:
                return showTextInputDialog(
                        NEW_FUNCTION_DIALOG_TITLE,
                        NEW_FUNCTION_DIALOG_HEADER_TEXT,
                        NEW_FUNCTION_DIALOG_CONTENT_TEXT,
                        Icon.PLUSFUNCTION,
                        defaultValue,
                        forbiddenValues,
                        variables);
            case NEW_VARIABLE_DIALOG:
                return showTextInputDialog(
                        NEW_VARIABLE_DIALOG_TITLE,
                        NEW_VARIABLE_DIALOG_HEADER_TEXT,
                        NEW_VARIABLE_DIALOG_CONTENT_TEXT,
                        Icon.PLUSVARIABLE,
                        defaultValue,
                        forbiddenValues,
                        variables);
            case RENAME_PACKAGE_DIALOG:
                return showTextInputDialog(
                        RENAME_PACKAGE_DIALOG_TITLE,
                        RENAME_PACKAGE_DIALOG_HEADER_TEXT,
                        RENAME_PACKAGE_DIALOG_CONTENT_TEXT,
                        Icon.RENAME,
                        defaultValue,
                        forbiddenValues,
                        variables);
            case RENAME_CLASS_DIALOG:
                return showTextInputDialog(
                        RENAME_CLASS_DIALOG_TITLE,
                        RENAME_CLASS_DIALOG_HEADER_TEXT,
                        RENAME_CLASS_DIALOG_CONTENT_TEXT,
                        Icon.RENAME,
                        defaultValue,
                        forbiddenValues,
                        variables);
            case RENAME_PROJECT_DIALOG:
                return showTextInputDialog(
                        RENAME_PROJECT_DIALOG_TITLE,
                        RENAME_PROJECT_DIALOG_HEADER_TEXT,
                        RENAME_PROJECT_DIALOG_CONTENT_TEXT,
                        Icon.RENAME,
                        defaultValue,
                        forbiddenValues,
                        variables);
            case RENAME_VARIABLE_DIALOG:
                return showTextInputDialog(
                        RENAME_VARIABLE_DIALOG_TITLE,
                        RENAME_VARIABLE_DIALOG_HEADER_TEXT,
                        RENAME_VARIABLE_DIALOG_CONTENT_TEXT,
                        Icon.RENAME,
                        defaultValue,
                        forbiddenValues,
                        variables);
            case NEW_TEXT_VALUE:
                return showTextInputDialog(
                        NEW_TEXT_VALUE_DIALOG_TITLE,
                        NEW_TEXT_VALUE_DIALOG_HEADER_TEXT,
                        NEW_TEXT_VALUE_DIALOG_CONTENT_TEXT,
                        Icon.TEXT,
                        defaultValue,
                        forbiddenValues,
                        variables);
        }
        return Optional.empty();
    }

    public static Optional<Double> showNumberInputDialog(NumberInputs type, String... variables) {
        return showNumberInputDialog(type, null, variables);
    }

    public static Optional<Double> showNumberInputDialog(NumberInputs type, Set<Double> forbiddenValues, String... variables) {
        return showNumberInputDialog(type, null, forbiddenValues, variables);
    }

    public static Optional<Double> showNumberInputDialog(NumberInputs type, Double defaultValue, Set<Double> forbiddenValues, String... variables) {
        switch (type) {
            case NEW_NUMBER_VALUE:
                return showNumberInputDialog(
                        NEW_NUMBER_VALUE_DIALOG_TITLE,
                        NEW_NUMBER_VALUE_DIALOG_HEADER_TEXT,
                        NEW_NUMBER_VALUE_DIALOG_CONTENT_TEXT,
                        Icon.NUMBER,
                        defaultValue,
                        forbiddenValues,
                        variables);
        }
        return Optional.empty();
    }

    public static Optional<Boolean> showBooleanInputDialog(BooleanInputs type, String... variables) {
        return showBooleanInputDialog(type, null, variables);
    }
    
    public static Optional<Boolean> showBooleanInputDialog(BooleanInputs type,Boolean forbiddenValue, String... variables) {
        return showBooleanInputDialog(type, null, forbiddenValue, variables);
    }
    
    public static Optional<Boolean> showBooleanInputDialog(BooleanInputs type, Boolean defaultValue, Boolean forbiddenValue, String... variables) {
        switch (type) {
            case NEW_BOOLEAN_VALUE:
                return showBooleanInputDialog(
                        NEW_BOOLEAN_VALUE_DIALOG_TITLE,
                        NEW_BOOLEAN_VALUE_DIALOG_HEADER_TEXT,
                        NEW_BOOLEAN_VALUE_DIALOG_CONTENT_TEXT,
                        Icon.BOOLEAN,
                        defaultValue,
                        forbiddenValue,
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
            case DELETE_CLASS_DIALOG:
                return showConfirmationDialog(
                        DELETE_CLASS_DIALOG_TITLE,
                        DELETE_CLASS_DIALOG_HEADER_TEXT,
                        DELETE_CLASS_DIALOG_CONTENT_TEXT,
                        Icon.TRASH,
                        variables);
            case DELETE_VARIABLE_DIALOG:
                return showConfirmationDialog(
                        DELETE_VARIABLE_DIALOG_TITLE,
                        DELETE_VARIABLE_DIALOG_HEADER_TEXT,
                        DELETE_VARIABLE_DIALOG_CONTENT_TEXT,
                        Icon.TRASH,
                        variables);
        }
        return Optional.empty();
    }

    public static Optional<String> showTextEditorDialog(TextEditors type, String defaultValue, String... variables) {
        switch (type) {
            case COMMENT_PACKAGE_DIALOG:
                return showTextEditorDialog(
                        COMMENT_PACKAGE_DIALOG_TITLE,
                        COMMENT_PACKAGE_DIALOG_HEADER_TEXT,
                        Icon.COMMENTS,
                        defaultValue,
                        variables);
            case COMMENT_PROJECT_DIALOG:
                return showTextEditorDialog(
                        COMMENT_PROJECT_DIALOG_TITLE,
                        COMMENT_PROJECT_DIALOG_HEADER_TEXT,
                        Icon.COMMENTS,
                        defaultValue,
                        variables);
            case COMMENT_CLASS_DIALOG:
                return showTextEditorDialog(
                        COMMENT_CLASS_DIALOG_TITLE,
                        COMMENT_CLASS_DIALOG_HEADER_TEXT,
                        Icon.COMMENTS,
                        defaultValue,
                        variables);
            case COMMENT_VARIABLE_DIALOG:
                return showTextEditorDialog(
                        COMMENT_VARIABLE_DIALOG_TITLE,
                        COMMENT_VARIABLE_DIALOG_HEADER_TEXT,
                        Icon.COMMENTS,
                        defaultValue,
                        variables);
        }
        return Optional.empty();
    }

    public static void showErrorDialog(Errors type, Exception exception, String... variables) {
        switch (type) {
            case PROJECT_CORRUPT_DIALOG:
                showErrorDialog(
                        PROJECT_CORRUPT_DIALOG_TITLE,
                        PROJECT_CORRUPT_DIALOG_HEADER_TEXT,
                        PROJECT_CORRUPT_DIALOG_CONTENT_TEXT,
                        Icon.ERROR,
                        exception,
                        variables);
        }
    }

    private static Optional<String> showTextInputDialog(Word title, Word headerText, Word contentText, Icon icon, String defaultValue, Set<String> forbiddenValues, String... variables) {
        AdvancedTextInputDialog dialog = new AdvancedTextInputDialog(defaultValue, forbiddenValues);
        dialog.setTitle(fill(Language.get(title), variables));
        dialog.setHeaderText(fill(Language.get(headerText), variables));
        dialog.setContentText(fill(Language.get(contentText), variables));
        setIcon(icon, dialog);
        setStyle(dialog);
        return dialog.showAndWait();
    }

    private static Optional<Double> showNumberInputDialog(Word title, Word headerText, Word contentText, Icon icon, Double defaultValue, Set<Double> forbiddenValues, String... variables) {
        AdvancedNumberInputDialog dialog = new AdvancedNumberInputDialog(defaultValue, forbiddenValues);
        dialog.setTitle(fill(Language.get(title), variables));
        dialog.setHeaderText(fill(Language.get(headerText), variables));
        dialog.setContentText(fill(Language.get(contentText), variables));
        setIcon(icon, dialog);
        setStyle(dialog);
        return dialog.showAndWait();
    }

    private static Optional<Boolean> showBooleanInputDialog(Word title, Word headerText, Word contentText, Icon icon, Boolean defaultValue, Boolean forbiddenValue, String... variables) {
        AdvancedBooleanInputDialog dialog = new AdvancedBooleanInputDialog(defaultValue, forbiddenValue);
        dialog.setTitle(fill(Language.get(title), variables));
        dialog.setHeaderText(fill(Language.get(headerText), variables));
        dialog.setContentText(fill(Language.get(contentText), variables));
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

    private static Optional<String> showTextEditorDialog(Word title, Word headerText, Icon icon, String defaultValue, String... variables) {
        TextEditorDialog dialog = new TextEditorDialog(defaultValue);
        dialog.setTitle(fill(Language.get(title), variables));
        dialog.setHeaderText(fill(Language.get(headerText), variables));
        setIcon(icon, dialog);
        setStyle(dialog);
        return dialog.showAndWait();
    }

    private static void showErrorDialog(Word title, Word headerText, Word contentText, Icon icon, Exception exception, String... variables) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(fill(Language.get(title), variables));
        alert.setHeaderText(fill(Language.get(headerText), variables));
        alert.setContentText(fill(Language.get(contentText), variables));

        if (exception != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            exception.printStackTrace(pw);
            String exceptionText = sw.toString();

            TextArea textArea = new TextArea(exceptionText);
            textArea.setEditable(false);
            textArea.setWrapText(true);
            textArea.setMaxWidth(Double.MAX_VALUE);
            textArea.setMaxHeight(Double.MAX_VALUE);

            alert.getDialogPane().setExpandableContent(textArea);
        }

        setIcon(icon, alert);
        setStyle(alert);
        alert.showAndWait();
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

    private static void setStyle(Dialog dialog) {
        Scene scene = dialog.getDialogPane().getScene();
        scene.getStylesheets().add(CSSPATH);
    }
}