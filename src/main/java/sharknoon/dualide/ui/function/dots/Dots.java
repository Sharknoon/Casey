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
package sharknoon.dualide.ui.function.dots;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import sharknoon.dualide.ui.function.Function;

/**
 *
 * @author Josua Frank
 */
public class Dots {

    private static final HashMap<Function, Set<Dot>> DOTS = new HashMap<>();
    private static final HashSet<Dot> EMPTY = new HashSet<>();
    private static Dot mouseOverDot = null;

    public static void registerDot(Function flowchart, Dot dot) {
        if (DOTS.containsKey(flowchart)) {
            DOTS.get(flowchart).add(dot);
        } else {
            HashSet<Dot> dots = new HashSet<>();
            dots.add(dot);
            DOTS.put(flowchart, dots);
        }
    }

    public static void setMouseOverDot(Dot dot) {
        mouseOverDot = dot;
    }

    public static boolean isMouseOverDot() {
        return mouseOverDot != null;
    }

    public static void removeMouseOverDot() {
        mouseOverDot = null;
    }

    public static Dot getMouseOverDot() {
        return mouseOverDot;
    }

    public static Optional<Dot> isOverDot(Function flowchart, double x, double y) {
        return DOTS
                .getOrDefault(flowchart, EMPTY)
                .stream()
                .filter(d -> d.getCenterX() == x && d.getCenterY() == y)
                .findFirst();
    }

}