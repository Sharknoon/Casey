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

import java.nio.file.Path;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import sharknoon.dualide.ui.sites.Site;
import sharknoon.dualide.ui.sites.project.ProjectSite;

/**
 *
 * @author Josua Frank
 */
public class Project extends Item<Project, Welcome, Package> {

    private final transient ObjectProperty<Path> saveFile = new SimpleObjectProperty<>();

    public Project(Welcome parent, String name) {
        super(parent, name);
    }

    @Override
    protected Site<Project> createSite() {
        return new ProjectSite(this);
    }

    public ObjectProperty<Path> saveFileProperty() {
        return saveFile;
    }

    public Path getSaveFile() {
        return saveFileProperty().get();
    }

    public void setSaveFile(Path path) {
        saveFileProperty().set(path);
    }

}
