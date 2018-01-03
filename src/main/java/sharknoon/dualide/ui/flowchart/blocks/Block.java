package sharknoon.dualide.ui.flowchart.blocks;

import sharknoon.dualide.ui.flowchart.blocks.block.Start;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeType;
import javafx.util.Duration;
import sharknoon.dualide.ui.flowchart.Line;
import sharknoon.dualide.ui.flowchart.Flowchart;
import sharknoon.dualide.ui.flowchart.UISettings;
import sharknoon.dualide.ui.flowchart.lines.Lines;

/**
 *
 * @author Josua Frank
 */
public abstract class Block implements Moveable {

    private final AnchorPane pane = new AnchorPane();
    private final Shape blockShape;
    private final Shape shadowShape;
    private final Timeline shadowShowTimeline = new Timeline();
    private final Timeline shadowRemoveTimeline = new Timeline();
    private final Timeline dotsShowTimeline = new Timeline();
    private final Timeline dotsRemoveTimeline = new Timeline();
    private final Timeline movingXTimeline = new Timeline();
    private final Timeline movingYTimeline = new Timeline();
    private final List<Circle> dots = new ArrayList<>();
    private final List<Line> lines = new ArrayList<>();
    private final Flowchart flowchart;
    private final double shapeWidth;
    private final double shapeHeight;
    private boolean selected;
    public double startX;
    public double startY;

    /**
     *
     * @param flowchart
     * @param shapeSupplier
     * @param dots 0 = bottom, 1 = top and bottom, 2 = all sides, 3 = top and
     * bottom and right, 4 = top
     */
    public Block(
            Flowchart flowchart,
            Supplier<Shape> shapeSupplier,
            Side... dots) {
        this.blockShape = shapeSupplier.get();
        shapeWidth = blockShape.getBoundsInLocal().getWidth();
        shapeHeight = blockShape.getBoundsInLocal().getHeight();
        pane.getChildren().add(blockShape);
        this.shadowShape = createShadow(blockShape);
        this.flowchart = flowchart;
        blockShape.setOnMousePressed(this::onMousePressed);
        blockShape.setOnMouseReleased(this::onMouseReleased);
        blockShape.setOnContextMenuRequested(this::onContextMenuRequested);
        blockShape.setOnMouseEntered(this::onMouseEntered);
        blockShape.setOnMouseExited(this::onMouseExited);
        setStrokeProperties(blockShape);
        addDropShadowEffect(blockShape);
        createDots(dots);
        Blocks.registerBlock(flowchart, this);
    }

    private static Shape createShadow(Shape original) {
        Shape shadow = Shape.union(original, original);
        shadow.setFill(Color.rgb(0, 0, 0, 0));
        shadow.setStroke(UISettings.predictionShadowStrokeColor);
        shadow.setStrokeWidth(UISettings.predictionShadowStrokeWidth);
        shadow.setStrokeType(StrokeType.INSIDE);
        shadow.setEffect(null);
        return shadow;
    }

    public Shape getShadow() {
        return shadowShape;
    }

    @Override
    public void setMinX(double x) {
        pane.setTranslateX(x);
    }

