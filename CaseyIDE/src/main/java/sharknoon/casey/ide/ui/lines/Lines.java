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
package sharknoon.casey.ide.ui.lines;

import sharknoon.casey.ide.ui.dots.Dot;
import sharknoon.casey.ide.ui.frames.Frame;
import sharknoon.casey.ide.ui.frames.Frames;
import sharknoon.casey.ide.ui.sites.function.FunctionSite;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

/**
 * @author Josua Frank
 */
public class Lines {
    
    private static final Map<FunctionSite, Line> CURRENT_DRAWING_LINE = new HashMap<>();
    
    public static Line createLine(FunctionSite functionSite, Dot startDot) {
        return new Line(startDot, functionSite);
    }
    
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
        return Frames
                .getAllFrames(functionSite)
                .map(Frame::getOutputDots)
                .flatMap(s -> s)
                .map(Dot::getLines)
                .flatMap(Set::stream);
    }
    
    //Duplicates are no longer possible, it isnt possible to connect two line to a output dot
//    public static boolean isDuplicate(FunctionSite functionSite, Line line, Dot endDot) {
//        var dot1 = line.getOutputDot();
//        var dot2 = endDot;
//        return getAllLines(functionSite)
//                .filter(l -> l != line)
//                .anyMatch(l -> {
//                    var dot1l = l.getOutputDot();
//                    var dot2l = l.getInputDot();
//                    return (dot1 == dot1l && dot2 == dot2l)
//                            || (dot1 == dot2l && dot2 == dot1l);
//                });
//    }
    
    public static boolean isConnectionAllowed(Dot dot1, Dot dot2) {
        return dot1.isInputDot() != dot2.isInputDot();
    }
    
}
