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
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import sharknoon.dualide.ui.sites.Site;

/**
 *
 * @author Josua Frank
 */
public class ItemTabPane {

    private static final Map<Site, Tab> TABS = new HashMap<>();
    private static final Map<Tab, Site> SITES = new HashMap<>();

    public static void init() {
        MainController
                .getTabPane()
                .getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    ItemTreeView.selectItem(SITES.get(newValue));
                });
        MainController
                .getTabPane()
                .setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
    }

    public static void setTab(Site site) {
        TabPane tabPane = MainController.getTabPane();
        if (TABS.containsKey(site)) {//Tab already exists
            tabPane.getSelectionModel().select(TABS.get(site));
        } else {//create tab
            Tab newTab = new Tab(site.getTabName(), site.getTabContentPane());
            newTab.setGraphic(site.getTabIcon());
            newTab.setOnClosed((event) -> {
                Site removedSite = SITES.remove(newTab);
                TABS.remove(removedSite);
            });
            TABS.put(site, newTab);
            SITES.put(newTab, site);
            tabPane.getTabs().add(newTab);
            tabPane.getSelectionModel().select(newTab);
        }
    }

}
