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
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import sharknoon.dualide.logic.Welcome;
import sharknoon.dualide.ui.sites.Site;

/**
 *
 * @author Josua Frank
 */
public class ItemTreeView {

    private static final Map<Site, TreeItem<Site>> ITEMS = new HashMap<>();

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
        update();
        selectItem(Welcome.getWelcome().getSite());
    }

    public static void update() {
        TreeView<Site> view = MainController.getTreeView();
        Welcome welcome = Welcome.getWelcome();
        ITEMS.clear();
        TreeItem<Site> root = welcome.getSite().recreateTreeItem();
        ITEMS.put(welcome.getSite(), root);
        view.setRoot(root);
    }

    public static void addItem(Site site, TreeItem<Site> parent, TreeItem<Site> item) {
        parent.getChildren().add(item);
        ITEMS.put(site, item);
    }

    public static void selectItem(Site site) {
        if (ITEMS.containsKey(site)) {
            MainController
                    .getTreeView()
                    .getSelectionModel()
                    .select(ITEMS.get(site));
        }
    }

}
