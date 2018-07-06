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

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Side;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import sharknoon.dualide.logic.types.PrimitiveType;
import sharknoon.dualide.ui.fields.ValueField;
import sharknoon.dualide.ui.sites.function.FunctionSite;
import sharknoon.dualide.ui.sites.function.blocks.Block;
import sharknoon.dualide.ui.sites.function.blocks.BlockContent;
import sharknoon.dualide.utils.javafx.BindUtils;

/**
 * This is a Decision block, similar like a if clausel
 *
 * @author Josua Frank
 */
public class Decision extends Block<Polygon> {
    
    public Decision(FunctionSite functionSite) {
        super(functionSite);
    }
    
    public Decision(FunctionSite functionSite, String id) {
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
        return new Side[]{Side.RIGHT, Side.LEFT};
    }
    
    @Override
    public Side[] initDotInputSides() {
        return new Side[]{Side.TOP};
    }
    
    @Override
    public Polygon initBlockShape() {
        var polygon = new Polygon(
                getWidth() / 2, 0,//oben mitte
                getWidth(), getHeight() / 2,
                getWidth() / 2, getHeight(),
                0, getHeight() / 2);
        polygon.setFill(Color.YELLOW);
        return polygon;
    }
    
    @Override
    public BlockContent initBlockContent() {
        return new DecisionBlockContent();
    }
    
    private static class DecisionBlockContent extends BlockContent {
        
        ValueField valueField;
        
        private DecisionBlockContent() {
            valueField = new ValueField(PrimitiveType.BOOLEAN);
            getChildren().add(valueField);
        }
        
        @Override
        public ObservableList<Text> toText() {
            ObservableList<Text> result = FXCollections.observableArrayList();
            BindUtils.addListener(valueField.statementProperty(), (observable, oldValue, newValue) -> {
                if (newValue != null) {
                    ObservableList<Text> text = newValue.getBody().toText();
                    BindUtils.addListener(text, c -> {
                        result.clear();
                        result.addAll(text);
                    });
                } else {
                    result.clear();
                    result.add(new Text("null"));
                }
            });
            return result;
        }
        
    }
    
}
