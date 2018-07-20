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
package sharknoon.casey.ide.ui.interfaces;

import javafx.beans.binding.DoubleExpression;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;

/**
 * This is a interface for all things, that can be moved around and need to
 * check the collision with other moveables
 *
 * @author Josua Frank
 */
public interface Moveable {
    
    double getMinX();
    
    void setMinX(double x);
    
    DoubleExpression minXExpression();
    
    double getMinY();
    
    void setMinY(double y);
    
    DoubleExpression minYExpression();
    
    default double getWidth() {
        return getMaxX() - getMinX();
    }
    
    default DoubleExpression widthExpression() {
        return maxXExpression().subtract(minXExpression());
    }
    
    default double getHeight() {
        return getMaxY() - getMinY();
    }
    
    default DoubleExpression heightExpression() {
        return maxYExpression().subtract(minYExpression());
    }
    
    double getMaxX();
    
    DoubleExpression maxXExpression();
    
    double getMaxY();
    
    DoubleExpression maxYExpression();
    
    boolean canMoveTo(double x, double y);
    
    default Bounds getBounds() {
        return new BoundingBox(getMinX(), getMinY(), getWidth(), getHeight());
    }

}
