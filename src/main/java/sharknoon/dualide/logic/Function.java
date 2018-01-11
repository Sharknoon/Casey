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
import sharknoon.dualide.logic.interfaces.ClassChildren;
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
public class Function extends Item implements PackageChildren, ClassChildren, VariableParent {

    private FunctionParent parent;

    private final List<Variable> variables = new ArrayList<>();

    public Function(String name) {
        super(name);
    }

    public List<Variable> getVariables() {
        return Collections.silentUnmodifiableList(variables);
    }

    @Override
    public TreeItem createTreeItem() {
        TreeItem functionItem = new TreeItem(getName(), getIcon());
        variables.forEach(v -> functionItem.getChildren().add(v.createTreeItem()));
        return functionItem;
    }

    @Override
    public Pane getPane() {
        return new Pane();
    }

    @Override
    public Node getIcon() {
        return GlyphsDude.createIcon(FontAwesomeIcon.ADN);
    }

}
