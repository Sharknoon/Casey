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

import java.util.ArrayList;
import java.util.List;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.beans.binding.DoubleExpression;
import javafx.geometry.Side;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import sharknoon.dualide.ui.sites.function.FunctionSite;
import sharknoon.dualide.ui.sites.function.lines.Line;
import sharknoon.dualide.ui.sites.function.UISettings;
import sharknoon.dualide.ui.sites.function.blocks.Block;
import sharknoon.dualide.ui.sites.function.blocks.Blocks;
import sharknoon.dualide.ui.sites.function.lines.Lines;

/**
 *
 * @author Josua Frank
 */
public class Dot {

    private final Circle circle;
    private final Block block;
    private final List<Line> lines = new ArrayList<>();
    private final Side side;
    private final boolean isInput;
    private final FunctionSite functionSite;

    public static Dot createDot(Block block, Side side, boolean isInput) {
        return new Dot(side, block, isInput);
    }

    private Dot(Side side, Block block, boolean isInput) {
        this.side = side;
        this.block = block;
        this.isInput = isInput;
        this.functionSite = block.getFunctionSite();

        circle = new Circle(UISettings.DOT_RADIUS, isInput ? UISettings.DOT_INPUT_COLOR : UISettings.DOT_OUTPUT_COLOR);
        circle.setOpacity(0);
        circle.setOnMouseEntered(this::onMouseEntered);
        circle.setOnMouseExited(this::onMouseExited);
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
        Dots.registerDot(block.getFunctionSite(), this);
    }

    public void addTo(Pane pane) {
        pane.getChildren().add(circle);
    }

    public KeyFrame[] show() {
        block.toFront();
        KeyValue opacityStart = new KeyValue(circle.opacityProperty(), circle.getOpacity());
        KeyValue opacityEnd = new KeyValue(circle.opacityProperty(), 1);
        return new KeyFrame[]{
            new KeyFrame(Duration.ZERO, opacityStart),
            new KeyFrame(UISettings.DOTS_MOVING_DURATION, opacityEnd)
        };
    }

    public KeyFrame[] hide() {
        if (!lines.isEmpty()) {
            return new KeyFrame[]{};//If there is alreay a line from this dot, dont hide it
        }
        KeyValue opacityStart = new KeyValue(circle.opacityProperty(), circle.getOpacity());
        KeyValue opacityEnd = new KeyValue(circle.opacityProperty(), 0);
        return new KeyFrame[]{
            new KeyFrame(Duration.ZERO, opacityStart),
            new KeyFrame(UISettings.DOTS_MOVING_DURATION, opacityEnd)
        };
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

    private void onMouseEntered(MouseEvent event) {
        Dots.setMouseOverDot(this);
        block.showDots();
        Blocks.setMouseOverBlock(block);
    }

    private void onMouseExited(MouseEvent event) {
        Dots.removeMouseOverDot();
        block.hideDots();
        Blocks.removeMouseOverBlock();
    }

    public void onMouseClicked(MouseEvent event) {
        if (!Lines.isLineDrawing(functionSite)) {
              var line = Lines.createLine(functionSite, this);
            lines.add(line);
        } else {
              var line = Lines.getDrawingLine(functionSite);
            if (!Lines.isDuplicate(functionSite, line, this)) {
                lines.add(line);
                line.setEndDot(this);
                Lines.removeLineDrawing(functionSite);
            } else {
                line.remove();
            }
        }
        block.toFront();
    }

    public Block getBlock() {
        return block;
    }

    public void removeLine(Line line) {
        lines.remove(line);
        block.hideDots();
    }

    public boolean hasLines() {
        return !lines.isEmpty();
    }

    public List<Line> getLines() {
        return lines;
    }

    public void addLine(Line line) {
        lines.add(line);
    }

    public Side getSide() {
        return side;
    }

    public boolean isInputDot() {
        return isInput;
    }

}
