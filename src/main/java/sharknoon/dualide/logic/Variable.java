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
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.Pane;
import sharknoon.dualide.logic.interfaces.ClassChildren;
import sharknoon.dualide.logic.interfaces.PackageChildren;
import sharknoon.dualide.logic.interfaces.VariableParent;

/**
 *
 * @author Josua Frank
 */
public class Variable extends Item implements ClassChildren, PackageChildren {

    private VariableParent parent;

    public Variable(String name) {
        super(name);
    }

    @Override
    public TreeItem createTreeItem() {
        TreeItem variableItem = new TreeItem(getName(), getIcon());
        return variableItem;
    }

    @Override
    public Pane getPane() {
        return new Pane();
    }
    
        @Override
    public Node getIcon() {
        return GlyphsDude.createIcon(FontAwesomeIcon.AMBULANCE);
    }
}
