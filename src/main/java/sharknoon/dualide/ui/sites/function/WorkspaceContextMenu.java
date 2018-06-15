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
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import sharknoon.dualide.ui.sites.function.blocks.Blocks;
import sharknoon.dualide.ui.misc.MouseConsumable;
import sharknoon.dualide.ui.sites.function.lines.Lines;
import sharknoon.dualide.utils.language.Language;
import sharknoon.dualide.utils.language.Word;

/**
 *
 * @author Josua Frank
 */
public class WorkspaceContextMenu implements MouseConsumable {

    private final FunctionSite functionSite;
    private ContextMenu menu;

    public WorkspaceContextMenu(FunctionSite functionSite) {
        this.functionSite = functionSite;
    }

    public void init() {

    }

    @Override
    public void onMousePressed(MouseEvent event) {
        if (menu != null) {
            menu.hide();
        }
    }
//Point2D workspaceOrigin, Point2D screenOrigin, Node originNode

    @Override
    public void onContextMenuRequested(ContextMenuEvent event) {
        var workspaceOrigin = new Point2D(event.getX(), event.getY());
        var x = workspaceOrigin.getX();
        var y = workspaceOrigin.getY();
        if (x < 0 + UISettings.WORKSPACE_PADDING) {
            return;
        } else if (x > UISettings.WORKSPACE_MAX_X - UISettings.WORKSPACE_PADDING) {
            return;
        } else if (y < 0 + UISettings.WORKSPACE_PADDING) {
            return;
        } else if (y > UISettings.WORKSPACE_MAX_Y - UISettings.WORKSPACE_PADDING) {
            return;
        }

        if (menu == null) {
            menu = new ContextMenu();
            menu.setAutoHide(true);
        } else {
            menu.hide();
        }
        if (!Blocks.isMouseOverBlock(functionSite) && !Lines.isMouseOverLine(functionSite)) {
            var addNewDecisionBlockItem = new MenuItem();
            var addNewAssignmentBlockItem = new MenuItem();
            var addNewEndBlockItem = new MenuItem();
            var addNewCallBlockItem = new MenuItem();
            var addNewInputBlockItem = new MenuItem();
            var addNewOutputBlockItem = new MenuItem();
            Language.setCustom(Word.FUNCTION_SITE_ADD_NEW_DECISION_BLOCK, addNewDecisionBlockItem::setText);
            Language.setCustom(Word.FUNCTION_SITE_ADD_NEW_ASSIGNMENT_BLOCK, addNewAssignmentBlockItem::setText);
            Language.setCustom(Word.FUNCTION_SITE_ADD_NEW_END_BLOCK, addNewEndBlockItem::setText);
            Language.setCustom(Word.FUNCTION_SITE_ADD_NEW_CALL_BLOCK, addNewCallBlockItem::setText);
            Language.setCustom(Word.FUNCTION_SITE_ADD_NEW_INPUT_BLOCK, addNewInputBlockItem::setText);
            Language.setCustom(Word.FUNCTION_SITE_ADD_NEW_OUTPUT_BLOCK, addNewOutputBlockItem::setText);
            addNewDecisionBlockItem.setOnAction(e -> {
                functionSite.getLogicSite().addDecisionBlock(workspaceOrigin);
            });
            addNewAssignmentBlockItem.setOnAction(e -> {
                functionSite.getLogicSite().addAssignmentBlock(workspaceOrigin);
            });
            addNewEndBlockItem.setOnAction(e -> {
                functionSite.getLogicSite().addEndBlock(workspaceOrigin);
            });
            addNewCallBlockItem.setOnAction(e -> {
                functionSite.getLogicSite().addCallBlock(workspaceOrigin);
            });
            addNewInputBlockItem.setOnAction(e -> {
                functionSite.getLogicSite().addInputBlock(workspaceOrigin);
            });
            addNewOutputBlockItem.setOnAction(e -> {
                functionSite.getLogicSite().addOutputBlock(workspaceOrigin);
            });
            menu.getItems().clear();
            menu.getItems().addAll(
                    addNewDecisionBlockItem,
                    addNewAssignmentBlockItem,
                    addNewEndBlockItem,
                    addNewCallBlockItem,
                    addNewInputBlockItem,
                    addNewOutputBlockItem
            );
            menu.show((Node) event.getSource(), event.getScreenX(), event.getScreenY());
        }
    }

}
