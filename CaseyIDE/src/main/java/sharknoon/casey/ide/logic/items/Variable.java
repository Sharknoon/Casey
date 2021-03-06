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
package sharknoon.casey.ide.logic.items;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import sharknoon.casey.ide.logic.ValueHoldable;
import sharknoon.casey.ide.logic.ValueReturnable;
import sharknoon.casey.ide.logic.types.PrimitiveType;
import sharknoon.casey.ide.logic.types.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Josua Frank
 */
public class Variable extends Item<Variable, Item<? extends Item, ? extends Item, Variable>, Item<? extends Item, Variable, ? extends Item>> implements ValueReturnable<Type>, ValueHoldable<Type> {

    private static final String TYPE = "type";
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
        returnTypeProperty().addListener((observable, oldValue, newValue) -> {
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
    public void destroy() {
        super.destroy();
        Type returnType = getReturnType();
        if (returnType != null && VARIABLES.containsKey(returnType)) {
            VARIABLES.get(returnType).remove(this);
        }
    }
    
    @Override
    public Map<String, JsonNode> getAdditionalProperties() {
        Map<String, JsonNode> map = super.getAdditionalProperties();
        String typeString = "";
        if (type.get() != null) {
            typeString = type.get().fullNameProperty().get();
        }
        map.put(TYPE, TextNode.valueOf(typeString));
        return map;
    }

    @Override
    public void setAdditionalProperties(Map<String, JsonNode> properties) {
        properties.forEach((key, value) -> {
            switch (key) {
                case TYPE:
                    Type.valueOf(value.asText()).ifPresent(type::set);
                    break;
            }
        });
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
