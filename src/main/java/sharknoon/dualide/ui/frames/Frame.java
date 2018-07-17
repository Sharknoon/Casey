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
package sharknoon.dualide.ui.frames;

import io.reactivex.Observable;
import io.reactivex.rxjavafx.observables.JavaFxObservable;
import io.reactivex.rxjavafx.observers.JavaFxObserver;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.DoubleExpression;
import javafx.collections.ObservableList;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
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
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import sharknoon.dualide.logic.blocks.Block;
import sharknoon.dualide.ui.UISettings;
import sharknoon.dualide.ui.dots.Dot;
import sharknoon.dualide.ui.interfaces.Moveable;
import sharknoon.dualide.ui.lines.Line;
import sharknoon.dualide.ui.lines.Lines;
import sharknoon.dualide.ui.misc.MouseConsumable;
import sharknoon.dualide.ui.sites.function.FunctionSite;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Josua Frank
 */
public abstract class Frame<S extends Shape> implements Moveable, MouseConsumable {
    
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
    
    private static Map<Dot, Boolean> initDots(Frame frame, Side[] outputSides, Side[] inputSides) {
        Map<Dot, Boolean> result = new HashMap<>();
        for (var outputSide : outputSides) {
            var dot = new Dot(outputSide, frame, false);
            result.put(dot, true);
        }
        for (var dotInputSide : inputSides) {
            var dot = new Dot(dotInputSide, frame, true);
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
    
    //The root pane for this frame
    private final AnchorPane root = new AnchorPane();
    //The height of the frame
    private final DoubleBinding shapeHeight;
    //The width of the frame
    private final DoubleBinding shapeWidth;
    //The frameshape itself
    private final S frameShape;
    //The content of the frame
    private final Pane frameContent;
    //The contentPlaceholder of the frame
    private final ObservableList<Text> frameText;
    //The Text of the statement in the frame
    private final TextFlow frameTextFlow;
    //The shape of the shadow of the frame
    private final Shape predictionShadowShape;
    //Timelines for the animation of the shadown, the dots and the moving of the frame
    private final Timeline shadowShowTimeline = new Timeline();
    private final Timeline shadowRemoveTimeline = new Timeline();
    private final Timeline movingXTimeline = new Timeline();
    private final Timeline movingYTimeline = new Timeline();
    //The 1-4 output and 1-4 input dots of a frame, unmodifiable, true for output
    private final Map<Dot, Boolean> dots;
    //The Block this frame belongs to
    private final Block block;
    //The Functionsite this frame counts to
    private final FunctionSite functionSite;
    //The contextmenu for a frame
    private final FrameContextMenu menu = new FrameContextMenu(this);
    //The hoverListener for the frameshape and the dotShapes
    private final Binding<Boolean> hoverBinding;
    //Just some handy variables, see FrameMoving
    public double startX;
    public double startY;
    //The current state of the frame
    private boolean selected;
    //indicator, of this frame shows the text or the statement body
    private boolean showsPlaceholder = true;
    
    /**
     * Frames only be created by Frames.create()
     *
     * @param block
     */
    public Frame(Block block, Point2D origin) {
        this.block = block;
        this.functionSite = (FunctionSite) block.getFunction().getSite();
        root.setPickOnBounds(false);
        shapeHeight = Bindings.createDoubleBinding(() -> (double) initFrameHeight());
        shapeWidth = Bindings.createDoubleBinding(() -> (double) initFrameWidth());
        frameShape = initFrameShape();
        frameContent = initFrameContent(block);
        initFrameContentPane(frameContent);
        frameText = initFrameText();
        frameTextFlow = initTextFlow();
        root.getChildren().addAll(frameShape, frameTextFlow, frameContent);
        dots = initDots(this, block.initDotOutputSides(), block.initDotInputSides());
        dots.keySet().forEach(d -> root.getChildren().add(d.getShape()));
        hoverBinding = initHoverListeners(frameShape, dots.keySet());
        this.predictionShadowShape = createPredictionShadow(frameShape);
        MouseConsumable.registerListeners(frameShape, this);
        MouseConsumable.registerListeners(frameTextFlow, this);
        setStrokeProperties(frameShape);
        addDropShadowEffect(frameShape);
        functionSite.getLogicSite().getWorkspaceZooming().zoomFactorProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.doubleValue() > UISettings.BLOCK_ZOOMING_BODY_THRESHOLD) {
                showContentBody();
            } else {
                showContentText();
            }
        });
        showContentText();
        Frames.registerFrame(functionSite, this);
        functionSite.getLogicSite().addFrame(this, origin);
    }
    
    protected abstract int initFrameHeight();
    
    protected abstract int initFrameWidth();
    
    /**
     * Gets the shape of this frame
     *
     * @return The shape of this frame
     */
    protected abstract S initFrameShape();
    
    /**
     * Gets the Content for the frame
     *
     * @param block
     */
    protected abstract Pane initFrameContent(Block block);
    
    protected abstract ObservableList<Text> initFrameText();
    
    private void initFrameContentPane(Pane contentPane) {
        DoubleBinding contentPaneScale = Bindings.min(
                0.4,
                widthExpression()
                        .subtract(UISettings.BLOCK_CONTENT_PADDING)
                        .divide(contentPane.widthProperty())
        );
        contentPane.setVisible(false);
        contentPane.scaleXProperty().bind(contentPaneScale);
        contentPane.scaleYProperty().bind(contentPaneScale);
        contentPane.translateXProperty().bind(widthExpression().divide(2).subtract(contentPane.widthProperty().divide(2)));
        contentPane.translateYProperty().bind(heightExpression().divide(2).subtract(contentPane.heightProperty().divide(2)));
    }
    
    private TextFlow initTextFlow() {
        TextFlow result = new TextFlow();
        Bindings.bindContent(result.getChildren(), frameText);
        result.translateYProperty().bind(heightExpression().divide(2).subtract(result.heightProperty().divide(2)));
        result.prefWidthProperty().bind(widthExpression());
        result.maxWidthProperty().bind(widthExpression());
        result.setTextAlignment(TextAlignment.CENTER);
        result.setPickOnBounds(false);
        return result;
    }
    
    @Override
    public void onMousePressed(MouseEvent event) {
        Frames.setMovingFrame(functionSite, this);
    }
    
    @Override
    public void onMouseDragged(MouseEvent event) {
        menu.hide();
        if (event.isPrimaryButtonDown()) {//moving
            if (selected) {
                Frames.getAllFrames(functionSite)
                        .filter(Frame::isSelected)
                        .forEach(Frame::highlight);
            } else {
                highlight();
            }
        }
        this.root.toFront();
    }
    
    @Override
    public void onMouseReleased(MouseEvent event) {
        if (isSelected()) {
            Frames.getAllFrames(functionSite)
                    .filter(Frame::isSelected)
                    .forEach(Frame::unhighlight);
        } else {
            unhighlight();
        }
    }
    
    @Override
    public void onMouseClicked(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY && event.isStillSincePress()) {
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
    
    /**
     * @return The shape of the frame
     */
    public Shape getShape() {
        return frameShape;
    }
    
    /**
     * @return The prediction shadow of the frame
     */
    Shape getShadow() {
        return predictionShadowShape;
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
     * Checks, if this frame can move to the desired destination
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return true if this frame can move
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
        return widthExpression().get();
    }
    
    @Override
    public DoubleExpression widthExpression() {
        return shapeWidth;
    }
    
    @Override
    public double getHeight() {
        return heightExpression().get();
    }
    
    @Override
    public DoubleExpression heightExpression() {
        return shapeHeight;
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
     * Checks, if this frame can move to the desired destination
     *
     * @param x               the x coordinate
     * @param y               the y coordinate
     * @param ignoreSelection true = ignores selection (default), false = has to
     *                        be unselected, because selected ones are dragged all together
     * @return true, if the frame can move, false otherwise
     */
    boolean canMoveTo(double x, double y, boolean ignoreSelection) {
        Bounds newBounds = new BoundingBox(x, y, getWidth(), getHeight());
        return Frames
                .getAllFrames(functionSite)
                .filter(b -> ignoreSelection || !b.isSelected())
                .noneMatch(frame -> frame != this && newBounds.intersects(frame.getBounds()));
    }
    
    /**
     * @return true, if this frame has been selected by the user
     */
    public boolean isSelected() {
        return selected;
    }
    
    /**
     * @return The functionSite, in which this frame is in
     */
    public FunctionSite getFunctionSite() {
        return functionSite;
    }
    
    /**
     * Shows the smaller shadow of a selection of that frame
     */
    public void select() {
        if (!selected) {
            selected = true;
            var dropShadow = (DropShadow) frameShape.getEffect();
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
     * Removes the smaller shadow of a selection of that frame
     */
    public void unselect() {
        if (selected) {
            selected = false;
            var dropShadow = (DropShadow) frameShape.getEffect();
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
     * Shows the bigger shadow, e.g. for the movement of the frame
     */
    private void highlight() {
        var dropShadow = (DropShadow) frameShape.getEffect();
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
     * Removes the bigger shadow, e.g. for the movement of the frame
     */
    private void unhighlight() {
        var dropShadow = (DropShadow) frameShape.getEffect();
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
     * Adds this frame to a pane
     *
     * @param pane The pane to receive this frame
     */
    public void addTo(Pane pane) {
        predictionShadowShape.setTranslateX(this.root.getTranslateX());
        predictionShadowShape.setTranslateY(this.root.getTranslateY());
        pane.getChildren().addAll(predictionShadowShape, this.root);
    }
    
    /**
     * Do NOT call this Method directly, call getBlock().remove() instead!
     */
    public void remove() {
        var lines = dots.keySet()
                .stream()
                .map(Dot::getLines)
                .flatMap(Set::stream)
                .collect(Collectors.toList());
        lines.forEach(Line::remove);
        hoverBinding.dispose();
        Frames.unregisterFrame(functionSite, this);
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
        frameContent.setVisible(true);
        frameTextFlow.setVisible(false);
    }
    
    private void showContentText() {
        if (showsPlaceholder) {
            return;
        }
        showsPlaceholder = true;
        frameContent.setVisible(false);
        frameTextFlow.setVisible(true);
    }
    
    public Block getBlock() {
        return block;
    }
}
