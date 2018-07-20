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
package sharknoon.casey.ide.serial;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import sharknoon.casey.ide.logic.items.Item;
import sharknoon.casey.ide.logic.items.ItemType;
import sharknoon.casey.ide.logic.items.Project;
import sharknoon.casey.ide.utils.settings.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

/**
 *
 * @author Josua Frank
 */
public class Serialisation {

    private static final String NAME = "name";
    private static final String COMMENTS = "comments";
    private static final String ITEM = "item";
    private static final String CHILDREN = "children";
    
    public static final ObjectMapper MAPPER = new ObjectMapper();

    private static final Map<Item, Map<String, JsonNode>> ADDITIONAL_NODES = new HashMap<>();

    private static Optional<Item> deserializeItem(ObjectNode item, Item... parentItem) {
        JsonNode nameNode = null;
        JsonNode commentsNode = null;
        JsonNode typeNode = null;
        JsonNode childrenNode = null;
        Map<String, JsonNode> additionalNodes = null;
        for (Iterator<Entry<String, JsonNode>> it = item.fields(); it.hasNext();) {
            Entry<String, JsonNode> f = it.next();
            switch (f.getKey()) {
                case NAME:
                    nameNode = f.getValue();
                    break;
                case COMMENTS:
                    commentsNode = f.getValue();
                    break;
                case ITEM:
                    typeNode = f.getValue();
                    break;
                case CHILDREN:
                    childrenNode = f.getValue();
                    break;
                default:
                    if (additionalNodes == null) {
                        additionalNodes = new HashMap<>();
                    }
                    additionalNodes.put(f.getKey(), f.getValue());
                    break;
            }
        }

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
            if (additionalNodes != null) {
                ADDITIONAL_NODES.put(result, additionalNodes);
            }
        } else {
            return Optional.empty();
        }
        if (children != null) {
            while (children.hasNext()) {
                JsonNode child = children.next();
                if (child.isObject()) {
                    deserializeItem((ObjectNode) child, result);//safe cast
                }
            }
        }
        if (parentItem.length < 1) {
            ADDITIONAL_NODES.forEach(Item::setAdditionalProperties);
            ADDITIONAL_NODES.clear();
        }
        return Optional.ofNullable(result);
    }

    private static Optional<ObjectNode> serializeItem(Item<?, ?, ?> item) {
        ObjectNode object = MAPPER.createObjectNode();
        object.put(NAME, item.getName());
        object.put(COMMENTS, item.getComments());
        object.put(ITEM, item.getType().name().toUpperCase());
        object.setAll(item.getAdditionalProperties());

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
            Optional<Item> item = deserializeItem((ObjectNode) MAPPER.readTree(path.toFile()));
            Optional<Project> project = item.map(i -> (Project) i);
            project.ifPresent(p -> {
                p.setSaveFile(path);
            });
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
                    MAPPER.writerWithDefaultPrettyPrinter().writeValue(saveFile.get().toFile(), jsonNode.get());
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
