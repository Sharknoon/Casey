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
        line = new Polyline(startX, startY);
        lastCorner = new Point2D(startX, startY);
        points.add(lastCorner);
        line.setStroke(UISettings.lineColor);
        line.setStrokeWidth(UISettings.lineWidth);
        this.flowchart.add(line);

    }

    public void setEndDot(Dot dot) {
        endDot = dot;
        if (startDot.getBlock() == endDot.getBlock()) {
            destroy();
            return;
        }
        createNewCorner(dot.getCenterX(), dot.getCenterY());
    }

    public void destroy() {
        flowchart.remove(line);
    }

    public double getLastCornerX() {
        return lastCorner.getX();
    }

    public double getLastCornerY() {
        return lastCorner.getY();
    }

    public void addPoint(double x, double y) {
        line.getPoints().addAll(x, y);
        points.add(new Point2D(x, y));
    }

    /**
     * can also be a corner
     *
     * @return
     */
    public Point2D getLastPoint() {
        return points.get(points.size() - 1);
    }

    public void removeLastPoint() {
        int size = line.getPoints().size();
        line.getPoints().remove(size - 2, size);
        points.remove(points.size() - 1);
    }

    public Point2D getLastCorner() {
        return lastCorner;
    }

    public boolean canExtendTo(double newValue, boolean vertical) {
        //TODO check between complete line
        Bounds newLine = new BoundingBox(getLastCornerX(), getLastCornerY(), vertical ? 1 : newValue, vertical ? newValue : 1);
        boolean noBlock;
        if (vertical) {
//            Blocks
//                    .getAllBlocks(flowchart)
//                    .stream()
//                    .map(b -> b.getBounds())
//                    .noneMatch(b -> b.contains(vertical));
        }
        noBlock = Blocks
                .getAllBlocks(flowchart)
                .stream()
                .noneMatch(block -> block.getBounds().contains(newLine.getMaxX(), newLine.getMaxY()));

        noBlock = Blocks
                .getAllBlocks(flowchart)
                .stream()
                .filter(b -> b != startDot.getBlock())
                .noneMatch(block -> block.getBounds().intersects(newLine));

        boolean insideWorkspace;
        if (vertical) {
            insideWorkspace = !(newValue < 0 + UISettings.paddingInsideWorkSpace
                    || newValue > UISettings.maxWorkSpaceY - UISettings.paddingInsideWorkSpace);
        } else {
            insideWorkspace = !(newValue < 0 + UISettings.paddingInsideWorkSpace
                    || newValue > UISettings.maxWorkSpaceX - UISettings.paddingInsideWorkSpace);
        }

//        boolean notMyLine = points
//                .stream()
//                .noneMatch(p -> p.getX() == x && p.getY() == y);
        return /*noBlock &&*/ insideWorkspace;
    }

    public boolean vertical = true;
    public double lastValue = 0;

    public void extend(double newValue, boolean vertical) {
        if (this.vertical == vertical && newValue == lastValue) {
            return;
        }
        if (this.vertical != vertical) {
            for (int i = points.size() - 1; i >= 0; i--) {
                Point2D point = points.get(i);
                if (point != lastCorner) {
                    removeLastPoint();
                } else {
                    break;
                }
            }
            this.vertical = vertical;
        }
        if (Math.abs(newValue) < Math.abs(lastValue)) {
            removeLastPoint();
        } else {
            double x = vertical ? getLastCornerX() : newValue;
            double y = vertical ? newValue : getLastCornerY();
            addPoint(x, y);
        }
        System.out.println("lastvalue: " + lastValue + ", newvalue: " + newValue);
        lastValue = newValue;
    }

    public void createNewCorner(double x, double y) {
        line.getPoints().addAll(x, y);
        Point2D corner = new Point2D(x, y);
        lastCorner = corner;
        points.add(corner);
    }

}
