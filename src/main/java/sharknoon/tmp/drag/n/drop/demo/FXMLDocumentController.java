package drag.n.drop.demo;

import drag.n.drop.demo.Shapes.Block;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeType;
import javafx.util.Duration;

/**
 *
 * @author Josua Frank
 */
public class FXMLDocumentController implements Initializable {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    Label label;

    double oldMouseX;
    double oldMouseY;
    double oldImageX;
    double oldImageY;

    boolean mouseOverShape = false;
    //Settings
    final Duration fadeDuration = Duration.millis(150);
    final double fadeRadius = 100;
    final double zoomFactor = 1.5;
    final double workSpaceX = 5000;
    final double workSpaceY = 3000;
    final double gridSnappingX = 200;
    final double gridSnappingY = 100;
    final double paddingWorkSpace = 200;

    @FXML
    private void addNewProcess() {
        Block proc = Shapes.createProcessBlock(this);
        addNewBlock(proc);
    }

    @FXML
    private void addNewDecision() {
        Block dec = Shapes.createDecisionBlock(this);
        addNewBlock(dec);
    }

    @FXML
    private void addNewEnd() {
        Block dec = Shapes.createEndBlock(this);
        addNewBlock(dec);
    }

    private void addNewBlock(Block block) {
        anchorPane.getChildren().add(block.pane);
        block.pane.setTranslateX(paddingWorkSpace);
        block.pane.setTranslateY(paddingWorkSpace);
    }

    public void onMousePressedFromBlock(MouseEvent event) {
        mouseOverShape = true;
        oldMouseX = event.getSceneX();
        oldMouseY = event.getSceneY();
        Node node = ((Node) event.getSource());
        Block block = Shapes.getBlock(node);
        oldImageX = block.pane.getTranslateX();
        oldImageY = block.pane.getTranslateY();

        Timeline fadeInTimeline = block.fadeInTimeline;
        Timeline fadeOutTimeline = block.fadeOutTimeline;
        fadeInTimeline.getKeyFrames().clear();
        DropShadow dropShadow = (DropShadow) block.shape.getEffect();
        fadeInTimeline.getKeyFrames().addAll(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(dropShadow.radiusProperty(), dropShadow.getRadius())),
                new KeyFrame(fadeDuration,
                        new KeyValue(dropShadow.radiusProperty(), fadeRadius)));

