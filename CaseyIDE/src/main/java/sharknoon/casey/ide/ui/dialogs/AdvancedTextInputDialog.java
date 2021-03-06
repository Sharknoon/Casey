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
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import sharknoon.casey.ide.utils.javafx.BindUtils;

import java.util.Collections;
import java.util.Set;

public class AdvancedTextInputDialog extends Dialog<String> {
    
    private final GridPane grid;
    private final Label label;
    private final TextField textField;
    private final String defaultValue;
    private final Set<String> forbiddenValues;
    
    public AdvancedTextInputDialog() {
        this("");
    }
    
    public AdvancedTextInputDialog(String defaultValue) {
        this(defaultValue, null);
    }
    
    public AdvancedTextInputDialog(String defaultValue, Set<String> forbiddenValues) {
        this(defaultValue, forbiddenValues, null);
    }
    
    public AdvancedTextInputDialog(String defaultValue, Set<String> forbiddenValues, String regex) {
        final DialogPane dialogPane = getDialogPane();
        
        this.forbiddenValues = forbiddenValues != null
                ? forbiddenValues
                : Collections.emptySet();
        
        // -- textfield
        this.textField = new TextField();
        this.textField.setMaxWidth(Double.MAX_VALUE);
        if (defaultValue != null) {
            this.textField.setText(defaultValue);
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
        
        //setTitle(ControlResources.getString("Dialog.confirm.title"));
        //dialogPane.setHeaderText(ControlResources.getString("Dialog.confirm.header"));
        dialogPane.getStyleClass().add("text-input-dialog");
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
    
        if (forbiddenValues != null || regex != null) {
            BindUtils.addListener(textField.textProperty(), (observable, oldValue, newValue) -> {
                if ((forbiddenValues != null && forbiddenValues.contains(newValue)) || (regex != null && !newValue.matches(regex))) {
                    this.textField.setStyle("-fx-text-fill: #FF0000;");
                    getDialogPane().lookupButton(ButtonType.OK).setDisable(true);
                } else {
                    this.textField.setStyle("-fx-text-fill: -fx-text-inner-color;");
                    getDialogPane().lookupButton(ButtonType.OK).setDisable(false);
                }
            });
        }
        
        updateGrid();
        
        setResultConverter((dialogButton) -> {
            ButtonData data = dialogButton == null ? null : dialogButton.getButtonData();
            return data == ButtonData.OK_DONE ? textField.getText() : null;
        });
    }
    
    public final TextField getEditor() {
        return textField;
    }
    
    public final String getDefaultValue() {
        return defaultValue;
    }
    
    public final Set<String> getForbiddenValues() {
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
    
}
