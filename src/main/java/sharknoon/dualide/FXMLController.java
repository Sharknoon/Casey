package sharknoon.dualide;

import com.sun.scenario.effect.impl.state.LinearConvolveRenderState;
import sharknoon.dualide.Shapes;
import sharknoon.dualide.Shapes.Block;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeType;
import javafx.util.Duration;

/**
 *
 * @author Josua Frank
 */
public class FXMLController implements Initializable {

    @FXML
    private AnchorPane anchorPane;

    double oldMouseX;
    double oldMouseY;
    double oldWorkspaceX;
    double oldWorkspaceY;

    boolean mouseOverShape = false;
    //Settings
    final Duration blockShadowDuration = Duration.millis(150);
    final double blockShadowRadius = 100;
    final double zoomFactor = 1.5;
    final Duration zoomDuration = Duration.millis(100);
    final double maxWorkSpaceX = 5000;
    final double maxWorkSpaceY = 3000;
    final double gridSnappingX = 200;
    final double gridSnappingY = 100;
    final double paddingInsideWorkSpace = 50;

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
        Block end = Shapes.createEndBlock(this);
        addNewBlock(end);
    }

    private void addNewBlock(Block block) {
        anchorPane.getChildren().add(block.pane);
        block.pane.setTranslateX(paddingInsideWorkSpace);
        block.pane.setTranslateY(paddingInsideWorkSpace);
    }

    public void onMousePressedFromBlock(MouseEvent event) {
        mouseOverShape = true;
        menu.hide();
        if (event.isPrimaryButtonDown()) {//moving
            Node node = ((Node) event.getSource());
            Block block = Shapes.getBlock(node);
            oldMouseX = event.getSceneX();
            oldMouseY = event.getSceneY();
            oldWorkspaceX = block.pane.getTranslateX();
            oldWorkspaceY = block.pane.getTranslateY();

            Timeline fadeInTimeline = block.fadeInTimeline;
            Timeline fadeOutTimeline = block.fadeOutTimeline;
            fadeInTimeline.getKeyFrames().clear();
            DropShadow dropShadow = (DropShadow) block.shape.getEffect();
            fadeInTimeline.getKeyFrames().addAll(
                    new KeyFrame(Duration.ZERO,
                            new KeyValue(dropShadow.radiusProperty(), dropShadow.getRadius())
                    ),
                    new KeyFrame(blockShadowDuration,
                            new KeyValue(dropShadow.radiusProperty(), blockShadowRadius)
                    ));
            fadeOutTimeline.stop();
            fadeInTimeline.play();
        }
    }

    public void onMouseReleasedFromBlock(MouseEvent event) {
        mouseOverShape = false;
        Node node = ((Node) event.getSource());
        Block block = Shapes.getBlock(node);
        DropShadow dropShadow = (DropShadow) block.shape.getEffect();
        Timeline fadeInTimeline = block.fadeInTimeline;
        Timeline fadeOutTimeline = block.fadeOutTimeline;
        fadeOutTimeline.getKeyFrames().clear();
        fadeOutTimeline.getKeyFrames().addAll(new KeyFrame(Duration.ZERO,
                new KeyValue(dropShadow.radiusProperty(), dropShadow.getRadius())),
                new KeyFrame(blockShadowDuration,
                        new KeyValue(dropShadow.radiusProperty(), 0)
                ));

        fadeInTimeline.stop();
        fadeOutTimeline.play();
    }

    public void onMouseDraggedFromBlock(MouseEvent event) {
        if (event.isPrimaryButtonDown()) {
            Node node = ((Node) event.getSource());
            Pane pane = Shapes.getBlock(node).pane;
            double newX = event.getSceneX();
            double newY = event.getSceneY();
            double deltaX = (newX - oldMouseX) / anchorPane.getScaleX();
            double deltaY = (newY - oldMouseY) / anchorPane.getScaleY();
            double absoluteX = oldWorkspaceX + deltaX;
            double absoluteY = oldWorkspaceY + deltaY;

            if ((absoluteX - paddingInsideWorkSpace) % gridSnappingX > gridSnappingX / 2) {
                absoluteX = (absoluteX - paddingInsideWorkSpace) - ((absoluteX - paddingInsideWorkSpace) % gridSnappingX) + gridSnappingX + paddingInsideWorkSpace;
            } else {
                absoluteX = (absoluteX - paddingInsideWorkSpace) - ((absoluteX - paddingInsideWorkSpace) % gridSnappingX) + paddingInsideWorkSpace;
            }
            if ((absoluteY - paddingInsideWorkSpace) % gridSnappingY > gridSnappingY / 2) {
                absoluteY = (absoluteY - paddingInsideWorkSpace) - ((absoluteY - paddingInsideWorkSpace) % gridSnappingY) + gridSnappingY + paddingInsideWorkSpace;
            } else {
                absoluteY = (absoluteY - paddingInsideWorkSpace) - ((absoluteY - paddingInsideWorkSpace) % gridSnappingY) + paddingInsideWorkSpace;
            }

            if ((absoluteX + pane.getLayoutBounds().getWidth() + paddingInsideWorkSpace) > maxWorkSpaceX) {
                absoluteX = maxWorkSpaceX - paddingInsideWorkSpace - pane.getLayoutBounds().getWidth();
            } else if ((absoluteX - paddingInsideWorkSpace) < 0) {
                absoluteX = paddingInsideWorkSpace;
            }
            if ((absoluteY + pane.getLayoutBounds().getHeight() + paddingInsideWorkSpace) > maxWorkSpaceY) {
                absoluteY = maxWorkSpaceY - paddingInsideWorkSpace - pane.getLayoutBounds().getHeight();
            } else if ((absoluteY - paddingInsideWorkSpace) < 0) {
                absoluteY = paddingInsideWorkSpace;
            }
            pane.setTranslateX(absoluteX);
            pane.setTranslateY(absoluteY);
        }
    }

    private ContextMenu menu = new ContextMenu();

    public void onContextMenuRequestedFromBlock(ContextMenuEvent event) {
        Shape shape = (Shape) event.getSource();
        MenuItem deleteItem = new MenuItem("Delete");
        deleteItem.setOnAction(e -> System.out.println("delete"));
        MenuItem deleteItem2 = new MenuItem("Delete2");
        deleteItem2.setOnAction(e -> System.out.println("delete2"));
        menu = new ContextMenu(deleteItem, deleteItem2);
        menu.setAutoHide(true);
        menu.show(shape, event.getScreenX(), event.getScreenY());
    }

    @FXML
    private void onScroll(ScrollEvent event) {
        zoom(anchorPane, event.getDeltaY() < 0 ? 1 / zoomFactor : zoomFactor, event.getSceneX(), event.getSceneY() - 90);
    }

    @FXML
    private void onZoom(ZoomEvent event) {
        zoom(anchorPane, event.getZoomFactor(), event.getSceneX(), event.getSceneY());
    }

    private final Timeline zoomTimeline = new Timeline();

    public void zoom(Node node, double factor, double x, double y) {
        double oldAbsoluteScale = node.getScaleX();
        double newAbsoluteScale = oldAbsoluteScale * factor;
        if (newAbsoluteScale < 0.25) {
            newAbsoluteScale = 0.25;
        } else if (newAbsoluteScale > 10) {
            newAbsoluteScale = 10;
        }

        double newRelativeScale = (newAbsoluteScale / oldAbsoluteScale) - 1;
        Bounds bounds = node.localToParent(node.getBoundsInLocal());

        double dx = (x - (bounds.getWidth() / 2 + bounds.getMinX()));
        double dy = (y - (bounds.getHeight() / 2 + bounds.getMinY()));

        double deltaX = node.getTranslateX() - newRelativeScale * dx;
        double deltaY = node.getTranslateY() - newRelativeScale * dy;

        zoomTimeline.getKeyFrames().clear();
        zoomTimeline.getKeyFrames().addAll(
                new KeyFrame(zoomDuration,
                        new KeyValue(node.scaleXProperty(), newAbsoluteScale),
                        new KeyValue(node.scaleYProperty(), newAbsoluteScale),
                        new KeyValue(node.translateXProperty(), deltaX),
                        new KeyValue(node.translateYProperty(), deltaY)
                )
        );
        zoomTimeline.stop();
        zoomTimeline.play();
    }

    @FXML
    private void resetZoom() {
        anchorPane.setScaleX(1);
        anchorPane.setScaleY(1);
        anchorPane.setTranslateX(0);
        anchorPane.setTranslateY(0);
        anchorPane.getTransforms().clear();
    }

    private final Rectangle selectionRectangle = new Rectangle();

    @FXML
    private void onMousePressed(MouseEvent event) {
        if (!mouseOverShape) {
            oldMouseX = event.getSceneX();
            oldMouseY = event.getSceneY();
            oldWorkspaceX = anchorPane.getTranslateX();
            oldWorkspaceY = anchorPane.getTranslateY();
            if (event.isSecondaryButtonDown()) {
            } else if (event.isPrimaryButtonDown()) {
                selectionRectangle.setVisible(true);
                System.out.println("---");
                double newX = event.getSceneX();
                double newY = event.getSceneY() - 90;
                System.out.println(newX);
                double absoluteX = oldWorkspaceX + newX;
                System.out.println(absoluteX);
                double absoluteY = oldWorkspaceY + newY;
                selectionRectangle.setTranslateX(absoluteX);
                selectionRectangle.setTranslateY(absoluteY);
            }
        }
    }

    @FXML
    private void onDrag(MouseEvent event) {
        if (!mouseOverShape) {
            double newX = event.getSceneX();
            double newY = event.getSceneY();
            double deltaX = newX - oldMouseX;
            double deltaY = newY - oldMouseY;
            if (event.isSecondaryButtonDown()) {//Move workspace
                anchorPane.setTranslateX(oldWorkspaceX + deltaX);
                anchorPane.setTranslateY(oldWorkspaceY + deltaY);
            } else if (event.isPrimaryButtonDown()) {//create selection rectangle
                selectionRectangle.setWidth(deltaX / anchorPane.getScaleX());
                selectionRectangle.setHeight(deltaY / anchorPane.getScaleY());
            }
        }
    }

    @FXML
    private void onMouseReleased(MouseEvent event) {
        selectionRectangle.setVisible(false);
        selectionRectangle.setWidth(0);
        selectionRectangle.setHeight(0);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        createSelectionRectangle();
        drawLineAroundWorkspace();
        addNewBlock(Shapes.createStartBlock(this));
    }

    private void createSelectionRectangle() {
        selectionRectangle.setFill(Color.LIGHTBLUE);
        selectionRectangle.setOpacity(0.4);
        selectionRectangle.setStrokeWidth(1);
        selectionRectangle.setStroke(Color.BLUE);
        selectionRectangle.setVisible(false);
        anchorPane.getChildren().add(selectionRectangle);
    }

    private void drawLineAroundWorkspace() {
        Line left = createStroke(0, 0, 0, maxWorkSpaceY);
        Line right = createStroke(maxWorkSpaceX, 0, maxWorkSpaceX, maxWorkSpaceY);
        Line top = createStroke(0, 0, maxWorkSpaceX, 0);
        Line bottom = createStroke(0, maxWorkSpaceY, maxWorkSpaceX, maxWorkSpaceY);
        anchorPane.getChildren().addAll(left, right, top, bottom);
    }

    private static Line createStroke(double startX, double startY, double endX, double endY) {
        Line s = new Line(startX, startY, endX, endY);
        s.setStrokeWidth(10);
        s.setStroke(Color.BLACK);
        s.setStrokeType(StrokeType.OUTSIDE);
        return s;
    }

}
