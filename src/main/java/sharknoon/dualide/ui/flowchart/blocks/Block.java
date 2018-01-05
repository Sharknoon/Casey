package sharknoon.dualide.ui.flowchart.blocks;

import sharknoon.dualide.ui.flowchart.dots.Dot;
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
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeType;
import javafx.util.Duration;
import sharknoon.dualide.ui.flowchart.lines.Line;
import sharknoon.dualide.ui.flowchart.Flowchart;
import sharknoon.dualide.ui.flowchart.UISettings;
import sharknoon.dualide.ui.flowchart.dots.Dots;
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
    private final Timeline dotsHideTimeline = new Timeline();
    private final Timeline movingXTimeline = new Timeline();
    private final Timeline movingYTimeline = new Timeline();
    private final List<Dot> dots = new ArrayList<>();
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
            Dot dot = Dot.createDot(this, dotSide);
            dot.addTo(pane);
            dots.add(dot);
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

    void onMouseEntered(MouseEvent event) {
        Blocks.setMouseOverBlock(this);
        if (!event.isPrimaryButtonDown() || Lines.isLineDrawing()) {
            showDots();
        }
    }

    /**
     * Shows the smaller shadow of a selection of that block
     */
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

    void onMouseExited(MouseEvent event) {
        if (!event.isPrimaryButtonDown()) {
            Blocks.removeMouseOverBlock();
        }
        if (!Lines.isLineDrawing() && !Dots.isMouseOverDot()) {
            hideDots();
        }
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

    public double oldMouseX;
    public double oldMouseY;

    public void onMousePressed(MouseEvent event) {
        Blocks.setMouseOverBlock(this);
        Blocks.setWorkingBlock(flowchart, this);
        hideDots();
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

    /**
     * Shows the bigger shadow, e.g. for the movement of the block
     */
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
        Lines.removeLineDrawing();
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
        if (Blocks.getWorkingBlock(flowchart) == this && !event.isControlDown()) {//Klick on this block
            Blocks.getSelectedBlocks(flowchart).stream().filter(b -> b != this).forEach(Block::unselect);
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

    public void showDots() {
        dotsShowTimeline.getKeyFrames().clear();
        dots.forEach(dot -> {
            dotsShowTimeline.getKeyFrames().addAll(dot.show());
        });
        dotsHideTimeline.stop();
        dotsShowTimeline.stop();
        dotsShowTimeline.play();
    }

    public void hideDots() {
        dotsHideTimeline.getKeyFrames().clear();
        dots.forEach(dot -> {
            dotsHideTimeline.getKeyFrames().addAll(dot.hide());
        });
        dotsShowTimeline.stop();
        dotsHideTimeline.stop();
        dotsHideTimeline.play();
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

    public Flowchart getFlowchart() {
        return flowchart;
    }

    public void toFront() {
        pane.toFront();
    }

}
