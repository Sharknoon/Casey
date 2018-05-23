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

import javafx.scene.control.TreeView;
import sharknoon.dualide.logic.items.Item;
import sharknoon.dualide.logic.items.ItemType;
import sharknoon.dualide.ui.MainController;
import sharknoon.dualide.ui.sites.Site;

/**
 *
 * @author Josua Frank
 */
public class ItemTreeView {

    public static void init() {
        TreeView<Item> treeView = MainController.getTreeView();
        treeView.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue != null && newValue.getValue() != null) {
                        newValue.getValue().getSite().select();
                        System.out.println("selecting " + newValue.getValue().toString() + " with children " + newValue.getValue().getChildren().toString());
                    }
                });
        Site.currentSelectedProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue.getType() == ItemType.WELCOME || newValue.getType() == ItemType.PROJECT) {
                        treeView.setRoot(newValue.getSite().getTreeItem());
                    }
                    treeView
                            .getSelectionModel()
                            .select(newValue.getSite().getTreeItem());

                });
        treeView.setFocusTraversable(false);
    }

}
