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

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import sharknoon.dualide.logic.statements.Statement;
import sharknoon.dualide.logic.statements.values.Value;
import sharknoon.dualide.logic.statements.values.ValueType;

/**
 *
 * @author Josua Frank
 * @param <RV> Return value
 * @param <CV> Child value, if the type is unimportant, use the abstract type
 * value
 */
public abstract class Operator<RV extends Value, CV extends Value> extends Statement<Value, RV, CV> {

    //A map instead of a list to leave empty spaces inbetween the indexes
    private final MapProperty<Integer, Statement<Value, CV, Value>> parameters = new SimpleMapProperty<>(FXCollections.observableMap(new TreeMap<>()));
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
        while (index > parameters.size()) {
            addParameter(returnType.getDefault());
        }
        parameters.put(index, parameter);
    }

    public Optional<Statement<Value, CV, Value>> getParameter(int index) {
        return Optional.ofNullable(parameters.get(index));
    }

    public Collection<Statement<Value, CV, Value>> getParameters() {
        return parameters.values();
    }
    
    public Map<Integer, Statement<Value, CV, Value>> getParameterIndexMap(){
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

    public int getMaximumParamerterAmount() {
        return maximumParamerters;
    }
    
    @Override
    public String toString() {
        return calculateResult().toString();
    }

}
