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
        Line line = new Line(dot, functionSite);
        if (LINES.containsKey(functionSite)) {
            LINES.get(functionSite).add(line);
        } else {
            List<Line> lines = new ArrayList<>();
            lines.add(line);
            LINES.put(functionSite, lines);
        }
        setLineDrawing(line);
        return line;
    }

    private static final Map<FunctionSite, List<Line>> LINES = new HashMap<>();
    private static final List<Line> EMPTY = new ArrayList<>();

    private static Line lineDrawing = null;

    public static void setLineDrawing(Line line) {
        lineDrawing = line;
    }

    public static boolean isLineDrawing() {
        return lineDrawing != null;
    }

    public static void removeLineDrawing() {
        lineDrawing = null;
    }

    public static Line getDrawingLine() {
        return lineDrawing;
    }

    public static Collection<Line> getAllLines(FunctionSite functionSite) {
        return LINES.getOrDefault(functionSite, EMPTY);
    }

    public static void unregisterLine(FunctionSite functionSite, Line line) {
        if (LINES.containsKey(functionSite)) {
            LINES.get(functionSite).remove(line);
        }
    }

}
