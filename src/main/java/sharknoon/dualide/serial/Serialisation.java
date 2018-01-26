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
package sharknoon.dualide.serial;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.hildan.fxgson.FxGson;
import sharknoon.dualide.logic.items.Class;
import sharknoon.dualide.logic.items.Function;
import sharknoon.dualide.logic.items.Item;
import sharknoon.dualide.logic.items.Package;
import sharknoon.dualide.logic.items.Project;
import sharknoon.dualide.logic.items.Variable;
import sharknoon.dualide.ui.sites.Dialogs;
import sharknoon.dualide.utils.settings.Logger;

/**
 *
 * @author Josua Frank
 */
public class Serialisation {

    private static final RuntimeTypeAdapterFactory<Item> ITEM_ADAPTER = RuntimeTypeAdapterFactory
            .of(Item.class, "item")
            .registerSubtype(Class.class)
            .registerSubtype(Function.class)
            .registerSubtype(Package.class)
            .registerSubtype(Project.class)
            .registerSubtype(Variable.class);
    public static final Gson GSON = FxGson
            .fullBuilder()
            .setPrettyPrinting()
            // Handles the abstract class Item to write the actual type of the item into the json
            .registerTypeAdapterFactory(ITEM_ADAPTER)
            //calls the method postProcess() in the class Item todo some further initialisation
            .registerTypeAdapterFactory(new PostProcessingEnabler())
            .create();

    private static Optional<Project> deserializeProject(String json) {
        Optional<Project> result = Optional.empty();
        try {
            result = Optional.ofNullable(GSON.fromJson(json, Project.class));
        } catch (JsonSyntaxException e) {
            Dialogs.showErrorDialog(Dialogs.Errors.PROJECT_CORRUPT_DIALOG, e);
            Logger.warning("Could not load Project", e);
        }
        return result;
    }

    private static String serializeProject(Item item) {
        return GSON.toJson(item);
    }

    public static Optional<Project> loadProject(Path path) {
        try {
            String json = new String(Files.readAllBytes(path));
            if (json.isEmpty()) {
                Exception e = new FileEmptyException("The Project file seems to be empty");
                Dialogs.showErrorDialog(Dialogs.Errors.PROJECT_CORRUPT_DIALOG, e);
                Logger.warning("Could not load Project", e);
                return Optional.empty();
            }
            Optional<Project> project = deserializeProject(json);
            project.ifPresent(p -> p.setSaveFile(path));
            return project;
        } catch (IOException e) {
            Logger.error("Could not read Project file", e);
        }
        return Optional.empty();
    }

    public static void saveProject(Project project) {
        try {
            Optional<Path> saveFile = project.getSaveFile();
            if (saveFile.isPresent()) {
                String json = serializeProject(project);
                List<String> lines = Arrays.asList(json.split("\\r?\\n"));
                Files.write(saveFile.get(), lines);
            }
        } catch (IOException ex) {
            Logger.error("Could not save File", ex);
        }
    }

    public static class FileEmptyException extends Exception {

        public FileEmptyException(String message) {
            super(message);
        }

    }
}
