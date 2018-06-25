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
package sharknoon.dualide.logic.items;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import sharknoon.dualide.serial.Serialisation;
import sharknoon.dualide.ui.MainApplication;
import sharknoon.dualide.ui.sites.welcome.RecentProject;
import sharknoon.dualide.utils.language.Language;
import sharknoon.dualide.utils.language.Word;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 *
 * @author Josua Frank
 */
public class Project extends Item<Project, Item, Package> {

    private String id;
    private static final String ID = "id";
    private final ObjectProperty<Path> saveFile = new SimpleObjectProperty<>();
    private static ObjectProperty<Project> currentProject = new SimpleObjectProperty<>();

    protected Project(Welcome parent, String name) {
        super(parent, name);
        init();
    }

    private void init() {
        currentProject.set(this);
        MainApplication.registerExitable(this::save);
    }

    public void setID(String id) {
        this.id = id;
    }

    public final String getID() {
        return id;
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
        return Optional.ofNullable(currentProject.get());
    }

    public static ObjectProperty<Project> currentProjectProperty() {
        return currentProject;
    }

    @Override
    public Map<String, JsonNode> getAdditionalProperties() {
        Map<String, JsonNode> result = new HashMap<>();
        TextNode idNode = TextNode.valueOf(getID());
        result.put("id", idNode);
        return result;
    }

    @Override
    public void setAdditionalProperties(Map<String, JsonNode> properties) {
        properties.forEach((k, v) -> {
            switch (k) {
                case ID:
                    id = v.asText();
                    break;
            }
        });
    }
    
    public void close() {
        save();
        currentProject.set(null);
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
            Window ownerWindow = Stage.getWindows().size() > 0 ? Stage.getWindows().get(0) : null;
            File file = chooser.showSaveDialog(ownerWindow);
            if (file != null) {
                saveFile.set(file.toPath());
            }
        }
        if (saveFile.get() != null) {//If the user closes the project without saving
            RecentProject.updateProject(this);
            Serialisation.saveProject(this);
        }
    }

}
