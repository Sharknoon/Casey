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

import com.sun.javafx.scene.control.skin.resources.ControlResources;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.web.HTMLEditor;

/**
 *
 * @author frank
 */
public class TextEditorDialog extends Dialog<String> {

    private final GridPane grid;
    private final HTMLEditor editor;
    private final String defaultValue;

    public TextEditorDialog() {
        this("");
    }

    public TextEditorDialog(String defaultValue) {
        final DialogPane dialogPane = getDialogPane();

        this.editor = new HTMLEditor();
        this.editor.setHtmlText(defaultValue);
        this.editor.setMaxWidth(Double.MAX_VALUE);
        this.editor.requestFocus();
        this.editor.setStyle("-fx-focus-color: #FFFFFF00; -fx-faint-focus-color: #FFFFFF00;");
        this.editor.addEventFilter(KeyEvent.ANY, (KeyEvent event) -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                event.consume();
            }
        });
        GridPane.setHgrow(editor, Priority.ALWAYS);
        GridPane.setFillWidth(editor, true);

        this.defaultValue = defaultValue;

        this.grid = new GridPane();
        this.grid.setHgap(10);
        this.grid.setMaxWidth(Double.MAX_VALUE);
        this.grid.setAlignment(Pos.CENTER_LEFT);
        this.grid.setPadding(Insets.EMPTY);
        this.grid.setGridLinesVisible(true);

        dialogPane.contentTextProperty().addListener(o -> updateGrid());

        setTitle(ControlResources.getString("Dialog.confirm.title"));
        dialogPane.setHeaderText(ControlResources.getString("Dialog.confirm.header"));
        dialogPane.getStyleClass().add("text-input-dialog");
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        updateGrid();

        setResultConverter((dialogButton) -> {
            ButtonBar.ButtonData data = dialogButton == null ? null : dialogButton.getButtonData();
            return data == ButtonBar.ButtonData.OK_DONE ? editor.getHtmlText() : null;
        });

    }

    /**
     * Returns the {@link TextField} used within this dialog.
     *
     * @return
     */
    public final HTMLEditor getEditor() {
        return editor;
    }

    /**
     * Returns the default value that was specified in the constructor.
     *
     * @return
     */
    public final String getDefaultValue() {
        return defaultValue;
    }

    private void updateGrid() {
        grid.getChildren().clear();

        grid.add(editor, 0, 0);
        getDialogPane().setContent(grid);

        Platform.runLater(() -> editor.requestFocus());
    }

}
