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

import java.util.Optional;
import org.json.simple.JSONObject;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import sharknoon.dualide.logic.Item;
import sharknoon.dualide.utils.settings.Logger;

/**
 *
 * @author Josua Frank
 */
public class Deserializer {

    private static final String TYPE = "type";
    private static final String NAME = "name";
    private static final String COMMENTS = "comments";
    private static final String CHILDREN = "children";

    public static Optional<Item> fromJSON(String json) {
        try {
            Object object = new JSONParser().parse(json);
            if (object instanceof JSONObject) {
                JSONObject root = (JSONObject) object;
                return fromJSON(root);
            } else {
                Logger.error("Could not load existing project (JSON-Root object not a object)");
            }
        } catch (ParseException ex) {
            Logger.error("Could not load existing project", ex);
        }
        return Optional.empty();
    }
    
    private static final String EMPTY = "";
    
    private static Optional<Item> fromJSON(JSONObject object){
        String type = (String) object.get(TYPE);
        String name = (String) object.get(NAME);
        String comments = (String) object.getOrDefault(COMMENTS, EMPTY);
        
        
        return Optional.empty();
    }

}
