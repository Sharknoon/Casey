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
package sharknoon.dualide.ui.function.lines;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.shape.Polyline;
import sharknoon.dualide.ui.function.Function;
import sharknoon.dualide.ui.function.UISettings;
import sharknoon.dualide.ui.function.dots.Dot;
import javafx.geometry.Point2D;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Path;
import javafx.scene.shape.Shape;
import javafx.util.Duration;
import sharknoon.dualide.ui.function.blocks.Block;
import sharknoon.dualide.ui.function.blocks.Blocks;
import sharknoon.dualide.ui.function.blocks.Moveable;
import sharknoon.dualide.ui.function.dots.Dots;

/**
 *
 * @author Josua Frank
 */
public class Line implements Moveable {

    private final Polyline line;
    private final Dot startDot;
    private final Function flowchart;
    private final List<Point2D> points = new ArrayList<>();
    private final Timeline shadowShowTimeline = new Timeline();
    private final Timeline shadowRemoveTimeline = new Timeline();
    private Point2D lastCorner;
    private Dot endDot;
    private boolean selected;
    private Bounds lineBounds = new BoundingBox(0, 0, 0, 0);

    public Line(Dot startDot, Function flowchart) {
        this.startDot = startDot;
        this.flowchart = flowchart;
        line = initLine();
        addDropShadowEffect();
        addPoint(startDot.getCenterX(), startDot.getCenterY());
        addCorner();
        line.setOnMouseClicked(this::onMouseClicked);
        this.flowchart.add(line);
    }

    private static Polyline initLine() {
        Polyline line = new Polyline();
        line.setStroke(UISettings.lineColor);
        line.setStrokeWidth(UISettings.lineWidth);
        return line;
    }

    public void setEndDot(Dot dot) {
        endDot = dot;
        lineBounds = line.getBoundsInParent();
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

    private Dot overDot;

    public boolean canExtendTo(double x, double y) {
        boolean noBlock = Blocks
                .getAllBlocks(flowchart)
                .stream()
                .noneMatch(block -> block.getBounds().contains(x, y));

        if (!noBlock) {
            Optional<Dot> dot = Dots.isOverDot(flowchart, x, y);
            if (dot.isPresent() && !dot.get().hasLine()) {
                noBlock = true;
                overDot = dot.get();
                overDot.getBlock().showDots();
            }
        } else {
            if (overDot != null) {
                overDot.getBlock().hideDots();
            }
            overDot = null;
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
        if (!Lines.isLineDrawing()) {
            if (selected) {
                unselect();
            } else {
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

    public boolean isOverDot() {
        return overDot != null;
    }

    public Dot getOverDot() {
        return overDot;
    }

    @Override
    public double getMinX() {
        return lineBounds.getMinX();
    }

    @Override
    public double getMinY() {
        return lineBounds.getMinY();
    }

    @Override
    public double getWidth() {
        return lineBounds.getWidth();
    }

    @Override
    public double getHeight() {
        return lineBounds.getHeight();
    }

    @Override
    public double getMaxX() {
        return lineBounds.getMaxX();
    }

    @Override
    public double getMaxY() {
        return lineBounds.getMaxY();
    }

    @Override
    public void setMinX(double x) {
        line.setTranslateX(x);
    }

    @Override
    public void setMinY(double y) {
        line.setTranslateY(y);
    }

    @Override
    public boolean canMoveTo(double x, double y) {
        return canMoveTo(x, y, true);
    }

    public Shape getShape() {
        return line;
    }

    /**
     * Checks, if this block can move to the desired destination
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @param ignoreSelection true = ignores selection (default), false = has to
     * be unselected, because selected ones are dragged all together
     * @return
     */
    public boolean canMoveTo(double x, double y, boolean ignoreSelection) {
        Stream<Line> lines = Lines.getAllLines(flowchart).stream();
        Stream<Block> blocks = Blocks.getAllBlocks(flowchart).stream();
        Stream<Moveable> moveables = Stream.concat(lines, blocks);
        return false;
    }

    @Override
    public double[] getPoints() {
        return null;
    }

}
