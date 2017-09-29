package sharknoon.dualide.ui.flowchart;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
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
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeType;
import javafx.stage.Screen;
import sharknoon.dualide.ui.MainController;
import sharknoon.dualide.ui.flowchart.blocks.Block;
import sharknoon.dualide.ui.flowchart.blocks.Blocks;
import sharknoon.dualide.ui.flowchart.blocks.block.Start;
import sharknoon.dualide.utils.settings.FileUtils;

/**
 *
 * @author Josua Frank
 */
public class Flowchart {

    private final AnchorPane root = new AnchorPane();
    private final Timeline zoomTimeline = new Timeline();
    private final BlockSelection bs = new BlockSelection(this);
    private final BlockMoving bm = new BlockMoving(this);
    private final WorkspaceMoving wm = new WorkspaceMoving(root);
    private final WorkspaceContextMenu wc = new WorkspaceContextMenu(this);
    private boolean mouseOverShape = false;

    public Flowchart(Tab tab) {
        tab.setContent(root);
        init();
    }

    public void add(Node node) {
        root.getChildren().add(node);
    }

    public boolean isMouseOverShape() {
        return mouseOverShape;
    }

    public void setMouseOverShape(boolean mouseOverShape) {
        this.mouseOverShape = mouseOverShape;
    }

    public void onMousePressed(MouseEvent event) {
        if (!mouseOverShape) {
            if (event.isPrimaryButtonDown()) {
                bs.onMousePressed(root.sceneToLocal(event.getSceneX(), event.getSceneY()));
            } else if (event.isSecondaryButtonDown()) {
                wm.onMousePressed(event.getSceneX(), event.getSceneY());
            }
        } else {
            if (event.isPrimaryButtonDown()) {
                bm.onMousePressed(root.sceneToLocal(event.getSceneX(), event.getSceneY()));
            }
        }
        wc.onMousePressed(new Point2D(event.getScreenX(), event.getScreenY()));
    }

    public void onMouseDragged(MouseEvent event) {
        if (!mouseOverShape) {
            if (event.isPrimaryButtonDown()) {
                bs.onMouseDragged(root.sceneToLocal(event.getSceneX(), event.getSceneY()));
            } else if (event.isSecondaryButtonDown()) {
                wm.onMouseDragged(event.getSceneX(), event.getSceneY());
            }
        } else {
            if (event.isPrimaryButtonDown()) {
                bm.onMouseDragged(root.sceneToLocal(event.getSceneX(), event.getSceneY()));
            }
        }
    }

    public void onMouseReleased(MouseEvent event) {
        if (!mouseOverShape) {
            bs.onMouseReleased(root.sceneToLocal(event.getSceneX(), event.getSceneY()));
        } else {
            bm.onMouseReleased();
        }
    }

    public void onContextMenuRequested(ContextMenuEvent event) {
        wc.onContextMenuRequested(
                root.sceneToLocal(event.getSceneX(), event.getSceneY()),
                new Point2D(event.getScreenX(), event.getScreenY()),
                (Node) event.getSource());
    }

    public void onScroll(ScrollEvent event) {
        zoom(root, event.getDeltaY() < 0 ? 1 / Settings.zoomFactor : Settings.zoomFactor, event.getSceneX(), event.getSceneY());
    }

    public void onZoom(ZoomEvent event) {
        zoom(root, event.getZoomFactor(), event.getSceneX(), event.getSceneY());
    }

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
                new KeyFrame(Settings.zoomDuration,
                        new KeyValue(node.scaleXProperty(), newAbsoluteScale),
                        new KeyValue(node.scaleYProperty(), newAbsoluteScale),
                        new KeyValue(node.translateXProperty(), deltaX),
                        new KeyValue(node.translateYProperty(), deltaY)
                )
        );
        zoomTimeline.stop();
        zoomTimeline.play();
    }

    private void init() {
        bs.init();
        bm.init();
        wm.init();
        wc.init();
        drawLineAroundWorkspace();
        centerWorkspaceView();
        addStartBlock();
    }

    private void addStartBlock() {
        Block startBlock = Blocks.createStartBlock(this);
        double screneHeight = Screen.getPrimary().getVisualBounds().getHeight();
        double minX = (Settings.maxWorkSpaceX / 2) - (startBlock.getWidth() / 2);
        double minY = (Settings.maxWorkSpaceY / 2) - (screneHeight / 2) + Settings.gridSnappingY;
        double x = minX - (minX % Settings.gridSnappingX) + Settings.paddingInsideWorkSpace;
        double y = minY - (minY % Settings.gridSnappingY) + Settings.paddingInsideWorkSpace;
        addBlock(startBlock, new Point2D(x, y));
    }

    public void addEndBlock(Point2D origin) {
        Block endBlock = Blocks.createEndBlock(this);
        addBlock(endBlock, origin);
    }

    public void addDecisionBlock(Point2D origin) {
        Block decisionBlock = Blocks.createDecisionBlock(this);
        addBlock(decisionBlock, origin);
    }

    public void addProcessBlock(Point2D origin) {
        Block processBlock = Blocks.createProcessBlock(this);
        addBlock(processBlock, origin);
    }

    private void addBlock(Block block, Point2D origin) {
        block.setMinX(origin.getX());
        block.setMinY(origin.getY());
        block.addTo(root);
    }

    private void centerWorkspaceView() {
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        double screenWidth = bounds.getWidth();
        double screenHeight = bounds.getHeight();
        root.setTranslateX(-((Settings.maxWorkSpaceX / 2) - (screenWidth / 2)));
        root.setTranslateY(-((Settings.maxWorkSpaceY / 2) - (screenHeight / 2)));
    }

    private void drawLineAroundWorkspace() {
        Line left = createStroke(0, 0, 0, Settings.maxWorkSpaceY);
        Line right = createStroke(Settings.maxWorkSpaceX, 0, Settings.maxWorkSpaceX, Settings.maxWorkSpaceY);
        Line top = createStroke(0, 0, Settings.maxWorkSpaceX, 0);
        Line bottom = createStroke(0, Settings.maxWorkSpaceY, Settings.maxWorkSpaceX, Settings.maxWorkSpaceY);
        root.getChildren().addAll(left, right, top, bottom);
        root.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 0, 0.75), CornerRadii.EMPTY, Insets.EMPTY)));
    }

    private static Line createStroke(double startX, double startY, double endX, double endY) {
        Line s = new Line(startX, startY, endX, endY);
        s.setStrokeWidth(Settings.workspaceLineWidth);
        s.setStroke(Settings.workspaceLineColor);
        s.setStrokeType(StrokeType.OUTSIDE);
        return s;
    }

}
