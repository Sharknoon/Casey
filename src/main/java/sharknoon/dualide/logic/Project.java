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

import java.io.File;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Optional;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sharknoon.dualide.serial.Serialisation;
import sharknoon.dualide.ui.ItemTreeView;
import sharknoon.dualide.ui.sites.Site;
import sharknoon.dualide.ui.sites.project.ProjectSite;
import sharknoon.dualide.ui.sites.welcome.RecentProject;
import sharknoon.dualide.utils.language.Language;
import sharknoon.dualide.utils.language.Word;

/**
 *
 * @author Josua Frank
 */
public class Project extends Item<Project, Welcome, Package> {

    private final transient ObjectProperty<Path> saveFile = new SimpleObjectProperty<>();

    private Project() {
        super();
    }

    protected Project(Welcome parent, String name) {
        super(parent, name);
    }

    @Override
    public void postProcess() {
        super.postProcess();
        setParent(Welcome.getWelcome());
        ItemTreeView.refresh();
    }

    public Optional<Path> getSaveFile() {
        return Optional.ofNullable(saveFile.get());
    }

    public void setSaveFile(Path path) {
        if (path != null) {
            saveFile.set(path);
        }
    }

    public static Optional<Project> getCurrentProject() {
        Welcome welcome = Welcome.getWelcome();
        Iterator<Project> project = welcome.getChildren().iterator();
        if (project.hasNext()) {
            return Optional.ofNullable(project.next());
        }
        return Optional.empty();
    }

    public void save() {
        if (saveFile.get() == null) {//If the programm has no path (hasnt been saved yet)
            FileChooser chooser = new FileChooser();
            chooser.setInitialDirectory(new File(System.getProperty("user.home")));
            chooser.setInitialFileName(nameProperty().get());
            chooser.setTitle(Language.get(Word.SAVE_DIALOG_TITLE));
            chooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter(Language.get(Word.SAVE_DIALOG_EXTENSION_FILTER_DUALIDE_PROJECT), "*.dip")
            );
            File file = chooser.showSaveDialog(Stage.impl_getWindows().next());
            if (file != null) {
                saveFile.set(file.toPath());
                RecentProject.updateProject(this);
            }
        }
        if (saveFile.get() != null) {//If the user closes the project without saving
            Serialisation.saveProject(this);
        }
    }

}
