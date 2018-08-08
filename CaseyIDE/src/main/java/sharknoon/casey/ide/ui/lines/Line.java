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

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleExpression;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Point2D;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Shape;
import javafx.util.Duration;
import sharknoon.casey.ide.ui.UISettings;
import sharknoon.casey.ide.ui.dots.Dot;
import sharknoon.casey.ide.ui.frames.Frame;
import sharknoon.casey.ide.ui.frames.Frames;
import sharknoon.casey.ide.ui.interfaces.Moveable;
import sharknoon.casey.ide.ui.misc.MouseConsumable;
import sharknoon.casey.ide.ui.sites.function.FunctionSite;

/**
 * @author Josua Frank
 */
public class Line implements Moveable, MouseConsumable {
    
    private final CubicCurve line;
    private final ObjectProperty<Dot> startDot = new SimpleObjectProperty<>();
    private final FunctionSite functionSite;
    private final Timeline shadowShowTimeline = new Timeline();
    private final Timeline shadowRemoveTimeline = new Timeline();
    private final ObjectProperty<Dot> endDot = new SimpleObjectProperty<>();
    //The contextmenu for a line
    private final LineContextMenu menu = new LineContextMenu(this);
    private boolean selected;
    
    Line(Dot dot, FunctionSite functionSite) {
        ChangeListener<Dot> onDotChange = (ObservableValue<? extends Dot> o, Dot od, Dot nd) -> {
            if (od != null) {
                od.removeLine(this);
            }
            if (nd != null) {
                nd.addLine(this);
            }
        };
        startDot.addListener(onDotChange);
        endDot.addListener(onDotChange);
        this.functionSite = functionSite;
        startDot.set(dot);
        line = initLine();
        MouseConsumable.registerListeners(line, this);
        addDropShadowEffect();
        this.functionSite.getLogicSite().addInBack(line);
    }
    
    private CubicCurve initLine() {
        var line = new CubicCurve();
        line.setStroke(UISettings.LINE_COLOR);
        line.setStrokeWidth(UISettings.LINE_WIDTH);
        line.startXProperty().bind(getStartDot().centerXExpression());
        line.startYProperty().bind(getStartDot().centerYExpression());
        line.setFill(Color.TRANSPARENT);
        switch (getStartDot().getSide()) {
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
        line.setEndX(getStartDot().getCenterX());
        line.setEndY(getStartDot().getCenterY());
        return line;
    }
    
    private void registerNewLine() {
        getOutputDot().getFrame().getBlock().addConnection(
                getOutputDot().getSide(),
                getInputDot().getFrame().getBlock(),
                getInputDot().getSide(),
                false
        );
    }
    
    private void bindLine() {
        line.endXProperty().bind(getEndDot().centerXExpression());
        line.endYProperty().bind(getEndDot().centerYExpression());
        switch (getEndDot().getSide()) {
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
        Dot outputDot = getOutputDot();
        Dot inputDot = getInputDot();
        if (outputDot != null && inputDot != null) {
            outputDot.getFrame().getBlock().removeConnection(
                    outputDot.getSide(),
                    inputDot.getFrame().getBlock()
            );
        }
        functionSite.getLogicSite().remove(line);
        startDot.set(null);
        endDot.set(null);
        Lines.removeLineDrawing(functionSite);
    }
    
    private void addDropShadowEffect() {
        var dropShadow = new DropShadow();
        dropShadow.setSpread(0.9);
        dropShadow.setRadius(0.0);
        dropShadow.setColor(UISettings.LINE_SELECTION_SHADOW_COLOR);
        line.setEffect(dropShadow);
    }
    
    @Override
    public void onMouseDragged(MouseEvent event) {
        menu.hide();
    }
    
    @Override
    public void onMouseClicked(MouseEvent event) {
        if (!Lines.isLineDrawing(functionSite) && event.getButton() == MouseButton.PRIMARY && event.isStillSincePress()) {
            if (!event.isControlDown()) {
                Frames.getAllFrames(functionSite).forEach(Frame::unselect);
                Lines.getAllLines(functionSite).forEach(Line::unselect);
                select();
            } else {
                toggleSelection();
            }
        }
    }
    
    @Override
    public void onContextMenuRequested(ContextMenuEvent event) {
        menu.onContextMenuRequested(event);
    }
    
    public void onMouseMoved(Point2D mousePosition) {
        if (Lines.isLineDrawing(functionSite)) {
            line.endXProperty().unbind();
            line.endYProperty().unbind();
            line.setEndX(mousePosition.getX());
            line.setEndY(mousePosition.getY());
        }
    }
    
    public void select() {
        if (!selected) {
            selected = true;
            var dropShadow = (DropShadow) line.getEffect();
            shadowShowTimeline.getKeyFrames().setAll(
                    new KeyFrame(Duration.ZERO,
                            new KeyValue(dropShadow.radiusProperty(), dropShadow.getRadius()),
                            new KeyValue(dropShadow.colorProperty(), dropShadow.getColor())
                    ),
                    new KeyFrame(UISettings.LINE_SELECTION_SHADOW_DURATION,
                            new KeyValue(dropShadow.radiusProperty(), UISettings.LINE_SELECTION_SHADOW_RADIUS),
                            new KeyValue(dropShadow.colorProperty(), UISettings.LINE_SELECTION_SHADOW_COLOR)
                    ));
            Platform.runLater(() -> {
                shadowRemoveTimeline.stop();
                shadowShowTimeline.stop();
                shadowShowTimeline.play();
            });
        }
    }
    
    public void unselect() {
        if (selected) {
            selected = false;
            var dropShadow = (DropShadow) line.getEffect();
            shadowRemoveTimeline.getKeyFrames().setAll(
                    new KeyFrame(Duration.ZERO,
                            new KeyValue(dropShadow.radiusProperty(), dropShadow.getRadius()),
                            new KeyValue(dropShadow.colorProperty(), dropShadow.getColor())
                    ),
                    new KeyFrame(UISettings.LINE_SELECTION_SHADOW_DURATION,
                            new KeyValue(dropShadow.radiusProperty(), 0),
                            new KeyValue(dropShadow.colorProperty(), UISettings.LINE_SELECTION_SHADOW_COLOR)
                    )
            );
            Platform.runLater(() -> {
                shadowShowTimeline.stop();
                shadowRemoveTimeline.stop();
                shadowRemoveTimeline.play();
            });
        }
    }
    
    public boolean isSelected() {
        return selected;
    }
    
    public void toggleSelection() {
        if (isSelected()) {
            unselect();
        } else {
            select();
        }
    }
    
    public Shape getShape() {
        return line;
    }
    
    public Dot getStartDot() {
        return startDot.get();
    }
    
    public Dot getEndDot() {
        return endDot.get();
    }
    
    public void setEndDot(Dot dot) {
        endDot.set(dot);
        bindLine();
        registerNewLine();
    }
    
    public Dot getOutputDot() {
        if (getStartDot() != null && !getStartDot().isInputDot()) {
            return getStartDot();
        }
        if (getEndDot() != null && !getEndDot().isInputDot()) {
            return getEndDot();
        }
        return null;
    }
    
    public Dot getInputDot() {
        if (getEndDot() != null && getEndDot().isInputDot()) {
            return getEndDot();
        }
        if (getStartDot() != null && getStartDot().isInputDot()) {
            return getStartDot();
        }
        return null;
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
    public void setMinY(double y) {
        line.setTranslateY(y);
    }
    
    @Override
    public boolean canMoveTo(double x, double y) {
        return true;
    }
    
    @Override
    public void setMinX(double x) {
        line.setTranslateX(x);
    }
    
}
