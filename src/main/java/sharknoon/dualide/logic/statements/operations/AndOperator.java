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
import sharknoon.dualide.logic.statements.values.BooleanValue;
import sharknoon.dualide.logic.statements.values.ValueType;

/**
 *
 * @author Josua Frank
 */
public class AndOperator extends Operator<BooleanValue, BooleanValue> {

    public AndOperator(Statement parent) {
        super(parent, 2, -1, true, ValueType.BOOLEAN, ValueType.BOOLEAN);
    }

    @Override
    public BooleanValue calculateResult() {
        return new BooleanValue(
                getParameters()
                        .stream()
                        .map(p -> p.calculateResult())
                        .map(v -> v.getValue())
                        .reduce(false, (l, r) -> l && r),
                parentProperty().get()
        );
    }

}
