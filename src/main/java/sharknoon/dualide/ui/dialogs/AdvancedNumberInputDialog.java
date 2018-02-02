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
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import sharknoon.dualide.utils.javafx.NumberField;

/**
 *
 * @author Josua Frank
 */
public class AdvancedNumberInputDialog extends Dialog<Double> {

    private final GridPane grid;
    private final Label label;
    private final NumberField numberfield;
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

        // -- numberfield
        defaultValue = defaultValue == null ? 0.0 : defaultValue;
        this.numberfield = new NumberField(defaultValue);
        this.numberfield.setMaxWidth(Double.MAX_VALUE);
        if (forbiddenValues != null) {
            this.numberfield.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
                if (!forbiddenValues
                        .stream()
                        .map(d -> d.toString())
                        .anyMatch((f) -> f.equals(newValue))) {
                    this.numberfield.setStyle("-fx-text-fill: #FF0000;");
                    getDialogPane().lookupButton(ButtonType.OK).setDisable(true);
                } else {
                    this.numberfield.setStyle("-fx-text-fill: -fx-text-inner-color;");
                    getDialogPane().lookupButton(ButtonType.OK).setDisable(false);
                }
            });
        }
        GridPane.setHgrow(numberfield, Priority.ALWAYS);
        GridPane.setFillWidth(numberfield, true);

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
            return data == ButtonBar.ButtonData.OK_DONE ? numberfield.getValue() : null;
        });
    }

    public final NumberField getEditor() {
        return numberfield;
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
        grid.add(numberfield, 1, 0);
        getDialogPane().setContent(grid);

        Platform.runLater(() -> numberfield.requestFocus());
    }

}