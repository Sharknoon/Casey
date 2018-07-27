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
package sharknoon.casey.ide.ui.dots;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.DoubleExpression;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.geometry.Side;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;
import javafx.util.Duration;
import sharknoon.casey.ide.ui.UISettings;
import sharknoon.casey.ide.ui.frames.Frame;
import sharknoon.casey.ide.ui.lines.Line;
import sharknoon.casey.ide.ui.lines.Lines;
import sharknoon.casey.ide.ui.sites.function.FunctionSite;

import java.util.Set;

/**
 * @author Josua Frank
 */
public class Dot {
    
    private final Polygon polygon;
    private final Frame<?> frame;
    private final ObservableSet<Line> lines = FXCollections.observableSet();
    private final Side side;
    private final boolean isInput;
    private final FunctionSite functionSite;
    private final BooleanBinding showingBinding;
    private final Timeline showTimeline = new Timeline();
    private final Timeline hideTimeline = new Timeline();
    
    public Dot(Side side, Frame frame, boolean isInput) {
        this.side = side;
        this.frame = frame;
        this.isInput = isInput;
        this.functionSite = frame.getFunctionSite();
    
        double s = UISettings.DOT_SIZE;
        polygon = new Polygon(
                -s, -s,
                +s, -s,
                0, s
        );
        polygon.setFill(isInput ? UISettings.DOT_INPUT_COLOR : UISettings.DOT_OUTPUT_COLOR);
        polygon.setPickOnBounds(true);
        showingBinding = Bindings.isNotEmpty(lines).or(polygon.hoverProperty()).or(frame.getShape().hoverProperty());
        showingBinding.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                show();
            } else {
                hide();
            }
        });
        polygon.setOpacity(0);
        polygon.setOnMouseClicked(this::onMouseClicked);
        var shadow = new DropShadow(25, Color.WHITE);
        shadow.setSpread(0.5);
        polygon.setEffect(shadow);
        switch (side) {
            case BOTTOM:
                polygon.setTranslateX(frame.getWidth() / 2);
                polygon.setTranslateY(frame.getHeight());
                if (isInput) {
                    polygon.setRotate(180);
                }
                break;
            case LEFT:
                polygon.setTranslateX(0);
                polygon.setTranslateY(frame.getHeight() / 2);
                polygon.setRotate(isInput ? -90 : +90);
                break;
            case RIGHT:
                polygon.setTranslateX(frame.getWidth());
                polygon.setTranslateY(frame.getHeight() / 2);
                polygon.setRotate(!isInput ? -90 : +90);
                break;
            case TOP:
                polygon.setTranslateX(frame.getWidth() / 2);
                polygon.setTranslateY(0);
                if (!isInput) {
                    polygon.setRotate(180);
                }
                break;
        }
    }
    
    public void show() {
        var opacityStart = new KeyValue(polygon.opacityProperty(), polygon.getOpacity());
        var opacityEnd = new KeyValue(polygon.opacityProperty(), 1);
        
        showTimeline.getKeyFrames().setAll(
                new KeyFrame(Duration.ZERO, opacityStart),
                new KeyFrame(UISettings.DOTS_MOVING_DURATION, opacityEnd)
        );
        Platform.runLater(() -> {
            hideTimeline.stop();
            showTimeline.stop();
            showTimeline.play();
        });
    }
    
    public void hide() {
        var opacityStart = new KeyValue(polygon.opacityProperty(), polygon.getOpacity());
        var opacityEnd = new KeyValue(polygon.opacityProperty(), 0);
        
        hideTimeline.getKeyFrames().setAll(
                new KeyFrame(Duration.ZERO, opacityStart),
                new KeyFrame(UISettings.DOTS_MOVING_DURATION, opacityEnd)
        );
        Platform.runLater(() -> {
            showTimeline.stop();
            hideTimeline.stop();
            hideTimeline.play();
        });
    }
    
    /**
     * relative to the scene
     *
     * @return
     */
    public double getCenterX() {
        return frame.getMinX() + polygon.getTranslateX();
    }
    
    public DoubleExpression centerXExpression() {
        return frame.minXExpression().add(polygon.translateXProperty());
    }
    
    /**
     * relative to the scene
     *
     * @return
     */
    public double getCenterY() {
        return frame.getMinY() + polygon.getTranslateY();
    }
    
    public DoubleExpression centerYExpression() {
        return frame.minYExpression().add(polygon.translateYProperty());
    }
    
    public void onMouseClicked(MouseEvent event) {
        if (lines.size() > 0) {
            return;
        }
        if (!Lines.isLineDrawing(functionSite)) {
            var line = Lines.createLine(functionSite, this);
            lines.add(line);
            Lines.setLineDrawing(functionSite, line);
        } else {
            var line = Lines.getDrawingLine(functionSite);
            if (!Lines.isDuplicate(functionSite, line, this)
                    && Lines.isConnectionAllowed(line.getStartDot(), this)) {
                line.setEndDot(this);
                lines.add(line);
                Lines.removeLineDrawing(functionSite);
            } else {
                line.remove();
            }
        }
    }
    
    public Frame getFrame() {
        return frame;
    }
    
    public void removeLine(Line line) {
        lines.remove(line);
    }
    
    public boolean hasLines() {
        return !lines.isEmpty();
    }
    
    public boolean hasNoLines() {
        return !hasLines();
    }
    
    public Set<Line> getLines() {
        return lines;
    }
    
    public Side getSide() {
        return side;
    }
    
    public boolean isInputDot() {
        return isInput;
    }
    
    public void addLine(Line line) {
        lines.add(line);
    }
    
    public Shape getShape() {
        return polygon;
    }
    
}
