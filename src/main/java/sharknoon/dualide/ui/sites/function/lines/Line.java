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

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleExpression;
import sharknoon.dualide.ui.sites.function.FunctionSite;
import sharknoon.dualide.ui.sites.function.UISettings;
import sharknoon.dualide.ui.sites.function.dots.Dot;
import javafx.geometry.Point2D;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Shape;
import javafx.util.Duration;
import sharknoon.dualide.ui.sites.function.blocks.Block;
import sharknoon.dualide.ui.sites.function.blocks.Blocks;
import sharknoon.dualide.ui.sites.function.blocks.MouseConsumable;
import sharknoon.dualide.ui.sites.function.blocks.Moveable;

/**
 *
 * @author Josua Frank
 */
public class Line implements Moveable, MouseConsumable {

    private final CubicCurve line;
    private final Dot startDot;
    private final FunctionSite functionSite;
    private final Timeline shadowShowTimeline = new Timeline();
    private final Timeline shadowRemoveTimeline = new Timeline();
    private Dot endDot;
    private boolean selected;
    //The contextmenu for a line
    private final LineContextMenu menu = new LineContextMenu(this);

    public Line(Dot startDot, FunctionSite functionSite) {
        this.startDot = startDot;
        this.functionSite = functionSite;
        line = initLine();
        registerListeners(line, this);
        addDropShadowEffect();
        this.functionSite.add(line);
    }

    private CubicCurve initLine() {
          var line = new CubicCurve();
        line.setStroke(UISettings.LINE_COLOR);
        line.setStrokeWidth(UISettings.LINE_WIDTH);
        line.startXProperty().bind(startDot.centerXExpression());
        line.startYProperty().bind(startDot.centerYExpression());
        line.setFill(Color.TRANSPARENT);
        switch (startDot.getSide()) {
            case BOTTOM:
                line.controlX1Property().bind(line.startXProperty());
                line.controlY1Property().bind(line.startYProperty().add(UISettings.LINE_CONTROL_OFFSET));
                break;
            case LEFT:
                line.controlX1Property().bind(line.startXProperty().subtract(UISettings.LINE_CONTROL_OFFSET));
                line.controlY1Property().bind(line.startYProperty());
                break;
            case RIGHT:
                line.controlX1Property().bind(line.startXProperty().add(UISettings.LINE_CONTROL_OFFSET));
                line.controlY1Property().bind(line.startYProperty());
                break;
            case TOP:
                line.controlX1Property().bind(line.startXProperty());
                line.controlY1Property().bind(line.startYProperty().subtract(UISettings.LINE_CONTROL_OFFSET));
                break;
        }
        line.controlX2Property().bind(line.endXProperty());
        line.controlY2Property().bind(line.endYProperty());
        line.setEndX(startDot.getCenterX());
        line.setEndY(startDot.getCenterY());
        return line;
    }

    public void setEndDot(Dot dot) {
        endDot = dot;
        line.endXProperty().bind(endDot.centerXExpression());
        line.endYProperty().bind(endDot.centerYExpression());
        switch (endDot.getSide()) {
            case BOTTOM:
                line.controlX2Property().bind(line.endXProperty());
                line.controlY2Property().bind(line.endYProperty().add(UISettings.LINE_CONTROL_OFFSET));
                break;
            case LEFT:
                line.controlX2Property().bind(line.endXProperty().subtract(UISettings.LINE_CONTROL_OFFSET));
                line.controlY2Property().bind(line.endYProperty());
                break;
            case RIGHT:
                line.controlX2Property().bind(line.endXProperty().add(UISettings.LINE_CONTROL_OFFSET));
                line.controlY2Property().bind(line.endYProperty());
                break;
            case TOP:
                line.controlX2Property().bind(line.endXProperty());
                line.controlY2Property().bind(line.endYProperty().subtract(UISettings.LINE_CONTROL_OFFSET));
                break;
        }
    }

    public void remove() {
        functionSite.remove(line);
        startDot.removeLine(this);
        if (endDot != null) {
            endDot.removeLine(this);
        }
        Lines.removeLineDrawing(functionSite);
        Lines.unregisterLine(functionSite, this);
    }

    private void addDropShadowEffect() {
          var dropShadow = new DropShadow();
        dropShadow.setSpread(0.9);
        dropShadow.setRadius(0.0);
        dropShadow.setColor(UISettings.LINE_SELECTION_SHADOW_COLOR);
        line.setEffect(dropShadow);
    }

    @Override
    public void onMousePressed(MouseEvent event) {
        menu.hide();
        Lines.setMouseOverLine(this);
    }

