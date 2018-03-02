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
import sharknoon.dualide.logic.Returnable;
import sharknoon.dualide.logic.types.PrimitiveType;
import sharknoon.dualide.logic.types.Type;

/**
 *
 * @author Josua Frank
 */
public class Variable extends Item<Variable, Item<? extends Item, ? extends Item, Variable>, Item<? extends Item, Variable, ? extends Item>> implements Returnable {

    private final ObjectProperty<Type> type = new SimpleObjectProperty<>(PrimitiveType.TEXT);
    private static final String TYPE = "type";
    private final BooleanProperty modifiable = new SimpleBooleanProperty(true);
    private static final String MODIFIABLE = "modifiable";

    protected Variable(Item<? extends Item, ? extends Item, Variable> parent, String name) {
        super(parent, name);
    }

    @Override
    public Map<String, JsonNode> getAdditionalProperties() {
        Map<String, JsonNode> map = super.getAdditionalProperties();
        String typeString = "";
        if (type.get() != null) {
            typeString = type.get().getFullName().get();
        }
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
                    Type.valueOf(value.asText()).ifPresent(type::set);
                    break;
            }
        });
    }

    public ObjectProperty<Type> typeProperty() {
        return type;
    }

    public BooleanProperty modifiableProperty() {
        return modifiable;
    }

    @Override
    public Type getReturnType() {
        return type.get();
    }

}
