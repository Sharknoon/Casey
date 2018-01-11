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
package sharknoon.dualide.ui.function;

import java.util.HashMap;
import java.util.Map;
import sharknoon.dualide.ui.function.blocks.Moveable;

/**
 *
 * @author Josua Frank
 */
public class Collision {

    private static final Map<Function, Map<Moveable, double[]>> OCCUPIED_POINTS = new HashMap<>();

    public static void update(Function flowchart, Moveable moveable) {
        if (OCCUPIED_POINTS.containsKey(flowchart)) {
            OCCUPIED_POINTS.get(flowchart).put(moveable, moveable.getPoints());
        } else {
            HashMap<Moveable, double[]> map = new HashMap<>();
            map.put(moveable, moveable.getPoints());
            OCCUPIED_POINTS.put(flowchart, map);
        }
    }
    
    public static boolean isSpaceFree(Function flowchart, double x, double y){
        return false;
    }
}
