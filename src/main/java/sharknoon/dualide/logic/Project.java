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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.Pane;
import sharknoon.dualide.utils.collection.Collections;

/**
 *
 * @author Josua Frank
 */
public class Project extends Item implements PackageParent {

    private static final Set<Project> PROJECTS = new LinkedHashSet<>();
    
    public static Set<Project> getAllProjects(){
        return Collections.silentUnmodifiableSet(PROJECTS);
    }
    
    private final List<Package> packages = new ArrayList<>();

    public Project(String name) {
        super(name);
        PROJECTS.add(this);
    }

    public List<Package> getPackages() {
        return Collections.silentUnmodifiableList(packages);
    }

    @Override
    public TreeItem createTreeItem() {
        TreeItem projectItem = new TreeItem(getName(), getIcon());
        packages.forEach(p -> projectItem.getChildren().add(p.createTreeItem()));
        return projectItem;
    }

    @Override
    public Pane getPane() {
        return new Pane();
    }

    @Override
    public Node getIcon() {
        return GlyphsDude.createIcon(FontAwesomeIcon.ALIGN_LEFT);
    }
}
