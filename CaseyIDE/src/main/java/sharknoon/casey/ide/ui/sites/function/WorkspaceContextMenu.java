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
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import sharknoon.casey.ide.logic.blocks.BlockType;
import sharknoon.casey.ide.logic.blocks.Blocks;
import sharknoon.casey.ide.ui.UISettings;
import sharknoon.casey.ide.ui.misc.MouseConsumable;
import sharknoon.casey.ide.utils.language.Language;

import java.util.EnumSet;
import java.util.UUID;

/**
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
        menu.getItems().clear();
        
        EnumSet
                .allOf(BlockType.class)
                .stream()
                .filter(bt -> bt != BlockType.START)
                .forEach(blockType -> {
                    var menuItemAddNewBlock = new MenuItem();
                    menuItemAddNewBlock.setId(blockType.name());
                    Language.setCustom(blockType.getContextMenuAddBlockWord(), menuItemAddNewBlock::setText);
                    menuItemAddNewBlock.setOnAction(e -> {
                        Blocks.createBlock(blockType, functionSite.getItem(), UUID.randomUUID().toString(), workspaceOrigin);
                    });
                    menu.getItems().add(menuItemAddNewBlock);
                });
        
        menu.show((Node) event.getSource(), event.getScreenX(), event.getScreenY());
    }
    
}
