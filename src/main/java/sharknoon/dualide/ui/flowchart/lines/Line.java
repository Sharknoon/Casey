package sharknoon.dualide.ui.flowchart.lines;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.shape.Polyline;
import sharknoon.dualide.ui.flowchart.Flowchart;
import sharknoon.dualide.ui.flowchart.UISettings;
import sharknoon.dualide.ui.flowchart.dots.Dot;
import javafx.geometry.Point2D;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Shape;
import javafx.util.Duration;
import sharknoon.dualide.ui.flowchart.blocks.Blocks;
import sharknoon.dualide.ui.flowchart.dots.Dots;

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
    private boolean selected;
    private final Timeline shadowShowTimeline = new Timeline();
    private final Timeline shadowRemoveTimeline = new Timeline();

    public Line(Dot startDot, Flowchart flowchart) {
        this.startDot = startDot;
        this.flowchart = flowchart;
        this.startX = this.startDot.getCenterX();
        this.startY = this.startDot.getCenterY();
        line = new Polyline();
        line.setStroke(UISettings.lineColor);
        line.setStrokeWidth(UISettings.lineWidth);
        addDropShadowEffect();
        addPoint(startX, startY);
        addCorner();
        line.setOnMouseClicked(this::onMouseClicked);
        this.flowchart.add(line);
    }

    public void setEndDot(Dot dot) {
        endDot = dot;
        if (startDot.getBlock() == endDot.getBlock()) {
            remove();
        }
    }

    public void remove() {
        flowchart.remove(line);
        startDot.removeLine();
        if (endDot != null) {
            endDot.removeLine();
        }
        Lines.removeLineDrawing();
        Lines.unregisterLine(flowchart, this);
    }

    public double getLastCornerX() {
        return lastCorner.getX();
    }

    public double getLastCornerY() {
        return lastCorner.getY();
    }

    public void addPoint(double x, double y) {
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
        boolean noBlock = Blocks
                .getAllBlocks(flowchart)
                .stream()
                .noneMatch(block -> block.getBounds().contains(x, y));

        if (!noBlock) {
            if (Dots.isOverDot(x, y).isPresent()) {
                noBlock = true;
            }
        }

        boolean insideWorkspace = !(x < 0 + UISettings.paddingInsideWorkSpace
                || x > UISettings.maxWorkSpaceX - UISettings.paddingInsideWorkSpace
                || y < 0 + UISettings.paddingInsideWorkSpace
                || y > UISettings.maxWorkSpaceY - UISettings.paddingInsideWorkSpace);

        boolean notMyLine = points
                .stream()
                .noneMatch(p -> p.getX() == x && p.getY() == y);
        return noBlock && insideWorkspace && notMyLine;
    }

    public void addCorner() {
        lastCorner = getLastPoint();
    }

    private void addDropShadowEffect() {
        DropShadow dropShadow = new DropShadow();
        dropShadow.setSpread(0.9);
        dropShadow.setRadius(0.0);
        dropShadow.setColor(UISettings.lineSelectionShadowColor);
        line.setEffect(dropShadow);
    }

    public void onMouseClicked(MouseEvent event) {
        if (Lines.isLineDrawing()) {

        } else {
            if (selected) {
                unselect();
            }else{
                select();
            }
        }
    }

    public void select() {
        if (!selected) {
            selected = true;
            shadowShowTimeline.getKeyFrames().clear();
            DropShadow dropShadow = (DropShadow) line.getEffect();
            shadowShowTimeline.getKeyFrames().addAll(
                    new KeyFrame(Duration.ZERO,
                            new KeyValue(dropShadow.radiusProperty(), dropShadow.getRadius()),
                            new KeyValue(dropShadow.colorProperty(), dropShadow.getColor())
                    ),
                    new KeyFrame(UISettings.lineSelectionShadowDuration,
                            new KeyValue(dropShadow.radiusProperty(), UISettings.lineSelectionShadowRadius),
                            new KeyValue(dropShadow.colorProperty(), UISettings.lineSelectionShadowColor)
                    ));
            shadowRemoveTimeline.stop();
            shadowShowTimeline.stop();
            shadowShowTimeline.play();
        }
    }

    public void unselect() {
        if (selected) {
            selected = false;
            DropShadow dropShadow = (DropShadow) line.getEffect();
            shadowRemoveTimeline.getKeyFrames().clear();
            shadowRemoveTimeline.getKeyFrames().addAll(
                    new KeyFrame(Duration.ZERO,
                            new KeyValue(dropShadow.radiusProperty(), dropShadow.getRadius()),
                            new KeyValue(dropShadow.colorProperty(), dropShadow.getColor())
                    ),
                    new KeyFrame(UISettings.lineSelectionShadowDuration,
                            new KeyValue(dropShadow.radiusProperty(), 0),
                            new KeyValue(dropShadow.colorProperty(), UISettings.lineSelectionShadowColor)
                    )
            );
            shadowShowTimeline.stop();
            shadowRemoveTimeline.stop();
            shadowRemoveTimeline.play();
        }
    }

    public boolean isSelected() {
        return selected;
    }

}
