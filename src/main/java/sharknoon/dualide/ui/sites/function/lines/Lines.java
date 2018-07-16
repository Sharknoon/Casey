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
package sharknoon.dualide.ui.sites.function.lines;

import sharknoon.dualide.ui.sites.function.FunctionSite;
import sharknoon.dualide.ui.sites.function.blocks.Block;
import sharknoon.dualide.ui.sites.function.blocks.Blocks;
import sharknoon.dualide.ui.sites.function.dots.Dot;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

/**
 *
 * @author Josua Frank
 */
public class Lines {

    public static Line createLine(FunctionSite functionSite, Dot dot) {
        return new Line(dot, functionSite);
    }

    private static final Map<FunctionSite, Line> CURRENT_DRAWING_LINE = new HashMap<>();

    public static void setLineDrawing(FunctionSite functionSite, Line line) {
        CURRENT_DRAWING_LINE.put(functionSite, line);
    }

    public static boolean isLineDrawing(FunctionSite functionSite) {
        return CURRENT_DRAWING_LINE.containsKey(functionSite);
    }

    public static void removeLineDrawing(FunctionSite functionSite) {
        CURRENT_DRAWING_LINE.remove(functionSite);
    }

    public static Line getDrawingLine(FunctionSite functionSite) {
        return CURRENT_DRAWING_LINE.get(functionSite);
    }
    
    public static void unselectAll(FunctionSite functionSite) {
        getAllLines(functionSite).forEach(Line::unselect);
    }

    public static Stream<Line> getAllLines(FunctionSite functionSite) {
        return Blocks
                .getAllBlocks(functionSite)
                .map(Block::getOutputDots)
                .flatMap(s -> s)
                .map(Dot::getLines)
                .flatMap(Set::stream);
    }

    public static boolean isDuplicate(FunctionSite functionSite, Line line, Dot endDot) {
          var dot1 = line.getOutputDot();
          var dot2 = endDot;
        return getAllLines(functionSite)
                .filter(l -> l != line)
                .anyMatch(l -> {
                      var dot1l = l.getOutputDot();
                      var dot2l = l.getInputDot();
                    return (dot1 == dot1l && dot2 == dot2l)
                            || (dot1 == dot2l && dot2 == dot1l);
                });
    }

    public static boolean isConnectionAllowed(Dot dot1, Dot dot2) {
        return dot1.isInputDot() != dot2.isInputDot();
    }

    private static final Map<FunctionSite, Line> MOUSE_OVER_LINE = new HashMap<>();

    public static void setMouseOverLine(FunctionSite fs, Line line) {
        MOUSE_OVER_LINE.put(fs, line);
    }

    public static boolean isMouseOverLine(FunctionSite fs) {
        return MOUSE_OVER_LINE.containsKey(fs);
    }

    public static void removeMouseOverLine(FunctionSite fs) {
        MOUSE_OVER_LINE.remove(fs);
    }

}
