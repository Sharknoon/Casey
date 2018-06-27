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
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import sharknoon.dualide.logic.ValueReturnable;
import sharknoon.dualide.logic.types.PrimitiveType;
import sharknoon.dualide.logic.types.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Josua Frank
 */
public class Variable extends Item<Variable, Item<? extends Item, ? extends Item, Variable>, Item<? extends Item, Variable, ? extends Item>> implements ValueReturnable<Type> {

    private static final String TYPE = "type";
    private static final String MODIFIABLE = "modifiable";
    private static final ObservableMap<Type, List<Variable>> VARIABLES = FXCollections.observableHashMap();

    static {
        VARIABLES.addListener((MapChangeListener.Change<? extends Type, ? extends List<Variable>> change) -> {
            if (change.wasAdded()) {
                change.getKey().onDelete(() -> VARIABLES.remove(change.getKey()));
            }
        });
    }

    private final ObjectProperty<Type> type = new SimpleObjectProperty<>(PrimitiveType.TEXT);
    private final BooleanProperty modifiable = new SimpleBooleanProperty(true);

    protected Variable(Item<? extends Item, ? extends Item, Variable> parent, String name) {
        superInit(parent, name);
        typeProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != null && VARIABLES.containsKey(oldValue)) {
                VARIABLES.get(oldValue).remove(this);
            }
            if (!VARIABLES.containsKey(newValue)) {
                VARIABLES.put(newValue, new ArrayList<>());
            }
            VARIABLES.get(newValue).add(this);
        });
    }

    static Map<Type, List<Variable>> getAllVariables() {
        return VARIABLES;
    }

    @Override
    public Map<String, JsonNode> getAdditionalProperties() {
        Map<String, JsonNode> map = super.getAdditionalProperties();
        String typeString = "";
        if (type.get() != null) {
            typeString = type.get().fullNameProperty().get();
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
    
    @Override
    public ObjectProperty<Type> returnTypeProperty() {
        return type;
    }
    
    public boolean isInFunction() {
        return isIn(ItemType.FUNCTION);
    }

    public boolean isInClass() {
        return isIn(ItemType.CLASS);
    }

    public boolean isInPackage(){
        return isIn(ItemType.PACKAGE);
    }
}
