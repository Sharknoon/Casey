package sharknoon.casey.ide.utils.jackson;/*
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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.TextNode;

public class JacksonUtils {
    
    public static JsonNode toNode(Object o) {
        if (o instanceof Boolean) {
            return BooleanNode.valueOf((Boolean) o);
        } else if (o instanceof Double) {
            return DoubleNode.valueOf((Double) o);
        } else if (o instanceof String) {
            return TextNode.valueOf((String) o);
        } else {
            return NullNode.getInstance();
        }
    }
    
    public static Object fromNode(JsonNode n) {
        if (n.isBoolean()) {
            return n.asBoolean();
        } else if (n.isDouble()) {
            return n.asDouble();
        } else if (n.isTextual()) {
            return n.asText();
        } else {
            return null;
        }
    }
    
}
