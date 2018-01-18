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
package sharknoon.dualide.ui;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javafx.scene.control.TreeItem;
import sharknoon.dualide.logic.Item;
import sharknoon.dualide.logic.Project;
import sharknoon.dualide.logic.Welcome;
import sharknoon.dualide.ui.misc.Icons;

/**
 *
 * @author Josua Frank
 */
public class ItemTreeView {

    private static final Map<Item, TreeItem<Item>> ITEMS = new HashMap<>();

    public static void init() {
        MainController
                .getTreeView()
                .getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        ItemTabPane.setTab(newValue.getValue());
                    }
                });
        MainController
                .getTreeView()
                .setFocusTraversable(false);
        createRootItem();
        selectItem(Welcome.getWelcome());
    }

    private static void createRootItem() {
        TreeItem<Item> rootItem = createTreeItem(Welcome.getWelcome());
        MainController.getTreeView().setRoot(rootItem);
    }

    public static void onItemAdded(Item item, Item parent) {
        if (ITEMS.containsKey(parent)) {
            TreeItem<Item> treeItem = createTreeItem(item);
            TreeItem<Item> parentItem = ITEMS.get(parent);
            parentItem.getChildren().add(treeItem);
        }
    }

    private static TreeItem<Item> createTreeItem(Item item) {
        TreeItem<Item> treeItem = new TreeItem<>(item);
        Icons.setCustom(g -> treeItem.setGraphic(g), item.getSite().getTabIcon());
        item.nameProperty().addListener((observable, oldValue, newValue) -> {
            treeItem.setValue(null);//Have to set it to null before resetting it to the same object again to update the text in the treevie
            treeItem.setValue(item);
        });
        ITEMS.put(item, treeItem);
        return treeItem;
    }

    public static void onItemRemoved(Item item, Item parent) {
        if (ITEMS.containsKey(parent) && ITEMS.containsKey(item)) {
            TreeItem<Item> parentItem = ITEMS.get(parent);
            TreeItem<Item> itemToRemove = ITEMS.get(item);
            parentItem.getChildren().remove(itemToRemove);
            ITEMS.remove(item);
        }
    }

    public static void selectItem(Item item) {
        if (ITEMS.containsKey(item)) {
            MainController
                    .getTreeView()
                    .getSelectionModel()
                    .select(ITEMS.get(item));
        }
    }

    public static void hideRootItem() {
        MainController.getTreeView().setShowRoot(false);
    }

    public static void closeAllItems() {
        MainController
                .getTreeView()
                .getRoot()
                .getChildren()
                .clear();
        TreeItem<Item> welcome = ITEMS.get(Welcome.getWelcome());
        ITEMS.clear();
        ITEMS.put(Welcome.getWelcome(), welcome);
        MainController.getTreeView().setShowRoot(true);
    }

    public static void refresh() {
        MainController
                .getTreeView()
                .getRoot()
                .getChildren()
                .clear();
        TreeItem<Item> welcome = ITEMS.get(Welcome.getWelcome());
        ITEMS.clear();
        ITEMS.put(Welcome.getWelcome(), welcome);
        Welcome.getWelcome().getChildren().forEach(ItemTreeView::refreshRecursive);
    }

    private static void refreshRecursive(Item item) {
        onItemAdded(item, item.getParent());
        item.getChildren().forEach((item1) -> ItemTreeView.refreshRecursive((Item) item1));
    }

}
