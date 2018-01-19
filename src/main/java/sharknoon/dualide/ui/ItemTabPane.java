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
import java.util.Optional;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;
import sharknoon.dualide.logic.Item;
import sharknoon.dualide.logic.Welcome;
import sharknoon.dualide.ui.misc.Icons;

/**
 *
 * @author Josua Frank
 */
public class ItemTabPane {

    private static final Map<Item, Tab> TABS = new HashMap<>();
    private static final Map<Tab, Item> ITEMS = new HashMap<>();
    private static ObjectProperty<Item> selectedItem = new SimpleObjectProperty<>();

    public static void init() {
        MainController
                .getTabPane()
                .getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        Item item = ITEMS.get(newValue);
                        ItemTreeView.selectItem(item);
                        selectedItem.set(item);
                    } else {
                        selectedItem.set(null);
                    }
                });
        MainController
                .getTabPane()
                .setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
    }

    public static void setTab(Item item) {
        TabPane tabPane = MainController.getTabPane();
        if (TABS.containsKey(item)) {//Tab already exists
            tabPane.getSelectionModel().select(TABS.get(item));
        } else {//create tab
            onItemAdded(item);
        }
    }

    public static void onItemAdded(Item item) {
        TabPane tabPane = MainController.getTabPane();
        Tab newTab = new Tab();
        item.getSite().getTabContentPane().thenAccept((t) -> {
            Platform.runLater(() -> {
                newTab.setContent((Pane) t);
            });
        });
        newTab.textProperty().bindBidirectional(item.getSite().getTabNameProperty());
        Icons.setCustom(g -> newTab.setGraphic(g), item.getSite().getTabIcon());
        newTab.setOnClosed((event) -> {
            TABS.remove(ITEMS.remove(newTab));
        });
        TABS.put(item, newTab);
        ITEMS.put(newTab, item);
        if (item.getType().equals(Welcome.class)) {
            newTab.setClosable(false);
        }
        tabPane.getTabs().add(newTab);
        tabPane.getSelectionModel().select(newTab);
    }

    public static void onItemRemoved(Item item) {
        if (TABS.containsKey(item)) {
            Tab tab = TABS.get(item);
            MainController.getTabPane().getTabs().remove(tab);
            ITEMS.remove(tab);
        }
        TABS.remove(item);
    }

    public static void hideRootTab() {
        Welcome welcome = Welcome.getWelcome();
        Tab tab = TABS.get(welcome);
        if (tab != null) {
            MainController.getTabPane().getTabs().remove(tab);
        }
    }

    public static void closeAllTabs() {
        MainController
                .getTabPane()
                .getTabs()
                .clear();
        TABS.clear();
        ITEMS.clear();
        onItemAdded(Welcome.getWelcome());
    }

    public static ObjectProperty<Item> selectedItemProperty() {
        return selectedItem;
    }
}
