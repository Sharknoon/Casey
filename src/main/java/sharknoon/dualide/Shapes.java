package sharknoon.dualide;

import java.security.KeyFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeType;
import javafx.util.Duration;

/**
 *
 * @author Josua Frank
 */
public class Shapes {

    private static final double PROCESS_HIGHT = 100;
    private static final double PROCESS_WIDTH = 200;
    private static final double DECISION_HIGHT = 100;
    private static final double DECISION_WIDTH = 200;
    private static final double START_HIGHT = 100;
    private static final double START_WIDTH = 200;

    public static Block createProcessBlock(Consumer<MouseEvent> onMouseDragged, Consumer<Boolean> mouseOverShape) {
        Rectangle processRectangle = new Rectangle(PROCESS_WIDTH, PROCESS_HIGHT);
        processRectangle.setFill(Color.WHITE);
        return new Block(processRectangle,
                onMouseDragged,
                mouseOverShape,
                Side.TOP, Side.BOTTOM);
    }

    public static Block createDecisionBlock(Consumer<MouseEvent> onMouseDragged, Consumer<Boolean> mouseOverShape) {
        Polygon decisionpolygon = new Polygon(
                DECISION_WIDTH / 2, 0,//oben mitte
                DECISION_WIDTH, DECISION_HIGHT / 2,
                DECISION_WIDTH / 2, DECISION_HIGHT,
                0, DECISION_HIGHT / 2);
        decisionpolygon.setFill(Color.WHEAT);
        return new Block(decisionpolygon,
                onMouseDragged,
                mouseOverShape,
                Side.TOP, Side.BOTTOM, Side.RIGHT);
    }

    public static Block createStartBlock(Consumer<MouseEvent> onMouseDragged, Consumer<Boolean> mouseOverShape) {
        Rectangle startRectangle = new Rectangle(START_WIDTH, START_HIGHT);
        startRectangle.setArcWidth(100);
        startRectangle.setArcHeight(100);
        startRectangle.setFill(Color.LIGHTGREEN);
        return new Block(startRectangle,
                onMouseDragged,
                mouseOverShape,
                Side.BOTTOM);
    }

    public static Block createEndBlock(Consumer<MouseEvent> onMouseDragged, Consumer<Boolean> mouseOverShape) {
        Rectangle startRectangle = new Rectangle(START_WIDTH, START_HIGHT);
        startRectangle.setArcWidth(100);
        startRectangle.setArcHeight(100);
        startRectangle.setFill(Color.LIGHTCORAL);
        return new Block(startRectangle,
                onMouseDragged,
                mouseOverShape,
                Side.TOP);
    }

    private static final HashMap<Node, Block> NODE_TO_BLOCK = new HashMap<>();

    public static Block getBlock(Node node) {
        return NODE_TO_BLOCK.get(node);
    }

    public static Collection<Block> getAllBlocks() {
        return NODE_TO_BLOCK.values();
    }

    public static class Block {

        //Settings
        private static final Duration DOTS_MOVING_DURATION = Duration.millis(50);
        private static final double DOTS_MOVING_DISTANCE = 5;
        private static final Duration BLOCK_SHADOW_SELECTION_DURATION = Duration.millis(50);
        private static final double BLOCK_SHADOW_SELECTION_RADIUS = 50;
        private static final Color BLOCK_SHADOW_SELECTION_COLOR = Color.CORNFLOWERBLUE;
        private static final Duration BLOCK_SHADOW_MOVING_DURATION = Duration.millis(150);
        private static final double BLOCK_SHADOW_MOVING_RADIUS = 100;
        private static final Color BLOCK_SHADOW_MOVING_COLOR = Color.valueOf("0095ed");

        public final AnchorPane pane = new AnchorPane();
        public final Shape shape;
        final Timeline shadowShowTimeline = new Timeline();
        final Timeline shadowRemoveTimeline = new Timeline();
        final Timeline dotsShowTimeline = new Timeline();
        final Timeline dotsRemoveTimeline = new Timeline();
        final List<Circle> dots = new ArrayList<>();
        boolean selected;

        Consumer<Boolean> mouseOverShape;

        /**
         *
         * @param shape
         * @param onMouseDragged
         * @param mouseOverShape
         * @param dots 0 = bottom, 1 = top and bottom, 2 = all sides, 3 = top
         * and bottom and right, 4 = top
         */
        public Block(Shape shape,
                Consumer<MouseEvent> onMouseDragged,
                Consumer<Boolean> mouseOverShape,
                Side... dots) {
            pane.getChildren().add(shape);
            this.shape = shape;
            this.mouseOverShape = mouseOverShape;
            shape.setOnMousePressed(this::onMousePressed);
            shape.setOnMouseReleased(this::onMouseReleased);
            shape.setOnMouseDragged(onMouseDragged::accept);
            shape.setOnContextMenuRequested(this::onContextMenuRequested);
            shape.setOnMouseEntered(this::onMouseEntered);
            shape.setOnMouseExited(this::onMouseExited);
            setStrokeProperties(shape);
            addDropShadowEffect(shape);
            register();
            createDots(dots);
        }

        private void register() {
            NODE_TO_BLOCK.put(shape, this);
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

        private static boolean mousePressed;

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
                highlight();
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
            unhighlight();
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
            MenuItem deleteItem = new MenuItem("Delete");
            deleteItem.setOnAction(e -> System.out.println("delete"));
            MenuItem deleteItem2 = new MenuItem("Delete2");
            deleteItem2.setOnAction(e -> System.out.println("delete2"));
            menu = new ContextMenu(deleteItem, deleteItem2);
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
    }
}
