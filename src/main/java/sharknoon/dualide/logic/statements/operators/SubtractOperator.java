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

import java.util.List;
import sharknoon.dualide.logic.statements.Statement;
import sharknoon.dualide.logic.statements.values.NumberValue;
import sharknoon.dualide.logic.statements.values.Value;
import sharknoon.dualide.logic.statements.values.ValueType;

/**
 *
 * @author Josua Frank
 */
public class SubtractOperator extends Operator<NumberValue, NumberValue> {

    public SubtractOperator(Statement parent) {
        super(parent, 2, -1,true, ValueType.NUMBER, ValueType.NUMBER);
    }

    @Override
    public NumberValue calculateResult() {
        List<Statement<NumberValue, NumberValue, Value>> parameters = getParameters();
        double result = Double.NaN;
        int iterations = 0;
        for (int i = 0; i < parameters.size(); i++) {
            Statement<NumberValue, NumberValue, Value> par = parameters.get(i);
            if (par != null) {
                double value = par.calculateResult().getValue();
                if (Double.isNaN(result)) {
                    result = value;
                } else {
                    result = result - value;
                }
                iterations++;
            }
        }
        if (iterations < getMinimumParameterAmount()) {
            return new NumberValue(null);
        }
        return new NumberValue(result, null);
    }

}
