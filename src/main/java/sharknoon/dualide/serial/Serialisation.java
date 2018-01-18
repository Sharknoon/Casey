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
import java.util.stream.Collectors;
import org.hildan.fxgson.FxGson;
import sharknoon.dualide.logic.Function;
import sharknoon.dualide.logic.Item;
import sharknoon.dualide.logic.Project;
import sharknoon.dualide.logic.Variable;
import sharknoon.dualide.utils.settings.Logger;

/**
 *
 * @author Josua Frank
 */
public class Serialisation {

    private static final RuntimeTypeAdapterFactory<Item> ITEM_ADAPTER = RuntimeTypeAdapterFactory
            .of(Item.class, "type")
            .registerSubtype(sharknoon.dualide.logic.Class.class)
            .registerSubtype(Function.class)
            .registerSubtype(sharknoon.dualide.logic.Package.class)
            .registerSubtype(Project.class)
            .registerSubtype(Variable.class);
    private static final Gson GSON = FxGson
            .fullBuilder()
            .setPrettyPrinting()
            .registerTypeHierarchyAdapter(java.lang.Class.class, new ClassTypeAdapter())
            .registerTypeAdapterFactory(ITEM_ADAPTER)
            .registerTypeAdapterFactory(new PostProcessingEnabler())
            .create();

    private static Optional<Project> deserializeProject(String json) {
        try {
            return Optional.ofNullable(GSON.fromJson(json, Project.class));
        } catch (JsonSyntaxException e) {
            Logger.error("Could not load Project", e);
        }
        return Optional.empty();
    }

    private static String serializeProject(Item item) {
        return GSON.toJson(item);
    }

    public static Optional<Project> loadProject(Path path) {
        try {
            String json = new String(Files.readAllBytes(path));
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
}
