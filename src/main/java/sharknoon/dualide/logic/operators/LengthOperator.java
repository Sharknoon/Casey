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
package sharknoon.dualide.logic.operators;

import java.util.List;
import java.util.function.Supplier;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import sharknoon.dualide.logic.Statement;
import sharknoon.dualide.logic.values.Value;
import sharknoon.dualide.logic.types.PrimitiveType;
import sharknoon.dualide.logic.types.PrimitiveType.NumberType;
import sharknoon.dualide.logic.types.PrimitiveType.TextType;
import sharknoon.dualide.logic.types.Type;
import sharknoon.dualide.logic.values.PrimitiveValue.NumberValue;
import sharknoon.dualide.logic.values.PrimitiveValue.TextValue;
import sharknoon.dualide.ui.bodies.Body;

/**
 *
 * @author Josua Frank
 */
public class LengthOperator extends Operator<NumberType, TextType> {

    public LengthOperator(Statement parent) {
        super(parent, 1, 1, false, PrimitiveType.NUMBER, PrimitiveType.TEXT);
    }

    @Override
    public NumberValue calculateResult() {
        List<Statement<NumberType, TextType, Type>> pars = getParameters();
        if (pars.size() > 0 && pars.get(0) != null) {
            Statement<NumberType, TextType, Type> statement = pars.get(0);
            TextValue text = Value.toTextValue(statement.calculateResult());
            return new NumberValue((double) text.getValue().length(), null);
        } else {
            return new NumberValue(0.0, null);
        }
    }

//    @Override
//    public ObservableList<Node> setOperatorsBetweenParameters(List<Body> parameters, Supplier<Node> operator) {
//        ObservableList<Node> result = FXCollections.observableArrayList();
//        result.add(operator.get());
//        result.add(parameters.get(0));
//        return result;
//    }

}
