package sharknoon.dualide.ui.flowchart.lines;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import sharknoon.dualide.ui.flowchart.Flowchart;
import sharknoon.dualide.ui.flowchart.dots.Dot;

/**
 *
 * @author Josua Frank
 */
public class Lines {

    public static Line createLine(Flowchart flowchart, Dot dot) {
        Line line = new Line(dot, flowchart);
        if (LINES.containsKey(flowchart)) {
            LINES.get(flowchart).add(line);
        } else {
            List<Line> lines = new ArrayList<>();
            lines.add(line);
            LINES.put(flowchart, lines);
        }
        return line;
    }

    private static final Map<Flowchart, List<Line>> LINES = new HashMap<>();
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

    public static Collection<Line> getAllLines(Flowchart flowchart) {
        return LINES.getOrDefault(flowchart, EMPTY);
    }

    public static void unregisterLine(Flowchart flowchart, Line line) {
        if (LINES.containsKey(flowchart)) {
            LINES.get(flowchart).remove(line);
        }
    }

}
