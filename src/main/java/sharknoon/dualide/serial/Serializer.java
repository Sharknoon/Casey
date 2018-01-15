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

import java.util.ArrayList;
import org.json.simple.JSONObject;
import sharknoon.dualide.logic.Item;
import sharknoon.dualide.logic.Welcome;

/**
 *
 * @author Josua Frank
 */
public class Serializer {

    private static final String TYPE = "type";
    private static final String NAME = "name";
    private static final String COMMENTS = "comments";
    private static final String CHILDREN = "children";

    public static String toJSON() {
        return toJSON(Welcome.getWelcome());
    }

    public static String toJSON(Item item) {
        JSONObject object = new JSONObject();
        //Type
        object.put(TYPE, item.getClass().getSimpleName().toLowerCase());
        //Name
        object.put(NAME, item.getName());
        //Eventually comments
        if (!item.getComments().isEmpty()) {
            object.put(COMMENTS, item.getComments());
        }
        //children
        ArrayList children = new ArrayList();
        item.getChildren().forEach((c) -> children.add(toJSON((Item) c)));
        object.put(CHILDREN, children);
        return object.toJSONString();
    }

}
