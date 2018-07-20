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
package sharknoon.casey.ide.ui.frames;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.ContextMenuEvent;
import sharknoon.casey.ide.logic.blocks.Block;
import sharknoon.casey.ide.logic.blocks.BlockType;
import sharknoon.casey.ide.utils.language.Language;
import sharknoon.casey.ide.utils.language.Word;

import java.util.stream.Collectors;

/**
 * This type represents the context menu of a frame
 *
 * @author Josua Frank
 */
public class FrameContextMenu {
    
    private final Frame frame;
    private final ContextMenu menu = new ContextMenu();
    
    FrameContextMenu(Frame frame) {
        this.frame = frame;
    }

    /**
     * opens the contextmenu
     *
     * @param event The ContextMenuEvent is needed for the orgin position of the
     * click
     */
    void onContextMenuRequested(ContextMenuEvent event) {
        menu.getItems().clear();
        if (frame.getBlock().getType() != BlockType.START) {
              var deleteItem = new MenuItem();
            Language.setCustom(Word.DELETE, deleteItem::setText);
            deleteItem.setOnAction(e -> {
                if (frame.isSelected()) {
                    var toRemove = Frames
                            .getAllFrames(frame.getFunctionSite())
                            .filter(Frame::isSelected)
                            .map(Frame::getBlock)
                            .filter(b -> b.getType() != BlockType.START)
                            .collect(Collectors.toList());
                    toRemove.forEach(Block::remove);
                } else {
                    frame.getBlock().remove();
                }
            });
            menu.getItems().add(deleteItem);
        }
        //...

        menu.setAutoHide(true);
        menu.show(frame.getShape(), event.getScreenX(), event.getScreenY());

    }

    /**
     * Hides the contextmenu
     */
    public void hide() {
        menu.hide();
    }
}
