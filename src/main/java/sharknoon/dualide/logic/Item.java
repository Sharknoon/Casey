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

import java.util.LinkedHashSet;
import java.util.Set;
import sharknoon.dualide.ui.sites.Site;
import sharknoon.dualide.utils.collection.Collections;

/**
 *
 * @author Josua Frank
 * @param <I>
 * @param <P>
 * @param <C>
 */
public abstract class Item<I extends Item, P extends Item, C extends Item> {

    private P parent;
    private final Set<C> children = new LinkedHashSet<>();

    private String name;
    private String comments = "";
    private Site<I> site;

    public Item(P parent, String name) {
        setParent(parent);
        setName(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? "" : name;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments == null ? "" : comments;
    }

    public P getParent() {
        return parent;
    }

    public void setParent(P parent) {
        this.parent = parent;
        if (parent != null) {
            //Backed by a set, so if its already in it, it changes nothing
            parent.addChildren(this);
        }

    }

    public Site<I> getSite() {
        if (site == null) {
            site = createSite();
        }
        return site;
    }

    protected abstract Site<I> createSite();

    public Set<C> getChildren() {
        return Collections.silentUnmodifiableSet(children);
    }

    public void addChildren(C children) {
        this.children.add(children);
    }

}