        fadeOutTimeline.stop();
        fadeInTimeline.play();
    }

    public void onMouseReleasedFromBlock(MouseEvent event) {
        mouseOverShape = false;
        Node node = ((Node) event.getSource());
        Block block = Shapes.getBlock(node);
        DropShadow dropShadow = (DropShadow) block.shape.getEffect();
        Timeline fadeInTimeline = block.fadeInTimeline;
        Timeline fadeOutTimeline = block.fadeOutTimeline;
        fadeOutTimeline.getKeyFrames().clear();
        fadeOutTimeline.getKeyFrames().addAll(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(dropShadow.radiusProperty(), dropShadow.getRadius())),
                new KeyFrame(fadeDuration,
                        new KeyValue(dropShadow.radiusProperty(), 0)
                ));

        fadeInTimeline.stop();
        fadeOutTimeline.play();
    }

    public void onMouseDraggedFromBlock(MouseEvent event) {
        Node node = ((Node) event.getSource());
        Pane pane = Shapes.getBlock(node).pane;
        double newX = event.getSceneX();
        double newY = event.getSceneY();
        double deltaX = (newX - oldMouseX) / anchorPane.getScaleX();
        double deltaY = (newY - oldMouseY) / anchorPane.getScaleY();
        double absoluteX = oldImageX + deltaX;
        double absoluteY = oldImageY + deltaY;

        if ((absoluteX - paddingWorkSpace) % gridSnappingX > gridSnappingX / 2) {
            absoluteX = (absoluteX - paddingWorkSpace) - ((absoluteX - paddingWorkSpace) % gridSnappingX) + gridSnappingX + paddingWorkSpace;
        } else {
            absoluteX = (absoluteX - paddingWorkSpace) - ((absoluteX - paddingWorkSpace) % gridSnappingX) + paddingWorkSpace;
        }
        if ((absoluteY - paddingWorkSpace) % gridSnappingY > gridSnappingY / 2) {
            absoluteY = (absoluteY - paddingWorkSpace) - ((absoluteY - paddingWorkSpace) % gridSnappingY) + gridSnappingY + paddingWorkSpace;
        } else {
            absoluteY = (absoluteY - paddingWorkSpace) - ((absoluteY - paddingWorkSpace) % gridSnappingY) + paddingWorkSpace;
        }

        if ((absoluteX + pane.getLayoutBounds().getWidth() + paddingWorkSpace) > workSpaceX) {
            absoluteX = workSpaceX - paddingWorkSpace - pane.getLayoutBounds().getWidth();
        } else if ((absoluteX - paddingWorkSpace) < 0) {
            absoluteX = paddingWorkSpace;
        }
        if ((absoluteY + pane.getLayoutBounds().getHeight() + paddingWorkSpace) > workSpaceY) {
            absoluteY = workSpaceY - paddingWorkSpace - pane.getLayoutBounds().getHeight();
        } else if ((absoluteY - paddingWorkSpace) < 0) {
            absoluteY = paddingWorkSpace;
        }
        pane.setTranslateX(absoluteX);
        pane.setTranslateY(absoluteY);
    }

    @FXML
    private void onScroll(ScrollEvent event) {
        zoom(anchorPane, event.getDeltaY() < 0 ? 1 / zoomFactor : zoomFactor, event.getSceneX(), event.getSceneY() - 90);
    }

    @FXML
    private void onZoom(ZoomEvent event) {
        zoom(anchorPane, event.getZoomFactor(), event.getSceneX(), event.getSceneY());
    }

    public static void zoom(Node node, double factor, double x, double y) {
        double oldAbsoluteScale = node.getScaleX();
        double newAbsoluteScale = oldAbsoluteScale * factor;
        if (newAbsoluteScale < 0.05) {
            newAbsoluteScale = 0.05;
        }
        if (newAbsoluteScale > 50) {
            newAbsoluteScale = 50;
        }
        node.setScaleX(newAbsoluteScale);
        node.setScaleY(newAbsoluteScale);

        double newRelativeScale = (newAbsoluteScale / oldAbsoluteScale) - 1;
        Bounds bounds = node.localToParent(node.getBoundsInLocal());

        double dx = (x - (bounds.getWidth() / 2 + bounds.getMinX()));
        double dy = (y - (bounds.getHeight() / 2 + bounds.getMinY()));

        node.setTranslateX(node.getTranslateX() - newRelativeScale * dx);
        node.setTranslateY(node.getTranslateY() - newRelativeScale * dy);
    }

    @FXML
    private void resetZoom() {
        anchorPane.setScaleX(1);
        anchorPane.setScaleY(1);
        anchorPane.setTranslateX(0);
        anchorPane.setTranslateY(0);
        anchorPane.getTransforms().clear();
    }

    @FXML
    private void onMousePressed(MouseEvent event) {
        if (!mouseOverShape) {
            oldMouseX = event.getSceneX();
            oldMouseY = event.getSceneY();
            oldImageX = anchorPane.getTranslateX();
            oldImageY = anchorPane.getTranslateY();
        }
    }

    @FXML
    private void onDrag(MouseEvent event) {
        if (!mouseOverShape) {
            double newX = event.getSceneX();
            double newY = event.getSceneY();
            double deltaX = newX - oldMouseX;
            double deltaY = newY - oldMouseY;
            anchorPane.setTranslateX(oldImageX + deltaX);
            anchorPane.setTranslateY(oldImageY + deltaY);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Line left = createStroke(0, 0, 0, workSpaceY);
        Line right = createStroke(workSpaceX, 0, workSpaceX, workSpaceY);
        Line top = createStroke(0, 0, workSpaceX, 0);
        Line bottom = createStroke(0, workSpaceY, workSpaceX, workSpaceY);
        anchorPane.getChildren().addAll(left, right, top, bottom);

        Block start = Shapes.createStartBlock(this);
        addNewBlock(start);
    }

    private static Line createStroke(double startX, double startY, double endX, double endY) {
        Line s = new Line(startX, startY, endX, endY);
        s.setStrokeWidth(10);
        s.setStroke(Color.BLACK);
        s.setStrokeType(StrokeType.OUTSIDE);
        return s;
    }

}
