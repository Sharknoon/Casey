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
package sharknoon.casey.ide.ui.lines;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.ContextMenuEvent;
import sharknoon.casey.ide.utils.language.Language;
import sharknoon.casey.ide.utils.language.Word;

import java.util.stream.Collectors;

/**
 *
 * @author Josua Frank
 */
public class LineContextMenu {

    private final Line line;
    private final ContextMenu menu = new ContextMenu();

    public LineContextMenu(Line line) {
        this.line = line;
    }

    /**
     * opens the contextmenu
     *
     * @param event The ContextMenuEvent is needed for the orgin position of the
     * click
     */
    public void onContextMenuRequested(ContextMenuEvent event) {
        menu.getItems().clear();
          var deleteItem = new MenuItem();
        Language.setCustom(Word.DELETE, deleteItem::setText);
        deleteItem.setOnAction(e -> {
            if (line.isSelected()) {
                var linesToDelete = Lines.getAllLines(line.getOutputDot().getFrame().getFunctionSite())
                        .filter(Line::isSelected)
                        .collect(Collectors.toList());
                linesToDelete.forEach(Line::remove);
            } else {
                line.remove();
            }
        });
        menu.getItems().add(deleteItem);
        //...

        menu.setAutoHide(true);
        menu.show(line.getShape(), event.getScreenX(), event.getScreenY());

    }

    /**
     * Hides the contextmenu
     */
    public void hide() {
        menu.hide();
    }
}
