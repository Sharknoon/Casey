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
package sharknoon.casey.ide.ui.sites.function;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.TabPane;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import sharknoon.casey.ide.logic.blocks.BlockType;
import sharknoon.casey.ide.logic.blocks.Blocks;
import sharknoon.casey.ide.ui.MainController;
import sharknoon.casey.ide.ui.UISettings;
import sharknoon.casey.ide.ui.frames.Frame;
import sharknoon.casey.ide.ui.frames.FrameMoving;
import sharknoon.casey.ide.ui.frames.Frames;
import sharknoon.casey.ide.ui.lines.LineDrawing;
import sharknoon.casey.ide.ui.lines.Lines;
import sharknoon.casey.ide.ui.misc.MouseConsumable;
import sharknoon.casey.ide.ui.styles.StyleClasses;

/**
 * @author Josua Frank
 */
public class FunctionSiteLogic implements MouseConsumable {
    
    private final FunctionSite functionSite;
    
    private final AnchorPane root = new AnchorPane();
    private final WorkspaceSelection workspaceSelection;
    private final FrameMoving frameMoving;
    private final WorkspaceMoving workspaceMoving;
    private final WorkspaceZooming workspaceZooming;
    private final WorkspaceContextMenu workspaceContextMenu;
    private final LineDrawing lineDrawing;
    private boolean initialized = false;
    private boolean startBlockAlreadyAdded = false;
    private Rectangle workspaceBackground;
    
    public FunctionSiteLogic(FunctionSite functionSite) {
        this.functionSite = functionSite;
        this.workspaceSelection = new WorkspaceSelection(functionSite);
        this.frameMoving = new FrameMoving(functionSite);
        this.workspaceMoving = new WorkspaceMoving(functionSite);
        this.workspaceZooming = new WorkspaceZooming(functionSite);
        this.workspaceContextMenu = new WorkspaceContextMenu(functionSite);
        this.lineDrawing = new LineDrawing(functionSite);
    }
    
    public void addInFront(Node node) {
        root.getChildren().add(node);
    }
    
    public void addInBack(Node node) {
        //index 0 is the workspacebackground
        root.getChildren().add(1, node);
    }
    
    public void remove(Node node) {
        root.getChildren().remove(node);
    }
    
    Pane getRoot() {
        return root;
    }
    
    @Override
    public void onMousePressed(MouseEvent event) {
        if (event.getTarget() == workspaceBackground) {
            if (event.isMiddleButtonDown()) {
                workspaceMoving.onMousePressed(event);
            } else if (event.isPrimaryButtonDown()) {
                workspaceSelection.onMousePressed(event);
            }
        } else {
            if (event.isPrimaryButtonDown()) {
                frameMoving.onMousePressed(event);
            }
        }
        workspaceContextMenu.onMousePressed(event);
    }
    
    @Override
    public void onMouseDragged(MouseEvent event) {
        if (workspaceSelection.isSelecting()) {
            if (event.isPrimaryButtonDown()) {
                workspaceSelection.onMouseDragged(event);
            }
        } else if (event.getTarget() == workspaceBackground) {
            if (event.isMiddleButtonDown()) {
                workspaceMoving.onMouseDragged(event);
            }
        } else {
            if (event.isPrimaryButtonDown()) {
                frameMoving.onMouseDragged(event);
            }
        }
    }
    
    @Override
    public void onMouseReleased(MouseEvent event) {
        if (workspaceSelection.isSelecting()) {
            workspaceSelection.onMouseReleased(event);
        } else {
            frameMoving.onMouseReleased(event);
        }
    }
    
    @Override
    public void onContextMenuRequested(ContextMenuEvent event) {
        if (event.getTarget() == workspaceBackground) {
            workspaceContextMenu.onContextMenuRequested(event);
        }
    }
    
    @Override
    public void onMouseMoved(MouseEvent event) {
        if (Lines.isLineDrawing(functionSite)) {
            lineDrawing.onMouseMoved(event);
        }
    }
    
    @Override
    public void onScroll(ScrollEvent event) {
        workspaceZooming.onScroll(event);
    }
    
    public void onZoom(ZoomEvent event) {
        workspaceZooming.onZoom(event);
    }
    
    private void init() {
        drawLineAroundWorkspace();
        centerWorkspaceView();
        if (!startBlockAlreadyAdded) {
            Blocks.createBlock(BlockType.START, functionSite.getItem());
        }
        MouseConsumable.registerListeners(root, this);
        initialized = true;
    }
    
    public void addFrame(Frame frame) {
        addFrame(frame, new Point2D(-1, -1));
    }
    
    public void addFrame(Frame frame, Point2D origin) {
        if (frame.getBlock().getType() == BlockType.START) {
            if (startBlockAlreadyAdded) {
                return;
            }
            startBlockAlreadyAdded = true;
        }
        if (origin.getX() < 0 || origin.getY() < 0) {
            var screenHeight = Screen.getPrimary().getVisualBounds().getHeight();
            double x = (UISettings.WORKSPACE_MAX_X / 2) - (frame.getWidth() / 2);
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
        
        while (!Frames.isSpaceFree(frame, newX, newY)) {
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
        
        frame.setMinX(newX);
        frame.setMinY(newY);
        frame.addTo(root);
        return;
    }
    
    private void centerWorkspaceView() {
        TabPane tabPane = MainController.getTabPane();
        double maxWidth = tabPane.getWidth();
        double maxHeight = tabPane.getHeight();
        root.setTranslateX(-((UISettings.WORKSPACE_MAX_X / 2) - (maxWidth / 2)));
        root.setTranslateY(-((UISettings.WORKSPACE_MAX_Y / 2) - (maxHeight / 2)) + 100);
    }
    
    private void drawLineAroundWorkspace() {
        workspaceBackground = new Rectangle();
        workspaceBackground.getStyleClass().add(StyleClasses.rectangleWorkspace.name());
        workspaceBackground.setWidth(UISettings.WORKSPACE_MAX_X);
        workspaceBackground.setHeight(UISettings.WORKSPACE_MAX_Y);
        root.getChildren().add(0, workspaceBackground);
    }
    
    Pane getTabContentPane() {
        if (!initialized) {
            init();
        }
        return root;
    }
    
    public WorkspaceSelection getWorkspaceSelection() {
        return workspaceSelection;
    }
    
    public FrameMoving getFrameMoving() {
        return frameMoving;
    }
    
    public WorkspaceMoving getWorkspaceMoving() {
        return workspaceMoving;
    }
    
    public WorkspaceZooming getWorkspaceZooming() {
        return workspaceZooming;
    }
    
    public WorkspaceContextMenu getWorkspaceContextMenu() {
        return workspaceContextMenu;
    }
    
    public LineDrawing getLineDrawing() {
        return lineDrawing;
    }
}
