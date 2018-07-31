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
package sharknoon.casey.ide.ui.navigation;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.WeakInvalidationListener;
import javafx.scene.Node;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import sharknoon.casey.ide.logic.items.Item;
import sharknoon.casey.ide.logic.items.ItemType;
import sharknoon.casey.ide.ui.sites.Site;
import sharknoon.casey.ide.utils.settings.Logger;

import java.lang.ref.WeakReference;

/**
 * @author Josua Frank
 */
public class ItemTreeView {
    
    private static TreeItem<Item> draggingItem;
    
    public static void init(TreeView<Item> treeView) {
        treeView.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue != null && newValue.getValue() != null) {
                        newValue.getValue().getSite().select();
                        Logger.debug("Selecting " + newValue.getValue().toString() + " with children " + newValue.getValue().getChildren().toString());
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
        treeView.setCellFactory(itemTreeView -> new DraggableTreeCell());
    }
    
    // Note: This is a copy/paste of javafx.scene.control.TreeView which is a copy/paste of javafx.scene.control.cell.DefaultTreeCell,
    // which is package-protected
    private static TreeCell<Item> createDefaultCellImpl() {
        return new TreeCell<>() {
            
            private WeakReference<TreeItem<Item>> treeItemRef;
            
            private InvalidationListener treeItemGraphicListener = observable -> {
                updateDisplay(getItem(), isEmpty());
            };
            private WeakInvalidationListener weakTreeItemGraphicListener =
                    new WeakInvalidationListener(treeItemGraphicListener);
            private InvalidationListener treeItemListener = new InvalidationListener() {
                @Override
                public void invalidated(Observable observable) {
                    TreeItem<Item> oldTreeItem = treeItemRef == null ? null : treeItemRef.get();
                    if (oldTreeItem != null) {
                        oldTreeItem.graphicProperty().removeListener(weakTreeItemGraphicListener);
                    }
                    
                    TreeItem<Item> newTreeItem = getTreeItem();
                    if (newTreeItem != null) {
                        newTreeItem.graphicProperty().addListener(weakTreeItemGraphicListener);
                        treeItemRef = new WeakReference<>(newTreeItem);
                    }
                }
            };
            private WeakInvalidationListener weakTreeItemListener =
                    new WeakInvalidationListener(treeItemListener);
            
            {
                treeItemProperty().addListener(weakTreeItemListener);
                
                if (getTreeItem() != null) {
                    getTreeItem().graphicProperty().addListener(weakTreeItemGraphicListener);
                }
            }
            
            private void updateDisplay(Item item, boolean empty) {
                if (item == null || empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    // update the graphic if one is set in the TreeItem
                    TreeItem<Item> treeItem = getTreeItem();
                    Node graphic = treeItem == null ? null : treeItem.getGraphic();
                    if (graphic != null) {
                        setText(item.toString());
                        setGraphic(graphic);
                    } else {
                        setText(item.toString());
                        setGraphic(null);
                    }
                }
            }
            
            @Override
            public void updateItem(Item item, boolean empty) {
                super.updateItem(item, empty);
                updateDisplay(item, empty);
            }
        };
    }
    
}
