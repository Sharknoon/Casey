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

import com.sun.javafx.scene.control.skin.resources.ControlResources;
import java.util.Collections;
import java.util.Set;
import java.util.regex.Pattern;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

/**
 *
 * @author Josua Frank
 */
public class AdvancedNumberInputDialog extends Dialog<Double> {

    private final GridPane grid;
    private final Label label;
    private final TextField textField;
    private final Double defaultValue;
    private final Set<Double> forbiddenValues;

    public AdvancedNumberInputDialog() {
        this(0.0);
    }

    public AdvancedNumberInputDialog(Double defaultValue) {
        this(defaultValue, null);
    }

    public AdvancedNumberInputDialog(Double defaultValue, Set<Double> forbiddenValues) {
        final DialogPane dialogPane = getDialogPane();

        this.forbiddenValues = forbiddenValues != null
                ? forbiddenValues
                : Collections.emptySet();

        // -- textfield
        this.textField = new TextField();
        this.textField.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            //Character filter
            if (!newValue.matches("[0-9\\.\\+-Ee]*")) {
                textField.setText(newValue.replaceAll("[^0-9\\.\\+-Ee]", ""));
            }
        });
        this.textField.setMaxWidth(Double.MAX_VALUE);
        if (defaultValue != null) {
            this.textField.setText(defaultValue.toString());
        }
        if (forbiddenValues != null) {
            this.textField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!isDouble(newValue)
                        || forbiddenValues
                                .stream()
                                .map(d -> d.toString())
                                .anyMatch((f) -> f.equals(newValue))) {
                    this.textField.setStyle("-fx-text-fill: #FF0000;");
                    getDialogPane().lookupButton(ButtonType.OK).setDisable(true);
                } else {
                    this.textField.setStyle("-fx-text-fill: -fx-text-inner-color;");
                    getDialogPane().lookupButton(ButtonType.OK).setDisable(false);
                }
            });
        }
        GridPane.setHgrow(textField, Priority.ALWAYS);
        GridPane.setFillWidth(textField, true);

        // -- label
        label = createContentLabel(dialogPane.getContentText());
        label.setPrefWidth(Region.USE_COMPUTED_SIZE);
        label.textProperty().bind(dialogPane.contentTextProperty());

        this.defaultValue = defaultValue;

        this.grid = new GridPane();
        this.grid.setHgap(10);
        this.grid.setMaxWidth(Double.MAX_VALUE);
        this.grid.setAlignment(Pos.CENTER_LEFT);

        dialogPane.contentTextProperty().addListener(o -> updateGrid());

        setTitle(ControlResources.getString("Dialog.confirm.title"));
        dialogPane.setHeaderText(ControlResources.getString("Dialog.confirm.header"));
        dialogPane.getStyleClass().add("text-input-dialog");
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        updateGrid();

        setResultConverter((dialogButton) -> {
            ButtonBar.ButtonData data = dialogButton == null ? null : dialogButton.getButtonData();
            String text = textField.getText();
            return data == ButtonBar.ButtonData.OK_DONE
                    ? (isDouble(text) ? Double.parseDouble(text) : 0.0)
                    : null;
        });
    }

    public final TextField getEditor() {
        return textField;
    }

    public final Double getDefaultValue() {
        return defaultValue;
    }

    public final Set<Double> getForbiddenValues() {
        return forbiddenValues;
    }

    private static Label createContentLabel(String text) {
        Label label = new Label(text);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setMaxHeight(Double.MAX_VALUE);
        label.getStyleClass().add("content");
        label.setWrapText(true);
        label.setPrefWidth(360);
        return label;
    }

    private void updateGrid() {
        grid.getChildren().clear();

        grid.add(label, 0, 0);
        grid.add(textField, 1, 0);
        getDialogPane().setContent(grid);

        Platform.runLater(() -> textField.requestFocus());
    }

    /**
     *
     * @param input
     * @return
     * @author
     * https://stackoverflow.com/questions/3543729/how-to-check-that-a-string-is-parseable-to-a-double
     */
    private static boolean isDouble(String input) {
        final String Digits = "(\\p{Digit}+)";
        final String HexDigits = "(\\p{XDigit}+)";
        // an exponent is 'e' or 'E' followed by an optionally 
        // signed decimal integer.
        final String Exp = "[eE][+-]?" + Digits;
        final String fpRegex
                = ("[\\x00-\\x20]*"
                + // Optional leading "whitespace"
                "[+-]?("
                + // Optional sign character
                "NaN|"
                + // "NaN" string
                "Infinity|"
                + // "Infinity" string
                // A decimal floating-point string representing a finite positive
                // number without a leading sign has at most five basic pieces:
                // Digits . Digits ExponentPart FloatTypeSuffix
                // 
                // Since this method allows integer-only strings as input
                // in addition to strings of floating-point literals, the
                // two sub-patterns below are simplifications of the grammar
                // productions from the Java Language Specification, 2nd 
                // edition, section 3.10.2.
                // Digits ._opt Digits_opt ExponentPart_opt FloatTypeSuffix_opt
                "(((" + Digits + "(\\.)?(" + Digits + "?)(" + Exp + ")?)|"
                + // . Digits ExponentPart_opt FloatTypeSuffix_opt
                "(\\.(" + Digits + ")(" + Exp + ")?)|"
                + // Hexadecimal strings
                "(("
                + // 0[xX] HexDigits ._opt BinaryExponent FloatTypeSuffix_opt
                "(0[xX]" + HexDigits + "(\\.)?)|"
                + // 0[xX] HexDigits_opt . HexDigits BinaryExponent FloatTypeSuffix_opt
                "(0[xX]" + HexDigits + "?(\\.)" + HexDigits + ")"
                + ")[pP][+-]?" + Digits + "))"
                + "[fFdD]?))"
                + "[\\x00-\\x20]*");// Optional trailing "whitespace"
        return Pattern.matches(fpRegex, input);
    }

}
