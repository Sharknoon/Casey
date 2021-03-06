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
import sharknoon.casey.ide.ui.fields.ValueField;
import sharknoon.casey.ide.ui.frames.Frame;

public class OutputFrame extends Frame<Polygon> {
    private ValueField valueField;
    
    public OutputFrame(Block block, Point2D origin) {
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
                getWidth() - (getHeight() / 2), 0,
                getWidth(), getHeight() / 2,
                getWidth() - (getHeight() / 2), getHeight(),
                0, getHeight());
        polygon.setFill(Color.BLUE);
        return polygon;
    }
    
    @Override
    protected Pane initFrameContent(Block block) {
        valueField = new ValueField();
        valueField.statementProperty().bindBidirectional(block.statementProperty());
        return new Pane(valueField);
    }
    
    @Override
    protected ObservableList<Text> initFrameText() {
        return valueField.toText();
    }
}
