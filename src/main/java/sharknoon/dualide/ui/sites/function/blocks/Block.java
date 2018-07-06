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

import io.reactivex.Observable;
import io.reactivex.rxjavafx.observables.JavaFxObservable;
import io.reactivex.rxjavafx.observers.JavaFxObserver;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleExpression;
import javafx.collections.ObservableList;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Side;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import sharknoon.dualide.ui.misc.MouseConsumable;
import sharknoon.dualide.ui.sites.function.FunctionSite;
import sharknoon.dualide.ui.sites.function.Moveable;
import sharknoon.dualide.ui.sites.function.UISettings;
import sharknoon.dualide.ui.sites.function.dots.Dot;
import sharknoon.dualide.ui.sites.function.lines.Line;
import sharknoon.dualide.ui.sites.function.lines.Lines;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Josua Frank
 */
public abstract class Block<S extends Shape> implements Moveable, MouseConsumable {
    
    private static Shape createPredictionShadow(Shape original) {
        var shadow = Shape.union(original, original);
        shadow.setFill(Color.rgb(0, 0, 0, 0));
        shadow.setStroke(UISettings.BLOCK_PREDICTION_SHADOW_STROKE_COLOR);
        shadow.setStrokeWidth(UISettings.BLOCK_PREDICTION_SHADOW_STROKE_WIDTH);
        shadow.setStrokeType(StrokeType.INSIDE);
        shadow.setEffect(null);
        return shadow;
    }
    
    private static void addDropShadowEffect(Shape shape) {
        var dropShadow = new DropShadow();
        dropShadow.setSpread(0.5);
        dropShadow.setRadius(0.0);
        dropShadow.setColor(UISettings.BLOCK_SELECTION_SHADOW_COLOR);
        shape.setEffect(dropShadow);
    }
    
    private static Map<Dot, Boolean> initDots(Block block, Side[] outputSides, Side[] inputSides) {
        Map<Dot, Boolean> result = new HashMap<>();
        for (var outputSide : outputSides) {
            var dot = new Dot(outputSide, block, false);
            result.put(dot, true);
        }
        for (var dotInputSide : inputSides) {
            var dot = new Dot(dotInputSide, block, true);
            result.put(dot, false);
        }
        return Collections.unmodifiableMap(result);
    }
    
    private static Binding<Boolean> initHoverListeners(Shape shape, Collection<Dot> dots) {
        Observable<Boolean> hover = JavaFxObservable.valuesOf(shape.hoverProperty());
        for (Dot dot : dots) {
            hover = hover.mergeWith(JavaFxObservable.valuesOf(dot.getShape().hoverProperty()));
        }
        return JavaFxObserver.toBinding(hover);
    }
    
    private static void setStrokeProperties(Shape shape) {
        shape.setStroke(UISettings.BLOCK_BORDER_STROKE_COLOR);
        shape.setStrokeWidth(UISettings.BLOCK_BORDER_STROKE_WIDTH);
        shape.setStrokeType(StrokeType.CENTERED);
    }
    //The root pane for this block
    private final AnchorPane root = new AnchorPane();
    //An id for the Block to uniquely identify them, needed for derialisation
    private final String id;
    //The height of the block
    private final double shapeHeight;
    //The width of the block
    private final double shapeWidth;
    //The sides, of where there are output dots
    private final Side[] dotOutputSides;
    //The sides, of where there are input dots
    private final Side[] dotInputSides;
    //The blockshape itself
    private final S blockShape;
    //The content of the block
    private final BlockContent blockContent;
    //The pane for the blockcontent to be centered
    private final BorderPane blockContentPane;
    //The contentPlaceholder of the block
    private final ObservableList<Text> blockText;
    //The Text of the statement in the block
    private final TextFlow blockTextFlow;
    //The shape of the shadow of the vlock
    private final Shape predictionShadowShape;
    //Timelines for the animation of the shadown, the dots and the moving of the block
    private final Timeline shadowShowTimeline = new Timeline();
    private final Timeline shadowRemoveTimeline = new Timeline();
    private final Timeline movingXTimeline = new Timeline();
    private final Timeline movingYTimeline = new Timeline();
    //The 1-4 output and 1-4 input dots of a block, unmodifiable, true for output
    private final Map<Dot, Boolean> dots;
    //The Functionsite this block counts to
    private final FunctionSite functionSite;
    //The contextmenu for a block
    private final BlockContextMenu menu = new BlockContextMenu(this);
    //The hoverListener for the blockshape and the dotShapes
    private final Binding<Boolean> hoverBinding;
    //Just some handy variables, see BlockMoving
    public double startX;
    public double startY;
    //The current state of the block
    private boolean selected;
    //indicator, of this block shows the text or the statement body
    private boolean showsPlaceholder = true;
    
