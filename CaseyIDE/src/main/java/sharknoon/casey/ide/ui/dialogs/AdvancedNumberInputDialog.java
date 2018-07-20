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
package sharknoon.casey.ide.ui.dialogs;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.util.StringConverter;
import javafx.util.converter.DoubleStringConverter;

import java.util.Collections;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author Josua Frank
 */
public class AdvancedNumberInputDialog extends Dialog<Double> {
    
    private static Label createContentLabel(String text) {
        Label label = new Label(text);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setMaxHeight(Double.MAX_VALUE);
        label.getStyleClass().add("content");
        label.setWrapText(true);
        label.setPrefWidth(360);
        return label;
    }
    private final GridPane grid;
    private final Label label;
    private final Spinner<Double> spinner;
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
        
        // -- spinner
        defaultValue = defaultValue == null ? 0.0 : defaultValue;
        this.spinner = new Spinner<>(-Double.MAX_VALUE, Double.MAX_VALUE, defaultValue);
        this.spinner.setMaxWidth(Double.MAX_VALUE);
        this.spinner.getValueFactory().setConverter(new StringConverter<Double>() {
            @Override
            public String toString(Double value) {
                // If the specified value is null, return a zero-length String
                if (value == null) {
                    return "";
                }
    
                return value.toString();
            }
    
            @Override
            public Double fromString(String value) {
                try {
                    // If the specified value is null or zero-length, return null
                    if (value == null) {
                        return null;
                    }
    
                    value = value.trim();
    
                    if (value.length() < 1) {
                        return null;
                    }
    
                    // Perform the requested parsing
                    return Double.parseDouble(value);
                } catch (NumberFormatException ex) {
                    return -1.0;
                }
            }
        });
        this.spinner.getEditor().setMinWidth(14);
        this.spinner.setEditable(true);
        if (forbiddenValues != null) {
            this.spinner.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
                if (forbiddenValues
                        .stream()
                        .map(Object::toString)
                        .noneMatch((f) -> f.equals(newValue))) {
                    this.spinner.setStyle("-fx-text-fill: #FF0000;");
                    getDialogPane().lookupButton(ButtonType.OK).setDisable(true);
                } else {
                    this.spinner.setStyle("-fx-text-fill: -fx-text-inner-color;");
                    getDialogPane().lookupButton(ButtonType.OK).setDisable(false);
                }
            });
        }
        this.spinner.getEditor().setOnKeyPressed((event) -> {
            if (event.getCode() == KeyCode.ENTER) {
                ((Button) dialogPane.lookupButton(ButtonType.OK)).fire();
                event.consume();
            }
        });
        this.spinner.getEditor().setTextFormatter(new TextFormatter<>(new DoubleStringConverter(), 0.0,
                change -> {
                    String newText = change.getControlNewText();
                    if (Pattern.compile("-?((\\d*)|(\\d+\\.\\d*))").matcher(newText).matches()) {
                        return change;
                    } else return null;
                }));
        GridPane.setHgrow(spinner, Priority.ALWAYS);
        GridPane.setFillWidth(spinner, true);
        
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
        
        //setTitle(ControlResources.getString("Dialog.confirm.title"));
        //dialogPane.setHeaderText(ControlResources.getString("Dialog.confirm.header"));
        dialogPane.getStyleClass().add("text-input-dialog");
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        updateGrid();
        
        setResultConverter((dialogButton) -> {
            ButtonBar.ButtonData data = dialogButton == null ? null : dialogButton.getButtonData();
            return data == ButtonBar.ButtonData.OK_DONE ? spinner.getValue() : null;
        });
    }
    
    public final Spinner<Double> getEditor() {
        return spinner;
    }
    
    public final Double getDefaultValue() {
        return defaultValue;
    }
    
    public final Set<Double> getForbiddenValues() {
        return forbiddenValues;
    }
    
    private void updateGrid() {
        grid.getChildren().clear();
        
        grid.add(label, 0, 0);
        grid.add(spinner, 1, 0);
        getDialogPane().setContent(grid);
        
        Platform.runLater(() -> {
            spinner.requestFocus();
            spinner.getEditor().selectAll();
        });
    }
    
}
