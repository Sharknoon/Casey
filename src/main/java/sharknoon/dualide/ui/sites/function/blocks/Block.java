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
package sharknoon.dualide.ui.sites.function.blocks;

import sharknoon.dualide.ui.sites.function.dots.Dot;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleExpression;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Side;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeType;
import javafx.util.Duration;
import sharknoon.dualide.ui.sites.function.FunctionSite;
import sharknoon.dualide.ui.sites.function.UISettings;
import sharknoon.dualide.ui.sites.function.dots.Dots;
import sharknoon.dualide.ui.sites.function.lines.Line;
import sharknoon.dualide.ui.sites.function.lines.Lines;

/**
 *
 * @author Josua Frank
 */
public abstract class Block implements Moveable, MouseConsumable {

    //The root pane for this block
    private final AnchorPane root = new AnchorPane();
    //The height of the block
    private final double shapeHeight;
    //The width of the block
    private final double shapeWidth;
    //The sides, of where there are output dots
    private final Side[] dotOutputSides;
    //The sides, of where there are input dots
    private final Side[] dotInputSides;
    //The blockshape itself
    private final Shape blockShape;
    //The shape of the shadow of the vlock
    private final Shape predictionShadowShape;
    //Timelines for the animation of the shadown, the dots and the moving of the block
    private final Timeline shadowShowTimeline = new Timeline();
    private final Timeline shadowRemoveTimeline = new Timeline();
    private final Timeline dotsShowTimeline = new Timeline();
    private final Timeline dotsHideTimeline = new Timeline();
    private final Timeline movingXTimeline = new Timeline();
    private final Timeline movingYTimeline = new Timeline();
    //The 1-4 output dots of a block
    private final List<Dot> outputDots = new ArrayList<>();
    //The 1-4 input dots of a block
    private final List<Dot> inputDots = new ArrayList<>();
    //The current functionSite of this block
    private final FunctionSite functionSite;
    //The current state of the block
    private boolean selected;
    //The contextmenu for a block
    private final BlockContextMenu menu = new BlockContextMenu(this);
    public double startX;
    public double startY;

    /**
     *
     * @param functionSite
     */
    public Block(FunctionSite functionSite) {
        this.functionSite = functionSite;
        shapeHeight = initShapeHeight();
        shapeWidth = initShapeWidth();
        dotOutputSides = initDotOutputSides();
        dotInputSides = initDotInputSides();
        blockShape = initBlockShape();
        root.getChildren().add(blockShape);
        this.predictionShadowShape = createPredictionShadow(blockShape);
        registerListeners(blockShape, this);
        setStrokeProperties(blockShape);
        addDropShadowEffect(blockShape);
        createDots();
        Blocks.registerBlock(functionSite, this);
    }

    /**
     * Gets the height of the block
     *
     * @return The height of the block
     */
    public abstract double initShapeHeight();

    /**
     * Gets the width of the block
     *
     * @return The width of the block
     */
    public abstract double initShapeWidth();

    /**
     * Gets the sides of this blocktype, where there are output dots
     *
     * @return The sides of this blocktype, where there are output dots
     */
    public abstract Side[] initDotOutputSides();

    /**
     * Gets the sides of this blocktype, where there are input dots
     *
     * @return The sides of this blocktype, where there are input dots
     */
    public abstract Side[] initDotInputSides();

    /**
     * Gets the shape of this block
     *
     * @return The shape of this block
     */
    public abstract Shape initBlockShape();

    private static Shape createPredictionShadow(Shape original) {
          var shadow = Shape.union(original, original);
        shadow.setFill(Color.rgb(0, 0, 0, 0));
        shadow.setStroke(UISettings.BLOCK_PREDICTION_SHADOW_STROKE_COLOR);
        shadow.setStrokeWidth(UISettings.BLOCK_PREDICTION_SHADOW_STROKE_WIDTH);
        shadow.setStrokeType(StrokeType.INSIDE);
        shadow.setEffect(null);
        return shadow;
    }

    private static void registerListeners(Shape shape, MouseConsumable consumable) {
        shape.setOnMousePressed(consumable::onMousePressed);
        shape.setOnMouseReleased(consumable::onMouseReleased);
        shape.setOnMouseClicked(consumable::onMouseClicked);
        shape.setOnContextMenuRequested(consumable::onContextMenuRequested);
        shape.setOnMouseEntered(consumable::onMouseEntered);
        shape.setOnMouseExited(consumable::onMouseExited);
    }

