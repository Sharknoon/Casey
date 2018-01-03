package sharknoon.dualide.ui.flowchart.lines;

import javafx.scene.shape.Polyline;
import sharknoon.dualide.ui.flowchart.Flowchart;
import sharknoon.dualide.ui.flowchart.dots.Dot;

/**
 *
 * @author Josua Frank
 */
public class Line {

    private Polyline line;
    private double startX;
    private double startY;
    private final Dot startDot;
    private Dot endDot;
    private Flowchart flowchart;

    public Line(Dot startDot, Flowchart flowchart) {
        this.startDot = startDot;
        this.startX = this.startDot.getCenterX();
        this.startY = this.startDot.getCenterX();
        this.flowchart = flowchart;
        line = new Polyline(startX, startY, startX, startY + 1);
        this.flowchart.add(line);

    }

    public void setEndDot(Dot dot) {
        endDot = dot;
        if (startDot.getBlock() == endDot.getBlock()) {
            destroy();
            return;
        }
        line.getPoints().addAll(endDot.getCenterX(), endDot.getCenterY());
    }

    public void destroy() {
        flowchart.remove(line);
    }

}
