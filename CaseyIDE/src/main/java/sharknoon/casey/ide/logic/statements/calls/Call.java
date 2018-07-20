package sharknoon.casey.ide.logic.statements.calls;/*
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
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.ObjectExpression;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import sharknoon.casey.ide.logic.ValueReturnable;
import sharknoon.casey.ide.logic.items.Item;
import sharknoon.casey.ide.logic.statements.Statement;
import sharknoon.casey.ide.logic.statements.values.Value;
import sharknoon.casey.ide.logic.types.Type;
import sharknoon.casey.ide.serial.Serialisation;
import sharknoon.casey.ide.utils.javafx.BindUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Call<I extends Item<?, ?, ?> & ValueReturnable> extends Statement<Type, Type, Type> {
    
    private static final ObjectBinding<Type> UNDEFINED = Bindings.createObjectBinding(() -> Type.UNDEFINED);
    private static final String typeKey = "type";
    private static final String callsKey = "calls";
    private final ReadOnlyObjectWrapper<Statement<Type, Type, Type>> firstChild = new ReadOnlyObjectWrapper<>();
    private final ReadOnlyObjectWrapper<Statement<Type, Type, Type>> lastChild = new ReadOnlyObjectWrapper<>();
    private final ReadOnlyObjectWrapper<Type> returnType = new ReadOnlyObjectWrapper<>();
    private final Type expectedReturnType;
    
    public Call(Statement<Type, Type, Type> parent, Item<?, ?, ?> startCall, Type expectedReturnType) {
        this(parent, expectedReturnType);
        new CallItem(this, startCall);
    }
    
    public Call(Statement<Type, Type, Type> parent, Type expectedReturnType) {
        initParent(parent, true);
        this.firstChild.bind(bindFirstChild(childs));
        this.lastChild.bind(bindLastChild(childs));
        this.returnType.bind(bindReturnType(lastChild));
        this.expectedReturnType = expectedReturnType;
        addAutoDestroyOnEmptyCallItems();
    }
    
    private ObjectExpression<Statement<Type, Type, Type>> bindFirstChild(ObservableList<Statement<Type, Type, Type>> childs) {
        return Bindings.valueAt(childs, 0);
    }
    
    private ObjectExpression<Statement<Type, Type, Type>> bindLastChild(ObservableList<Statement<Type, Type, Type>> childs) {
        return BindUtils.getLast(childs);
    }
    
    private ObjectExpression<Type> bindReturnType(ObjectExpression<Statement<Type, Type, Type>> lastChild) {
        ObjectProperty<Type> returnType = new SimpleObjectProperty<>();
        lastChild.addListener((o, old, new_) -> {
            if (new_ != null && new_.getReturnType() != null) {
                returnType.bind(new_.returnTypeProperty());
            } else {
                returnType.bind(UNDEFINED);
            }
        });
        return returnType;
    }
    
    private void addAutoDestroyOnEmptyCallItems() {
        childsProperty().emptyProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                destroy();
            }
        });
    }
    
    public BooleanExpression isExtensible() {
        return Bindings.createBooleanBinding(() -> {
            Type type = returnType.get();
            return type != null && type.getReturnType().isObject();
        }, returnType);
    }
    
    public BooleanExpression isReducible() {
        return Bindings.size(childs).greaterThan(1);
    }
    
    public ObjectExpression<Statement<Type, Type, Type>> firstChildProperty() {
        return firstChild.getReadOnlyProperty();
    }
    
    public ObjectExpression<Statement<Type, Type, Type>> lastChildProperty() {
        return lastChild.getReadOnlyProperty();
    }
    
    @Override
    public Type getReturnType() {
        return returnTypeProperty().get();
    }
    
    @Override
    public ReadOnlyObjectProperty<Type> returnTypeProperty() {
        return returnType.getReadOnlyProperty();
    }
    
    @Override
    public Value<Type> calculateResult() {
        return getReturnType().createEmptyValue(parentProperty().get());
    }
    
    @Override
    public String toString() {
        return childs
                .stream()
                .map(String::valueOf)
                .collect(Collectors.joining(" -> "));
    }
    
    @Override
    public Map<String, JsonNode> getAdditionalProperties() {
        Map<String, JsonNode> map = new HashMap<>();
        
        String type = "CALL";
        List<JsonNode> calls = getChilds().stream()
                .map(s -> s != null ? s.getAdditionalProperties() : null)
                .map(m -> Serialisation.MAPPER.convertValue(m, JsonNode.class))
                .collect(Collectors.toList());
        
        map.put(typeKey, TextNode.valueOf(type));
        map.put(callsKey, Serialisation.MAPPER.convertValue(calls, ArrayNode.class));
        
        return map;
    }
    
    public Type getExpectedReturnType() {
        return expectedReturnType;
    }
}
