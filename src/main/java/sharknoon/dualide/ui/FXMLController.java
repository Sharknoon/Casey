package sharknoon.dualide.ui;

import java.io.IOException;
import sharknoon.dualide.ui.blocks.Blocks;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
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
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeType;
import javafx.stage.Stage;
import javafx.util.Duration;
import sharknoon.dualide.ui.blocks.Block;
import sharknoon.dualide.ui.blocks.BlockGroup;
import sharknoon.dualide.ui.settings.FXMLSettingsSceneController;
import sharknoon.dualide.utils.settings.FileUtils;
import sharknoon.dualide.utils.settings.Props;

/**
 *
 * @author Josua Frank
 */
public class FXMLController implements Initializable {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private TabPane tabpane;

    @FXML
    Menu IDE;

    @FXML
    Button buttonAddDecision;

    @FXML
    Button buttonAddProcess;

    @FXML
    Button buttonAddEnd;

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
        Block proc = Blocks.createProcessBlock(getHandler());
        addNewBlock(proc);
    }

    @FXML
    private void addNewDecision() {
        Block dec = Blocks.createDecisionBlock(getHandler());
        addNewBlock(dec);
    }

    @FXML
    private void addNewEnd() {
        Block end = Blocks.createEndBlock(getHandler());
        addNewBlock(end);
    }

    private BlockEventHandler handler;

    private BlockEventHandler getHandler() {
        if (handler == null) {
            handler = new BlockEventHandler(
                    this::onMousePressedFromBlock,
                    this::onMouseReleasedFromBlock,
                    this::onMouseDraggedFromBlock,
                    this::onMouseEnteredFromBlock,
                    this::onMouseExitedFromBlock);
        }
        return handler;
    }

    private void addNewBlock(Block block) {
        block.setMinX(paddingInsideWorkSpace);
        block.setMinY(paddingInsideWorkSpace);
        block.addTo(anchorPane);
    }

    @FXML
    private void openSettings() {
        try {
            FXMLLoader loader = new FXMLLoader();
            Parent root = loader.load(getClass().getResource("/fxml/FXMLSettingsScene.fxml").openStream());
            FXMLSettingsSceneController controller = loader.getController();

            Scene scene = new Scene(root);
            scene.getStylesheets().add("/styles/fxml.css");

            Stage settingsStage = new Stage();
            settingsStage.setTitle(Props.get("name").orElse("Unnamed Dual Universe IDE"));
            settingsStage.setScene(scene);
            settingsStage.setMaximized(true);
            settingsStage.show();
        } catch (IOException ex) {
            Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    int startMouseGridX;
    int startMouseGridY;

    public void onMousePressedFromBlock(MouseEvent event) {
        mouseOverShape = true;
        lastDragSwitch = !lastDragSwitch;
        Block block = Blocks.getBlock((Node) event.getSource());
        if (block.isSelected()) {
            Blocks.getSelectedBlocks().forEach(b -> {
                b.tmpX = b.getMinX();
                b.tmpY = b.getMinY();
            });
        } else {
            block.tmpX = block.getMinX();
            block.tmpY = block.getMinY();
        }
        Point2D localMouseStart = anchorPane.sceneToLocal(event.getSceneX(), event.getSceneY());
        startMouseGridX = (int) ((localMouseStart.getX() - paddingInsideWorkSpace) / gridSnappingX);
        startMouseGridY = (int) ((localMouseStart.getY() - paddingInsideWorkSpace) / gridSnappingY);
    }

    Map<Block, Integer> lastX = new HashMap<>();
    Map<Block, Integer> lastY = new HashMap<>();
    boolean lastDragSwitch = false;
    boolean currentDragSwitch = true;

    public void onMouseDraggedFromBlock(MouseEvent event) {
        if (event.isPrimaryButtonDown()) {
            Block block = Blocks.getBlock((Node) event.getSource());
            Point2D localMouse = anchorPane.sceneToLocal(event.getSceneX(), event.getSceneY());
            Point2D localOldMouse = anchorPane.sceneToLocal(oldMouseX, oldMouseY);

            double mouseX = localMouse.getX();
            double mouseY = localMouse.getY();

            //dragging part
            double deltaX = mouseX - localOldMouse.getX();
            double deltaY = mouseY - localOldMouse.getY();

            if (block.isSelected()) {
                Blocks.getSelectedBlocksGroup().getBlocks().forEach(b -> {
                    b.setMinX(b.tmpX + deltaX);
                    b.setMinY(b.tmpY + deltaY);
                });
            } else {
                block.setMinX(block.tmpX + deltaX);
                block.setMinY(block.tmpY + deltaY);
            }

            //shadow part
            double mouseXWithoutPadding = mouseX - paddingInsideWorkSpace;
            double mouseYWithoutPadding = mouseY - paddingInsideWorkSpace;

            int currentMouseGridX = (int) (mouseXWithoutPadding / gridSnappingX);
            int currentMouseGridY = (int) (mouseYWithoutPadding / gridSnappingY);

            if (currentDragSwitch != lastDragSwitch || !lastX.containsKey(block)) {
                lastX.put(block, currentMouseGridX);
                lastDragSwitch = currentDragSwitch;
            }
            if (currentDragSwitch != lastDragSwitch || !lastY.containsKey(block)) {
                lastY.put(block, currentMouseGridY);
                lastDragSwitch = currentDragSwitch;
            }

            int lastMouseGridX = lastX.get(block);
            int lastMouseGridY = lastY.get(block);

            if (currentMouseGridX == lastMouseGridX && currentMouseGridY == lastMouseGridY) {
                return;
            }
            lastX.put(block, currentMouseGridX);
            lastY.put(block, currentMouseGridY);

            if (block.isSelected()) {
                HashMap<Shape, Double[]> futureShadows = new HashMap<>();
                boolean allowed = Blocks.getSelectedBlocks().stream()
                        .allMatch(b -> {
                            Shape shadow = b.getShadow();
                            double newX = b.tmpX + ((currentMouseGridX - startMouseGridX) * gridSnappingX);
                            double newY = b.tmpY + ((currentMouseGridY - startMouseGridY) * gridSnappingY);
                            futureShadows.put(shadow, new Double[]{newX, newY});
                            return isInsideWorkspace(b, newX, newY) && b.canMoveTo(newX, newY, true);
                        });
                if (allowed) {
                    futureShadows.forEach((s, c) -> {
                        s.setTranslateX(c[0]);
                        s.setTranslateY(c[1]);
                    });
                }
            } else {
                Shape shadow = block.getShadow();
                double newX = block.tmpX + ((currentMouseGridX - startMouseGridX) * gridSnappingX);
                double newY = block.tmpY + ((currentMouseGridY - startMouseGridY) * gridSnappingY);
                if (isInsideWorkspace(block, newX, newY) && block.canMoveTo(newX, newY)) {
                    shadow.setTranslateX(newX);
                    shadow.setTranslateY(newY);
                }
            }
        }
    }

    public void onMouseReleasedFromBlock(MouseEvent event) {
        Block block = Blocks.getBlock((Node) event.getSource());
        if (block.isSelected()) {
            Blocks.getSelectedBlocks().forEach(b -> {
                b.setMinXAnimated(b.getShadow().getTranslateX());
                b.setMinYAnimated(b.getShadow().getTranslateY());
            });
        } else {
            System.out.println(block.getShadow().getTranslateX());
            block.setMinXAnimated(block.getShadow().getTranslateX());
            block.setMinYAnimated(block.getShadow().getTranslateY());
        }
    }

    public void onMouseEnteredFromBlock(MouseEvent event) {
        mouseOverShape = true;
    }

    public void onMouseExitedFromBlock(MouseEvent event) {
        if (!event.isPrimaryButtonDown()) {
            mouseOverShape = false;
        }
    }

    private boolean isInsideWorkspace(Block b, double x, double y) {
        if (x < 0 + paddingInsideWorkSpace) {
            return false;
        } else if (x + b.getWidth() > maxWorkSpaceX - paddingInsideWorkSpace) {
            return false;
        } else if (y < 0 + paddingInsideWorkSpace) {
            return false;
        } else if (y + b.getHeight() > maxWorkSpaceY - paddingInsideWorkSpace) {
            return false;
        }
        return true;
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
        addNewBlock(Blocks.createStartBlock(getHandler()));
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
            //tabpane.setBackground(new Background(new BackgroundImage(image, BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));
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
