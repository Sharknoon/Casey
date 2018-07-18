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
package sharknoon.dualide.logic.statements;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.commons.lang3.EnumUtils;
import sharknoon.dualide.logic.ValueReturnable;
import sharknoon.dualide.logic.items.Function;
import sharknoon.dualide.logic.items.Items;
import sharknoon.dualide.logic.statements.calls.Call;
import sharknoon.dualide.logic.statements.calls.CallItem;
import sharknoon.dualide.logic.statements.operators.Operator;
import sharknoon.dualide.logic.statements.operators.OperatorType;
import sharknoon.dualide.logic.statements.values.Value;
import sharknoon.dualide.logic.statements.values.ValueType;
import sharknoon.dualide.logic.types.Type;
import sharknoon.dualide.ui.bodies.Body;
import sharknoon.dualide.utils.jackson.JacksonUtils;
import sharknoon.dualide.utils.settings.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * The statement is the base class for values, operators, ...
 *
 * @param <PT> The parent type of this statement
 * @param <T>  The type of this statement
 * @param <CT> The child type of this statement
 * @author Josua Frank
 */
public abstract class Statement<PT extends Type, T extends Type, CT extends Type> implements ValueReturnable<T> {
    
    public static Statement<?, ?, ?> deserialize(Statement<?, ?, ?> parent, ObjectNode properties) {
        try {
            String type = properties.get("type").asText("");
            if (EnumUtils.isValidEnum(ValueType.class, type)) {
                Object value = JacksonUtils.fromNode(properties.get("value"));
                return ValueType.valueOf(type).create(value, parent);
            } else if (EnumUtils.isValidEnum(OperatorType.class, type)) {
                Operator operator = OperatorType.valueOf(type).create(parent);
                var parameters = (ArrayNode) properties.get("parameter");
                parameters.elements().forEachRemaining(p -> {
                    operator.addParameter(deserialize(operator, (ObjectNode) p));
                });
                return operator;
            } else if (Objects.equals(type, "CALL")) {
                Call<?> c = new Call(parent, Type.UNDEFINED);//TODO not undefined
                ArrayNode calls = (ArrayNode) properties.get("calls");
                calls.iterator().forEachRemaining(call -> {
                    String itemType = call.get("type").asText();
                    var itemOptional = Items.forName(itemType);//always a variable, function or parameter
                    assert itemOptional.isPresent();
                    var item = itemOptional.get();
                    CallItem callItem = new CallItem(c, item, false);
                    if (item instanceof Function) {
                        var parameter = (ArrayNode) call.get("parameter");
                        parameter.elements().forEachRemaining(e -> {
                            callItem.getChilds().add(deserialize(callItem, (ObjectNode) e));
                        });
                    }
                });
                return c;
            }
        } catch (Exception e) {
            Logger.error("Error during Statement deserialisation", e);
        }
        return null;
    }
    protected final ReadOnlyListWrapper<Statement<T, CT, Type>> childs = new ReadOnlyListWrapper<>(FXCollections.observableArrayList());
    private final transient ReadOnlyObjectWrapper<Statement<Type, PT, T>> parent = new ReadOnlyObjectWrapper<>();
    private final ReadOnlyObjectWrapper<Body<Statement>> body = new ReadOnlyObjectWrapper<>();
    private final List<Runnable> changeListeners = new ArrayList<>();
    
    public void initParent(Statement<Type, PT, T> parent, boolean addChildToParent) {
        if (parent != null) {
            this.parent.set(parent);
            if ((parentProperty().get() instanceof Value) && addChildToParent) {//Operators are managing their childs itself
                parentProperty().get().childs.add((Statement) this);
            }
        }
        childs.addListener((observable, oldValue, newValue) -> onChange());
    }
    
    public ReadOnlyObjectProperty<Statement<Type, PT, T>> parentProperty() {
        return parent.getReadOnlyProperty();
    }
    
    public ListProperty<Statement<T, CT, Type>> childsProperty() {
        return childs;
    }
    
    public ObservableList<Statement<T, CT, Type>> getChilds() {
        return childsProperty();
    }
    
    public Body<Statement> getBody() {
        if (body.get() == null) {
            body.set(Body.createBody(this));
        }
        return body.get();
    }
    
    public void destroy() {
        destroy_impl();
        if (parentProperty().get() != null && !(parentProperty().get() instanceof Operator)) {
            parentProperty().get().childs.remove(this);
        }
    }
    
    private void destroy_impl() {
        childs.forEach(c -> {
            if (c != null) {
                c.destroy_impl();
            }
        });
        childs.clear();
        Body body = getBody();
        if (body != null) {
            body.destroy();
        }
    }
    
    protected void onChange() {
        changeListeners.forEach(Runnable::run);
        if (parentProperty().get() != null) {
            parentProperty().get().onChange();
        }
    }
    
    public void addChangeListener(Runnable onChange) {
        changeListeners.add(onChange);
    }
    
    /**
     * You shouldnt need this method, use the resultproperty instead
     *
     * @return
     */
    public abstract Value<T> calculateResult();
    
    @Override
    public abstract String toString();
    
    //to be overridden
    
    public abstract Map<String, JsonNode> getAdditionalProperties();
    
}
