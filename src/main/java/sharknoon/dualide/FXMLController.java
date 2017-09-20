package sharknoon.dualide;

import com.sun.scenario.effect.impl.state.LinearConvolveRenderState;
import java.io.IOException;
import sharknoon.dualide.ui.blocks.Blocks;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalTime;
import java.util.Collection;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TabPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeType;
import javafx.util.Duration;
import sharknoon.dualide.ui.blocks.Block;
import sharknoon.dualide.ui.blocks.BlockGroup;
import sharknoon.dualide.utils.settings.FileUtils;

/**
 *
 * @author Josua Frank
 */
public class FXMLController implements Initializable {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private TabPane tabpane;

    double oldMouseX;
    double oldMouseY;
    double oldX;//used for workspace moving as well as slection rectangle drawing
    double oldY;

    boolean mouseOverShape = false;
    //Settings
    final double zoomFactor = 1.5;
    final Duration zoomDuration = Duration.millis(100);
    final double maxWorkSpaceX = 5000;
    final double maxWorkSpaceY = 3000;
    final double gridSnappingX = 100;
    final double gridSnappingY = 100;
    final double paddingInsideWorkSpace = 50;

    @FXML
    private void addNewProcess() {
        Block proc = Blocks.createProcessBlock(this::onMouseDraggedFromBlock, b -> mouseOverShape = b);
        addNewBlock(proc);
    }

    @FXML
    private void addNewDecision() {
        Block dec = Blocks.createDecisionBlock(this::onMouseDraggedFromBlock, b -> mouseOverShape = b);
        addNewBlock(dec);
    }

    @FXML
    private void addNewEnd() {
        Block end = Blocks.createEndBlock(this::onMouseDraggedFromBlock, b -> mouseOverShape = b);
        addNewBlock(end);
    }

    private void addNewBlock(Block block) {
        block.addTo(anchorPane);
        block.setMinX(paddingInsideWorkSpace);
        block.setMinY(paddingInsideWorkSpace);
    }

    double lastAbsoluteX = -1;
    double lastAbsoluteY = -1;

    public void onMouseDraggedFromBlock(MouseEvent event) {
        if (event.isPrimaryButtonDown()) {
            Node node = ((Node) event.getSource());
            Block block = Blocks.getBlock(node);
            Point2D localCoordinates = anchorPane.sceneToLocal(event.getSceneX(), event.getSceneY());
            double absoluteX = localCoordinates.getX() - paddingInsideWorkSpace;
            double absoluteY = localCoordinates.getY() - paddingInsideWorkSpace;

            //snapping
            absoluteX -= absoluteX % gridSnappingX;
            absoluteY -= absoluteY % gridSnappingY;
            absoluteX += paddingInsideWorkSpace;
            absoluteY += paddingInsideWorkSpace;

            if (absoluteX == lastAbsoluteX && absoluteY == lastAbsoluteY) {
                return;//much better performance
            }

            if (block.isSelected()) {//multi-selection
                double deltaX = absoluteX - block.getMinX();
                double deltaY = absoluteY - block.getMinY();
                //range check
                BlockGroup selectedBlocks = Blocks.getSelectedBlocks();
                Bounds bounds = selectedBlocks.getBounds();
                if (bounds.getMinX() + deltaX < paddingInsideWorkSpace
                        || bounds.getMaxX() + deltaX > maxWorkSpaceX - paddingInsideWorkSpace) {
                    deltaX = 0;
                }
                if (bounds.getMinY() + deltaY < paddingInsideWorkSpace
                        || bounds.getMaxY() + deltaY > maxWorkSpaceY - paddingInsideWorkSpace) {
                    deltaY = 0;
                }
                final double finalDeltaX = deltaX;
                final double finalDeltaY = deltaY;
                //Existing block check
                if (selectedBlocks.canMoveTo(deltaX, deltaY)) {
                    selectedBlocks.getBlocks().forEach(b -> {
                        b.setMinX(b.getMinX() + finalDeltaX);
                        b.setMinY(b.getMinY() + finalDeltaY);
                    });
                }
            } else {//single selection
                //range check
                if (absoluteX + block.getWidth() + paddingInsideWorkSpace > maxWorkSpaceX) {
                    absoluteX = lastAbsoluteX;
                } else if (absoluteX - paddingInsideWorkSpace < 0) {
                    absoluteX = paddingInsideWorkSpace;
                }
                if (absoluteY + block.getHeight() + paddingInsideWorkSpace > maxWorkSpaceY) {
                    absoluteY = lastAbsoluteY;
                } else if (absoluteY - paddingInsideWorkSpace < 0) {
                    absoluteY = paddingInsideWorkSpace;
                }
                //Existing block check
                if (block.canMoveTo(absoluteX, absoluteY)) {
                    //Final translation setting
                    block.setMinX(absoluteX);
                    block.setMinY(absoluteY);
                }
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

                Blocks.getAllBlocks().stream().forEach(b -> {
                    if (b.getMinX() > translateX
                            && b.getMinY() > translateY
                            && b.getMinX() + b.getWidth() < translateX + finalWidth
                            && b.getMinY() + b.getHeight() < translateY + finalHight) {
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
            Blocks.unselectAll();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        createSelectionRectangle();
        drawLineAroundWorkspace();
        addNewBlock(Blocks.createStartBlock(this::onMouseDraggedFromBlock, b -> mouseOverShape = b));
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
        try {
            Line left = createStroke(0, 0, 0, maxWorkSpaceY);
            Line right = createStroke(maxWorkSpaceX, 0, maxWorkSpaceX, maxWorkSpaceY);
            Line top = createStroke(0, 0, maxWorkSpaceX, 0);
            Line bottom = createStroke(0, maxWorkSpaceY, maxWorkSpaceX, maxWorkSpaceY);
            anchorPane.getChildren().addAll(left, right, top, bottom);

            Path path = FileUtils.getFile("images/landscape.jpg", true).orElse(null);
            Image image = new Image(Files.newInputStream(path));
            tabpane.setBackground(new Background(new BackgroundImage(image, BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));
            anchorPane.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 0, 0.75), CornerRadii.EMPTY, Insets.EMPTY)));
        } catch (IOException ex) {
            Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static Line createStroke(double startX, double startY, double endX, double endY) {
        Line s = new Line(startX, startY, endX, endY);
        s.setStrokeWidth(3);
        s.setStroke(Color.BLACK);
        s.setStrokeType(StrokeType.OUTSIDE);
        return s;
    }

}
