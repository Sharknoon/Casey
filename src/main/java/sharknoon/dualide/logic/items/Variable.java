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
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.TextNode;
import java.util.Map;
import java.util.Optional;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import sharknoon.dualide.logic.statements.values.ValueType;

/**
 *
 * @author Josua Frank
 */
public class Variable extends Item<Variable, Item<? extends Item, ? extends Item, Variable>, Item<? extends Item, Variable, ? extends Item>> {

    private final ObjectProperty<Class> objectType = new SimpleObjectProperty<>();
    private final ObjectProperty<ValueType> valueType = new SimpleObjectProperty<>(ValueType.TEXT);//Default is a empty text
    private final BooleanProperty isPrimitive = new SimpleBooleanProperty(true);
    private static final String TYPE = "type";
    private final BooleanProperty modifiable = new SimpleBooleanProperty(true);
    private static final String MODIFIABLE = "modifiable";

    protected Variable(Item<? extends Item, ? extends Item, Variable> parent, String name) {
        super(parent, name);
    }

    @Override
    public Map<String, JsonNode> getAdditionalProperties() {
        Map<String, JsonNode> map = super.getAdditionalProperties();
        String typeString = isPrimitive.get()
                ? valueType.get() != null ? valueType.get().name() : ""
                : objectTypeProperty().get() != null ? objectTypeProperty().get().getFullName() : "";
        map.put(TYPE, TextNode.valueOf(typeString));
        map.put(MODIFIABLE, BooleanNode.valueOf(modifiable.get()));
        return map;
    }

    @Override
    public void setAdditionalProperties(Map<String, JsonNode> properties) {
        properties.forEach((key, value) -> {
            switch (key) {
                case MODIFIABLE:
                    modifiable.set(value.asBoolean(true));
                    break;
                case TYPE:
                    try {
                        ValueType vt = ValueType.valueOf(value.asText());
                        valueType.set(vt);
                        isPrimitive.set(true);
                    } catch (Exception e) {
                        Optional<Class> clazz = Class.forName(value.asText());
                        clazz.ifPresent(c -> {
                            objectType.set(c);
                            isPrimitive.set(false);
                        });
                    }
                    break;
            }
        });
    }

    public ObjectProperty<Class> objectTypeProperty() {
        return objectType;
    }
    
    public ObjectProperty<ValueType> ValueTypeProperty(){
        return valueType;
    }

    public BooleanProperty modifiableProperty() {
        return modifiable;
    }

}
