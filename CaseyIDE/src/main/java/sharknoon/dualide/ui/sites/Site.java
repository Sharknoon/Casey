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
package sharknoon.dualide.ui.sites;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeItem.TreeModificationEvent;
import sharknoon.dualide.logic.items.Class;
import sharknoon.dualide.logic.items.*;
import sharknoon.dualide.logic.items.Package;
import sharknoon.dualide.logic.types.PrimitiveType;
import sharknoon.dualide.ui.MainController;
import sharknoon.dualide.ui.misc.Icon;
import sharknoon.dualide.ui.misc.Icons;
import sharknoon.dualide.ui.sites.clazz.ClassSite;
import sharknoon.dualide.ui.sites.function.FunctionSite;
import sharknoon.dualide.ui.sites.package_.PackageSite;
import sharknoon.dualide.ui.sites.parameter.ParameterSite;
import sharknoon.dualide.ui.sites.project.ProjectSite;
import sharknoon.dualide.ui.sites.variable.VariableSite;
import sharknoon.dualide.ui.sites.welcome.WelcomeSite;
import sharknoon.dualide.utils.settings.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @param <I>
 * @author Josua Frank
 */
public abstract class Site<I extends Item> {

    private static final ObjectProperty<Item<?, ?, ?>> CURRENT_SELECTED_ITEM = new SimpleObjectProperty<>();
    private final I item;
    private final TreeItem<Item> treeItem = new TreeItem<>();
    private final Tab tab = new Tab();
    
    public static Site createSite(Item item) {
        switch (item.getType()) {
            case CLASS:
                return new ClassSite((Class) item);
            case FUNCTION:
                return new FunctionSite((Function) item);
            case PACKAGE:
                return new PackageSite((Package) item);
            case PROJECT:
                return new ProjectSite((Project) item);
            case VARIABLE:
                return new VariableSite((Variable) item);
            case PARAMETER:
                return new ParameterSite((Parameter) item);
            case WELCOME:
                return new WelcomeSite((Welcome) item);
        }
        return null;
    }
    
    public void afterInit() {
    }

    public Site(I item) {
        this.item = item;
        //treeitem setup
        treeItem.setValue(item);
        treeItem.graphicProperty().bind(Icons.iconToNodeProperty(tabIconProperty()));
        //Not working Properly
        //ObservableList<TreeItem<Item>> treeItems = EasyBind.map((ObservableList<Item>) item.childrenProperty(), i -> i.getSite().getTreeItem());
        item.childrenProperty().addListener((ListChangeListener<? super Item>) c -> {
            while (c.next()) {
                List<TreeItem<Item>> newTreeItems = new ArrayList<>();
                item.childrenProperty().forEach(ch -> {
                    newTreeItems.add(((Item) ch).getSite().getTreeItem());
                });
                treeItem.getChildren().setAll(newTreeItems);
            }
        });
        //Name changing should also change tree item name
        item.nameProperty().addListener((observable, oldValue, newValue) -> {
            var event = new TreeModificationEvent<>(TreeItem.valueChangedEvent(), treeItem);
            Event.fireEvent(treeItem, event);
        });
        //tab setup
        tab.textProperty().bind(item.nameProperty());
        tab.graphicProperty().bind(Icons.iconToNodeProperty(tabIconProperty()));
        if (item.getType().equals(ItemType.WELCOME)) {
            tab.setClosable(false);
        }
        tab.setOnSelectionChanged((event) -> {
            if (tab.isSelected() && tab.getContent() == null) {
                getTabContentPane().thenAccept((t) -> {
                    Platform.runLater(() -> {
                        tab.setContent(t);
                    });
                }).exceptionally((ex) -> {
                    if (ex instanceof Exception) {
                        Logger.error("Could not set the content pane", ex);
                    } else {
                        Logger.error("Could not set the content pane " + ex);
                    }
                    return null;
                });
            }
        });
    }

    public static ObjectProperty<Item<?, ?, ?>> currentSelectedProperty() {
        return CURRENT_SELECTED_ITEM;
    }

    public void destroy() {
        //Stupid javafx implementation, just need a method tab.close() or tabPane.closeTab(tab)
        var tabPane = MainController.getTabPane();
        var onClosed = tab.getOnClosed();
        if (onClosed != null) {
            onClosed.handle(null);
        }
        tabPane.getTabs().remove(tab);
    }

    public I getItem() {
        return item;
    }

    public TreeItem<Item> getTreeItem() {
        return treeItem;
    }

    public Tab getTab() {
        return tab;
    }

    public void select() {
        CURRENT_SELECTED_ITEM.set(item);
    }

    public boolean isSelected() {
        return CURRENT_SELECTED_ITEM.get() == getItem();
    }

    /**
     * The Pane of the Tab in the Tabpane
     *
     * @return
     */
    public abstract CompletableFuture<Node> getTabContentPane();

    /**
     * The Name of the Tab in the Tabpane
     *
     * @return
     */
    public String getTabName() {
        return item.getName();
    }

    public StringProperty tabNameProperty() {
        return item.nameProperty();
    }

    /**
     * The Icon of the Tab in the Tabpane
     *
     * @return
     */
    public abstract ObjectProperty<Icon> tabIconProperty();

    public Icon getTabIcon(){
        return tabIconProperty().get();
    }

    public abstract Icon getAddIcon();

    public Set<String> getForbittenChildNames() {
        return getForbittenChildNames(null);
    }

    public Set<String> getForbittenChildNames(String ignoreMe) {
        Set<String> set = (Set<String>) getItem()
                .getChildren()
                .stream()
                .map(i -> ((Item) i).getName())
                .filter(n -> ignoreMe == null || !n.equals(ignoreMe))
                .collect(Collectors.toSet());
        set.addAll(PrimitiveType.getForbiddenNames());
        return set;
    }


    @Override
    public String toString() {
        return getTabName();
    }

}
