package sharknoon.dualide.ui.blocks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Side;
import javafx.scene.Node;
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

/**
 *
 * @author Josua Frank
 */
public abstract class Block implements Moveable {

    //Settings
    private static final Duration DOTS_MOVING_DURATION = Duration.millis(50);
    private static final double DOTS_MOVING_DISTANCE = 5;
    private static final Duration BLOCK_SHADOW_SELECTION_DURATION = Duration.millis(50);
    private static final double BLOCK_SHADOW_SELECTION_RADIUS = 50;
    private static final Color BLOCK_SHADOW_SELECTION_COLOR = Color.CORNFLOWERBLUE;
    private static final Duration BLOCK_SHADOW_MOVING_DURATION = Duration.millis(150);
    private static final double BLOCK_SHADOW_MOVING_RADIUS = 100;
    private static final Color BLOCK_SHADOW_MOVING_COLOR = Color.valueOf("0095ed");
    private static final Duration BLOCK_MOVING_DURATION = Duration.millis(50);

    private final AnchorPane pane = new AnchorPane();
    private final Shape shape;
    public final Timeline shadowShowTimeline = new Timeline();
    public final Timeline shadowRemoveTimeline = new Timeline();
    public final Timeline dotsShowTimeline = new Timeline();
    public final Timeline dotsRemoveTimeline = new Timeline();
    public final Timeline movingXTimeline = new Timeline();
    public final Timeline movingYTimeline = new Timeline();
    public final List<Circle> dots = new ArrayList<>();
    private boolean selected;
    private static boolean mousePressed;
    private final Consumer<Boolean> mouseOverShape;

    /**
     *
     * @param shapeSupplier
     * @param onMouseDragged
     * @param mouseOverShape
     * @param dots 0 = bottom, 1 = top and bottom, 2 = all sides, 3 = top and
     * bottom and right, 4 = top
     */
    public Block(
            Consumer<MouseEvent> onMouseDragged,
            Consumer<Boolean> mouseOverShape,
            Supplier<Shape> shapeSupplier,
            Side... dots) {
        this.shape = shapeSupplier.get();
        pane.getChildren().add(shape);
        this.mouseOverShape = mouseOverShape;
        shape.setOnMousePressed(this::onMousePressed);
        shape.setOnMouseReleased(this::onMouseReleased);
        shape.setOnMouseDragged(onMouseDragged::accept);
        shape.setOnContextMenuRequested(this::onContextMenuRequested);
        shape.setOnMouseEntered(this::onMouseEntered);
        shape.setOnMouseExited(this::onMouseExited);
        setStrokeProperties(shape);
        addDropShadowEffect(shape);
        createDots(dots);
        Blocks.registerBlock(this);
    }

