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
package sharknoon.dualide.ui.flowchart.dots;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.geometry.Side;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import sharknoon.dualide.ui.flowchart.Flowchart;
import sharknoon.dualide.ui.flowchart.lines.Line;
import sharknoon.dualide.ui.flowchart.UISettings;
import sharknoon.dualide.ui.flowchart.blocks.Block;
import sharknoon.dualide.ui.flowchart.lines.Lines;

/**
 *
 * @author Josua Frank
 */
public class Dot {

    private final Circle circle;
    private final Side side;
    private final Block block;
    private final Flowchart flowchart;
    private Line line;

    public static Dot createDot(Block block, Side side) {
        return new Dot(side, block);
    }

    private Dot(Side side, Block block) {
        this.side = side;
        this.block = block;
        this.flowchart = block.getFlowchart();

        circle = new Circle(UISettings.blockDotRadius, UISettings.blockDotColor);
        circle.setOpacity(0);
        circle.setOnMouseEntered(this::onMouseEntered);
        circle.setOnMouseExited(this::onMouseExited);
        circle.setOnMousePressed(this::onMousePressed);
        circle.setOnMouseReleased(this::onMouseReleased);
        DropShadow shadow = new DropShadow(25, Color.WHITE);
        shadow.setSpread(0.5);
        circle.setEffect(shadow);
        switch (side) {
            case BOTTOM:
                circle.setCenterX(block.getWidth() / 2);
                circle.setCenterY(block.getHeight());
                circle.setTranslateY(-UISettings.dotsMovingDistance);
                break;
            case LEFT:
                circle.setCenterX(0);
                circle.setCenterY(block.getHeight() / 2);
                circle.setTranslateX(UISettings.dotsMovingDistance);
                break;
            case RIGHT:
                circle.setCenterX(block.getWidth());
                circle.setCenterY(block.getHeight() / 2);
                circle.setTranslateX(-UISettings.dotsMovingDistance);
                break;
            case TOP:
                circle.setCenterX(block.getWidth() / 2);
                circle.setCenterY(0);
                circle.setTranslateY(UISettings.dotsMovingDistance);
                break;
        }
    }

    public void addTo(Pane pane) {
        pane.getChildren().add(circle);
    }

    public KeyFrame[] show() {
        KeyValue movingStart;
        KeyValue movingEnd;
        KeyValue opacityStart = new KeyValue(circle.opacityProperty(), circle.getOpacity());
        KeyValue opacityEnd = new KeyValue(circle.opacityProperty(), 1);
        if (circle.getCenterX() == 0) {//left
            movingStart = new KeyValue(circle.translateXProperty(), circle.getTranslateX());
            movingEnd = new KeyValue(circle.translateXProperty(), 0);
        } else if (circle.getCenterY() == 0) {//top
            movingStart = new KeyValue(circle.translateYProperty(), circle.getTranslateY());
            movingEnd = new KeyValue(circle.translateYProperty(), 0);
        } else if (circle.getCenterX() < block.getWidth()) {//bottom
            movingStart = new KeyValue(circle.translateYProperty(), circle.getTranslateY());
            movingEnd = new KeyValue(circle.translateYProperty(), 0);
        } else {//right
            movingStart = new KeyValue(circle.translateXProperty(), circle.getTranslateX());
            movingEnd = new KeyValue(circle.translateXProperty(), 0);
        }
        return new KeyFrame[]{
            new KeyFrame(Duration.ZERO, movingStart, opacityStart),
            new KeyFrame(UISettings.dotsMovingDuration, movingEnd, opacityEnd)
        };
    }

    public KeyFrame[] hide() {
        if (line != null) {
            return new KeyFrame[]{};//If there is alreay a line from this dot, dont hide it
        }
        KeyValue movingStart;
        KeyValue movingEnd;
        KeyValue opacityStart = new KeyValue(circle.opacityProperty(), circle.getOpacity());
        KeyValue opacityEnd = new KeyValue(circle.opacityProperty(), 0);
        if (circle.getCenterX() == 0) {//left
            movingStart = new KeyValue(circle.translateXProperty(), circle.getTranslateX());
            movingEnd = new KeyValue(circle.translateXProperty(), UISettings.dotsMovingDistance);
        } else if (circle.getCenterY() == 0) {//top
            movingStart = new KeyValue(circle.translateYProperty(), circle.getTranslateY());
            movingEnd = new KeyValue(circle.translateYProperty(), UISettings.dotsMovingDistance);
        } else if (circle.getCenterX() < block.getWidth()) {//bottom
            movingStart = new KeyValue(circle.translateYProperty(), circle.getTranslateY());
            movingEnd = new KeyValue(circle.translateYProperty(), -UISettings.dotsMovingDistance);
        } else {//right
            movingStart = new KeyValue(circle.translateXProperty(), circle.getTranslateX());
            movingEnd = new KeyValue(circle.translateXProperty(), -UISettings.dotsMovingDistance);
        }
        return new KeyFrame[]{
            new KeyFrame(Duration.ZERO, movingStart, opacityStart),
            new KeyFrame(UISettings.dotsMovingDuration, movingEnd, opacityEnd)
        };
    }

    public double getCenterX() {
        return circle.getCenterX();
    }

    public double getCenterY() {
        return circle.getCenterY();
    }

    private void onMouseEntered(MouseEvent event){
        Dots.setMouseOverDot(this);
    }
    
    private void onMouseExited(MouseEvent event){
        Dots.removeMouseOverDot();
    }
    
    private void onMousePressed(MouseEvent event) {
        Line line = Lines.createLine(flowchart, this);
        Lines.setLineDrawing(line);
    }

    private void onMouseReleased(MouseEvent event) {
        if (Lines.isLineDrawing()) {//connecting the two lines
            Lines.getDrawingLine().setEndDot(this);
        }
        Lines.removeLineDrawing();
    }

    public Block getBlock() {
        return block;
    }

    public Flowchart getFlowchart() {
        return flowchart;
    }
}