    public void setMinXAnimated(double x) {
        movingXTimeline.getKeyFrames().clear();
        movingXTimeline.getKeyFrames().addAll(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(pane.translateXProperty(), pane.getTranslateX())
                ),
                new KeyFrame(UISettings.blockMovingDuration,
                        new KeyValue(pane.translateXProperty(), x)
                ));
        movingXTimeline.stop();
        movingXTimeline.play();
    }

    @Override
    public void setMinY(double y) {
        pane.setTranslateY(y);
    }

    public void setMinYAnimated(double y) {
        movingYTimeline.getKeyFrames().clear();
        movingYTimeline.getKeyFrames().addAll(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(pane.translateYProperty(), pane.getTranslateY())
                ),
                new KeyFrame(UISettings.blockMovingDuration,
                        new KeyValue(pane.translateYProperty(), y)
                ));
        movingYTimeline.stop();
        movingYTimeline.play();
    }

    @Override
    public double getWidth() {
        return shapeWidth;
    }

    @Override
    public double getHeight() {
        return shapeHeight;
    }

    @Override
    public double getMinX() {
        return pane.getTranslateX();
    }

    @Override
    public double getMinY() {
        return pane.getTranslateY();
    }

    @Override
    public double getMaxX() {
        return getMinX() + getWidth();
    }

    @Override
    public double getMaxY() {
        return getMinY() + getHeight();
    }

    @Override
    public boolean canMoveTo(double x, double y) {
        return canMoveTo(x, y, true);
    }

    /**
     *
     * @param x
     * @param y
     * @param ignoreSelection true = ignores selection, false = has to be
     * unselected, because selected ones are dragged all together
     * @return
     */
    public boolean canMoveTo(double x, double y, boolean ignoreSelection) {
        Bounds newBounds = new BoundingBox(x, y, getWidth(), getHeight());
        return Blocks
                .getAllBlocks(flowchart)
                .stream()
                .filter(b -> ignoreSelection || !b.isSelected())
                .noneMatch(block -> block != this && newBounds.intersects(block.getBounds()));
    }

    public boolean isSelected() {
        return selected;
    }

    Shape getShape() {
        return blockShape;
    }

    private void createDots(Side... dotSides) {
        for (Side dotSide : dotSides) {
            Circle dot = new Circle(10, Color.BLACK);
            dot.setOpacity(0);
            dot.setOnMouseEntered(this::onMouseEntered);
            dot.setOnMouseExited(this::onMouseExited);
            dot.setOnMousePressed(this::onMousePressedOnDot);
            dot.setOnMouseReleased(this::onMouseReleasedOnDot);
            DropShadow shadow = new DropShadow(25, Color.WHITE);
            shadow.setSpread(0.5);
            dot.setEffect(shadow);
            switch (dotSide) {
                case BOTTOM:
                    dot.setCenterX(shapeWidth / 2);
                    dot.setCenterY(shapeHeight);
                    dot.setTranslateY(-UISettings.dotsMovingDistance);
                    break;
                case LEFT:
                    dot.setCenterX(0);
                    dot.setCenterY(shapeHeight / 2);
                    dot.setTranslateX(UISettings.dotsMovingDistance);
                    break;
                case RIGHT:
                    dot.setCenterX(shapeWidth);
                    dot.setCenterY(shapeHeight / 2);
                    dot.setTranslateX(-UISettings.dotsMovingDistance);
                    break;
                case TOP:
                    dot.setCenterX(shapeWidth / 2);
                    dot.setCenterY(0);
                    dot.setTranslateY(UISettings.dotsMovingDistance);
                    break;
            }
            dots.add(dot);
            pane.getChildren().add(dot);
        }
    }

    private static void setStrokeProperties(Shape shape) {
        shape.setStroke(UISettings.blockBorderStrokeColor);
        shape.setStrokeWidth(UISettings.blockBorderStrokeWidth);
        shape.setStrokeType(StrokeType.CENTERED);
    }

    private static void addDropShadowEffect(Shape shape) {
        DropShadow dropShadow = new DropShadow();
        dropShadow.setSpread(0.5);
        dropShadow.setRadius(0.0);
        dropShadow.setColor(UISettings.selectionShadowColor);
        shape.setEffect(dropShadow);
    }

    private void onMouseEntered(MouseEvent event) {
        flowchart.setMouseOverShape(true);
        if (!event.isPrimaryButtonDown()) {
            showDots();
        }
    }

    public void select() {
        if (!selected) {
            selected = true;
            shadowShowTimeline.getKeyFrames().clear();
            DropShadow dropShadow = (DropShadow) blockShape.getEffect();
            shadowShowTimeline.getKeyFrames().addAll(
                    new KeyFrame(Duration.ZERO,
                            new KeyValue(dropShadow.radiusProperty(), dropShadow.getRadius()),
                            new KeyValue(dropShadow.colorProperty(), dropShadow.getColor())
                    ),
                    new KeyFrame(UISettings.selectionShadowDuration,
                            new KeyValue(dropShadow.radiusProperty(), UISettings.selectionShadowRadius),
                            new KeyValue(dropShadow.colorProperty(), UISettings.selectionShadowColor)
                    ));
            shadowRemoveTimeline.stop();
            shadowShowTimeline.stop();
            shadowShowTimeline.play();
        }
    }

    private void onMouseExited(MouseEvent event) {
        if (!event.isPrimaryButtonDown()) {
            flowchart.setMouseOverShape(false);
        }
        removeDots();
    }

    public void unselect() {
        if (selected) {
            selected = false;
            DropShadow dropShadow = (DropShadow) blockShape.getEffect();
            shadowRemoveTimeline.getKeyFrames().clear();
            shadowRemoveTimeline.getKeyFrames().addAll(
                    new KeyFrame(Duration.ZERO,
                            new KeyValue(dropShadow.radiusProperty(), dropShadow.getRadius()),
                            new KeyValue(dropShadow.colorProperty(), dropShadow.getColor())
                    ),
                    new KeyFrame(UISettings.selectionShadowDuration,
                            new KeyValue(dropShadow.radiusProperty(), 0),
                            new KeyValue(dropShadow.colorProperty(), UISettings.selectionShadowColor)
                    )
            );
            shadowShowTimeline.stop();
            shadowRemoveTimeline.stop();
            shadowRemoveTimeline.play();
        }
    }

    double oldMouseX;
    double oldMouseY;

    public void onMousePressed(MouseEvent event) {
        flowchart.setMouseOverShape(true);
        Blocks.setCurrentBlock(flowchart, this);
        removeDots();
        menu.hide();
        if (event.isPrimaryButtonDown()) {//moving
            if (selected) {
                Blocks.getAllBlocks(flowchart).stream()
                        .filter(Block::isSelected)
                        .forEach(Block::highlight);
            } else {
                highlight();
            }
            oldMouseX = event.getSceneX();
            oldMouseY = event.getSceneY();
        }
        this.pane.toFront();
    }

    public void onMousePressedOnDot(MouseEvent event){
        flowchart.setMouseOverShape(true);
        Blocks.setCurrentBlock(flowchart, this);
        menu.hide();
        if (event.isPrimaryButtonDown()) {//connecting
            oldMouseX = event.getSceneX();
            oldMouseY = event.getSceneY();
            Line line = Lines.createLine(flowchart, this);
            Lines.setCurrentLine(flowchart, line);
        }
    }
    
    public void highlight() {
        shadowShowTimeline.getKeyFrames().clear();
        DropShadow dropShadow = (DropShadow) blockShape.getEffect();
        shadowShowTimeline.getKeyFrames().addAll(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(dropShadow.radiusProperty(), dropShadow.getRadius()),
                        new KeyValue(dropShadow.colorProperty(), dropShadow.getColor())
                ),
                new KeyFrame(UISettings.movingShadowDuration,
                        new KeyValue(dropShadow.radiusProperty(), UISettings.movingShadowRadius),
                        new KeyValue(dropShadow.colorProperty(), UISettings.movingShadowColor)
                ));
        shadowRemoveTimeline.stop();
        shadowShowTimeline.stop();
        shadowShowTimeline.play();
    }

    public void onMouseReleased(MouseEvent event) {
        if (blockShape.contains(event.getX(), event.getY())) {
            showDots();
        }
        if (selected) {
            Blocks.getAllBlocks(flowchart).stream()
                    .filter(Block::isSelected)
                    .forEach(Block::unhighlight);
        } else {
            unhighlight();
        }
        if (Math.abs(oldMouseX - event.getSceneX()) <= UISettings.blockSelectionThreshold
                && Math.abs(oldMouseY - event.getSceneY()) <= UISettings.blockSelectionThreshold) {
            select();
        }
    }
    
    public void onMouseReleasedOnDot(MouseEvent event){
        
    }

    public void unhighlight() {
        DropShadow dropShadow = (DropShadow) blockShape.getEffect();
        shadowRemoveTimeline.getKeyFrames().clear();
        shadowRemoveTimeline.getKeyFrames().addAll(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(dropShadow.radiusProperty(), dropShadow.getRadius()),
                        new KeyValue(dropShadow.colorProperty(), dropShadow.getColor())
                ),
                new KeyFrame(UISettings.movingShadowDuration,
                        new KeyValue(dropShadow.radiusProperty(), selected ? UISettings.selectionShadowRadius : 0),
                        new KeyValue(dropShadow.colorProperty(), UISettings.selectionShadowColor)
                )
        );
        shadowShowTimeline.stop();
        shadowRemoveTimeline.stop();
        shadowRemoveTimeline.play();
    }

    private ContextMenu menu = new ContextMenu();

    public void onContextMenuRequested(ContextMenuEvent event) {
        menu.getItems().clear();
        if (getClass() != Start.class) {
            MenuItem deleteItem = new MenuItem("Delete");
            deleteItem.setOnAction(e -> {
                if (selected) {
                    Collection<Block> allBlocks = Blocks.getAllBlocks(flowchart);
                    List<Block> toRemove = Blocks.getAllBlocks(flowchart).stream()
                            .filter(Block::isSelected)
                            .filter(b -> b.getClass() != Start.class)
                            .collect(Collectors.toList());
                    toRemove.forEach(Block::remove);
                } else {
                    remove();
                }
            });
            menu.getItems().add(deleteItem);
        }
        //...
        menu.setAutoHide(true);
        menu.show(blockShape, event.getScreenX(), event.getScreenY());
    }

    private void showDots() {
        dotsShowTimeline.getKeyFrames().clear();
        dots.forEach(dot -> {
            KeyValue movingStart;
            KeyValue movingEnd;
            KeyValue opacityStart = new KeyValue(dot.opacityProperty(), dot.getOpacity());
            KeyValue opacityEnd = new KeyValue(dot.opacityProperty(), 1);
            if (dot.getCenterX() == 0) {//left
                movingStart = new KeyValue(dot.translateXProperty(), dot.getTranslateX());
                movingEnd = new KeyValue(dot.translateXProperty(), 0);
            } else if (dot.getCenterY() == 0) {//top
                movingStart = new KeyValue(dot.translateYProperty(), dot.getTranslateY());
                movingEnd = new KeyValue(dot.translateYProperty(), 0);
            } else if (dot.getCenterX() < shapeWidth) {//bottom
                movingStart = new KeyValue(dot.translateYProperty(), dot.getTranslateY());
                movingEnd = new KeyValue(dot.translateYProperty(), 0);
            } else {//right
                movingStart = new KeyValue(dot.translateXProperty(), dot.getTranslateX());
                movingEnd = new KeyValue(dot.translateXProperty(), 0);
            }
            dotsShowTimeline.getKeyFrames().addAll(
                    new KeyFrame(Duration.ZERO, movingStart, opacityStart),
                    new KeyFrame(UISettings.dotsMovingDuration, movingEnd, opacityEnd)
            );
        });
        dotsRemoveTimeline.stop();
        dotsShowTimeline.stop();
        dotsShowTimeline.play();
    }

    private void removeDots() {
        dotsRemoveTimeline.getKeyFrames().clear();
        dots.forEach(dot -> {
            KeyValue movingStart;
            KeyValue movingEnd;
            KeyValue opacityStart = new KeyValue(dot.opacityProperty(), dot.getOpacity());
            KeyValue opacityEnd = new KeyValue(dot.opacityProperty(), 0);
            if (dot.getCenterX() == 0) {//left
                movingStart = new KeyValue(dot.translateXProperty(), dot.getTranslateX());
                movingEnd = new KeyValue(dot.translateXProperty(), UISettings.dotsMovingDistance);
            } else if (dot.getCenterY() == 0) {//top
                movingStart = new KeyValue(dot.translateYProperty(), dot.getTranslateY());
                movingEnd = new KeyValue(dot.translateYProperty(), UISettings.dotsMovingDistance);
            } else if (dot.getCenterX() < shapeWidth) {//bottom
                movingStart = new KeyValue(dot.translateYProperty(), dot.getTranslateY());
                movingEnd = new KeyValue(dot.translateYProperty(), -UISettings.dotsMovingDistance);
            } else {//right
                movingStart = new KeyValue(dot.translateXProperty(), dot.getTranslateX());
                movingEnd = new KeyValue(dot.translateXProperty(), -UISettings.dotsMovingDistance);
            }
            dotsRemoveTimeline.getKeyFrames().addAll(
                    new KeyFrame(Duration.ZERO, movingStart, opacityStart),
                    new KeyFrame(UISettings.dotsMovingDuration, movingEnd, opacityEnd)
            );
        });
        dotsShowTimeline.stop();
        dotsRemoveTimeline.stop();
        dotsRemoveTimeline.play();
    }

    public void addTo(Pane pane) {
        shadowShape.setTranslateX(this.pane.getTranslateX());
        shadowShape.setTranslateY(this.pane.getTranslateY());
        pane.getChildren().addAll(shadowShape, this.pane);
    }

    public void remove() {
        Blocks.unregisterBlock(flowchart, this);
        ((Pane) pane.getParent()).getChildren().removeAll(shadowShape, pane);
    }
    
}
