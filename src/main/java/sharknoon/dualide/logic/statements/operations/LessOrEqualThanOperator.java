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

import java.util.List;
import sharknoon.dualide.logic.statements.Statement;
import sharknoon.dualide.logic.statements.values.BooleanValue;
import sharknoon.dualide.logic.statements.values.NumberValue;
import sharknoon.dualide.logic.statements.values.ValueType;

/**
 *
 * @author Josua Frank
 */
public class LessOrEqualThanOperator extends Operator<BooleanValue, NumberValue> {

    public LessOrEqualThanOperator(Statement parent) {
        super(parent, ValueType.BOOLEAN, ValueType.NUMBER);
    }

    @Override
    public BooleanValue calculateResult() {
        List<NumberValue> parameters = getParameters();
        if (parameters.size() < 2) {
            return new BooleanValue(parentProperty().get());
        }
        boolean result = parameters.get(0).getValue() <= parameters.get(1).getValue();
        return new BooleanValue(result, parentProperty().get());
    }

}
