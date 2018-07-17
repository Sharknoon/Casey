package sharknoon.dualide.logic.statements.calls;/*
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
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.TextNode;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import sharknoon.dualide.logic.ValueReturnable;
import sharknoon.dualide.logic.items.Item;
import sharknoon.dualide.logic.items.ItemType;
import sharknoon.dualide.logic.items.Parameter;
import sharknoon.dualide.logic.statements.Statement;
import sharknoon.dualide.logic.statements.values.Value;
import sharknoon.dualide.logic.types.Type;
import sharknoon.dualide.serial.Serialisation;
import sharknoon.dualide.utils.javafx.BindUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CallItem<I extends Item<Item, Item, Item> & ValueReturnable> extends Statement<Type, Type, Type> {
    
    private static final ObjectProperty<Type> UNDEFINED = new SimpleObjectProperty<>(Type.UNDEFINED);
    private final I item;
    //Only for Funktions
    private final ObservableList<Item> parameter;
    
    public CallItem(Statement<Type, Type, Type> parent, I item) {
        this.item = item;
        this.parameter = item.getChildren().filtered(i -> i.getType() == ItemType.PARAMETER);
        bindStatementChildrenToItemChildren();
        initParent(parent, false);
        addToParent(parent);
        item.onDestroy(parent::destroy);
    }
    
    private void addToParent(Statement<Type, Type, Type> parent) {
        ReadOnlyListProperty<Statement<Type, Type, Type>> childs = parent.childsProperty();
        if (childs.size() == 0 || childs.get(childs.size() - 1) != null) {
            childs.add(this);
        } else {
            childs.set(childs.size() - 1, this);
        }
    }
    
    private void bindStatementChildrenToItemChildren() {
        BindUtils.addListener(parameter, c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    for (int i = c.getFrom(); i < c.getTo(); i++) {
                        childs.add(i, null);
                    }
                }
                if (c.wasRemoved()) {
                    //+1 because when one item is removed, getfrom and getto are the same
                    childs.remove(c.getFrom(), c.getTo() + 1);
                }
            }
        });
    }
    
    public ObjectProperty<Type> getReturnTypePropertyForIndex(int index) {
        Parameter parameter = (Parameter) this.parameter.get(index);
        if (parameter != null) {
            return parameter.returnTypeProperty();
        }
        return new SimpleObjectProperty<>();
    }
    
    public ObjectProperty<Type> lastParameterTypeProperty() {
        ObjectBinding<Item> lastParameter = BindUtils.getLast(parameter);
        ObjectProperty<Type> lastParameterReturnType = new SimpleObjectProperty<>();
        BindUtils.addListener(lastParameter, (observable, oldValue, newValue) -> {
            if (newValue != null) {
                lastParameterReturnType.bind(((Parameter) newValue).returnTypeProperty());
            } else {
                lastParameterReturnType.bind(UNDEFINED);
            }
        });
        return lastParameterReturnType;
    }
    
    public I getItem() {
        return item;
    }
    
    @Override
    public Value<Type> calculateResult() {
        return item.getReturnType().createEmptyValue(parentProperty().get());
    }
    
    @Override
    public String toString() {
        return item.toString();
    }
    
    @Override
    public Type getReturnType() {
        return item.getReturnType();
    }
    
    @Override
    public ReadOnlyObjectProperty<Type> returnTypeProperty() {
        return item.returnTypeProperty();
    }
    
    private static final String typeKey = "type";
    private static final String parameterKey = "parameter";
    
    @Override
    public Map<String, JsonNode> getAdditionalProperties() {
        Map<String, JsonNode> map = new HashMap<>();
        
        String type = getItem().getFullName();
        List<JsonNode> parameter = getChilds().stream()
                .map(s -> s != null ? s.getAdditionalProperties() : null)
                .map(m -> Serialisation.MAPPER.convertValue(m, JsonNode.class))
                .collect(Collectors.toList());
        
        map.put(typeKey, TextNode.valueOf(type));
        map.put(parameterKey, Serialisation.MAPPER.convertValue(parameter, ArrayNode.class));
        
        return map;
    }
}