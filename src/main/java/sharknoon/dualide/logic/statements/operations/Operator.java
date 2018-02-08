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
package sharknoon.dualide.logic.statements.operations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import sharknoon.dualide.logic.statements.Statement;
import sharknoon.dualide.logic.statements.values.Value;
import sharknoon.dualide.logic.statements.values.ValueType;
import sharknoon.dualide.ui.bodies.Body;

/**
 *
 * @author Josua Frank
 * @param <RV> Return value
 * @param <CV> Child value, if the type is unimportant, use the abstract type
 * value
 */
public abstract class Operator<RV extends Value, CV extends Value> extends Statement<Value, RV, CV> {

    //A map instead of a list to leave empty spaces inbetween the indexes
    private final TreeMap<Integer, Statement<Value, CV, Value>> internal_parameters = new TreeMap<>();
    private final MapProperty<Integer, Statement<Value, CV, Value>> parameters = new SimpleMapProperty<>(FXCollections.observableMap(internal_parameters));
    private final ValueType returnType;
    private final Set<ValueType> parameterTypes;
    private final int minimumParameters;
    private final int maximumParamerters;

    public Operator(Statement parent, int minimumParameters, int maximumParameters, ValueType returnType, ValueType... parameterTypes) {
        super(parent);
        this.returnType = returnType;
        if (parameterTypes.length > 0) {
            this.parameterTypes = new HashSet<>(Arrays.asList(parameterTypes));
        } else {
            this.parameterTypes = EnumSet.allOf(ValueType.class);
        }
        this.minimumParameters = minimumParameters;
        this.maximumParamerters = maximumParameters;
    }

    public void addParameter(Statement<Value, CV, Value> parameter) {
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            if (!parameters.containsKey(i)) {
                addParameter(i, parameter);
                return;
            }
        }
    }

    /**
     * Overrides the parameter
     *
     * @param index starting at 0
     * @param parameter
     */
    public void addParameter(int index, Statement<Value, CV, Value> parameter) {
        parameters.put(index, parameter);
    }

    public Optional<Statement<Value, CV, Value>> getParameter(int index) {
        return Optional.ofNullable(parameters.get(index));
    }

    /**
     * be warned, there could be some gapps inbetween the parameters, to get the
     * correct parameters, use getParemeterIndexMap()
     *
     * @return
     */
    public Collection<Statement<Value, CV, Value>> getParameters() {
        return parameters.values();
    }

    public Map<Integer, Statement<Value, CV, Value>> getParameterIndexMap() {
        return parameters;
    }

    @Override
    public ValueType getReturnType() {
        return returnType;
    }

    public Set<ValueType> getParameterTypes() {
        return parameterTypes;
    }

    public OperatorType getOperatorType() {
        return OperatorType.valueOf(this);
    }

    @Override
    public StatementType getStatementType() {
        return StatementType.OPERATOR;
    }

    public int getMinimumParameterAmount() {
        return minimumParameters;
    }

    /**
     *
     * @return -1 for infinite parameters
     */
    public int getMaximumParameterAmount() {
        return maximumParamerters;
    }

    public int getParameterAmount() {
        if (internal_parameters.isEmpty()) {
            return 0;
        }
        return internal_parameters.lastKey() + 1;
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
        for (int i = 0; i < parameters.size(); i++) {
            Node nodePar = parameters.get(i);
            listParameter.add(nodePar);
            listParameter.add(operator.get());
        }
        if (listParameter.size() > 0) {
            listParameter.remove(listParameter.size() - 1);
        }
        return listParameter;
    }

    public Optional<Statement<Value, CV, Value>> getFirstParameter() {
        return Optional.ofNullable(parameters.get(0));
    }

    public Optional<Statement<Value, CV, Value>> getLastParameter() {
        return Optional.ofNullable(parameters.get(getParameterAmount() - 1));
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
        for (int i = 0; i < Math.max(getMinimumParameterAmount(), getParameterAmount()); i++) {
            builder.append(getParameterIndexMap().get(i));
            builder.append(getOperatorType());
        }
        builder.deleteCharAt(builder.length() - 1);
        return builder.append(')').toString();
    }

}