    public Block(FunctionSite functionSite) {
        this(functionSite, UUID.randomUUID().toString());
    }
    
    public Block(FunctionSite functionSite, String id) {
        this.functionSite = functionSite;
        this.id = id == null ? UUID.randomUUID().toString() : id;
        shapeHeight = initShapeHeight();
        shapeWidth = initShapeWidth();
        dotOutputSides = initDotOutputSides();
        dotInputSides = initDotInputSides();
        blockShape = initBlockShape();
        blockContent = initBlockContent();
        blockContentPane = initBlockContentPane();
        blockText = blockContent.toText();
        blockTextFlow = initTextFlow();
        root.getChildren().addAll(blockShape, blockTextFlow, blockContentPane);
        dots = initDots(this, dotOutputSides, dotInputSides);
        dots.keySet().forEach(d -> root.getChildren().add(d.getShape()));
        hoverBinding = initHoverListeners(blockShape, dots.keySet());
        hoverBinding.addListener((observable, oldValue, newValue) -> Blocks.hoverOverBlockProperty(functionSite).set(newValue));
        this.predictionShadowShape = createPredictionShadow(blockShape);
        MouseConsumable.registerListeners(blockShape, this);
        MouseConsumable.registerListeners(blockTextFlow, this);
        setStrokeProperties(blockShape);
        addDropShadowEffect(blockShape);
        functionSite.getLogicSite().getWz().zoomFactorProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.doubleValue() > UISettings.BLOCK_ZOOMING_BODY_THRESHOLD) {
                showContentBody();
            } else {
                showContentText();
            }
        });
        showContentText();
        Blocks.registerBlock(functionSite, this);
    }
    
    private BorderPane initBlockContentPane() {
        BorderPane bp = new BorderPane();
        blockContent.setVisible(false);
        blockContent.setScaleX(0.4);
        blockContent.setScaleY(0.4);
        bp.setCenter(blockContent);
        bp.prefWidthProperty().bind(widthExpression());
        bp.maxWidthProperty().bind(widthExpression());
        bp.prefHeightProperty().bind(heightExpression());
        bp.maxHeightProperty().bind(heightExpression());
        return bp;
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
    public abstract S initBlockShape();
    
    /**
     * Gets the Content for the block
     */
    public abstract BlockContent initBlockContent();
    
    private TextFlow initTextFlow() {
        TextFlow result = new TextFlow();
        Bindings.bindContent(result.getChildren(), blockText);
        result.translateXProperty().bind(widthExpression().divide(2).subtract(result.widthProperty().divide(2)));
        result.translateYProperty().bind(heightExpression().divide(2).subtract(result.heightProperty().divide(2)));
        return result;
    }
    
    @Override
    public void onMousePressed(MouseEvent event) {
        Blocks.setMovingBlock(functionSite, this);
    }
    
    @Override
    public void onMouseDragged(MouseEvent event) {
        menu.hide();
        if (event.isPrimaryButtonDown()) {//moving
            if (selected) {
                Blocks.getAllBlocks(functionSite)
                        .filter(Block::isSelected)
                        .forEach(Block::highlight);
            } else {
                highlight();
            }
        }
        this.root.toFront();
    }
    
    @Override
    public void onMouseReleased(MouseEvent event) {
        if (isSelected()) {
            Blocks.getAllBlocks(functionSite)
                    .filter(Block::isSelected)
                    .forEach(Block::unhighlight);
        } else {
            unhighlight();
        }
    }
    
    @Override
    public void onMouseClicked(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY && event.isStillSincePress()) {
            if (!event.isControlDown()) {
                Blocks.getAllBlocks(functionSite).forEach(Block::unselect);
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
    
    /**
     * @return The shape of the block
     */
    public Shape getShape() {
        return blockShape;
    }
    
    /**
     * @return The prediction shadow of the block
     */
    Shape getShadow() {
        return predictionShadowShape;
    }
    
    public String getId() {
        return id;
    }
    
    void setMinXAnimated(double x) {
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
    
    void setMinYAnimated(double y) {
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
    public void setMinX(double x) {
        root.setTranslateX(x);
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
    public void setMinY(double y) {
        root.setTranslateY(y);
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
     * @param x               the x coordinate
     * @param y               the y coordinate
     * @param ignoreSelection true = ignores selection (default), false = has to
     *                        be unselected, because selected ones are dragged all together
     * @return true, if the block can move, false otherwise
     */
    boolean canMoveTo(double x, double y, boolean ignoreSelection) {
        Bounds newBounds = new BoundingBox(x, y, getWidth(), getHeight());
        return Blocks
                .getAllBlocks(functionSite)
                .filter(b -> ignoreSelection || !b.isSelected())
                .noneMatch(block -> block != this && newBounds.intersects(block.getBounds()));
    }
    
    /**
     * @return true, if this block has been selected by the user
     */
    public boolean isSelected() {
        return selected;
    }
    
    /**
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
            var dropShadow = (DropShadow) blockShape.getEffect();
            shadowShowTimeline.getKeyFrames().setAll(
                    new KeyFrame(Duration.ZERO,
                            new KeyValue(dropShadow.radiusProperty(), dropShadow.getRadius()),
                            new KeyValue(dropShadow.colorProperty(), dropShadow.getColor())
                    ),
                    new KeyFrame(UISettings.BLOCK_SELECTION_SHADOW_DURATION,
                            new KeyValue(dropShadow.radiusProperty(), UISettings.BLOCK_SELECTION_SHADOW_RADIUS),
                            new KeyValue(dropShadow.colorProperty(), UISettings.BLOCK_SELECTION_SHADOW_COLOR)
                    ));
            Platform.runLater(() -> {
                shadowRemoveTimeline.stop();
                shadowShowTimeline.stop();
                shadowShowTimeline.play();
            });
        }
    }
    
    /**
     * Removes the smaller shadow of a selection of that block
     */
    public void unselect() {
        if (selected) {
            selected = false;
            var dropShadow = (DropShadow) blockShape.getEffect();
            shadowRemoveTimeline.getKeyFrames().setAll(
                    new KeyFrame(Duration.ZERO,
                            new KeyValue(dropShadow.radiusProperty(), dropShadow.getRadius()),
                            new KeyValue(dropShadow.colorProperty(), dropShadow.getColor())
                    ),
                    new KeyFrame(UISettings.BLOCK_SELECTION_SHADOW_DURATION,
                            new KeyValue(dropShadow.radiusProperty(), 0),
                            new KeyValue(dropShadow.colorProperty(), UISettings.BLOCK_SELECTION_SHADOW_COLOR)
                    )
            );
            Platform.runLater(() -> {
                shadowShowTimeline.stop();
                shadowRemoveTimeline.stop();
                shadowRemoveTimeline.play();
            });
        }
    }
    
    private void toggleSelection() {
        if (isSelected()) {
            unselect();
        } else {
            select();
        }
    }
    
    /**
     * Shows the bigger shadow, e.g. for the movement of the block
     */
    private void highlight() {
        var dropShadow = (DropShadow) blockShape.getEffect();
        shadowShowTimeline.getKeyFrames().setAll(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(dropShadow.radiusProperty(), dropShadow.getRadius()),
                        new KeyValue(dropShadow.colorProperty(), dropShadow.getColor())
                ),
                new KeyFrame(UISettings.BLOCK_MOVING_SHADOW_DURATION,
                        new KeyValue(dropShadow.radiusProperty(), UISettings.BLOCK_MOVING_SHADOW_RADIUS),
                        new KeyValue(dropShadow.colorProperty(), UISettings.BLOCK_MOVING_SHADOW_COLOR)
                ));
        Platform.runLater(() -> {
            shadowRemoveTimeline.stop();
            shadowShowTimeline.stop();
            shadowShowTimeline.play();
        });
    }
    
    /**
     * Removes the bigger shadow, e.g. for the movement of the block
     */
    private void unhighlight() {
        var dropShadow = (DropShadow) blockShape.getEffect();
        shadowRemoveTimeline.getKeyFrames().setAll(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(dropShadow.radiusProperty(), dropShadow.getRadius()),
                        new KeyValue(dropShadow.colorProperty(), dropShadow.getColor())
                ),
                new KeyFrame(UISettings.BLOCK_MOVING_SHADOW_DURATION,
                        new KeyValue(dropShadow.radiusProperty(), selected ? UISettings.BLOCK_SELECTION_SHADOW_RADIUS : 0),
                        new KeyValue(dropShadow.colorProperty(), UISettings.BLOCK_SELECTION_SHADOW_COLOR)
                )
        );
        Platform.runLater(() -> {
            shadowShowTimeline.stop();
            shadowRemoveTimeline.stop();
            shadowRemoveTimeline.play();
        });
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
        var lines = dots.keySet()
                .stream()
                .map(Dot::getLines)
                .flatMap(Set::stream)
                .collect(Collectors.toList());
        lines.forEach(Line::remove);
        hoverBinding.dispose();
        Blocks.unregisterBlock(functionSite, this);
        ((Pane) root.getParent()).getChildren().removeAll(predictionShadowShape, root);
    }
    
    public Set<Dot> getAllDots() {
        return dots.keySet();
    }
    
    private Stream<Dot> getInputDots() {
        return dots.entrySet().stream().filter(e -> !e.getValue()).map(Entry::getKey);
    }
    
    public Optional<Dot> getInputDot(Side side) {
        return getInputDots().filter(d -> d.getSide() == side).findAny();
    }
    
    public Stream<Dot> getOutputDots() {
        return dots.entrySet().stream().filter(Entry::getValue).map(Entry::getKey);
    }
    
    public Optional<Dot> getOutputDot(Side side) {
        return getOutputDots().filter(d -> d.getSide() == side).findAny();
    }
    
    private void showContentBody() {
        if (!showsPlaceholder) {
            return;
        }
        showsPlaceholder = false;
        blockContent.setVisible(true);
        blockTextFlow.setVisible(false);
        functionSite.getLogicSite().getWs().disable();
    }
    
    private void showContentText() {
        if (showsPlaceholder) {
            return;
        }
        showsPlaceholder = true;
        blockContent.setVisible(false);
        blockTextFlow.setVisible(true);
        functionSite.getLogicSite().getWs().enable();
    }
    
    @Override
    public int hashCode() {
        return id.hashCode();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
    
        Block<?> block = (Block<?>) o;
    
        return id.equals(block.id);
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" + "id=" + id + ", shapeHeight=" + shapeHeight + ", shapeWidth=" + shapeWidth + ", selected=" + selected + ", startX=" + startX + ", startY=" + startY + '}';
    }
    
}