    private static void setStrokeProperties(Shape shape) {
        shape.setStroke(UISettings.BLOCK_BORDER_STROKE_COLOR);
        shape.setStrokeWidth(UISettings.BLOCK_BORDER_STROKE_WIDTH);
        shape.setStrokeType(StrokeType.CENTERED);
    }

    private static void addDropShadowEffect(Shape shape) {
          var dropShadow = new DropShadow();
        dropShadow.setSpread(0.5);
        dropShadow.setRadius(0.0);
        dropShadow.setColor(UISettings.BLOCK_SELECTION_SHADOW_COLOR);
        shape.setEffect(dropShadow);
    }

    private void createDots() {
        for (  var dotOutputSide : dotOutputSides) {
              var dot = Dot.createDot(this, dotOutputSide, false);
            dot.addTo(root);
            outputDots.add(dot);
        }
        for (  var dotInputSide : dotInputSides) {
              var dot = Dot.createDot(this, dotInputSide, true);
            dot.addTo(root);
            inputDots.add(dot);
        }
    }

    @Override
    public void onMousePressed(MouseEvent event) {
        Blocks.setMouseOverBlock(this);
        Blocks.setMovingBlock(functionSite, this);
        hideDots();
        menu.hide();
        if (event.isPrimaryButtonDown()) {//moving
            if (selected) {
                Blocks.getAllBlocks(functionSite).stream()
                        .filter(Block::isSelected)
                        .forEach(Block::highlight);
            } else {
                highlight();
            }
        }
        this.root.toFront();
    }

    @Override
    public void onMouseDragged(MouseEvent event) {
    }

    @Override
    public void onMouseReleased(MouseEvent event) {
        if (blockShape.contains(event.getX(), event.getY())) {
            showDots();
        }
        if (isSelected()) {
            Blocks.getAllBlocks(functionSite).stream()
                    .filter(Block::isSelected)
                    .forEach(Block::unhighlight);
        } else {
            unhighlight();
        }
    }

