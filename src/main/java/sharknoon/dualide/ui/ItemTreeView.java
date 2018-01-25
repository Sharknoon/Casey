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
import sharknoon.dualide.logic.items.Item;
import sharknoon.dualide.logic.items.Project;
import sharknoon.dualide.logic.items.Type;
import sharknoon.dualide.logic.items.Welcome;
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
        selectWelcomeItem();
    }

    private static boolean isWelcome = true;

    private static void selectWelcomeItem() {
        isWelcome = true;
        MainController.getTreeView().setRoot(createAndGetTreeItem(Welcome.getWelcome()));
        selectItem(Welcome.getWelcome());
    }

    public static void onItemAdded(Item item) {
        TreeItem<Item> treeItem = createAndGetTreeItem(item);
        if (item.getParent() != null && ITEMS.containsKey(item.getParent())) {
            TreeItem<Item> parentItem = ITEMS.get(item.getParent());
            parentItem.getChildren().add(treeItem);
        } else {
            MainController.getTreeView().setRoot(treeItem);
        }
    }

    private static TreeItem<Item> createAndGetTreeItem(Item item) {
        if (ITEMS.containsKey(item)) {
            return ITEMS.get(item);
        }
        TreeItem<Item> treeItem = new TreeItem<>(item);
        Icons.setCustom(g -> treeItem.setGraphic(g), item.getSite().getTabIcon());
        item.nameProperty().addListener((observable, oldValue, newValue) -> {
            treeItem.setValue(null);//Have to set it to null before resetting it to the same object again to update the text in the treevie
            treeItem.setValue(item);
        });
        ITEMS.put(item, treeItem);
        return treeItem;
    }

    public static void onItemRemoved(Item item) {
        if (item.getParent() != null && ITEMS.containsKey(item.getParent()) && ITEMS.containsKey(item)) {
            TreeItem<Item> parentItem = ITEMS.get(item.getParent());
            TreeItem<Item> itemToRemove = ITEMS.get(item);
            parentItem.getChildren().remove(itemToRemove);
        }
        ITEMS.remove(item);
    }

    public static void selectItem(Item item) {
        if (!ITEMS.containsKey(item)) {
            createAndGetTreeItem(item);
        }
        if (isWelcome && item.getType() == Type.PROJECT) {
            MainController.getTreeView().setRoot(ITEMS.get(item));
            isWelcome = false;
        }
        MainController
                .getTreeView()
                .getSelectionModel()
                .select(ITEMS.get(item));
    }

    public static void closeProjectAndShowWelcome() {
        MainController
                .getTreeView()
                .getRoot()
                .getChildren()
                .clear();
        TreeItem<Item> welcome = ITEMS.get(Welcome.getWelcome());
        ITEMS.clear();
        ITEMS.put(Welcome.getWelcome(), welcome);
        selectWelcomeItem();
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
        Project.getCurrentProject().ifPresent(p -> refreshRecursive(p));
    }

    private static void refreshRecursive(Item item) {
        onItemAdded(item);
        item.getChildren().forEach(c -> refreshRecursive((Item) c));
    }

}
