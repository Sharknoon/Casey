package sharknoon.casey.ide.ui.browsers;/*
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

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import sharknoon.casey.ide.logic.ValueHoldable;
import sharknoon.casey.ide.logic.items.Item;
import sharknoon.casey.ide.logic.items.ItemType;
import sharknoon.casey.ide.logic.types.Type;
import sharknoon.casey.ide.utils.language.Language;
import sharknoon.casey.ide.utils.language.Word;
import sharknoon.casey.ide.utils.settings.Logger;

import java.util.List;
import java.util.function.Consumer;

public class VariableBrowser extends VBox {
    
    public static VariableBrowser createVariableBrowser(Consumer<ValueHoldable> variableConsumer, Type allowedType) {
        return new VariableBrowser(variableConsumer, allowedType);
    }
    
    private final Consumer<ValueHoldable> variableConsumer;
    private final Type allowedType;
    
    private VariableBrowser(Consumer<ValueHoldable> variableConsumer, Type allowedType) {
        this.variableConsumer = variableConsumer;
        this.allowedType = allowedType;
        init();
        if (allowedType == null) {
            addNoTypeLabel();
        } else {
            addVariableBrowser();
        }
    }
    
    private void init() {
        setSpacing(10);
        setPadding(new Insets(25));
        setPrefWidth(520);
        setMaxSize(800, 400);
        setAlignment(Pos.CENTER);
    }
    
    private void addNoTypeLabel() {
        var text = Language.get(Word.VARIABLE_SELECTION_POPUP_NO_TYPES);
        var labelNoType = new Label(text);
        labelNoType.setFont(Font.font(20));
        labelNoType.setMaxWidth(Double.MAX_VALUE);
        labelNoType.setAlignment(Pos.CENTER);
        getChildren().add(labelNoType);
    }
    
    private void addVariableBrowser() {
        Consumer<Item> itemConsumer = i -> {
            if (i instanceof ValueHoldable) {
                variableConsumer.accept((ValueHoldable) i);
            } else {
                Logger.error("Could not cast variable from browser");
            }
        };
        var root = BrowserUtils.getItemSelector(allowedType, itemConsumer, List.of(ItemType.PARAMETER, ItemType.VARIABLE));
        getChildren().add(root);
    }
}
