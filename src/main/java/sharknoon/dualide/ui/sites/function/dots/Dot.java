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
package sharknoon.dualide.ui.sites.function.dots;

import java.util.Set;
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
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;
import sharknoon.dualide.ui.sites.function.FunctionSite;
import sharknoon.dualide.ui.sites.function.lines.Line;
import sharknoon.dualide.ui.sites.function.UISettings;
import sharknoon.dualide.ui.sites.function.blocks.Block;
import sharknoon.dualide.ui.sites.function.lines.Lines;

/**
 *
 * @author Josua Frank
 */
public class Dot {

    private final Circle circle;
    private final Block block;
    private final ObservableSet<Line> lines = FXCollections.observableSet();
    private final Side side;
    private final boolean isInput;
    private final FunctionSite functionSite;
    private final BooleanBinding showingBinding;
    private final Timeline showTimeline = new Timeline();
    private final Timeline hideTimeline = new Timeline();

    public Dot(Side side, Block block, boolean isInput) {
        this.side = side;
        this.block = block;
        this.isInput = isInput;
        this.functionSite = block.getFunctionSite();

        circle = new Circle(UISettings.DOT_RADIUS, isInput ? UISettings.DOT_INPUT_COLOR : UISettings.DOT_OUTPUT_COLOR);
        showingBinding = Bindings.isNotEmpty(lines).or(circle.hoverProperty()).or(block.getShape().hoverProperty());
        showingBinding.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                show();
            } else {
                hide();
            }
        });
        circle.setOpacity(0);
        circle.setOnMouseClicked(this::onMouseClicked);
          var shadow = new DropShadow(25, Color.WHITE);
        shadow.setSpread(0.5);
        circle.setEffect(shadow);
        switch (side) {
            case BOTTOM:
                circle.setCenterX(block.getWidth() / 2);
                circle.setCenterY(block.getHeight());
                break;
            case LEFT:
                circle.setCenterX(0);
                circle.setCenterY(block.getHeight() / 2);
                break;
            case RIGHT:
                circle.setCenterX(block.getWidth());
                circle.setCenterY(block.getHeight() / 2);
                break;
            case TOP:
                circle.setCenterX(block.getWidth() / 2);
                circle.setCenterY(0);
                break;
        }
    }

    public void show() {
          var opacityStart = new KeyValue(circle.opacityProperty(), circle.getOpacity());
          var opacityEnd = new KeyValue(circle.opacityProperty(), 1);

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
          var opacityStart = new KeyValue(circle.opacityProperty(), circle.getOpacity());
          var opacityEnd = new KeyValue(circle.opacityProperty(), 0);

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
        return block.getMinX() + circle.getCenterX();
    }

    public DoubleExpression centerXExpression() {
        return block.minXExpression().add(circle.centerXProperty());
    }

    /**
     * relative to the scene
     *
     * @return
     */
    public double getCenterY() {
        return block.getMinY() + circle.getCenterY();
    }

    public DoubleExpression centerYExpression() {
        return block.minYExpression().add(circle.centerYProperty());
    }

    public void onMouseClicked(MouseEvent event) {
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

    public Block getBlock() {
        return block;
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
        return circle;
    }

}
