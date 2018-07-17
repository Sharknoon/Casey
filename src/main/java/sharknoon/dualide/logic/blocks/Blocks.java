package sharknoon.dualide.logic.blocks;/*
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

import javafx.geometry.Point2D;
import sharknoon.dualide.logic.items.Function;
import sharknoon.dualide.utils.settings.Logger;

public class Blocks {
    
    public static Block createBlock(BlockType type, Function function) {
        return createBlock(type, function, null, new Point2D(-1, -1));
    }
    
    public static Block createBlock(BlockType type, Function function, String id, Point2D origin) {
        Block block;
        switch (type) {
            case ASSIGNMENT:
                block = new Assignment(function, id, origin);
                break;
            case START:
                block = new Start(function, id, origin);
                break;
            case END:
                block = new End(function, id, origin);
                break;
            case CALL:
                block = new Call(function, id, origin);
                break;
            case DECISION:
                block = new Decision(function, id, origin);
                break;
            case INPUT:
                block = new Input(function, id, origin);
                break;
            case OUTPUT:
                block = new Output(function, id, origin);
                break;
            default:
                Logger.error("Could not create block of type " + type);
                block = new Call(function, id, origin);
        }
        function.blocksProperty().add(block);
        return block;
    }
    
}
