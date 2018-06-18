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
import sharknoon.dualide.ui.MainController;
import sharknoon.dualide.ui.sites.function.blocks.Block;
import sharknoon.dualide.ui.sites.function.blocks.Blocks;
import sharknoon.dualide.ui.misc.MouseConsumable;
import sharknoon.dualide.ui.sites.function.blocks.BlockType;
import sharknoon.dualide.ui.sites.function.lines.Lines;

/**
 *
 * @author Josua Frank
 */
public class FunctionSiteLogic implements MouseConsumable {

    private final FunctionSite functionSite;

    private final AnchorPane root = new AnchorPane();
    private final WorkspaceSelection ws;
    private final BlockMoving bm;
    private final WorkspaceMoving wm;
    private final WorkspaceZooming wz;
    private final WorkspaceContextMenu wc;
    private final LineDrawing ld;

    public FunctionSiteLogic(FunctionSite functionSite) {
        this.functionSite = functionSite;
        this.ws = new WorkspaceSelection(functionSite);
        this.bm = new BlockMoving(functionSite);
        this.wm = new WorkspaceMoving(functionSite);
        this.wz = new WorkspaceZooming(functionSite);
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

    Pane getRoot() {
        return root;
    }

    @Override
    public void onMousePressed(MouseEvent event) {
        if (!Blocks.isMouseOverBlock(functionSite) && !Lines.isMouseOverLine(functionSite)) {
            if (event.isPrimaryButtonDown()) {
                ws.onMousePressed(event);
            } else if (event.isMiddleButtonDown()) {
                wm.onMousePressed(event);
            }
        } else if (Blocks.isMouseOverBlock(functionSite)) {
            if (event.isPrimaryButtonDown()) {
                bm.onMousePressed(event);
            }
        }
        wc.onMousePressed(event);
    }

    @Override
    public void onMouseDragged(MouseEvent event) {
        if (!BlockMoving.isDragging(functionSite)) {
            if (event.isPrimaryButtonDown()) {
                ws.onMouseDragged(event);
            } else if (event.isMiddleButtonDown()) {
                wm.onMouseDragged(event);
            }
        } else {
            if (event.isPrimaryButtonDown()) {
                bm.onMouseDragged(event);
            }
        }
    }

    @Override
    public void onMouseReleased(MouseEvent event) {
        if (!Blocks.isMouseOverBlock(functionSite)) {
            ws.onMouseReleased(event);

        } else {
            bm.onMouseReleased(event);
        }
    }

    @Override
    public void onMouseMoved(MouseEvent event) {
        if (Lines.isLineDrawing(functionSite)) {
            ld.onMouseMoved(event);
        }
    }

    @Override
    public void onContextMenuRequested(ContextMenuEvent event) {
        wc.onContextMenuRequested(event);
    }

    @Override
    public void onScroll(ScrollEvent event) {
        wz.onScroll(event);
    }

    public void onZoom(ZoomEvent event) {
        wz.onZoom(event);
    }

    private boolean initialized = false;

    private void init() {
        drawLineAroundWorkspace();
        centerWorkspaceView();
        if (!startBlockAlreadyAdded) {
            addBlock(BlockType.START, new Point2D(-1, -1));
        }
        initialized = true;
    }

    private boolean startBlockAlreadyAdded = false;

    public Block addBlock(BlockType type, Point2D origin) {
        return addBlock(type, origin, null);
    }

    public Block addBlock(BlockType type, Point2D origin, String id) {
        if (type == BlockType.START) {
            if (startBlockAlreadyAdded) {
                return null;
            }
            startBlockAlreadyAdded = true;
        }
          var block = Blocks.createBlock(functionSite, type, id);
        if (origin.getX() < 0 || origin.getY() < 0) {
              var screenHeight = Screen.getPrimary().getVisualBounds().getHeight();
            double x = (UISettings.WORKSPACE_MAX_X / 2) - (block.getWidth() / 2);
            double y = (UISettings.WORKSPACE_MAX_Y / 2) - (screenHeight / 2) + UISettings.BLOCK_GRID_SNAPPING_Y;
            origin = new Point2D(x, y);
        }

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
        return block;
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

    Pane getTabContentPane() {
        if (!initialized) {
            init();
        }
        return root;
    }

    public WorkspaceSelection getWs() {
        return ws;
    }

    public BlockMoving getBm() {
        return bm;
    }

    public WorkspaceMoving getWm() {
        return wm;
    }

    public WorkspaceZooming getWz() {
        return wz;
    }

    public WorkspaceContextMenu getWc() {
        return wc;
    }

    public LineDrawing getLd() {
        return ld;
    }
}
