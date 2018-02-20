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

import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;
import javafx.util.StringConverter;
import sharknoon.dualide.logic.items.Item;
import sharknoon.dualide.logic.items.ItemType;
import sharknoon.dualide.ui.misc.Icons;
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
                    }
                });
        Site.currentSelectedProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue.getType() == ItemType.WELCOME || newValue.getType() == ItemType.PROJECT) {
                        treeView.setRoot(newValue.getSite().getTreeItem());
                    } else {
                        treeView
                                .getSelectionModel()
                                .select(newValue.getSite().getTreeItem());
                    }
                });
        treeView.setFocusTraversable(false);

        //Works, but its very buggy, probably a javafx bug
//        treeView.setCellFactory((param) -> {
//            return new TreeCell<Item>() {
//                @Override
//                protected void updateItem(Item item, boolean empty) {
//                    super.updateItem(item, empty); //To change body of generated methods, choose Tools | Templates.
//                    if (item != null) {
//                        setText(item.getName());//textProperty().bind(item.nameProperty());
//                        setGraphic(Icons.get(item.getSite().getTabIcon()));
//                    }
//                }
//            };
//        });
    }

}
