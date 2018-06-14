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

import sharknoon.dualide.ui.sites.function.lines.LineDrawing;
import sharknoon.dualide.ui.sites.function.blocks.BlockMoving;
import java.util.concurrent.CompletableFuture;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.TabPane;
import javafx.scene.input.ContextMenuEvent;
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
import sharknoon.dualide.logic.items.Function;
import sharknoon.dualide.ui.MainController;
import sharknoon.dualide.ui.misc.Icon;
import sharknoon.dualide.ui.sites.Site;
import sharknoon.dualide.ui.sites.function.blocks.Block;
import sharknoon.dualide.ui.sites.function.blocks.Blocks;
import sharknoon.dualide.ui.sites.function.blocks.MouseConsumable;
import sharknoon.dualide.ui.sites.function.dots.Dots;
import sharknoon.dualide.ui.sites.function.lines.Lines;

/**
 *
 * @author Josua Frank
 */
public class FunctionSiteLogic implements MouseConsumable {

    private final FunctionSite functionSite;

    private final AnchorPane root = new AnchorPane();
    private final Timeline zoomTimeline = new Timeline();
    private final Selection s;
    private final BlockMoving bm;
    private final WorkspaceMoving wm;
    private final WorkspaceContextMenu wc;
    private final LineDrawing ld;

    public FunctionSiteLogic(FunctionSite functionSite) {
        this.functionSite = functionSite;
        this.s = new Selection(functionSite);
        this.bm = new BlockMoving(functionSite);
        this.wm = new WorkspaceMoving(root);
        this.wc = new WorkspaceContextMenu(functionSite);
        this.ld = new LineDrawing(functionSite);

        MouseConsumable.registerListeners(root, this);
    }

    public void addInFront(Node node) {
        root.getChildren().add(node);
    }

    public void addInBack(Node node) {
        root.getChildren().add(0, node);
    }

    public void remove(Node node) {
        root.getChildren().remove(node);
    }

    @Override
    public void onMouseEntered(MouseEvent event) {
    }

    @Override
    public void onMouseExited(MouseEvent event) {
    }

