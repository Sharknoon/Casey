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

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import sharknoon.dualide.logic.statements.Statement;
import sharknoon.dualide.logic.statements.values.BooleanValue;
import sharknoon.dualide.logic.statements.values.Value;
import sharknoon.dualide.logic.statements.values.ValueType;
import sharknoon.dualide.ui.bodies.Body;

/**
 *
 * @author Josua Frank
 */
public class NotOperator extends Operator<BooleanValue, BooleanValue> {

    public NotOperator(Statement parent) {
        super(parent, 1, 1, false, ValueType.BOOLEAN, ValueType.BOOLEAN);
    }

    @Override
    public BooleanValue calculateResult() {
        Collection<Statement<BooleanValue, BooleanValue, Value>> parameters = getParameters();
        if (getParameters().size() > 0) {
            Statement<BooleanValue, BooleanValue, Value> next = parameters.iterator().next();
            if (next != null) {
                return new BooleanValue(!next.calculateResult().getValue(), null);
            }
        }
        return new BooleanValue(null);
    }

    @Override
    public ObservableList<Node> setOperatorsBetweenParameters(List<Body> parameters, Supplier<Node> operator) {
        ObservableList<Node> listResult = FXCollections.observableArrayList();
        listResult.add(operator.get());
        if (parameters.size() > 0 && parameters.get(0) != null) {
            listResult.add(parameters.get(0));
        } else {
            listResult.add(null);
        }
        return listResult;
    }

    @Override
    public String toString() {
        List<Statement<BooleanValue, BooleanValue, Value>> parameters = getParameters();
        String parameter = "null";
        if (parameters.size() > 0 && parameters.get(0) != null) {
            parameter = parameters.get(0).toString();
        }
        return '(' + getOperatorType().toString() + parameter + ')';
    }

    @Override
    public boolean startsWithParameter() {
        return false;
    }

}
