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
package sharknoon.dualide.ui.buttonbar;

import java.util.EnumSet;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import sharknoon.dualide.logic.items.Project;
import sharknoon.dualide.logic.statements.operations.AddOperator;
import sharknoon.dualide.logic.statements.operations.Operator;
import sharknoon.dualide.logic.statements.values.BooleanValue;
import sharknoon.dualide.logic.statements.values.NumberValue;
import sharknoon.dualide.logic.statements.values.TextValue;
import sharknoon.dualide.ui.misc.Icon;
import sharknoon.dualide.ui.misc.Icons;
import sharknoon.dualide.logic.statements.values.ValueType;
import sharknoon.dualide.ui.statements.Body;

import sharknoon.dualide.utils.language.Language;
import sharknoon.dualide.utils.language.Word;

/**
 *
 * @author Josua Frank
 */
public class ToolBarInit {

    private static ToolBar buttonbar;

    public static void init(ToolBar buttonBar) {
        buttonbar = buttonBar;
        initSaveButton();
        initDebugButton();
    }

    private static void initSaveButton() {
        Button buttonSave = new Button();
        Icons.set(buttonSave, Icon.SAVE);
        Language.set(Word.TOOLBAR_BUTTON_SAVE_TEXT, buttonSave);
        buttonSave.setOnAction(e -> Project.getCurrentProject().ifPresent(Project::save));
        buttonbar.getItems().add(buttonSave);
    }

    private static void initDebugButton() {
        Button buttonDebug = new Button("ValuesCheck");
        buttonDebug.setOnAction(e -> TMP());
        buttonbar.getItems().add(buttonDebug);
    }

    private static void TMP() {
        FlowPane root = new FlowPane();
        Scene scene = new Scene(root);
        scene.getStylesheets().add("sharknoon/dualide/ui/MainCSS.css");

//        FieldStatement bool = new FieldStatement(null, EnumSet.of(ValueType.BOOLEAN));
        Operator op = new AddOperator(null);
        op.addParameter(0, new NumberValue(42.0, op));
        op.addParameter(1, new NumberValue(op));

        root.getChildren().add(new BooleanValue(true, null).getBody());
        root.getChildren().add(new TextValue("sdg", null).getBody());

        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();
    }

}
