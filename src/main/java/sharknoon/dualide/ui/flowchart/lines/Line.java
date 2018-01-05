package sharknoon.dualide.ui.flowchart.lines;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.collections.ObservableList;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.shape.Polyline;
import sharknoon.dualide.ui.flowchart.Flowchart;
import sharknoon.dualide.ui.flowchart.UISettings;
import sharknoon.dualide.ui.flowchart.dots.Dot;
import javafx.geometry.Point2D;
import sharknoon.dualide.ui.flowchart.blocks.Blocks;

/**
 *
 * @author Josua Frank
 */
public class Line {

    private final Polyline line;
    private final double startX;
    private final double startY;
    private final Dot startDot;
    private final Flowchart flowchart;
    private final List<Point2D> points = new ArrayList<>();
    private Point2D lastCorner;
    private Dot endDot;

    public Line(Dot startDot, Flowchart flowchart) {
        this.startDot = startDot;
        this.flowchart = flowchart;
        this.startX = this.startDot.getCenterX();
        this.startY = this.startDot.getCenterY();
        line = new Polyline();
        line.setStroke(UISettings.lineColor);
        line.setStrokeWidth(UISettings.lineWidth);
        addPoint(startX, startY);
        addCorner();
        this.flowchart.add(line);

    }

    public void setEndDot(Dot dot) {
        endDot = dot;
        if (startDot.getBlock() == endDot.getBlock()) {
            destroy();
            return;
        }
        //createNewCorner(dot.getCenterX(), dot.getCenterY());
    }

    public void destroy() {
        flowchart.remove(line);
        Lines.removeLineDrawing();
    }

    public double getLastCornerX() {
        return lastCorner.getX();
    }

    public double getLastCornerY() {
        return lastCorner.getY();
    }

    private void addPoint(double x, double y) {
        line.getPoints().addAll(x, y);
        Point2D point = new Point2D(x, y);
        points.add(point);
    }

    /**
     * can also be a corner
     *
     * @return
     */
    public Point2D getLastPoint() {
        return points.get(points.size() - 1);
    }

    public void removePointsSinceLastCorner() {
        int lastPoints = getPointsSinceLastCorner().size();
        int pointsSize = points.size();
        for (int i = 0; i < lastPoints; i++) {
            points.remove(pointsSize - 1 - i);
        }
        line.getPoints().remove((pointsSize * 2) - (lastPoints * 2), pointsSize * 2);
    }

    public Point2D getLastCorner() {
        return lastCorner;
    }

    public List<Point2D> getPointsSinceLastCorner() {
        List<Point2D> pointsSinceLastCorner = new ArrayList<>();
        for (int i = points.size() - 1; i >= 0; i--) {
            if (!lastCorner.equals(points.get(i))) {
                pointsSinceLastCorner.add(points.get(i));
            } else {
                return pointsSinceLastCorner;
            }
        }
        return pointsSinceLastCorner;
    }

    public boolean canExtendTo(double x, double y) {
        //TODO check between complete line

        boolean noBlock = Blocks
                .getAllBlocks(flowchart)
                .stream()
                .noneMatch(block -> block.getBounds().contains(x, y));

        boolean insideWorkspace = !(x < 0 + UISettings.paddingInsideWorkSpace
                || x > UISettings.maxWorkSpaceX - UISettings.paddingInsideWorkSpace
                || y < 0 + UISettings.paddingInsideWorkSpace
                || y > UISettings.maxWorkSpaceY - UISettings.paddingInsideWorkSpace);

//        boolean notMyLine = points
//                .stream()
//                .noneMatch(p -> p.getX() == x && p.getY() == y);
        return noBlock && insideWorkspace;
    }

    public void extend(double x, double y) {
        addPoint(x, y);
    }

    public void addCorner() {
        lastCorner = getLastPoint();
    }

}
