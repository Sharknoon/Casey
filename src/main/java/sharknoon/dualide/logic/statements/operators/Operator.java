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
package sharknoon.dualide.logic.statements.operators;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.TextNode;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import sharknoon.dualide.logic.statements.Statement;
import sharknoon.dualide.logic.types.PrimitiveType;
import sharknoon.dualide.logic.types.Type;
import sharknoon.dualide.serial.Serialisation;
import sharknoon.dualide.ui.bodies.Body;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @param <T>  type
 * @param <CT> Child type, if the type is unimportant, use the abstract type
 *             value
 * @author Josua Frank
 */
public abstract class Operator<T extends PrimitiveType, CT extends Type> extends Statement<Type, T, CT> {
    
    private static final String typeKey = "type";
    private static final String parameterKey = "parameter";
    private final T returnType;
    //A map instead of a list to leave empty spaces inbetween the indexes
    private final Type parameterType;
    private final int minimumParameters;
    private final int maximumParamerters;
    private final boolean isExtensible;
    
    public Operator(Statement parent, int minimumParameters, int maximumParameters, boolean isExtensible, T returnType) {
        this(parent, minimumParameters, maximumParameters, isExtensible, returnType, null);
    }
    
    public Operator(Statement parent, int minimumParameters, int maximumParameters, boolean isExtensible, T returnType, PrimitiveType parameterType) {
        initParent(parent, true);
        this.returnType = returnType;
        this.parameterType = parameterType != null ? parameterType : Type.UNDEFINED;
        this.minimumParameters = minimumParameters;
        for (int i = 0; i < minimumParameters; i++) {
            addParameter(null);
        }
        this.maximumParamerters = maximumParameters;
        this.isExtensible = isExtensible;
    }
    
    public void addParameter(Statement<T, CT, Type> parameter) {
        if (parameter != null) {//If the parameter is null, add it to the end, if not, replace the first null value
            for (int i = 0; i < childs.size(); i++) {
                if (childs.get(i) == null) {
                    setParameter(i, parameter);
                    return;
                }
            }
        }
        setParameter(getParameterAmount(), parameter);
    }
    
    public void removeLastParameter() {
        childs.remove(childs.size() - 1);
    }
    
    /**
     * Overrides the parameter
     *
     * @param index     starting at 0
     * @param parameter
     */
    public void setParameter(int index, Statement<T, CT, Type> parameter) {
        if (index < 0) {
            return;
        }
        while (index > getParameterAmount()) {
            childs.add(null);
        }
        if (getParameterAmount() > index) {
            childs.set(index, parameter);
        } else {
            childs.add(parameter);
        }
    }
    
    public Optional<Statement<T, CT, Type>> getParameter(int index) {
        return Optional.ofNullable(childs.get(index));
    }
    
    /**
     * be warned, there could be some null gapps inbetween the parameters
     *
     * @return
     */
    public List<Statement<T, CT, Type>> getParameters() {
        return childs;
    }
    
    @Override
    public T getReturnType() {
        return returnType;
    }
    
    @Override
    public ObjectProperty returnTypeProperty() {
        return new SimpleObjectProperty<>(returnType);
    }
    
    public Type getParameterType() {
        return parameterType;
    }
    
    public ObjectProperty parameterTypeProperty() {
        return new SimpleObjectProperty(parameterType);
    }
    
    public OperatorType getOperatorType() {
        return OperatorType.valueOf(this);
    }
    
    public int getMinimumParameterAmount() {
        return minimumParameters;
    }
    
    /**
     * @return -1 for infinite parameters
     */
    public int getMaximumParameterAmount() {
        return maximumParamerters;
    }
    
    public int getParameterAmount() {
        return parameterAmountProperty().get();
    }
    
    public ReadOnlyIntegerProperty parameterAmountProperty() {
        return childs.sizeProperty();
    }
    
