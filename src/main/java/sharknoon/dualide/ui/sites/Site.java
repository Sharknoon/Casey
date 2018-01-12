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

import java.util.List;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.Pane;
import sharknoon.dualide.logic.Item;
import sharknoon.dualide.ui.ItemTreeView;
import sharknoon.dualide.utils.javafx.RecursiveTreeItem;

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
    public abstract String getTabName();

    /**
     * The Icon of the Tab in the Tabpane
     *
     * @return
     */
    public abstract Node getTabIcon();

    public TreeItem<Site> recreateTreeItem() {
        TreeItem<Site> treeItem = new TreeItem(this, getTabIcon());
        getChildrenSites().forEach(s -> {
            ItemTreeView.addItem(s, treeItem, s.recreateTreeItem());
        });
        return treeItem;
    }

    public ObservableList<Site> getChildrenSites() {
        ObservableList<Site> observableArrayList = FXCollections.observableArrayList();
        getItem()
                .getChildren()
                .stream()
                .map(i -> ((Item) i).getSite())
                .collect(Collectors.toCollection(() -> observableArrayList));
        return observableArrayList;
    }

    @Override
    public String toString() {
        return getTabName();
    }

}
