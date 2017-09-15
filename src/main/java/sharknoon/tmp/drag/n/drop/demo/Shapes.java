package drag.n.drop.demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
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

    public static Block createProcessBlock(FXMLDocumentController c) {
        Rectangle processRectangle = new Rectangle(PROCESS_WIDTH, PROCESS_HIGHT);
        processRectangle.setFill(Color.WHITE);
        setStrokeProperties(processRectangle);
        addDropShadowEffect(processRectangle);
        return new Block(processRectangle, c::onMousePressedFromBlock, c::onMouseReleasedFromBlock, c::onMouseDraggedFromBlock, Side.TOP, Side.BOTTOM);
    }

    public static Block createDecisionBlock(FXMLDocumentController c) {
        Polygon decisionpolygon = new Polygon(
                DECISION_WIDTH / 2, 0,//oben mitte
                DECISION_WIDTH, DECISION_HIGHT / 2,
                DECISION_WIDTH / 2, DECISION_HIGHT,
                0, DECISION_HIGHT / 2);
        decisionpolygon.setFill(Color.WHEAT);
        setStrokeProperties(decisionpolygon);
        addDropShadowEffect(decisionpolygon);
        return new Block(decisionpolygon, c::onMousePressedFromBlock, c::onMouseReleasedFromBlock, c::onMouseDraggedFromBlock, Side.TOP, Side.BOTTOM, Side.RIGHT);
    }

    public static Block createStartBlock(FXMLDocumentController c) {
        Rectangle startRectangle = new Rectangle(START_WIDTH, START_HIGHT);
        startRectangle.setArcWidth(100);
        startRectangle.setArcHeight(100);
        startRectangle.setFill(Color.LIGHTGREEN);
        setStrokeProperties(startRectangle);
        addDropShadowEffect(startRectangle);
        return new Block(startRectangle, c::onMousePressedFromBlock, c::onMouseReleasedFromBlock, c::onMouseDraggedFromBlock, Side.BOTTOM);
    }

    public static Block createEndBlock(FXMLDocumentController c) {
        Rectangle startRectangle = new Rectangle(START_WIDTH, START_HIGHT);
        startRectangle.setArcWidth(100);
        startRectangle.setArcHeight(100);
        startRectangle.setFill(Color.LIGHTCORAL);
        setStrokeProperties(startRectangle);
        addDropShadowEffect(startRectangle);
        return new Block(startRectangle, c::onMousePressedFromBlock, c::onMouseReleasedFromBlock, c::onMouseDraggedFromBlock, Side.TOP);
    }

    private static void setStrokeProperties(Shape shape) {
        shape.setStroke(Color.BLACK);
        shape.setStrokeWidth(10);
        shape.setStrokeType(StrokeType.INSIDE);
    }

    private static void addDropShadowEffect(Shape shape) {
        DropShadow dropShadow = new DropShadow();
        dropShadow.setSpread(0.5);
        dropShadow.setRadius(0.0);
        dropShadow.setColor(Color.color(0.4, 0.5, 0.5));
        shape.setEffect(dropShadow);
    }

    private static final HashMap<Node, Block> NODE_TO_BLOCK = new HashMap<>();

    public static Block getBlock(Node node) {
        return NODE_TO_BLOCK.get(node);
    }

    public static class Block {

        //Settings
        private final Duration opacityDuration = Duration.millis(150);

        public final AnchorPane pane = new AnchorPane();
        public final Shape shape;
        final Timeline fadeInTimeline = new Timeline();
        final Timeline fadeOutTimeline = new Timeline();
        final Timeline dotsShowTimeline = new Timeline();
        final Timeline dotsRemoveTimeline = new Timeline();
        final List<Circle> dots = new ArrayList<>();

        /**
         *
         * @param shape
         * @param onMousePressed
         * @param onMouseReleased
         * @param onMouseDragged
         * @param dots 0 = bottom, 1 = top and bottom, 2 = all sides, 3 = top
         * and bottom and right, 4 = top
         */
        public Block(Shape shape, Consumer<MouseEvent> onMousePressed, Consumer<MouseEvent> onMouseReleased, Consumer<MouseEvent> onMouseDragged, Side... dots) {
            pane.getChildren().add(shape);
            this.shape = shape;
            shape.setOnMousePressed(onMousePressed::accept);
            shape.setOnMouseReleased(onMouseReleased::accept);
            shape.setOnMouseDragged(onMouseDragged::accept);
            shape.setOnMouseEntered(this::onMouseEntered);
            shape.setOnMouseExited(this::onMouseExited);
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
                DropShadow shadow = new DropShadow(10, Color.WHITE);
                dot.setEffect(shadow);
                switch (dotSide) {
                    case BOTTOM:
                        dot.setCenterX(shape.getLayoutBounds().getWidth() / 2);
                        dot.setCenterY(shape.getLayoutBounds().getHeight());
                        break;
                    case LEFT:
                        dot.setCenterX(0);
                        dot.setCenterY(shape.getLayoutBounds().getHeight() / 2);
                        break;
                    case RIGHT:
                        dot.setCenterX(shape.getLayoutBounds().getWidth());
                        dot.setCenterY(shape.getLayoutBounds().getHeight() / 2);
                        break;
                    case TOP:
                        dot.setCenterX(shape.getLayoutBounds().getWidth() / 2);
                        dot.setCenterY(0);
                        break;
                }
                dots.add(dot);
                pane.getChildren().add(dot);
            }
        }

        private void onMouseEntered(MouseEvent event) {
            dotsShowTimeline.getKeyFrames().clear();
            dots.forEach(dot -> dotsShowTimeline
                    .getKeyFrames().addAll(
                            new KeyFrame(Duration.ZERO,
                                    new KeyValue(dot.opacityProperty(), dot.getOpacity())
                            ),
                            new KeyFrame(opacityDuration,
                                    new KeyValue(dot.opacityProperty(), 1)
                            )
                    )
            );
            dotsRemoveTimeline.stop();
            dotsShowTimeline.play();
        }

        private void onMouseExited(MouseEvent event) {
            dotsRemoveTimeline.getKeyFrames().clear();
            dots.forEach(dot -> dotsRemoveTimeline
                    .getKeyFrames().addAll(
                            new KeyFrame(Duration.ZERO,
                                    new KeyValue(dot.opacityProperty(), dot.getOpacity())
                            ),
                            new KeyFrame(opacityDuration,
                                    new KeyValue(dot.opacityProperty(), 0)
                            )
                    )
            );
            dotsShowTimeline.stop();
            dotsRemoveTimeline.play();
        }

    }
}
