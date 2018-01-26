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

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonWriter;
import com.sun.xml.internal.ws.developer.Serialization;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import org.dizitart.no2.Document;
import org.dizitart.no2.mapper.NitriteMapper;
import java.util.Map;

/**
 *
 * @author Josua Frank
 */
public class GSONMapper implements NitriteMapper {

    private JsonParser parser;

    public GSONMapper() {
        getJsonParser();
    }

    @Override
    public <T> Document asDocument(T object) {
        JsonElement jsonElement = Serialisation.GSON.toJsonTree(object);
        return loadDocument(jsonElement);
    }

    @Override
    public <T> T asObject(Document document, Class<T> type) {
        Type documentType = new TypeToken<Map<String, Object>>() {
        }.getType();
        JsonElement elem = Serialisation.GSON.toJsonTree(document, documentType);
        return Serialisation.GSON.fromJson(elem, type);
    }

    @Override
    public boolean isValueType(Object object) {
        JsonElement jsonTree = Serialisation.GSON.toJsonTree(object);
        return !jsonTree.isJsonNull() && jsonTree.isJsonPrimitive();
    }

    @Override
    public Object asValue(Object object) {
        JsonElement jsonTree = Serialisation.GSON.toJsonTree(object);
        if (jsonTree.isJsonPrimitive()) {
            JsonPrimitive primitive = jsonTree.getAsJsonPrimitive();
            if (primitive.isBoolean()) {
                return primitive.getAsBoolean();
            } else if (primitive.isNumber()) {
                return primitive.getAsNumber();
            } else if (primitive.isString()) {
                return primitive.getAsString();
            }
        }
        return null;
    }

    @Override
    public Document parse(String json) {
        JsonElement element = getJsonParser().parse(json);
        return loadDocument(element);
    }

    @Override
    public String toJson(Object object) {
        return Serialisation.GSON.toJson(object);
    }

    private JsonParser getJsonParser() {
        if (parser == null) {
            parser = new JsonParser();
        }
        return parser;
    }

    private Document loadDocument(JsonElement element) {
        Map<String, Object> objectMap = new LinkedHashMap<>();
        if (element.isJsonObject()) {
            JsonObject jsonObject = element.getAsJsonObject();
            jsonObject.entrySet().forEach((entry) -> {
                String key = entry.getKey();
                JsonElement value = entry.getValue();
                Object object = loadObject(value);
                objectMap.put(key, object);
            });
        }
        return new Document(objectMap);
    }

    private Object loadObject(JsonElement element) {
        if (element == null) {
            return null;
        }
        if (element.isJsonArray()) {
            return loadArray(element.getAsJsonArray());
        } else if (element.isJsonNull()) {
            return null;
        } else if (element.isJsonObject()) {
            return loadDocument(element);
        } else if (element.isJsonPrimitive()) {
            JsonPrimitive primitive = element.getAsJsonPrimitive();
            if (primitive.isBoolean()) {
                return primitive.getAsBoolean();
            } else if (primitive.isNumber()) {
                return primitive.getAsNumber();
            } else if (primitive.isString()) {
                return primitive.getAsString();
            }
        }
        return null;
    }

    private List loadArray(JsonArray array) {
        List list = new ArrayList();
        Iterator<JsonElement> iterator = array.iterator();
        while (iterator.hasNext()) {
            JsonElement element = iterator.next();
            list.add(loadObject(element));
        }
        return list;
    }

}
