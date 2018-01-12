/*
 * Copyright 2018 Shark Industries.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package sharknoon.dualide.ui.sites.function;

import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeItem;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeType;
import javafx.stage.Screen;
import sharknoon.dualide.logic.Function;
import sharknoon.dualide.logic.Item;
import sharknoon.dualide.logic.Variable;
import sharknoon.dualide.ui.sites.Site;
import sharknoon.dualide.ui.sites.clazz.ClassSite;
import sharknoon.dualide.ui.sites.function.blocks.Block;
import sharknoon.dualide.ui.sites.function.blocks.Blocks;
import sharknoon.dualide.ui.sites.function.lines.Lines;
import sharknoon.dualide.utils.javafx.RecursiveTreeItem;
import sharknoon.dualide.utils.language.Language;
import sharknoon.dualide.utils.language.Word;

/**
 *
 * @author Josua Frank
 */
public class FunctionSite extends Site<Function> {

    private final AnchorPane root = new AnchorPane();
    private final Timeline zoomTimeline = new Timeline();
    private final Selection bs = new Selection(this);
    private final BlockMoving bm = new BlockMoving(this);
    private final WorkspaceMoving wm = new WorkspaceMoving(root);
    private final WorkspaceContextMenu wc = new WorkspaceContextMenu(this);
    private final Keyboard kb = new Keyboard(this);
    private final LineDrawing ld = new LineDrawing(this);

    public FunctionSite(Function item) {
        super(item);
        init();
    }

    public void add(Node node) {
        root.getChildren().add(node);
    }

    public void remove(Node node) {
        root.getChildren().remove(node);
    }

    public void onMousePressed(MouseEvent event) {
        if (!Blocks.isMouseOverBlock()) {
            if (event.isPrimaryButtonDown()) {
                bs.onMousePressed(root.sceneToLocal(event.getSceneX(), event.getSceneY()));
            } else if (event.isMiddleButtonDown()) {
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
        if (!Blocks.isMouseOverBlock()) {
            if (event.isPrimaryButtonDown()) {
                bs.onMouseDragged(root.sceneToLocal(event.getSceneX(), event.getSceneY()));
            } else if (event.isMiddleButtonDown()) {
                wm.onMouseDragged(event.getSceneX(), event.getSceneY());
            }
        } else {
            if (event.isPrimaryButtonDown()) {
                bm.onMouseDragged(root.sceneToLocal(event.getSceneX(), event.getSceneY()));
            }
        }
    }

    public void onMouseReleased(MouseEvent event) {
        if (!Blocks.isMouseOverBlock()) {
            bs.onMouseReleased(root.sceneToLocal(event.getSceneX(), event.getSceneY()));

        } else {
            bm.onMouseReleased();
        }
    }

    public void onMouseMoved(MouseEvent event) {
        if (Lines.isLineDrawing()) {
            ld.onMouseMoved(root.sceneToLocal(event.getSceneX(), event.getSceneY()));
        }
    }

    public void onMouseClicked(MouseEvent event) {
        if (Lines.isLineDrawing()) {
            ld.onMouseClicked(root.sceneToLocal(event.getSceneX(), event.getSceneY()));
        }
    }

    public void onContextMenuRequested(ContextMenuEvent event) {
        wc.onContextMenuRequested(
                root.sceneToLocal(event.getSceneX(), event.getSceneY()),
                new Point2D(event.getScreenX(), event.getScreenY()),
                (Node) event.getSource());
    }

    public void onKeyReleased(KeyEvent event) {
        kb.onKeyReleased(event);
    }

    public void onScroll(ScrollEvent event) {
        zoom(root, event.getDeltaY() < 0 ? 1 / UISettings.zoomFactor : UISettings.zoomFactor, event.getSceneX(), event.getSceneY());
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
                new KeyFrame(UISettings.zoomDuration,
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
        kb.init();
        drawLineAroundWorkspace();
        centerWorkspaceView();
        addStartBlock();
    }

    private void addStartBlock() {
        Block startBlock = Blocks.createStartBlock(this);
        double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();
        double minX = (UISettings.maxWorkSpaceX / 2) - (startBlock.getWidth() / 2);
        double minY = (UISettings.maxWorkSpaceY / 2) - (screenHeight / 2) + UISettings.blockGridSnappingY;
        addBlock(startBlock, new Point2D(minX, minY));
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
        double minX = origin.getX() - UISettings.paddingInsideWorkSpace;
        minX -= (minX % UISettings.blockGridSnappingX);
        minX += UISettings.paddingInsideWorkSpace;
        double minY = origin.getY() - UISettings.paddingInsideWorkSpace;
        minY -= (minY % UISettings.blockGridSnappingY);
        minY += UISettings.paddingInsideWorkSpace;

        int counter = 1;
        byte amountMoved = 0;
        boolean secondMove = false;
        int side = 0;
        double newX = minX;
        double newY = minY;

        while (!Blocks.isSpaceFree(block, newX, newY)) {
            switch (side) {
                case 0://Upwards
                    newY = newY - UISettings.blockGridSnappingY;
                    break;
                case 1://Right
                    newX = newX + UISettings.blockGridSnappingX;
                    break;
                case 2://Downwards
                    newY = newY + UISettings.blockGridSnappingY;
                    break;
                case 3://Left
                    newX = newX - UISettings.blockGridSnappingX;
                    break;
            }
            amountMoved++;
            if (amountMoved >= counter) {
                side = side > 2 ? 0 : side + 1;
                amountMoved = 0;
                if (secondMove) {
                    counter++;
                }
                secondMove = !secondMove;
            }
        }

        block.setMinX(newX);
        block.setMinY(newY);
        block.addTo(root);
    }

    private void centerWorkspaceView() {
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        double screenWidth = bounds.getWidth();
        double screenHeight = bounds.getHeight();
        root.setTranslateX(-((UISettings.maxWorkSpaceX / 2) - (screenWidth / 2)));
        root.setTranslateY(-((UISettings.maxWorkSpaceY / 2) - (screenHeight / 2)));
    }

    private void drawLineAroundWorkspace() {
        Line left = createStroke(0, 0, 0, UISettings.maxWorkSpaceY);
        Line right = createStroke(UISettings.maxWorkSpaceX, 0, UISettings.maxWorkSpaceX, UISettings.maxWorkSpaceY);
        Line top = createStroke(0, 0, UISettings.maxWorkSpaceX, 0);
        Line bottom = createStroke(0, UISettings.maxWorkSpaceY, UISettings.maxWorkSpaceX, UISettings.maxWorkSpaceY);
        root.getChildren().addAll(left, right, top, bottom);
        root.setBackground(new Background(new BackgroundFill(UISettings.workspaceBackgroundColor, CornerRadii.EMPTY, Insets.EMPTY)));
    }

    private static Line createStroke(double startX, double startY, double endX, double endY) {
        Line s = new Line(startX, startY, endX, endY);
        s.setStrokeWidth(UISettings.workspaceLineWidth);
        s.setStroke(UISettings.workspaceLineColor);
        s.setStrokeType(StrokeType.OUTSIDE);
        return s;
    }

    @Override
    public Pane getTabContentPane() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getTabName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Node getTabIcon() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
