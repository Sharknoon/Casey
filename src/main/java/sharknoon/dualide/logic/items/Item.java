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
package sharknoon.dualide.logic.items;

import java.util.Iterator;
import java.util.LinkedHashSet;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SetProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleSetProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import sharknoon.dualide.serial.PostProcessable;
import sharknoon.dualide.ui.ItemTabPane;
import sharknoon.dualide.ui.ItemTreeView;
import sharknoon.dualide.ui.sites.Site;

/**
 *
 * @author Josua Frank
 * @param <I> The type of the item itself
 * @param <P> The type of the parent item
 * @param <C> The type of the children, only useful for the type project and
 * welcome
 */
public abstract class Item<I extends Item, P extends Item, C extends Item> implements PostProcessable {

    private final transient ObjectProperty<P> parent = new SimpleObjectProperty<>();
    private final SetProperty<C> children = new SimpleSetProperty<>(FXCollections.observableSet(new LinkedHashSet<>()));

    private final StringProperty name = new SimpleStringProperty("");
    private final StringProperty comments = new SimpleStringProperty("");
    //DO NOT CHANGE ORDER!!!
    private final transient ObjectProperty<ItemType> itemType = new SimpleObjectProperty<>(ItemType.valueOf(this));
    private final transient ReadOnlyStringWrapper fullName = new ReadOnlyStringWrapper();
    private final transient ObjectProperty<Site<I>> site = new SimpleObjectProperty<>(Site.createSite(this));

    /**
     * can return null!!!
     *
     * @param <ITEM>
     * @param itemType
     * @param parent
     * @param name
     * @return
     */
    public static <ITEM extends Item> ITEM createItem(ItemType itemType, Item parent, String name) {
        switch (itemType) {
            case CLASS:
                return (ITEM) new Class((Package) parent, name);
            case FUNCTION:
                return (ITEM) new Function(parent, name);
            case PACKAGE:
                return (ITEM) new Package(parent, name);
            case PROJECT:
                return (ITEM) new Project(null, name);
            case VARIABLE:
                return (ITEM) new Variable(parent, name);
            case WELCOME:
                return (ITEM) new Welcome(null, name);
        }
        return null;
    }

    //Just for initializing fields
    protected Item() {
    }

    protected Item(P parent, String name) {
        setParent(parent);
        setName(name);
        addListeners();
    }

    @Override
    public void postProcess() {
        getChildren().forEach(c -> c.setParent(this));
        addListeners();
    }

    public String getName() {
        return nameProperty().get();
    }

    public void setName(String name) {
        nameProperty().set(name == null ? "" : name);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getComments() {
        return commentsProperty().get();
    }

    public void setComments(String comments) {
        commentsProperty().set(comments == null ? "" : comments);
    }

    public StringProperty commentsProperty() {
        return comments;
    }

    public P getParent() {
        return parentProperty().get();
    }

    protected void setParent(P parent) {
        parentProperty().set(parent);
    }

    public ObjectProperty<P> parentProperty() {
        return parent;
    }

    public ObjectProperty<ItemType> typeProperty() {
        return itemType;
    }

    public ItemType getType() {
        return typeProperty().get();
    }

    public Site<I> getSite() {
        return siteProperty().get();
    }

    public ObjectProperty<Site<I>> siteProperty() {
        return site;
    }

    public ObservableSet<C> getChildren() {
        return children;
    }

    public void addChildren(C children) {
        getChildren().add(children);
    }

    public SetProperty<C> childrenProperty() {
        return children;
    }

    @Override
    public String toString() {
        return nameProperty().get();
    }

    public void destroy() {
        destroyImpl();
        if (parentProperty().get() != null) {
            parentProperty().get().getChildren().remove(this);
        }
    }

    protected void destroyImpl() {
        Iterator<C> iterator = getChildren().iterator();
        while (iterator.hasNext()) {
            C next = iterator.next();
            next.destroyImpl();
            iterator.remove();
        }
    }

    private void addListeners() {
        getChildren().addListener((SetChangeListener.Change<? extends C> change) -> {
            if (change.wasAdded()) {
                C elementAdded = change.getElementAdded();
                elementAdded.setParent(this);
                ItemTreeView.onItemAdded(elementAdded);
                ItemTabPane.onItemAdded(elementAdded);
            } else if (change.wasRemoved()) {
                C elementRemoved = change.getElementRemoved();
                ItemTreeView.onItemRemoved(elementRemoved);
                ItemTabPane.onItemRemoved(elementRemoved);
            }
        });
        nameProperty().addListener((observable, oldValue, newValue) -> {
            refreshFullNameRecursive();
        });
        parentProperty().addListener((observable, oldValue, newValue) -> {
            refreshFullNameRecursive();
        });
    }

    private void refreshFullNameRecursive() {
        refreshFullName();
        childrenProperty().forEach(Item::refreshFullNameRecursive);
    }

    private void refreshFullName() {
        if (getParent() != null) {
            String idString = getParent().getFullName() + "." + getName();
            fullName.set(idString);
        } else {
            fullName.set(getName());
        }
    }

    /**
     * Also the unique id
     *
     * @return
     */
    public String getFullName() {
        if (fullName.get() == null || !fullName.get().endsWith(getName())) {
            if (getParent() != null) {
                String idString = getParent().getFullName() + "." + getName();
                fullName.set(idString);
                return idString;
            } else {
                fullName.set(getName());
                return getName();
            }
        } else {
            return fullName.get();
        }
    }

    public ReadOnlyStringProperty fullNameProperty() {
        return fullName.getReadOnlyProperty();
    }

    @Override
    public int hashCode() {
        return getFullName().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Item<?, ?, ?> other = (Item<?, ?, ?>) obj;
        return this.getFullName().equals(other.getFullName());
    }

}
