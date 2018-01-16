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
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import org.hildan.fxgson.FxGson;
import sharknoon.dualide.serial.ClassTypeAdapter;
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
public abstract class Item<I extends Item, P extends Item, C extends Item> {

    private final transient ObjectProperty<P> parent = new SimpleObjectProperty<>();
    private final ObservableSet<C> children = FXCollections.observableSet(new LinkedHashSet<>());

    private final StringProperty name = new SimpleStringProperty("");
    private final StringProperty comments = new SimpleStringProperty("");
    private final transient ObjectProperty<java.lang.Class> type = new SimpleObjectProperty<>();
    private final transient ObjectProperty<Site<I>> site = new SimpleObjectProperty<>(createSite());

    public Item(P parent, String name) {
        setType((java.lang.Class<I>) this.getClass());
        setName(name);
        onChange();
        setParent(parent);
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

    public void setParent(P parent) {
        parentProperty().set(parent);
        if (parent != null) {
            //Backed by a set, so if its already in it, it changes nothing
            parent.addChildren(this);
        }

    }

    public ObjectProperty<P> parentProperty() {
        return parent;
    }

    public java.lang.Class getType() {
        return typeProperty().get();
    }

    public void setType(java.lang.Class type) {
        typeProperty().set(type);
    }

    public ObjectProperty<java.lang.Class> typeProperty() {
        return type;
    }

    public Site<I> getSite() {
        return siteProperty().get();
    }

    public ObjectProperty<Site<I>> siteProperty() {
        return site;
    }

    protected abstract Site<I> createSite();

    public ObservableSet<C> getChildren() {
        return children;
    }

    public void addChildren(C children) {
        getChildren().add(children);
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
                ItemTreeView.onItemAdded(elementAdded, this);
                ItemTabPane.onItemAdded(elementAdded);
                elementAdded.setParent(this);//For filling up this item by gson
            } else if (change.wasRemoved()) {
                C elementRemoved = change.getElementRemoved();
                ItemTreeView.onItemRemoved(elementRemoved, this);
                ItemTabPane.onItemRemoved(elementRemoved);
            }
        });
    }

    public void test() {
        String json = FxGson
                .fullBuilder()
                .setPrettyPrinting()
                .registerTypeHierarchyAdapter(Class.class, new ClassTypeAdapter())
                .create()
                .toJson(this);
        System.out.println(json);
    }

}
