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
package sharknoon.dualide.ui.navigation;

import java.util.HashMap;
import java.util.Map;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import sharknoon.dualide.logic.items.Item;
import sharknoon.dualide.logic.items.ItemType;
import sharknoon.dualide.logic.items.Welcome;
import sharknoon.dualide.ui.MainController;
import sharknoon.dualide.ui.sites.Site;

/**
 *
 * @author Josua Frank
 */
public class ItemTabPane {

    private static final Map<Tab, Item> ITEMS = new HashMap<>();

    public static void init(TabPane tabPane) {
        tabPane
                .getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    var item = ITEMS.get(newValue);
                    if (item != null) {
                        item.getSite().select();
                    }
                });
        Site.currentSelectedProperty()
                .addListener((observable, oldValue, newValue) -> {
                    var tab = newValue.getSite().getTab();
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

        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
    }

}
