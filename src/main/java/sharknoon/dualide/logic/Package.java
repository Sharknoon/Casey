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

import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import sharknoon.dualide.logic.interfaces.PackageParent;
import sharknoon.dualide.logic.interfaces.PackageChildren;
import sharknoon.dualide.logic.interfaces.FunctionParent;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.Pane;
import sharknoon.dualide.logic.interfaces.VariableParent;
import sharknoon.dualide.utils.collection.Collections;

/**
 *
 * @author Josua Frank
 */
public class Package extends Item implements PackageChildren, PackageParent, FunctionParent, VariableParent {

    private PackageParent parent;

    private final List<PackageChildren> children = new ArrayList<>();

    public Package(String name) {
        super(name);
    }

    public List<PackageChildren> getChildren() {
        return Collections.silentUnmodifiableList(children);
    }

    @Override
    public TreeItem createTreeItem() {
        TreeItem packageItem = new TreeItem(getName(), getIcon());
        children.forEach(c -> packageItem.getChildren().add(c.createTreeItem()));
        return packageItem;
    }

    @Override
    public Pane getPane() {
        return new Pane();
    }

    @Override
    public Node getIcon() {
        return GlyphsDude.createIcon(FontAwesomeIcon.ALIGN_JUSTIFY);
    }
}