    @Override
    public void setMinX(double x) {
        movingXTimeline.getKeyFrames().clear();
        movingXTimeline.getKeyFrames().addAll(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(pane.translateXProperty(), pane.getTranslateX())
                ),
                new KeyFrame(BLOCK_MOVING_DURATION,
                        new KeyValue(pane.translateXProperty(), x)
                ));
        movingXTimeline.stop();
        movingXTimeline.play();
    }

    @Override
    public void setMinY(double y) {
        movingYTimeline.getKeyFrames().clear();
        movingYTimeline.getKeyFrames().addAll(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(pane.translateYProperty(), pane.getTranslateY())
                ),
                new KeyFrame(BLOCK_MOVING_DURATION,
                        new KeyValue(pane.translateYProperty(), y)
                ));
        movingYTimeline.stop();
        movingYTimeline.play();
    }

    @Override
    public double getWidth() {
        return shape.getLayoutBounds().getWidth();
    }

    @Override
    public double getHeight() {
        return shape.getLayoutBounds().getHeight();
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
                .getAllBlocks()
                .stream()
                .filter(b -> ignoreSelection || !b.isSelected())
                .noneMatch(block -> block != this && newBounds.intersects(block.getBounds()));
    }

    public boolean isSelected() {
        return selected;
    }

    Shape getShape() {
        return shape;
    }

    private void createDots(Side... dotSides) {
        for (Side dotSide : dotSides) {
            Circle dot = new Circle(10, Color.BLACK);
            dot.setOpacity(0);
            dot.setOnMouseEntered(this::onMouseEntered);
            dot.setOnMouseExited(this::onMouseExited);
            DropShadow shadow = new DropShadow(25, Color.WHITE);
            shadow.setSpread(0.5);
            dot.setEffect(shadow);
            switch (dotSide) {
                case BOTTOM:
                    dot.setCenterX(shape.getLayoutBounds().getWidth() / 2);
                    dot.setCenterY(shape.getLayoutBounds().getHeight());
                    dot.setTranslateY(-DOTS_MOVING_DISTANCE);
                    break;
                case LEFT:
                    dot.setCenterX(0);
                    dot.setCenterY(shape.getLayoutBounds().getHeight() / 2);
                    dot.setTranslateX(DOTS_MOVING_DISTANCE);
                    break;
                case RIGHT:
                    dot.setCenterX(shape.getLayoutBounds().getWidth());
                    dot.setCenterY(shape.getLayoutBounds().getHeight() / 2);
                    dot.setTranslateX(-DOTS_MOVING_DISTANCE);
                    break;
                case TOP:
                    dot.setCenterX(shape.getLayoutBounds().getWidth() / 2);
                    dot.setCenterY(0);
                    dot.setTranslateY(DOTS_MOVING_DISTANCE);
                    break;
            }
            dots.add(dot);
            pane.getChildren().add(dot);
        }
    }

    private static void setStrokeProperties(Shape shape) {
        shape.setStroke(Color.BLACK);
        shape.setStrokeWidth(3);
        shape.setStrokeType(StrokeType.INSIDE);
    }

    private static void addDropShadowEffect(Shape shape) {
        DropShadow dropShadow = new DropShadow();
        dropShadow.setSpread(0.5);
        dropShadow.setRadius(0.0);
        dropShadow.setColor(BLOCK_SHADOW_SELECTION_COLOR);
        shape.setEffect(dropShadow);
    }

    private void onMouseEntered(MouseEvent event) {
        mouseOverShape.accept(true);
        if (!mousePressed) {
            showDots();
        }
    }

    public void select() {
        if (!selected) {
            selected = true;
            shadowShowTimeline.getKeyFrames().clear();
            DropShadow dropShadow = (DropShadow) shape.getEffect();
            shadowShowTimeline.getKeyFrames().addAll(
                    new KeyFrame(Duration.ZERO,
                            new KeyValue(dropShadow.radiusProperty(), dropShadow.getRadius()),
                            new KeyValue(dropShadow.colorProperty(), dropShadow.getColor())
                    ),
                    new KeyFrame(BLOCK_SHADOW_SELECTION_DURATION,
                            new KeyValue(dropShadow.radiusProperty(), BLOCK_SHADOW_SELECTION_RADIUS),
                            new KeyValue(dropShadow.colorProperty(), BLOCK_SHADOW_SELECTION_COLOR)
                    ));
            shadowRemoveTimeline.stop();
            shadowShowTimeline.stop();
            shadowShowTimeline.play();
        }
    }

    private void onMouseExited(MouseEvent event) {
        removeDots();
        if (!mousePressed) {
            mouseOverShape.accept(false);
        }
    }

    public void unselect() {
        if (selected) {
            selected = false;
            DropShadow dropShadow = (DropShadow) shape.getEffect();
            shadowRemoveTimeline.getKeyFrames().clear();
            shadowRemoveTimeline.getKeyFrames().addAll(
                    new KeyFrame(Duration.ZERO,
                            new KeyValue(dropShadow.radiusProperty(), dropShadow.getRadius()),
                            new KeyValue(dropShadow.colorProperty(), dropShadow.getColor())
                    ),
                    new KeyFrame(BLOCK_SHADOW_SELECTION_DURATION,
                            new KeyValue(dropShadow.radiusProperty(), 0),
                            new KeyValue(dropShadow.colorProperty(), BLOCK_SHADOW_SELECTION_COLOR)
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
        removeDots();
        mouseOverShape.accept(true);
        menu.hide();
        if (event.isPrimaryButtonDown()) {//moving
            mousePressed = true;
            if (selected) {
                Blocks.getAllBlocks().stream()
                        .filter(Block::isSelected)
                        .forEach(Block::highlight);
            } else {
                highlight();
            }
            oldMouseX = event.getSceneX();
            oldMouseY = event.getSceneY();
        }
    }

    public void highlight() {
        shadowShowTimeline.getKeyFrames().clear();
        DropShadow dropShadow = (DropShadow) shape.getEffect();
        shadowShowTimeline.getKeyFrames().addAll(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(dropShadow.radiusProperty(), dropShadow.getRadius()),
                        new KeyValue(dropShadow.colorProperty(), dropShadow.getColor())
                ),
                new KeyFrame(BLOCK_SHADOW_MOVING_DURATION,
                        new KeyValue(dropShadow.radiusProperty(), BLOCK_SHADOW_MOVING_RADIUS),
                        new KeyValue(dropShadow.colorProperty(), BLOCK_SHADOW_MOVING_COLOR)
                ));
        shadowRemoveTimeline.stop();
        shadowShowTimeline.stop();
        shadowShowTimeline.play();
    }

    public void onMouseReleased(MouseEvent event) {
        showDots();
        mousePressed = false;
        if (selected) {
            Blocks.getAllBlocks().stream()
                    .filter(Block::isSelected)
                    .forEach(Block::unhighlight);
        } else {
            unhighlight();
        }
        if (oldMouseX == event.getSceneX() && oldMouseY == event.getSceneY()) {
            select();
        }
    }

    public void unhighlight() {
        DropShadow dropShadow = (DropShadow) shape.getEffect();
        shadowRemoveTimeline.getKeyFrames().clear();
        shadowRemoveTimeline.getKeyFrames().addAll(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(dropShadow.radiusProperty(), dropShadow.getRadius()),
                        new KeyValue(dropShadow.colorProperty(), dropShadow.getColor())
                ),
                new KeyFrame(BLOCK_SHADOW_MOVING_DURATION,
                        new KeyValue(dropShadow.radiusProperty(), selected ? BLOCK_SHADOW_SELECTION_RADIUS : 0),
                        new KeyValue(dropShadow.colorProperty(), BLOCK_SHADOW_SELECTION_COLOR)
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
                    Collection<Block> allBlocks = Blocks.getAllBlocks();
                    List<Block> toRemove = Blocks.getAllBlocks().stream()
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
        menu.show(shape, event.getScreenX(), event.getScreenY());
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
            } else if (dot.getCenterX() < shape.getLayoutBounds().getWidth()) {//bottom
                movingStart = new KeyValue(dot.translateYProperty(), dot.getTranslateY());
                movingEnd = new KeyValue(dot.translateYProperty(), 0);
            } else {//right
                movingStart = new KeyValue(dot.translateXProperty(), dot.getTranslateX());
                movingEnd = new KeyValue(dot.translateXProperty(), 0);
            }
            dotsShowTimeline.getKeyFrames().addAll(
                    new KeyFrame(Duration.ZERO, movingStart, opacityStart),
                    new KeyFrame(DOTS_MOVING_DURATION, movingEnd, opacityEnd)
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
                movingEnd = new KeyValue(dot.translateXProperty(), DOTS_MOVING_DISTANCE);
            } else if (dot.getCenterY() == 0) {//top
                movingStart = new KeyValue(dot.translateYProperty(), dot.getTranslateY());
                movingEnd = new KeyValue(dot.translateYProperty(), DOTS_MOVING_DISTANCE);
            } else if (dot.getCenterX() < shape.getLayoutBounds().getWidth()) {//bottom
                movingStart = new KeyValue(dot.translateYProperty(), dot.getTranslateY());
                movingEnd = new KeyValue(dot.translateYProperty(), -DOTS_MOVING_DISTANCE);
            } else {//right
                movingStart = new KeyValue(dot.translateXProperty(), dot.getTranslateX());
                movingEnd = new KeyValue(dot.translateXProperty(), -DOTS_MOVING_DISTANCE);
            }
            dotsRemoveTimeline.getKeyFrames().addAll(
                    new KeyFrame(Duration.ZERO, movingStart, opacityStart),
                    new KeyFrame(DOTS_MOVING_DURATION, movingEnd, opacityEnd)
            );
        });
        dotsShowTimeline.stop();
        dotsRemoveTimeline.stop();
        dotsRemoveTimeline.play();
    }

    public void addTo(Pane pane) {
        pane.getChildren().add(this.pane);
    }

    public void remove() {
        Blocks.unregisterBlock(this);
        ((Pane) pane.getParent()).getChildren().remove(pane);
    }
}
