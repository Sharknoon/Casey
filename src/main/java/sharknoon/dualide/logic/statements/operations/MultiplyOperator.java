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

import sharknoon.dualide.logic.statements.Statement;
import sharknoon.dualide.logic.statements.values.NumberValue;
import sharknoon.dualide.logic.statements.values.ValueType;

/**
 *
 * @author Josua Frank
 */
public class MultiplyOperator extends Operator<NumberValue, NumberValue> {

    public MultiplyOperator(Statement parent) {
        super(parent, ValueType.NUMBER, ValueType.NUMBER);
    }

    @Override
    public NumberValue calculateResult() {
        return new NumberValue(
                getParameters()
                        .stream()
                        .mapToDouble(p -> p.getValue())
                        .reduce(0.0, (l, r) -> l * r),
                parentProperty().get()
        );
    }

}