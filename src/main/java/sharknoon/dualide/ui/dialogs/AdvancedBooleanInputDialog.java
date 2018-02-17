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

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public class AdvancedBooleanInputDialog extends Dialog<Boolean> {

    private final GridPane grid;
    private final Label label;
    private final CheckBox checkBox;
    private final Boolean defaultValue;
    private final Boolean forbiddenValue;

    public AdvancedBooleanInputDialog() {
        this(false);
    }

    public AdvancedBooleanInputDialog(Boolean defaultValue) {
        this(defaultValue, null);
    }

    public AdvancedBooleanInputDialog(Boolean defaultValue, Boolean forbiddenValue) {
        final DialogPane dialogPane = getDialogPane();

        this.forbiddenValue = forbiddenValue;

        // -- checkbox
        this.checkBox = new CheckBox();
        this.checkBox.setMaxWidth(Double.MAX_VALUE);
        if (defaultValue != null) {
            this.checkBox.setSelected(defaultValue);
        }
        if (forbiddenValue != null) {
            this.checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
                if (forbiddenValue.equals(newValue)) {
                    this.checkBox.getStyleClass().add("check-box-incorrect");
                    getDialogPane().lookupButton(ButtonType.OK).setDisable(true);
                } else {
                    this.checkBox.getStyleClass().remove("check-box-incorrect");
                    getDialogPane().lookupButton(ButtonType.OK).setDisable(false);
                }
            });
        }
        checkBox.setOnKeyPressed((event) -> {
            if (event.getCode() == KeyCode.ENTER) {
                ((Button) dialogPane.lookupButton(ButtonType.OK)).fire();
                event.consume();
            }
        });
        GridPane.setHgrow(checkBox, Priority.ALWAYS);
        GridPane.setFillWidth(checkBox, true);

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
            ButtonData data = dialogButton == null ? null : dialogButton.getButtonData();
            return data == ButtonData.OK_DONE ? checkBox.isSelected() : null;
        });
    }

    public final CheckBox getEditor() {
        return checkBox;
    }

    public final Boolean getDefaultValue() {
        return defaultValue;
    }

    public final Boolean getForbiddenValue() {
        return forbiddenValue;
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
        grid.add(checkBox, 1, 0);
        getDialogPane().setContent(grid);

        Platform.runLater(() -> checkBox.requestFocus());
    }

}
