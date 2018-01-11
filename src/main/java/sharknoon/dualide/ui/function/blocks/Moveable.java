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
package sharknoon.dualide.ui.function.blocks;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.shape.Shape;
import sharknoon.dualide.ui.function.lines.Lines;

/**
 * This is a interface for all things, that can be moved around and need to
 * check the collision with other moveables
 *
 * @author Josua Frank
 */
public interface Moveable {

    public double getMinX();

    public double getMinY();

    public default double getWidth() {
        return getMaxX() - getMinX();
    }

    public default double getHeight() {
        return getMaxY() - getMinY();
    }

    public double getMaxX();

    public double getMaxY();

    public void setMinX(double x);

    public void setMinY(double y);

    public boolean canMoveTo(double x, double y);

    public default Bounds getBounds() {
        return new BoundingBox(getMinX(), getMinY(), getWidth(), getHeight());
    }
    
    public double[] getPoints();

}
