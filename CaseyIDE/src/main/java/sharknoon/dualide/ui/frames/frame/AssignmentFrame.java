package sharknoon.dualide.ui.frames.frame;/*
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

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import sharknoon.dualide.logic.ValueReturnable;
import sharknoon.dualide.logic.blocks.Block;
import sharknoon.dualide.logic.types.Type;
import sharknoon.dualide.ui.fields.ValueField;
import sharknoon.dualide.ui.fields.VariableField;
import sharknoon.dualide.ui.frames.Frame;
import sharknoon.dualide.ui.misc.Icon;
import sharknoon.dualide.ui.misc.Icons;
import sharknoon.dualide.utils.javafx.BindUtils;

public class AssignmentFrame extends Frame<Rectangle> {
    
    
    private VariableField variableField;
    private ValueField valueField;
    
    public AssignmentFrame(Block block, Point2D origin) {
        super(block, origin);
    }
    
    @Override
    protected int initFrameHeight() {
        return 100;
    }
    
    @Override
    protected int initFrameWidth() {
        return 200;
    }
    
    @Override
    protected Rectangle initFrameShape() {
        var rectangle = new Rectangle(getWidth(), getHeight());
        rectangle.setFill(Color.WHITE);
        return rectangle;
    }
    
    @Override
    protected Pane initFrameContent(Block block) {
        variableField = new VariableField();
        variableField.variableProperty().bindBidirectional(block.variableProperty());
        ObjectProperty<Type> variableType = new SimpleObjectProperty<>();
        BindUtils.addListener(variableField.variableProperty(), (observable, oldValue, newValue) -> {
            if (newValue == null) {
                variableType.set(Type.UNDEFINED);
                return;
            }
            BindUtils.addListener(((ValueReturnable<?>) newValue).returnTypeProperty(), (observable1, oldValue1, newValue1) -> {
                variableType.set(newValue1 == null ? Type.UNDEFINED : newValue1);
            });
        });
        
        var equals = Icons.get(Icon.EQUAL);
        
        valueField = new ValueField(variableType);
        valueField.statementProperty().bindBidirectional(block.statementProperty());
        
        var gridPaneContent = new GridPane();
        gridPaneContent.setVgap(5);
        gridPaneContent.setAlignment(Pos.CENTER);
        gridPaneContent.addRow(0, variableField, equals, valueField);
        return gridPaneContent;
    }
    
    @Override
    protected ObservableList<Text> initFrameText() {
        ObservableList<Text> varTexts = variableField.toText();
        ObservableList<Text> equalsTexts = FXCollections.observableArrayList(new Text(" = "));
        ObservableList<Text> valueTexts = valueField.toText();
        return BindUtils.concatAll(varTexts, equalsTexts, valueTexts);
    }
}