    @Override
    public void onMouseClicked(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            if (!event.isControlDown()) {
                Blocks.getAllBlocks(functionSite).forEach(Block::unselect);
                Lines.getAllLines(functionSite).forEach(Line::unselect);
            }
            select();
        }
    }

    @Override
    public void onContextMenuRequested(ContextMenuEvent event) {
        menu.onContextMenuRequested(event);
    }

    @Override
    public void onMouseEntered(MouseEvent event) {
        Blocks.setMouseOverBlock(this);
        if (!event.isPrimaryButtonDown() || Lines.isLineDrawing(functionSite)) {
            showDots();
        }
    }

    @Override
    public void onMouseExited(MouseEvent event) {
        if (!event.isPrimaryButtonDown()) {
            Blocks.removeMouseOverBlock();
        }
        if (!Lines.isLineDrawing(functionSite) && !Dots.isMouseOverDot()) {
            hideDots();
        }
    }

    /**
     *
     * @return The shape of the block
     */
    public Shape getShape() {
        return blockShape;
    }

    /**
     *
     * @return The prediction shadow of the block
     */
    public Shape getShadow() {
        return predictionShadowShape;
    }

    @Override
    public void setMinX(double x) {
        root.setTranslateX(x);
    }

    public void setMinXAnimated(double x) {
        movingXTimeline.getKeyFrames().clear();
        movingXTimeline.getKeyFrames().addAll(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(root.translateXProperty(), root.getTranslateX())
                ),
                new KeyFrame(UISettings.BLOCK_MOVING_DURATION,
                        new KeyValue(root.translateXProperty(), x)
                ));
        movingXTimeline.stop();
        movingXTimeline.play();
    }

    @Override
    public void setMinY(double y) {
        root.setTranslateY(y);
    }

    public void setMinYAnimated(double y) {
        movingYTimeline.getKeyFrames().clear();
        movingYTimeline.getKeyFrames().addAll(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(root.translateYProperty(), root.getTranslateY())
                ),
                new KeyFrame(UISettings.BLOCK_MOVING_DURATION,
                        new KeyValue(root.translateYProperty(), y)
                ));
        movingYTimeline.stop();
        movingYTimeline.play();
    }

    @Override
    public double getMinX() {
        return root.getTranslateX();
    }

    @Override
    public DoubleExpression minXExpression() {
        return root.translateXProperty();
    }

    @Override
    public double getMinY() {
        return root.getTranslateY();
    }

    @Override
    public DoubleExpression minYExpression() {
        return root.translateYProperty();
    }

    @Override
    public double getWidth() {
        return shapeWidth;
    }

    @Override
    public DoubleExpression widthExpression() {
        return Bindings.createDoubleBinding(() -> shapeWidth);
    }

    @Override
    public double getHeight() {
        return shapeHeight;
    }

    @Override
    public DoubleExpression heightExpression() {
        return Bindings.createDoubleBinding(() -> shapeHeight);
    }

    @Override
    public double getMaxX() {
        return getMinX() + getWidth();
    }

    @Override
    public DoubleExpression maxXExpression() {
        return minXExpression().add(widthExpression());
    }

    @Override
    public double getMaxY() {
        return getMinY() + getHeight();
    }

    @Override
    public DoubleExpression maxYExpression() {
        return minYExpression().add(heightExpression());
    }

    /**
     * Checks, if this block can move to the desired destination
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return true if this block can move
     */
    @Override
    public boolean canMoveTo(double x, double y) {
        return canMoveTo(x, y, true);
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

        Bounds newBounds = new BoundingBox(x, y, getWidth(), getHeight());
          var noBlock = Blocks
                .getAllBlocks(functionSite)
                .stream()
                .filter(b -> ignoreSelection || !b.isSelected())
                .noneMatch(block -> block != this && newBounds.intersects(block.getBounds()));
        return noBlock;
    }

    /**
     *
     * @return true, if this block has been selected by the user
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     *
     * @return The functionSite, in which this block is in
     */
    public FunctionSite getFunctionSite() {
        return functionSite;
    }

    /**
     * Shows the smaller shadow of a selection of that block
     */
    public void select() {
        if (!selected) {
            selected = true;
            shadowShowTimeline.getKeyFrames().clear();
              var dropShadow = (DropShadow) blockShape.getEffect();
            shadowShowTimeline.getKeyFrames().addAll(
                    new KeyFrame(Duration.ZERO,
                            new KeyValue(dropShadow.radiusProperty(), dropShadow.getRadius()),
                            new KeyValue(dropShadow.colorProperty(), dropShadow.getColor())
                    ),
                    new KeyFrame(UISettings.BLOCK_SELECTION_SHADOW_DURATION,
                            new KeyValue(dropShadow.radiusProperty(), UISettings.BLOCK_SELECTION_SHADOW_RADIUS),
                            new KeyValue(dropShadow.colorProperty(), UISettings.BLOCK_SELECTION_SHADOW_COLOR)
                    ));
            shadowRemoveTimeline.stop();
            shadowShowTimeline.stop();
            shadowShowTimeline.play();
        }
    }

    /**
     * Removes the smaller shadow of a selection of that block
     */
    public void unselect() {
        if (selected) {
            selected = false;
              var dropShadow = (DropShadow) blockShape.getEffect();
            shadowRemoveTimeline.getKeyFrames().clear();
            shadowRemoveTimeline.getKeyFrames().addAll(
                    new KeyFrame(Duration.ZERO,
                            new KeyValue(dropShadow.radiusProperty(), dropShadow.getRadius()),
                            new KeyValue(dropShadow.colorProperty(), dropShadow.getColor())
                    ),
                    new KeyFrame(UISettings.BLOCK_SELECTION_SHADOW_DURATION,
                            new KeyValue(dropShadow.radiusProperty(), 0),
                            new KeyValue(dropShadow.colorProperty(), UISettings.BLOCK_SELECTION_SHADOW_COLOR)
                    )
            );
            shadowShowTimeline.stop();
            shadowRemoveTimeline.stop();
            shadowRemoveTimeline.play();
        }
    }

    /**
     * Shows the bigger shadow, e.g. for the movement of the block
     */
    public void highlight() {
        shadowShowTimeline.getKeyFrames().clear();
          var dropShadow = (DropShadow) blockShape.getEffect();
        shadowShowTimeline.getKeyFrames().addAll(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(dropShadow.radiusProperty(), dropShadow.getRadius()),
                        new KeyValue(dropShadow.colorProperty(), dropShadow.getColor())
                ),
                new KeyFrame(UISettings.BLOCK_MOVING_SHADOW_DURATION,
                        new KeyValue(dropShadow.radiusProperty(), UISettings.BLOCK_MOVING_SHADOW_RADIUS),
                        new KeyValue(dropShadow.colorProperty(), UISettings.BLOCK_MOVING_SHADOW_COLOR)
                ));
        shadowRemoveTimeline.stop();
        shadowShowTimeline.stop();
        shadowShowTimeline.play();
    }

    /**
     * Removes the bigger shadow, e.g. for the movement of the block
     */
    public void unhighlight() {
          var dropShadow = (DropShadow) blockShape.getEffect();
        shadowRemoveTimeline.getKeyFrames().clear();
        shadowRemoveTimeline.getKeyFrames().addAll(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(dropShadow.radiusProperty(), dropShadow.getRadius()),
                        new KeyValue(dropShadow.colorProperty(), dropShadow.getColor())
                ),
                new KeyFrame(UISettings.BLOCK_MOVING_SHADOW_DURATION,
                        new KeyValue(dropShadow.radiusProperty(), selected ? UISettings.BLOCK_SELECTION_SHADOW_RADIUS : 0),
                        new KeyValue(dropShadow.colorProperty(), UISettings.BLOCK_SELECTION_SHADOW_COLOR)
                )
        );
        shadowShowTimeline.stop();
        shadowRemoveTimeline.stop();
        shadowRemoveTimeline.play();
    }

    /**
     * Shows the connection dots around the block
     */
    public void showDots() {
        dotsShowTimeline.getKeyFrames().clear();
        inputDots.forEach(dot -> {
            dotsShowTimeline.getKeyFrames().addAll(dot.show());
        });
        outputDots.forEach(dot -> {
            dotsShowTimeline.getKeyFrames().addAll(dot.show());
        });
        dotsHideTimeline.stop();
        dotsShowTimeline.stop();
        dotsShowTimeline.play();
    }

    /**
     * Hides the conection dots around the block
     */
    public void hideDots() {
        dotsHideTimeline.getKeyFrames().clear();
        inputDots.forEach(dot -> {
            dotsHideTimeline.getKeyFrames().addAll(dot.hide());
        });
        outputDots.forEach(dot -> {
            dotsHideTimeline.getKeyFrames().addAll(dot.hide());
        });
        dotsShowTimeline.stop();
        dotsHideTimeline.stop();
        dotsHideTimeline.play();
    }

    /**
     * Reloads the connection dots around the block, e.g. when a line is
     * connected, this dot stays as long as a line is connected open
     */
    public void reloadDots() {
        dotsShowTimeline.getKeyFrames().clear();
        dotsHideTimeline.getKeyFrames().clear();
        inputDots.stream()
                .forEach(dot -> {
                    if (dot.hasLines()) {
                        dotsShowTimeline.getKeyFrames().addAll(dot.show());
                    } else {
                        dotsHideTimeline.getKeyFrames().addAll(dot.hide());
                    }
                });
        outputDots.stream()
                .forEach(dot -> {
                    if (dot.hasLines()) {
                        dotsShowTimeline.getKeyFrames().addAll(dot.show());
                    } else {
                        dotsHideTimeline.getKeyFrames().addAll(dot.hide());
                    }
                });
        dotsShowTimeline.stop();
        dotsHideTimeline.stop();
        dotsShowTimeline.play();
        dotsHideTimeline.play();
    }

    /**
     * Adds this block to a pane
     *
     * @param pane The pane to receive this block
     */
    public void addTo(Pane pane) {
        predictionShadowShape.setTranslateX(this.root.getTranslateX());
        predictionShadowShape.setTranslateY(this.root.getTranslateY());
        pane.getChildren().addAll(predictionShadowShape, this.root);
    }

    /**
     * Destroyes this block completely
     */
    public void remove() {
          var lines = inputDots.stream().flatMap(d -> d.getLines().stream()).collect(Collectors.toList());
        lines.forEach(Line::remove);
        lines = outputDots.stream().flatMap(d -> d.getLines().stream()).collect(Collectors.toList());
        lines.forEach(Line::remove);
        Blocks.unregisterBlock(functionSite, this);
        ((Pane) root.getParent()).getChildren().removeAll(predictionShadowShape, root);
    }

    /**
     * Brings this block all the way to the front, covering some lines
     */
    public void toFront() {
        root.toFront();
    }

    public List<Dot> getOutputDots(){
        return outputDots;
    }
    
}
