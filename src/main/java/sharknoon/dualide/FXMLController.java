package sharknoon.dualide;

import com.sun.scenario.effect.impl.state.LinearConvolveRenderState;
import sharknoon.dualide.Shapes;
import sharknoon.dualide.Shapes.Block;
import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
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
    double oldX;//used for workspace moving as well as slectrion rectangle drawing
    double oldY;

    boolean mouseOverShape = false;
    //Settings
    final double zoomFactor = 1.5;
    final Duration zoomDuration = Duration.millis(100);
    final double maxWorkSpaceX = 5000;
    final double maxWorkSpaceY = 3000;
    final double gridSnappingX = 200;
    final double gridSnappingY = 100;
    final double paddingInsideWorkSpace = 50;

    @FXML
    private void addNewProcess() {
        Block proc = Shapes.createProcessBlock(this::onMouseDraggedFromBlock, b -> mouseOverShape = b);
        addNewBlock(proc);
    }

    @FXML
    private void addNewDecision() {
        Block dec = Shapes.createDecisionBlock(this::onMouseDraggedFromBlock, b -> mouseOverShape = b);
        addNewBlock(dec);
    }

    @FXML
    private void addNewEnd() {
        Block end = Shapes.createEndBlock(this::onMouseDraggedFromBlock, b -> mouseOverShape = b);
        addNewBlock(end);
    }

    private void addNewBlock(Block block) {
        anchorPane.getChildren().add(block.pane);
        block.pane.setTranslateX(paddingInsideWorkSpace);
        block.pane.setTranslateY(paddingInsideWorkSpace);
    }

    double minX = maxWorkSpaceX;
    double minY = maxWorkSpaceY;
    double maxX = 0;
    double maxY = 0;
    double lastAbsoluteX = -1;
    double lastAbsoluteY = -1;

    public void onMouseDraggedFromBlock(MouseEvent event) {
        if (event.isPrimaryButtonDown()) {
            Node node = ((Node) event.getSource());
            Block block = Shapes.getBlock(node);
            Pane pane = block.pane;
            Point2D localCoordinates = anchorPane.sceneToLocal(event.getSceneX(), event.getSceneY());
            double absoluteX = localCoordinates.getX() - paddingInsideWorkSpace;
            double absoluteY = localCoordinates.getY() - paddingInsideWorkSpace;

            //snapping
            absoluteX -= absoluteX % gridSnappingX;
            absoluteY -= absoluteY % gridSnappingY;

            if (absoluteX == lastAbsoluteX && absoluteY == lastAbsoluteY) {
                return;//much better performance
            }

            absoluteX += paddingInsideWorkSpace;
            absoluteY += paddingInsideWorkSpace;

            if (block.selected) {//multi-selection
                double deltaX = absoluteX - pane.getTranslateX();
                double deltaY = absoluteY - pane.getTranslateY();
                //range check
                Collection<Block> allBlocks = Shapes.getAllBlocks();
                minX = maxWorkSpaceX;
                minY = maxWorkSpaceY;
                maxX = 0;
                maxY = 0;
                allBlocks.stream().forEach(b -> {
                    minX = Math.min(minX, b.pane.getTranslateX());
                    minY = Math.min(minY, b.pane.getTranslateY());
                    maxX = Math.max(maxX, b.pane.getTranslateX() + b.pane.getWidth());
                    maxY = Math.max(maxY, b.pane.getTranslateY() + b.pane.getHeight());
                });
                if (minX + deltaX >= paddingInsideWorkSpace
                        && maxX + deltaX <= maxWorkSpaceX - paddingInsideWorkSpace) {
                    allBlocks.stream()
                            .filter(b -> b.selected)
                            .map(b -> b.pane)
                            .forEach(p -> {
                                p.setTranslateX(p.getTranslateX() + deltaX);
                            });
                }
                if (minY + deltaY >= paddingInsideWorkSpace
                        && maxY + deltaY <= maxWorkSpaceY - paddingInsideWorkSpace) {
                    allBlocks.stream()
                            .filter(b -> b.selected)
                            .map(b -> b.pane)
                            .forEach(p -> {
                                p.setTranslateY(p.getTranslateY() + deltaY);
                            });
                }
            } else {//single selection
                //range check
                if (absoluteX + pane.getLayoutBounds().getWidth() + paddingInsideWorkSpace > maxWorkSpaceX) {
                    absoluteX = lastAbsoluteX;
                } else if (absoluteX - paddingInsideWorkSpace < 0) {
                    absoluteX = paddingInsideWorkSpace;
                }
                if (absoluteY + pane.getLayoutBounds().getHeight() + paddingInsideWorkSpace > maxWorkSpaceY) {
                    absoluteY = lastAbsoluteY;
                } else if (absoluteY - paddingInsideWorkSpace < 0) {
                    absoluteY = paddingInsideWorkSpace;
                }
                pane.setTranslateX(absoluteX);
                pane.setTranslateY(absoluteY);
            }
            lastAbsoluteX = absoluteX;
            lastAbsoluteY = absoluteY;
        }
    }

    @FXML
    private void onScroll(ScrollEvent event) {
        zoom(anchorPane, event.getDeltaY() < 0 ? 1 / zoomFactor : zoomFactor, event.getSceneX(), event.getSceneY());
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
        Bounds bounds = node.localToScene(node.getBoundsInLocal());

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
        oldMouseX = event.getSceneX();
        oldMouseY = event.getSceneY();
        if (!mouseOverShape) {
            if (event.isSecondaryButtonDown()) {
                oldX = anchorPane.getTranslateX();
                oldY = anchorPane.getTranslateY();
            } else if (event.isPrimaryButtonDown()) {
                Point2D localCoordinates = anchorPane.sceneToLocal(event.getSceneX(), event.getSceneY());
                if (localCoordinates.getX() < 0 || localCoordinates.getX() > maxWorkSpaceX
                        || localCoordinates.getY() < 0 || localCoordinates.getY() > maxWorkSpaceY) {
                    return;
                }
                selectionRectangle.setVisible(true);
                oldX = localCoordinates.getX();
                oldY = localCoordinates.getY();

                selectionRectangle.setTranslateX(oldX);
                selectionRectangle.setTranslateY(oldY);
            }
        }
    }

    @FXML
    private void onDrag(MouseEvent event) {
        if (!mouseOverShape) {
            double deltaX = event.getSceneX() - oldMouseX;
            double deltaY = event.getSceneY() - oldMouseY;
            if (event.isSecondaryButtonDown()) {//Move workspace
                anchorPane.setTranslateX(oldX + deltaX);
                anchorPane.setTranslateY(oldY + deltaY);
            } else if (event.isPrimaryButtonDown()) {//create selection rectangle
                double width = deltaX / anchorPane.getScaleX();
                double hight = deltaY / anchorPane.getScaleY();
                Point2D localCoordinates = anchorPane.sceneToLocal(event.getSceneX(), event.getSceneY());
                double localX = localCoordinates.getX();
                double localY = localCoordinates.getY();
                if (oldX + width > maxWorkSpaceX) {
                    width = maxWorkSpaceX - oldX;
                } else if (oldX + width < 0) {
                    width = -oldX;
                }
                if (oldY + hight > maxWorkSpaceY) {
                    hight = maxWorkSpaceY - oldY;
                } else if (oldY + hight < 0) {
                    hight = -oldY;
                }

                double translateX;
                double translateY;
                if (width > 0) {
                    translateX = oldX;
                } else {
                    if (localX < 0) {
                        localX = 0;
                    }
                    translateX = localX;
                    width = -width;
                }
                if (hight > 0) {
                    translateY = oldY;
                } else {
                    if (localY < 0) {
                        localY = 0;
                    }
                    translateY = localY;
                    hight = -hight;
                }

                selectionRectangle.setTranslateX(translateX);
                selectionRectangle.setTranslateY(translateY);
                selectionRectangle.setWidth(width);
                selectionRectangle.setHeight(hight);

                final double finalWidth = width;
                final double finalHight = hight;

                Shapes.getAllBlocks().stream().forEach(b -> {
                    if (b.pane.getTranslateX() > translateX
                            && b.pane.getTranslateY() > translateY
                            && b.pane.getTranslateX() + b.pane.getWidth() < translateX + finalWidth
                            && b.pane.getTranslateY() + b.pane.getHeight() < translateY + finalHight) {
                        b.select();
                    } else {
                        b.unselect();
                    }
                });
            }
        }
    }

    @FXML
    private void onMouseReleased(MouseEvent event) {
        selectionRectangle.setVisible(false);
        selectionRectangle.setWidth(0);
        selectionRectangle.setHeight(0);
        if (!mouseOverShape && oldMouseX == event.getSceneX() && oldMouseY == event.getSceneY()) {
            Shapes.getAllBlocks().forEach(b -> b.unselect());
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        createSelectionRectangle();
        drawLineAroundWorkspace();
        addNewBlock(Shapes.createStartBlock(this::onMouseDraggedFromBlock, b -> mouseOverShape = b));
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
