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

import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import sharknoon.dualide.logic.Item;
import sharknoon.dualide.ui.misc.Icon;


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
    public String getTabName(){
        return item.getName();
    }
    
    public StringProperty getTabNameProperty(){
        return item.nameProperty();
    }

    /**
     * The Icon of the Tab in the Tabpane
     *
     * @return
     */
    public abstract Icon getTabIcon();

    @Override
    public String toString() {
        return getTabName();
    }

}
