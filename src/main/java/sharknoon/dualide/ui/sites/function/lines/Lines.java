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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import sharknoon.dualide.ui.sites.function.FunctionSite;
import sharknoon.dualide.ui.sites.function.dots.Dot;

/**
 *
 * @author Josua Frank
 */
public class Lines {

    public static Line createLine(FunctionSite functionSite, Dot dot) {
          var line = new Line(dot, functionSite);
        if (LINES.containsKey(functionSite)) {
            LINES.get(functionSite).add(line);
        } else {
            List<Line> lines = new ArrayList<>();
            lines.add(line);
            LINES.put(functionSite, lines);
        }
        setLineDrawing(functionSite, line);
        return line;
    }

    private static final Map<FunctionSite, List<Line>> LINES = new HashMap<>();

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

    public static Collection<Line> getAllLines(FunctionSite functionSite) {
        return LINES.getOrDefault(functionSite, List.of());
    }

    public static void unregisterLine(FunctionSite functionSite, Line line) {
        if (LINES.containsKey(functionSite)) {
            LINES.get(functionSite).remove(line);
        }
    }

    public static boolean isDuplicate(FunctionSite functionSite, Line line, Dot endDot) {
          var dot1 = line.getStartDot();
          var dot2 = endDot;
        return getAllLines(functionSite)
                .stream()
                .filter(l -> l != line)
                .anyMatch(l -> {
                      var dot1l = l.getStartDot();
                      var dot2l = l.getEndDot();
                    return (dot1 == dot1l && dot2 == dot2l)
                            || (dot1 == dot2l && dot2 == dot1l);
                });
    }

    public static boolean isConnectionAllowed(Dot dot1, Dot dot2) {
        return dot1.isInputDot() != dot2.isInputDot();
    }

    private static Line mouseOverLine = null;

    public static void setMouseOverLine(Line line) {
        mouseOverLine = line;
    }

    public static boolean isMouseOverLine() {
        return mouseOverLine != null;
    }

    public static void removeMouseOverLine() {
        mouseOverLine = null;
    }

}
