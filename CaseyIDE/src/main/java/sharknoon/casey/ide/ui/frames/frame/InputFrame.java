package sharknoon.casey.ide.ui.frames.frame;/*
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

import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import sharknoon.casey.ide.logic.blocks.Block;
import sharknoon.casey.ide.ui.fields.VariableField;
import sharknoon.casey.ide.ui.frames.Frame;

public class InputFrame extends Frame<Polygon> {
    private VariableField variableField;
    
    public InputFrame(Block block, Point2D origin) {
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
    protected Polygon initFrameShape() {
        var polygon = new Polygon(
                0, 0,
                getWidth(), 0,
                getWidth(), getHeight(),
                0, getHeight(),
                getHeight() / 2, getHeight() / 2);
        polygon.setFill(Color.ORANGE);
        return polygon;
    }
    
    @Override
    protected Pane initFrameContent(Block block) {
        variableField = new VariableField();
        variableField.variableProperty().bindBidirectional(block.variableProperty());
        return new Pane(variableField);
    }
    
    @Override
    protected ObservableList<Text> initFrameText() {
        return variableField.toText();
    }
}
