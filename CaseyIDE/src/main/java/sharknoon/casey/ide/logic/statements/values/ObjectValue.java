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
package sharknoon.casey.ide.logic.statements.values;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;
import sharknoon.casey.ide.logic.items.Class.ObjectType;
import sharknoon.casey.ide.logic.statements.Statement;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Josua Frank
 */
public class ObjectValue extends Value<ObjectType> {
    
    private static final String typeKey = "type";
    private static final String valueKey = "value";
    
    /**
     * @param type
     * @param parent
     * @return Null for wrong type
     */
    public static ObjectValue createObject(String type, Statement parent) {
        return ObjectType
                .forName(type)
                .map(ot -> new ObjectValue(ot, parent))
                .orElse(null);
    }
    
    public static ObjectValue createObject(ObjectType type, Statement parent) {
        return new ObjectValue(type, parent);
    }
    
    public ObjectValue(ObjectType type, Statement parent) {
        super(type, parent);
        if (parentProperty().get() != null && (parentProperty().get() instanceof Value)) {
            parentProperty().get().childsProperty().add(this);
        }
    }
    
    @Override
    public Value<ObjectType> calculateResult() {
        return this;
    }
    
    @Override
    public String toString() {
        return getReturnType().simpleNameProperty().get();
    }
    
    @Override
    public Map<String, JsonNode> getAdditionalProperties() {
        Map<String, JsonNode> map = new HashMap<>();
        
        String type = ObjectType.GENERAL.fullNameProperty().get();
        String value = getReturnType().fullNameProperty().get();
        
        map.put(typeKey, TextNode.valueOf(type));
        map.put(valueKey, TextNode.valueOf(value));
        
        return map;
    }
    
    @Override
    public int hashCode() {
        return getReturnType().hashCode();
        //TODO implement methods and paramteres
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ObjectValue other = (ObjectValue) obj;
        return Objects.equals(this.getReturnType(), other.getReturnType());
        //TODO implement methods and paramteres
    }
    
}
