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
package sharknoon.dualide.logic;

import java.util.Iterator;
import java.util.LinkedHashSet;
import javafx.beans.property.ObjectProperty;
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
import sharknoon.dualide.utils.language.Language;
import sharknoon.dualide.utils.language.Word;

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
    private final transient ObjectProperty<Site<I>> site = new SimpleObjectProperty<>();
    private final transient StringProperty fullName = new SimpleStringProperty();
    private final transient ObjectProperty<Type> type = new SimpleObjectProperty<>(Type.valueOf(this.getClass()));

    /**
     * can return null!!!
     *
     * @param <ITEM>
     * @param itemType
     * @param parent
     * @param name
     * @return
     */
    public static <ITEM extends Item> ITEM createItem(Type itemType, Item parent, String name) {
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
        setSite(Site.createSite(this));
        setParent(parent);
        setName(name);
        onChange();
    }

    @Override
    public void postProcess() {
        setSite(Site.createSite(this));
        getChildren().forEach(c -> c.setParent(this));
        onChange();
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
        if (parent != null) {
            //Backed by a set, so if its already in it, it changes nothing
            parent.addChildren(this);
        }

    }

    public ObjectProperty<P> parentProperty() {
        return parent;
    }

    public ObjectProperty<Type> typeProperty() {
        return type;
    }

    public Type getType() {
        return typeProperty().get();
    }

    private void setSite(Site<I> site) {
        siteProperty().set(site);
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

    private void onChange() {
        getChildren().addListener((SetChangeListener.Change<? extends C> change) -> {
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

    /**
     * Also the unique id
     *
     * @return
     */
    public String getFullName() {
        if (fullName.get() == null) {
            if (getParent() != null && getParent().getClass() != Welcome.class) {
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
