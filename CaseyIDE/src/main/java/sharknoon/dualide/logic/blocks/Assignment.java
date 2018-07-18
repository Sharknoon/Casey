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
package sharknoon.dualide.logic.blocks;

import javafx.geometry.Point2D;
import javafx.geometry.Side;
import sharknoon.dualide.logic.items.Function;

/**
 * Creates a Assignment block, which executes commands
 *
 * @author Josua Frank
 */
public class Assignment extends Block {
    
    public Assignment(Function function) {
        super(function, BlockType.ASSIGNMENT);
    }
    
    public Assignment(Function function, String id, Point2D origin) {
        super(function, BlockType.ASSIGNMENT, id, origin);
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
    public boolean hasStatement() {
        return true;
    }
    
    @Override
    public boolean hasVariable() {
        return true;
    }
}
