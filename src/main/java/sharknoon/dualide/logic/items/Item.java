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
import java.util.Optional;
import java.util.Set;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlySetProperty;
import javafx.beans.property.ReadOnlySetWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SetProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import sharknoon.dualide.ui.ItemTabPane;
import sharknoon.dualide.ui.ItemTreeView;
import sharknoon.dualide.ui.sites.Site;
import sharknoon.dualide.utils.collection.Collections;

/**
 *
 * @author Josua Frank
 * @param <I> The type of the item itself
 * @param <P> The type of the parent item
 * @param <C> The type of the children, only useful for the type project and
 * welcome
 */
public abstract class Item<I extends Item, P extends Item, C extends Item> {

    private final transient ObjectProperty<P> parent = new SimpleObjectProperty<>();
    private final ReadOnlySetWrapper<C> children = new ReadOnlySetWrapper<>(FXCollections.observableSet(new LinkedHashSet<>()));

    private final StringProperty name = new SimpleStringProperty("");
    private final StringProperty comments = new SimpleStringProperty("");
    //DO NOT CHANGE ORDER!!!
    private final transient ReadOnlyObjectWrapper<ItemType> itemType = new ReadOnlyObjectWrapper<>(ItemType.valueOf(this));
    private final transient ReadOnlyObjectWrapper<Site<I>> site = new ReadOnlyObjectWrapper<>(Site.createSite(this));

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

    protected Item(P parent, String name) {
        parentProperty().set(parent);
        if (name != null) {
            nameProperty().set(name);
        }
        addListeners();
        if (parentProperty().get() != null) {
            parentProperty().get().childrenProperty().add(this);
        }
    }

    public void move(P newParent) {
        //parentProperty().set(null);
        if (parentProperty().get() != null) {
            parentProperty().get().childrenProperty().remove(this);
        }
        parentProperty().set(newParent);
        parentProperty().get().childrenProperty().add(this);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty commentsProperty() {
        return comments;
    }

    public ObjectProperty<P> parentProperty() {
        return parent;
    }

    public ReadOnlyObjectProperty<ItemType> typeProperty() {
        return itemType.getReadOnlyProperty();
    }

    public ReadOnlyObjectProperty<Site<I>> siteProperty() {
        return site.getReadOnlyProperty();
    }

    public void addChildren(C children) {
        childrenProperty().add(children);
    }

    public ReadOnlySetProperty<C> childrenProperty() {
        return children.getReadOnlyProperty();
    }

    @Override
    public String toString() {
        return nameProperty().get();
    }

    public void destroy() {
        childrenProperty().forEach(c -> c.destroy());
        parentProperty().set(null);
        childrenProperty().clear();
    }

    private void addListeners() {
        childrenProperty().addListener((SetChangeListener.Change<? extends C> change) -> {
            if (change.wasAdded()) {
                C elementAdded = change.getElementAdded();
                ItemTreeView.onItemAdded(elementAdded);
                ItemTabPane.onItemAdded(elementAdded);
            } else if (change.wasRemoved()) {
                C elementRemoved = change.getElementRemoved();
                ItemTreeView.onItemRemoved(elementRemoved);
                ItemTabPane.onItemRemoved(elementRemoved);
            }
        });
    }

    public StringExpression fullNameProperty() {
        StringProperty sp = new SimpleStringProperty();
        if (parentProperty().get() != null) {
            sp.bind(parentProperty().get().fullNameProperty().concat(".").concat(nameProperty()));
        } else {
            sp.bind(nameProperty());
        }
        parentProperty().addListener((ObservableValue<? extends P> observable, P oldValue, P newValue) -> {
            if (parentProperty().get() != null) {
                sp.bind(parentProperty().get().fullNameProperty().concat(".").concat(nameProperty()));
            } else {
                sp.bind(nameProperty());
            }
        });
        return sp;
    }

    @Override
    public int hashCode() {
        return fullNameProperty().get().hashCode();
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
        return this.fullNameProperty().get().equals(other.fullNameProperty().get());
    }

    //Utils
    public Optional<P> getParent() {
        return Optional.ofNullable(parentProperty().get());
    }

    public Set<C> getChildren() {
        return Collections.silentUnmodifiableSet(childrenProperty().get());
    }

    public String getName() {
        return nameProperty().get();
    }

    public void setName(String name) {
        nameProperty().set(name);
    }

    public String getComments() {
        return commentsProperty().get();
    }

    public void setComments(String comments) {
        commentsProperty().set(comments);
    }

    public ItemType getType() {
        return typeProperty().get();
    }

    public String getFullName() {
        return fullNameProperty().get();
    }

    public Site getSite() {
        return siteProperty().get();
    }

}
