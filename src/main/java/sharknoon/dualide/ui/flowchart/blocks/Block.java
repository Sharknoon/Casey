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
import sharknoon.dualide.ui.flowchart.BlockEventHandler;
import sharknoon.dualide.ui.flowchart.Flowchart;

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
    private static final Color SHADOW_STROKE_COLOR = Color.GREY;
    private static final double SHADOW_STROKE_WIDTH = 5;

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
    private final Flowchart flowchart;
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
        shadow.setStroke(SHADOW_STROKE_COLOR);
        shadow.setStrokeWidth(SHADOW_STROKE_WIDTH);
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
                new KeyFrame(BLOCK_MOVING_DURATION,
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
                new KeyFrame(BLOCK_MOVING_DURATION,
                        new KeyValue(pane.translateYProperty(), y)
                ));
        movingYTimeline.stop();
        movingYTimeline.play();
    }

    @Override
    public double getWidth() {
        return blockShape.getLayoutBounds().getWidth();
    }

    @Override
    public double getHeight() {
        return blockShape.getLayoutBounds().getHeight();
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
            DropShadow shadow = new DropShadow(25, Color.WHITE);
            shadow.setSpread(0.5);
            dot.setEffect(shadow);
            switch (dotSide) {
                case BOTTOM:
                    dot.setCenterX(blockShape.getLayoutBounds().getWidth() / 2);
                    dot.setCenterY(blockShape.getLayoutBounds().getHeight());
                    dot.setTranslateY(-DOTS_MOVING_DISTANCE);
                    break;
                case LEFT:
                    dot.setCenterX(0);
                    dot.setCenterY(blockShape.getLayoutBounds().getHeight() / 2);
                    dot.setTranslateX(DOTS_MOVING_DISTANCE);
                    break;
                case RIGHT:
                    dot.setCenterX(blockShape.getLayoutBounds().getWidth());
                    dot.setCenterY(blockShape.getLayoutBounds().getHeight() / 2);
                    dot.setTranslateX(-DOTS_MOVING_DISTANCE);
                    break;
                case TOP:
                    dot.setCenterX(blockShape.getLayoutBounds().getWidth() / 2);
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
    }

    public void highlight() {
        shadowShowTimeline.getKeyFrames().clear();
        DropShadow dropShadow = (DropShadow) blockShape.getEffect();
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
        if (oldMouseX == event.getSceneX() && oldMouseY == event.getSceneY()) {
            select();
        }
    }

    public void unhighlight() {
        DropShadow dropShadow = (DropShadow) blockShape.getEffect();
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
            } else if (dot.getCenterX() < blockShape.getLayoutBounds().getWidth()) {//bottom
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
            } else if (dot.getCenterX() < blockShape.getLayoutBounds().getWidth()) {//bottom
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
        shadowShape.setTranslateX(this.pane.getTranslateX());
        shadowShape.setTranslateY(this.pane.getTranslateY());
        pane.getChildren().addAll(shadowShape, this.pane);
    }

    public void remove() {
        Blocks.unregisterBlock(flowchart, this);
        ((Pane) pane.getParent()).getChildren().remove(pane);
    }
}
