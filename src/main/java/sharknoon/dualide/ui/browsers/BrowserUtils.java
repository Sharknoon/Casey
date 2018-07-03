package sharknoon.dualide.ui.browsers;/*
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

import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import sharknoon.dualide.logic.items.Item;
import sharknoon.dualide.ui.misc.Icons;
import sharknoon.dualide.utils.language.Language;
import sharknoon.dualide.utils.language.Word;

public class BrowserUtils {
    
    public static HBox getEntries(Item<?, ?, ?> item, EventHandler<MouseEvent> onClick) {
        HBox hBoxChildren = new HBox(10);
        Node icon = Icons.get(item.getSite().getTabIcon());
        Label name = new Label(item.getName());
        hBoxChildren.setOnMouseClicked(onClick);
        hBoxChildren.getChildren().addAll(icon, name);
        hBoxChildren.setAlignment(Pos.CENTER_LEFT);
        return hBoxChildren;
    }
    
    public static Label getNoTypeLabel() {
        var text = Language.get(Word.TYPE_SELECTION_POPUP_NO_TYPES);
        var labelNoType = new Label(text);
        labelNoType.setFont(Font.font(20));
        labelNoType.setMaxWidth(Double.MAX_VALUE);
        labelNoType.setAlignment(Pos.CENTER);
        return labelNoType;
    }
    
}
