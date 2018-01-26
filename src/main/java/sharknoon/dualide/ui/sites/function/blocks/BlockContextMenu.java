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
package sharknoon.dualide.ui.sites.function.blocks;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.ContextMenuEvent;
import sharknoon.dualide.ui.sites.function.blocks.block.Start;

/**
 * This type represents the context menu of a block
 *
 * @author Josua Frank
 */
public class BlockContextMenu {

    private final Block block;
    private final ContextMenu menu = new ContextMenu();

    public BlockContextMenu(Block block) {
        this.block = block;
    }

    /**
     * opens the contextmenu
     *
     * @param event The ContextMenuEvent is needed for the orgin position of the
     * click
     */
    public void onContextMenuRequested(ContextMenuEvent event) {
        menu.getItems().clear();
        if (!block.getClass().equals(Start.class)) {
            MenuItem deleteItem = new MenuItem("Delete");
            deleteItem.setOnAction(e -> {
                if (block.isSelected()) {
                    Collection<Block> allBlocks = Blocks.getAllBlocks(block.getfunctionSite());
                    List<Block> toRemove = Blocks
                            .getAllBlocks(block.getfunctionSite())
                            .stream()
                            .filter(Block::isSelected)
                            .filter(b -> !b.getClass().equals(Start.class))
                            .collect(Collectors.toList());
                    toRemove.forEach(Block::remove);
                } else {
                    block.remove();
                }
            });
            menu.getItems().add(deleteItem);
        }
        //...

        menu.setAutoHide(true);
        menu.show(block.getShape(), event.getScreenX(), event.getScreenY());

    }

    /**
     * Hides the contextmenu
     */
    public void hide() {
        menu.hide();
    }
}
