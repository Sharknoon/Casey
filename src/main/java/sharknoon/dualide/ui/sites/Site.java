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
package sharknoon.dualide.ui.sites;

import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import sharknoon.dualide.logic.Item;
import sharknoon.dualide.ui.misc.Icon;
import sharknoon.dualide.ui.misc.Icons;
import sharknoon.dualide.utils.language.Language;
import sharknoon.dualide.utils.language.Word;

/**
 *
 * @author Josua Frank
 * @param <I>
 *
 */
public abstract class Site<I extends Item> {

    private final I item;

    public Site(I item) {
        this.item = item;
    }

    public I getItem() {
        return item;
    }

    /**
     * The Pane of the Tab in the Tabpane
     *
     * @return
     */
    public abstract Pane getTabContentPane();

    /**
     * The Name of the Tab in the Tabpane
     *
     * @return
     */
    public String getTabName() {
        return item.getName();
    }

    public StringProperty getTabNameProperty() {
        return item.nameProperty();
    }

    /**
     * The Icon of the Tab in the Tabpane
     *
     * @return
     */
    public abstract Icon getTabIcon();
    
    public abstract void refresh();

    protected Set<String> getForbittenValues() {
        return getForbittenValues(null);
    }

    protected Set<String> getForbittenValues(String ignoreMe) {
        return (Set<String>) getItem()
                .getChildren()
                .stream()
                .map(i -> ((Item) i).getName())
                .filter(n -> ignoreMe == null || !n.equals(ignoreMe))
                .collect(Collectors.toSet());
    }

    protected Button createButton(Word buttonText, Consumer<ActionEvent> onAction) {
        return createButton(buttonText, null, onAction);
    }

    protected Button createButton(Icon icon, Consumer<ActionEvent> onAction) {
        return createButton(null, icon, onAction);
    }

    protected Button createButton(Word buttonText, Icon icon, Consumer<ActionEvent> onAction) {
        return createButton(buttonText, icon, onAction, true, true);
    }

    protected Button createButton(Word buttonText, Icon icon, Consumer<ActionEvent> onAction, boolean withText, boolean withTooltip) {
        Button buttonAdd = new Button();
        if (buttonText != null) {
            if (withText) {
                Language.set(buttonText, buttonAdd);
            } else if (withTooltip) {
                Language.setCustom(buttonText, s -> buttonAdd.setTooltip(new Tooltip(s)));
            }
        }
        if (icon != null) {
            Icons.set(buttonAdd, icon);
        }
        if (onAction != null) {
            buttonAdd.setOnAction(e -> onAction.accept(e));
        }
        return buttonAdd;
    }

    @Override
    public String toString() {
        return getTabName();
    }

}
