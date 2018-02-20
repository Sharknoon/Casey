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
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;
import sharknoon.dualide.logic.items.Item;
import sharknoon.dualide.logic.items.ItemType;
import sharknoon.dualide.logic.items.Welcome;
import sharknoon.dualide.ui.misc.Icons;
import sharknoon.dualide.ui.sites.Site;

/**
 *
 * @author Josua Frank
 */
public class ItemTabPane {

    private static final Map<Tab, Item> ITEMS = new HashMap<>();

    public static void init() {
        MainController
                .getTabPane()
                .getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    Item item = ITEMS.get(newValue);
                    if (item != null) {
                        item.getSite().select();
                    }
                });
        Site.currentSelectedProperty()
                .addListener((observable, oldValue, newValue) -> {
                    TabPane tabPane = MainController.getTabPane();
                    Tab tab = newValue.getSite().getTab();
                    if (newValue.getType() == ItemType.WELCOME) {
                        tabPane.getTabs().clear();
                        tabPane.getTabs().add(tab);
                    } else {
                        tabPane.getTabs().remove(Welcome.getWelcome().getSite().getTab());
                        if (!tabPane.getTabs().contains(tab)) {
                            tabPane.getTabs().add(tab);
                            ITEMS.put(tab, newValue);
                        }
                        tabPane
                                .getSelectionModel()
                                .select(tab);
                    }
                });

        MainController
                .getTabPane()
                .setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
    }

}
