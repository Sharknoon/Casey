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
package sharknoon.dualide.ui.toolbar;

import java.util.EnumSet;
import java.util.List;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import sharknoon.dualide.logic.items.Class.ObjectType;
import sharknoon.dualide.logic.items.Project;
import sharknoon.dualide.ui.fields.ValueField;
import sharknoon.dualide.ui.misc.Icon;
import sharknoon.dualide.ui.misc.Icons;
import sharknoon.dualide.logic.types.PrimitiveType;
import sharknoon.dualide.ui.bodies.StatementPlaceholderBody;
import sharknoon.dualide.ui.fields.TypeField;

import sharknoon.dualide.utils.language.Language;
import sharknoon.dualide.utils.language.Word;

/**
 *
 * @author Josua Frank
 */
public class ToolBarInit {

    private static ToolBar toolBar;

    public static void init(ToolBar buttonBar) {
        toolBar = buttonBar;
        initSaveButton();
        initDebugButton();
    }

    private static void initSaveButton() {
        Button buttonSave = new Button();
        Icons.set(buttonSave, Icon.SAVE);
        Language.set(Word.TOOLBAR_BUTTON_SAVE_TEXT, buttonSave);
        buttonSave.setOnAction(e -> Project.getCurrentProject().ifPresent(Project::save));
        toolBar.getItems().add(buttonSave);
    }

    private static void initDebugButton() {
        Button buttonDebug = new Button("ValuesCheck");
        buttonDebug.setOnAction(e -> TMP());
        toolBar.getItems().add(buttonDebug);
    }

    private static void TMP() {
        VBox center = new VBox();
        Group vBoxGroup = new Group(center);
        BorderPane root = new BorderPane(vBoxGroup);
        Scene scene = new Scene(root);
        scene.getStylesheets().add("sharknoon/dualide/ui/MainCSS.css");

        Label result = new Label();
        ValueField field = new ValueField();
        field.setOnStatementSet(s -> {
            result.setText(s.calculateResult().toString() + "\n" + s.toString());
        });
        field.setOnStatementDestroyed(() -> result.setText(""));
        field.addStatementChangeListener(s -> {
            result.setText(s.calculateResult().toString() + "\n" + s.toString());
        });

        TypeField typeField = new TypeField(List.of(PrimitiveType.BOOLEAN, PrimitiveType.TEXT));

        TypeField typeField2 = new TypeField(List.of());
        
        center.getChildren().addAll(field, result, typeField, typeField2);

        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setHeight(750);
        stage.setWidth(1200);
        stage.show();
    }

}
