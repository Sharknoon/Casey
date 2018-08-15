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
package sharknoon.casey.ide.logic.statements.operators;

import sharknoon.casey.ide.logic.statements.Statement;
import sharknoon.casey.ide.logic.statements.values.PrimitiveValue.NumberValue;
import sharknoon.casey.ide.logic.statements.values.Value;
import sharknoon.casey.ide.logic.types.*;
import sharknoon.casey.ide.logic.types.PrimitiveType.NumberType;

import java.util.List;

/**
 *
 * @author Josua Frank
 */
public class ModuloOperator extends Operator<NumberType, NumberType> {

    public ModuloOperator(Statement parent) {
        super(parent, 2, -1, true, PrimitiveType.NUMBER, PrimitiveType.NUMBER);
    }

    @Override
    public NumberValue calculateResult() {
        List<Statement<NumberType, NumberType, Type>> parameters = getParameters();
        double result = Double.NaN;
        int iterations = 0;
        for (var par : parameters) {
            if (par != null) {
                double value = Value.toNumberValue(par.calculateResult()).getValue();
                if (Double.isNaN(result)) {
                    result = value;
                } else {
                    if (value == 0.0) {
                        return new NumberValue(null);//modulo through zero is not okay
                    } else {
                        result = result % value;
                    }
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