    @Override
    public void onMousePressed(MouseEvent event) {
        if (!Blocks.isMouseOverBlock(functionSite) && !Lines.isMouseOverLine(functionSite)) {
            if (event.isPrimaryButtonDown()) {
                s.onMousePressed(root.sceneToLocal(event.getSceneX(), event.getSceneY()));
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

    @Override
    public void onMouseDragged(MouseEvent event) {
        if (!BlockMoving.isDragging(functionSite)) {
            if (event.isPrimaryButtonDown()) {
                s.onMouseDragged(root.sceneToLocal(event.getSceneX(), event.getSceneY()));
            } else if (event.isMiddleButtonDown()) {
                wm.onMouseDragged(event.getSceneX(), event.getSceneY());
            }
        } else {
            if (event.isPrimaryButtonDown()) {
                bm.onMouseDragged(root.sceneToLocal(event.getSceneX(), event.getSceneY()));
            }
        }
    }

    @Override
    public void onMouseReleased(MouseEvent event) {
        if (!Blocks.isMouseOverBlock(functionSite)) {
            s.onMouseReleased(event);

        } else {
            bm.onMouseReleased();
        }
    }

    @Override
    public void onMouseMoved(MouseEvent event) {
        if (Lines.isLineDrawing(functionSite)) {
            ld.onMouseMoved(root.sceneToLocal(event.getSceneX(), event.getSceneY()));
        }
    }

    @Override
    public void onMouseClicked(MouseEvent event) {
    }

    @Override
    public void onContextMenuRequested(ContextMenuEvent event) {
        wc.onContextMenuRequested(
                root.sceneToLocal(event.getSceneX(), event.getSceneY()),
                new Point2D(event.getScreenX(), event.getScreenY()),
                (Node) event.getSource());
    }

    @Override
    public void onScroll(ScrollEvent event) {
        zoom(root, event.getDeltaY() < 0 ? 1 / UISettings.WORKSPACE_ZOOM_FACTOR : UISettings.WORKSPACE_ZOOM_FACTOR, event.getSceneX(), event.getSceneY());
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
                new KeyFrame(UISettings.WORKSPACE_ZOOM_DURATION,
                        new KeyValue(node.scaleXProperty(), newAbsoluteScale),
                        new KeyValue(node.scaleYProperty(), newAbsoluteScale),
                        new KeyValue(node.translateXProperty(), deltaX),
                        new KeyValue(node.translateYProperty(), deltaY)
                )
        );
        Platform.runLater(() -> {
            zoomTimeline.stop();
            zoomTimeline.play();
        });
    }

    private boolean initialized = false;

    private void init() {
        s.init();
        drawLineAroundWorkspace();
        centerWorkspaceView();
        if (!startBlockAlreadyAdded) {
            addStartBlock(new Point2D(-1, -1));
        }
        initialized = true;
    }

    private boolean startBlockAlreadyAdded = false;

    public Block addStartBlock(Point2D origin) {
        if (startBlockAlreadyAdded) {
            return null;
        }
          var startBlock = Blocks.createStartBlock(functionSite);
        if (origin.getX() < 0 || origin.getY() < 0) {
              var screenHeight = Screen.getPrimary().getVisualBounds().getHeight();
            double x = (UISettings.WORKSPACE_MAX_X / 2) - (startBlock.getWidth() / 2);
            double y = (UISettings.WORKSPACE_MAX_Y / 2) - (screenHeight / 2) + UISettings.BLOCK_GRID_SNAPPING_Y;
            origin = new Point2D(x, y);
        }
        addBlock(startBlock, origin);
        startBlockAlreadyAdded = true;
        return startBlock;
    }

    public Block addEndBlock(Point2D origin) {
          var endBlock = Blocks.createEndBlock(functionSite);
        addBlock(endBlock, origin);
        return endBlock;
    }

    public Block addDecisionBlock(Point2D origin) {
          var decisionBlock = Blocks.createDecisionBlock(functionSite);
        addBlock(decisionBlock, origin);
        return decisionBlock;
    }

    public Block addAssignmentBlock(Point2D origin) {
          var assignmentBlock = Blocks.createAssignmentBlock(functionSite);
        addBlock(assignmentBlock, origin);
        return assignmentBlock;
    }

    public Block addCallBlock(Point2D origin) {
          var assignmentBlock = Blocks.createCallBlock(functionSite);
        addBlock(assignmentBlock, origin);
        return assignmentBlock;
    }

    public Block addInputBlock(Point2D origin) {
          var assignmentBlock = Blocks.createInputBlock(functionSite);
        addBlock(assignmentBlock, origin);
        return assignmentBlock;
    }

    public Block addOutputBlock(Point2D origin) {
          var assignmentBlock = Blocks.createOutputBlock(functionSite);
        addBlock(assignmentBlock, origin);
        return assignmentBlock;
    }

    private void addBlock(Block block, Point2D origin) {
        double minX = origin.getX() - UISettings.WORKSPACE_PADDING;
        minX -= (minX % UISettings.BLOCK_GRID_SNAPPING_X);
        minX += UISettings.WORKSPACE_PADDING;
        double minY = origin.getY() - UISettings.WORKSPACE_PADDING;
        minY -= (minY % UISettings.BLOCK_GRID_SNAPPING_Y);
        minY += UISettings.WORKSPACE_PADDING;

        int counter = 1;
        byte amountMoved = 0;
        boolean secondMove = false;
        int side = 0;
        double newX = minX;
        double newY = minY;

        while (!Blocks.isSpaceFree(block, newX, newY)) {
            switch (side) {
                case 0://Upwards
                    newY = newY - UISettings.BLOCK_GRID_SNAPPING_Y;
                    break;
                case 1://Right
                    newX = newX + UISettings.BLOCK_GRID_SNAPPING_X;
                    break;
                case 2://Downwards
                    newY = newY + UISettings.BLOCK_GRID_SNAPPING_Y;
                    break;
                case 3://Left
                    newX = newX - UISettings.BLOCK_GRID_SNAPPING_X;
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
        TabPane tabPane = MainController.getTabPane();
        double maxWidth = tabPane.getWidth();
        double maxHeight = tabPane.getHeight();
        root.setTranslateX(-((UISettings.WORKSPACE_MAX_X / 2) - (maxWidth / 2)));
        root.setTranslateY(-((UISettings.WORKSPACE_MAX_Y / 2) - (maxHeight / 2)) + 100);
    }

    private void drawLineAroundWorkspace() {
        Line left = createStroke(0, 0, 0, UISettings.WORKSPACE_MAX_Y);
        Line right = createStroke(UISettings.WORKSPACE_MAX_X, 0, UISettings.WORKSPACE_MAX_X, UISettings.WORKSPACE_MAX_Y);
        Line top = createStroke(0, 0, UISettings.WORKSPACE_MAX_X, 0);
        Line bottom = createStroke(0, UISettings.WORKSPACE_MAX_Y, UISettings.WORKSPACE_MAX_X, UISettings.WORKSPACE_MAX_Y);
        root.getChildren().addAll(left, right, top, bottom);
        root.setBackground(new Background(new BackgroundFill(UISettings.WORKSPACE_BACKGROUND_COLOR, CornerRadii.EMPTY, Insets.EMPTY)));
    }

    private static Line createStroke(double startX, double startY, double endX, double endY) {
        Line s = new Line(startX, startY, endX, endY);
        s.setStrokeWidth(UISettings.WORKSPACE_LINE_WIDTH);
        s.setStroke(UISettings.WORKSPACE_LINE_COLOR);
        s.setStrokeType(StrokeType.OUTSIDE);
        return s;
    }

    public Pane getTabContentPane() {
        if (!initialized) {
            init();
        }
        return root;
    }

}