    @Override
    public void onMouseClicked(MouseEvent event) {
        if (!Lines.isLineDrawing(functionSite) && event.getButton() == MouseButton.PRIMARY) {
            if (!event.isControlDown()) {
                Blocks.getAllBlocks(functionSite).forEach(Block::unselect);
                Lines.getAllLines(functionSite).forEach(Line::unselect);
            }
            select();
        }
    }

    public void onMouseMoved(Point2D mousePosition) {
        if (Lines.isLineDrawing(functionSite)) {
            line.setEndX(mousePosition.getX());
            line.setEndY(mousePosition.getY());
        }
    }

    @Override
    public void onMouseDragged(MouseEvent event) {
    }

    @Override
    public void onMouseReleased(MouseEvent event) {
    }

    @Override
    public void onMouseEntered(MouseEvent event) {
        Lines.setMouseOverLine(this);
    }

    @Override
    public void onMouseExited(MouseEvent event) {
        Lines.removeMouseOverLine();
    }

    private static void registerListeners(Shape shape, MouseConsumable consumable) {
        shape.setOnMousePressed(consumable::onMousePressed);
        shape.setOnMouseReleased(consumable::onMouseReleased);
        shape.setOnMouseClicked(consumable::onMouseClicked);
        shape.setOnContextMenuRequested(consumable::onContextMenuRequested);
        shape.setOnMouseEntered(consumable::onMouseEntered);
        shape.setOnMouseExited(consumable::onMouseExited);
    }

    @Override
    public void onContextMenuRequested(ContextMenuEvent event) {
        menu.onContextMenuRequested(event);
    }

    public void select() {
        if (!selected) {
            selected = true;
            shadowShowTimeline.getKeyFrames().clear();
              var dropShadow = (DropShadow) line.getEffect();
            shadowShowTimeline.getKeyFrames().addAll(
                    new KeyFrame(Duration.ZERO,
                            new KeyValue(dropShadow.radiusProperty(), dropShadow.getRadius()),
                            new KeyValue(dropShadow.colorProperty(), dropShadow.getColor())
                    ),
                    new KeyFrame(UISettings.LINE_SELECTION_SHADOW_DURATION,
                            new KeyValue(dropShadow.radiusProperty(), UISettings.LINE_SELECTION_SHADOW_RADIUS),
                            new KeyValue(dropShadow.colorProperty(), UISettings.LINE_SELECTION_SHADOW_COLOR)
                    ));
            shadowRemoveTimeline.stop();
            shadowShowTimeline.stop();
            shadowShowTimeline.play();
        }
    }

    public void unselect() {
        if (selected) {
            selected = false;
              var dropShadow = (DropShadow) line.getEffect();
            shadowRemoveTimeline.getKeyFrames().clear();
            shadowRemoveTimeline.getKeyFrames().addAll(
                    new KeyFrame(Duration.ZERO,
                            new KeyValue(dropShadow.radiusProperty(), dropShadow.getRadius()),
                            new KeyValue(dropShadow.colorProperty(), dropShadow.getColor())
                    ),
                    new KeyFrame(UISettings.LINE_SELECTION_SHADOW_DURATION,
                            new KeyValue(dropShadow.radiusProperty(), 0),
                            new KeyValue(dropShadow.colorProperty(), UISettings.LINE_SELECTION_SHADOW_COLOR)
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

    public Shape getShape() {
        return line;
    }

    public Dot getStartDot() {
        return startDot;
    }

    public Dot getEndDot() {
        return endDot;
    }

    @Override
    public double getMinX() {
        return line.getBoundsInParent().getMinX();
    }

    @Override
    public DoubleExpression minXExpression() {
        return Bindings.createDoubleBinding(() -> getMinX(), line.boundsInParentProperty());
    }

    @Override
    public double getMinY() {
        return line.getBoundsInParent().getMinY();
    }

    @Override
    public DoubleExpression minYExpression() {
        return Bindings.createDoubleBinding(() -> getMinY(), line.boundsInParentProperty());
    }

    @Override
    public double getMaxX() {
        return line.getBoundsInParent().getMaxX();
    }

    @Override
    public DoubleExpression maxXExpression() {
        return Bindings.createDoubleBinding(() -> getMaxX(), line.boundsInParentProperty());
    }

    @Override
    public double getMaxY() {
        return line.getBoundsInParent().getMaxY();
    }

    @Override
    public DoubleExpression maxYExpression() {
        return Bindings.createDoubleBinding(() -> getMaxY(), line.boundsInParentProperty());
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
        return true;
    }

}
