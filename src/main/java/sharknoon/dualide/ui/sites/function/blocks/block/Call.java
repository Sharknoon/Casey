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

import javafx.beans.property.DoubleProperty;
import javafx.geometry.Side;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import sharknoon.dualide.ui.sites.function.FunctionSite;
import sharknoon.dualide.ui.sites.function.blocks.Block;

/**
 * @author Josua Frank
 */
public class Call extends Block {

    public Call(FunctionSite functionSite) {
        super(functionSite);
    }

    public Call(FunctionSite functionSite, String id) {
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
    public Shape initBlockShape() {
        Rectangle rectangle = new Rectangle(getWidth(), getHeight());
        rectangle.setFill(Color.PURPLE);
        rectangle.setArcHeight(25);
        rectangle.arcWidthProperty().bind(rectangle.arcHeightProperty());
        return rectangle;
    }

    @Override
    public Pane initBody() {
        return null;
    }

}