    /**
     * To be overridden by e.g. the negation
     *
     * @param parameters
     * @param operator
     * @return
     */
    public ObservableList<Node> setOperatorsBetweenParameters(List<Body> parameters, Supplier<Node> operator) {
        //Default implementation of setting the operators inbetween e.g. 1+2+3, other implementations are e.g. the negation !false
        ObservableList<Node> listParameter = FXCollections.observableArrayList();
        if (parameters.size() > 1) {
            for (int i = 0; i < parameters.size(); i++) {
                Node nodePar = parameters.get(i);
                listParameter.add(nodePar);
                listParameter.add(operator.get());
            }
            if (listParameter.size() > 0) {
                listParameter.remove(listParameter.size() - 1);
            }
        } else if (parameters.size() > 0) {
            Node nodePar = parameters.get(0);
            listParameter.add(operator.get());
            listParameter.add(nodePar);
        }
        return listParameter;
    }
    
    /**
     * to be overridden
     *
     * @param operator
     * @return
     */
    public List<Node> extend(Supplier<Node> operator) {
        List<Node> result = new ArrayList<>();
        result.add(operator.get());
        result.add(null);//The placeholder
        addParameter(null);
        return result;
    }
    
    public int indexWithOperatorsToRegularIndex(int indexWithOperators) {
        return indexWithOperators / 2;
    }
    
    /**
     * to be overridden
     *
     * @return
     */
    public int reduce() {
        removeLastParameter();
        return 2;
    }
    
    public Optional<Statement<T, CT, Type>> getFirstParameter() {
        return Optional.ofNullable(childs.get(0));
    }
    
    public ReadOnlyObjectProperty<Statement<T, CT, Type>> firstParameterProperty() {
        ReadOnlyObjectWrapper<Statement<T, CT, Type>> wrapper = new ReadOnlyObjectWrapper<>();
        ObjectBinding<Statement<T, CT, Type>> firstChild = Bindings.valueAt(childs, 0);
        wrapper.bind(firstChild);
        return wrapper.getReadOnlyProperty();
    }
    
    public Optional<Statement<T, CT, Type>> getLastParameter() {
        return Optional.ofNullable(childs.get(getParameterAmount() - 1));
    }
    
    public ReadOnlyObjectProperty<Statement<T, CT, Type>> lastParameterProperty() {
        ReadOnlyObjectWrapper<Statement<T, CT, Type>> wrapper = new ReadOnlyObjectWrapper<>();
        ObjectBinding<Statement<T, CT, Type>> lastChild = Bindings.valueAt(childs, parameterAmountProperty());
        wrapper.bind(lastChild);
        return wrapper.getReadOnlyProperty();
    }
    
    /**
     * To be overridden by some subclasses
     *
     * @return Wether this operator starts with a parameter od a operatoricon
     */
    public boolean startsWithParameter() {
        return true;
    }
    
    /**
     * To be overridden by some subclasses
     *
     * @return Wether this operator ends with a parameter od a operatoricon
     */
    public boolean endsWithParameter() {
        return true;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder().append('(');
        if (childs.size() > 1) {//infix
            childs.stream().forEach((child) -> {
                builder.append(String.valueOf(child));
                builder.append(String.valueOf(getOperatorType()));
            });
            if (childs.size() > 0) {
                builder.delete(builder.length() - getOperatorType().toString().length(), builder.length());
            }
        } else {//op text
            builder.append(String.valueOf(getOperatorType()));
            if (childs.size() > 0) {
                builder.append(String.valueOf(childs.get(0)));
            }
        }
        return builder.append(')').toString();
    }
    
    @Override
    public Map<String, JsonNode> getAdditionalProperties() {
        Map<String, JsonNode> map = new HashMap<>();
        
        String type = getOperatorType().name();
        List<JsonNode> parameter = getChilds().stream()
                .map(s -> s != null ? s.getAdditionalProperties() : null)
                .map(m -> Serialisation.MAPPER.convertValue(m, JsonNode.class))
                .collect(Collectors.toList());
        
        map.put(typeKey, TextNode.valueOf(type));
        map.put(parameterKey, Serialisation.MAPPER.convertValue(parameter, ArrayNode.class));
        
        return map;
    }
    
    public boolean isExtensible() {
        return isExtensible;
    }
}
