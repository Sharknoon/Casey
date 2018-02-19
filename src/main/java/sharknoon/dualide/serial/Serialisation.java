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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import sharknoon.dualide.logic.items.Item;
import sharknoon.dualide.logic.items.ItemType;
import sharknoon.dualide.logic.items.Project;
import sharknoon.dualide.ui.dialogs.Dialogs;
import sharknoon.dualide.utils.settings.Logger;

/**
 *
 * @author Josua Frank
 */
public class Serialisation {

    private static final String NAME = "name";
    private static final String COMMENTS = "comments";
    private static final String ITEM = "item";
    private static final String CHILDREN = "children";
    
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static Optional<Item> deserializeItem(ObjectNode item, Item... parentItem) {
        JsonNode nameNode = item.get(NAME);
        JsonNode commentsNode = item.get(COMMENTS);
        JsonNode typeNode = item.get(ITEM);
        JsonNode childrenNode = item.get(CHILDREN);

        String name = nameNode != null && nameNode.isValueNode() ? nameNode.asText() : null;
        String comments = commentsNode != null && commentsNode.isValueNode() ? commentsNode.asText() : null;
        ItemType type = typeNode != null && typeNode.isValueNode() ? ItemType.valueOf(typeNode.asText().toUpperCase()) : null;
        Iterator<JsonNode> children = childrenNode != null && childrenNode.isArray() ? childrenNode.elements() : null;

        if (typeNode == null) {
            return Optional.empty();
        }
        Item result = Item.createItem(type, parentItem.length > 0 ? parentItem[0] : null, name);
        if (result != null) {
            result.setComments(comments);
        } else {
            return Optional.empty();
        }
        if (children != null) {
            while (children.hasNext()) {
                JsonNode child = children.next();
                if (child.isObject()) {
                    Optional<Item> childItem = deserializeItem((ObjectNode) child, result);//safe cast
                    if (childItem.isPresent()) {
                        result.addChildren(childItem.get());
                    }
                }
            }
        }
        return Optional.ofNullable(result);
    }

    private static Optional<ObjectNode> serializeItem(Item<? extends Item, ? extends Item, ? extends Item> item) {
        ObjectNode object = MAPPER.createObjectNode();
        object.put(NAME, item.getName());
        object.put(COMMENTS, item.getComments());
        object.put(ITEM, item.getType().name().toLowerCase());

        ArrayNode array = object.putArray(CHILDREN);
        item.childrenProperty().forEach(c -> {
            Optional<ObjectNode> sc = serializeItem(c);
            if (sc.isPresent()) {
                array.add(sc.get());
            }
        });
        return Optional.ofNullable(object);
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
            Optional<Item> item = deserializeItem((ObjectNode) new ObjectMapper().readTree(json));
            Optional<Project> project = item.map(i -> (Project) i);
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
                Optional<ObjectNode> jsonNode = serializeItem(project);
                if (jsonNode.isPresent()) {
                    String json = MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode.get());
                    List<String> lines = Arrays.asList(json.split("\\r?\\n"));
                    Files.write(saveFile.get(), lines);
                } else {
                    Logger.error("Could not save project " + project.getName());
                }
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
