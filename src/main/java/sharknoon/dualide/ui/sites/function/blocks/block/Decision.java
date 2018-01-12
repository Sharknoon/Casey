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

import javafx.geometry.Side;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;
import sharknoon.dualide.ui.sites.function.FunctionSite;
import sharknoon.dualide.ui.sites.function.blocks.Block;

/**
 * This is a Decision block, similar like a if clausel
 *
 * @author Josua Frank
 */
public class Decision extends Block {

    public Decision(FunctionSite flowchart) {
        super(flowchart);
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
    public Side[] initDotSides() {
        return new Side[]{Side.TOP, Side.BOTTOM, Side.RIGHT};
    }

    @Override
    public Shape initBlockShape() {
        Polygon polygon = new Polygon(
                getWidth() / 2, 0,//oben mitte
getWidth(), getHeight() / 2,
                getWidth() / 2, getHeight(),
                0, getHeight() / 2);
        polygon.setFill(Color.rgb(0, 0, 0, 0.5));
        return polygon;
    }

}
