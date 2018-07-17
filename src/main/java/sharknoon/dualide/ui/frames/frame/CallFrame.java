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

import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import sharknoon.dualide.logic.blocks.Block;
import sharknoon.dualide.ui.fields.ValueField;
import sharknoon.dualide.ui.frames.Frame;

public class CallFrame extends Frame<Rectangle> {
    
    private ValueField valueField;
    
    public CallFrame(Block block, Point2D origin) {
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
        Rectangle rectangle = new Rectangle(getWidth(), getHeight());
        rectangle.setFill(Color.PURPLE);
        rectangle.setArcHeight(25);
        rectangle.arcWidthProperty().bind(rectangle.arcHeightProperty());
        return rectangle;
    }
    
    @Override
    protected Pane initFrameContent(Block block) {
        valueField = new ValueField();
        return new Pane(valueField);
    }
    
    @Override
    protected ObservableList<Text> initFrameText() {
        return valueField.toText();
    }
}
