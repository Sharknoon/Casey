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
package sharknoon.dualide.ui.sites.function.blocks.block;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import sharknoon.dualide.logic.ValueReturnable;
import sharknoon.dualide.logic.types.Type;
import sharknoon.dualide.ui.fields.ValueField;
import sharknoon.dualide.ui.fields.VariableField;
import sharknoon.dualide.ui.misc.Icon;
import sharknoon.dualide.ui.misc.Icons;
import sharknoon.dualide.ui.sites.function.FunctionSite;
import sharknoon.dualide.ui.sites.function.blocks.Block;
import sharknoon.dualide.ui.sites.function.blocks.BlockContent;
import sharknoon.dualide.utils.javafx.BindUtils;

/**
 * Creates a Assignment block, which executes commands
 *
 * @author Josua Frank
 */
public class Assignment extends Block<Rectangle> {
    
    public Assignment(FunctionSite functionSite) {
        super(functionSite);
    }
    
    public Assignment(FunctionSite functionSite, String id) {
        super(functionSite, id);
    }
    
    @Override
    public double initShapeHeight() {
        return 100;
    }
    
    @Override
    public double initShapeWidth() {
        return 200;
    }
    
    @Override
    public Side[] initDotOutputSides() {
        return new Side[]{Side.BOTTOM};
    }
    
    @Override
    public Side[] initDotInputSides() {
        return new Side[]{Side.TOP};
    }
    
    @Override
    public Rectangle initBlockShape() {
        var rectangle = new Rectangle(getWidth(), getHeight());
        rectangle.setFill(Color.WHITE);
        return rectangle;
    }
    
    @Override
    public BlockContent initBlockContent() {
        return new AssignmentBlockContent();
    }
    
    private static class AssignmentBlockContent extends BlockContent {
        
        VariableField variableField;
        ValueField valueField;
        
        private AssignmentBlockContent() {
            variableField = new VariableField();
            var equals = Icons.get(Icon.EQUAL);
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
            valueField = new ValueField(variableType);
            
            var gridPaneContent = new GridPane();
            gridPaneContent.setVgap(5);
            gridPaneContent.setAlignment(Pos.CENTER);
            gridPaneContent.addRow(0, variableField, equals, valueField);
            getChildren().add(gridPaneContent);
        }
        
        
        @Override
        public ObservableList<Text> toText() {
            ObservableList<Text> varTexts = variableField.toText();
            ObservableList<Text> eqalsTexts = FXCollections.observableArrayList(new Text(" = "));
            ObservableList<Text> valueTexts = valueField.toText();
            return BindUtils.concatAll(varTexts, eqalsTexts, valueTexts);
        }
    }
    
}
