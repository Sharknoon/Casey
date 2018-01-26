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

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import sharknoon.dualide.ui.sites.function.blocks.Blocks;

/**
 *
 * @author Josua Frank
 */
public class WorkspaceContextMenu {

    private final FunctionSite functionSite;
    private ContextMenu menu;
    private Point2D origin;

    public WorkspaceContextMenu(FunctionSite functionSite) {
        this.functionSite = functionSite;
    }

    public void init() {

    }

    public void onMousePressed(Point2D screenOrigin) {
        origin = screenOrigin;
        if (menu != null) {
            menu.hide();
        }
    }

    public void onContextMenuRequested(Point2D workspaceOrigin, Point2D screenOrigin, Node originNode) {
        double x = workspaceOrigin.getX();
        double y = workspaceOrigin.getY();
        if (x < 0 + UISettings.paddingInsideWorkSpace) {
            return;
        } else if (x > UISettings.maxWorkSpaceX - UISettings.paddingInsideWorkSpace) {
            return;
        } else if (y < 0 + UISettings.paddingInsideWorkSpace) {
            return;
        } else if (y > UISettings.maxWorkSpaceY - UISettings.paddingInsideWorkSpace) {
            return;
        }

        if (menu == null) {
            menu = new ContextMenu();
            menu.setAutoHide(true);
        } else {
            menu.hide();
        }
        if (!Blocks.isMouseOverBlock()) {
            MenuItem addNewDecisionBlockItem = new MenuItem("Add Decision Block");
            MenuItem addNewProgressBlockItem = new MenuItem("Add Progress Block");
            MenuItem addNewEndBlockItem = new MenuItem("Add End Block");
            addNewDecisionBlockItem.setOnAction(e -> {
                functionSite.addDecisionBlock(workspaceOrigin);
            });
            addNewProgressBlockItem.setOnAction(e -> {
                functionSite.addProcessBlock(workspaceOrigin);
            });
            addNewEndBlockItem.setOnAction(e -> {
                functionSite.addEndBlock(workspaceOrigin);
            });
            menu.getItems().clear();
            menu.getItems().addAll(addNewDecisionBlockItem, addNewProgressBlockItem, addNewEndBlockItem);
            menu.show(originNode, screenOrigin.getX(), screenOrigin.getY());
        }
    }

}
