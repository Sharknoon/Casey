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

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
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
    private Line line;

    public static Dot createDot(Block block, Side side) {
        return new Dot(side, block);
    }

    private Dot(Side side, Block block) {
        this.block = block;

        circle = new Circle(UISettings.dotRadius, UISettings.dotColor);
        circle.setOpacity(0);
        circle.setOnMouseEntered(this::onMouseEntered);
        circle.setOnMouseExited(this::onMouseExited);
        circle.setOnMouseClicked(this::onMouseClicked);
        DropShadow shadow = new DropShadow(25, Color.WHITE);
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
        Dots.registerDot(block.getFlowchart(), this);
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
            new KeyFrame(UISettings.dotsMovingDuration, opacityEnd)
        };
    }

    public KeyFrame[] hide() {
        if (line != null) {
            return new KeyFrame[]{};//If there is alreay a line from this dot, dont hide it
        }
        KeyValue opacityStart = new KeyValue(circle.opacityProperty(), circle.getOpacity());
        KeyValue opacityEnd = new KeyValue(circle.opacityProperty(), 0);
        return new KeyFrame[]{
            new KeyFrame(Duration.ZERO, opacityStart),
            new KeyFrame(UISettings.dotsMovingDuration, opacityEnd)
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

    /**
     * relative to the scene
     *
     * @return
     */
    public double getCenterY() {
        return block.getMinY() + circle.getCenterY();
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
        if (!Lines.isLineDrawing()) {
            line = Lines.createLine(block.getFlowchart(), this);
        } else {
            line = Lines.getDrawingLine();
            Lines.removeLineDrawing();
            line.setEndDot(this);
        }
        block.toFront();
    }

    public Block getBlock() {
        return block;
    }

    public void removeLine() {
        line = null;
        block.hideDots();
    }

    public boolean hasLine() {
        return line != null;
    }

    public void setLine(Line line) {
        this.line = line;
    }

}
