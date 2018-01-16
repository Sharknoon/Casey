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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.hildan.fxgson.FxGson;
import sharknoon.dualide.serial.ClassTypeAdapter;
import sharknoon.dualide.ui.sites.Site;
import sharknoon.dualide.ui.sites.project.ProjectSite;
import sharknoon.dualide.utils.language.Language;
import sharknoon.dualide.utils.language.Word;
import sharknoon.dualide.utils.settings.Logger;

/**
 *
 * @author Josua Frank
 */
public class Project extends Item<Project, Welcome, Package> {

    private final transient ObjectProperty<Path> saveFile = new SimpleObjectProperty<>();
    private static final transient ObjectProperty<Project> CURRENT_PROJECT = new SimpleObjectProperty<>();

    public Project(Welcome parent, String name) {
        super(parent, name);
        currentProjectProperty().set(this);
    }

    @Override
    protected Site<Project> createSite() {
        return new ProjectSite(this);
    }

    public static ObjectProperty<Project> currentProjectProperty() {
        return CURRENT_PROJECT;
    }

    public static Optional<Project> getCurrentProject() {
        return Optional.ofNullable(CURRENT_PROJECT.get());
    }

    public void save() {
        String json = FxGson
                .fullBuilder()
                .setPrettyPrinting()
                .registerTypeHierarchyAdapter(Class.class, new ClassTypeAdapter())
                .create()
                .toJson(this);
        Path pathToSaveTo = null;
        if (saveFile.get() != null) {
            pathToSaveTo = saveFile.get();
        } else {
            FileChooser chooser = new FileChooser();
            chooser.setInitialDirectory(new File(System.getProperty("user.home")));
            chooser.setInitialFileName(nameProperty().get());
            chooser.setTitle(Language.get(Word.SAVE_DIALOG_TITLE));
            chooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter(Language.get(Word.SAVE_DIALOG_EXTENSION_FILTER_DUALIDE_PROJECT), "*.dip")
            );
            File file = chooser.showSaveDialog(Window.impl_getWindows().next());
            if (file != null) {
                pathToSaveTo = file.toPath();
                saveFile.set(pathToSaveTo);
            }
        }
        if (pathToSaveTo != null) {
            try {
                List<String> lines = Arrays.asList(json.split("\\r?\\n"));
                Files.write(pathToSaveTo, lines);
            } catch (IOException ex) {
                Logger.error("Could not save File", ex);
            }
        }
    }

}
