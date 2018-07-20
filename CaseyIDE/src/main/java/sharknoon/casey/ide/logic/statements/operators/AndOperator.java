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
import sharknoon.casey.ide.logic.statements.values.PrimitiveValue.BooleanValue;
import sharknoon.casey.ide.logic.statements.values.Value;
import sharknoon.casey.ide.logic.types.PrimitiveType;
import sharknoon.casey.ide.logic.types.PrimitiveType.BooleanType;
import sharknoon.casey.ide.logic.types.Type;

/**
 *
 * @author Josua Frank
 */
public class AndOperator extends Operator<BooleanType, BooleanType> {

    public AndOperator(Statement parent) {
        super(parent, 2, -1, true, PrimitiveType.BOOLEAN, PrimitiveType.BOOLEAN);
    }

    @Override
    public BooleanValue calculateResult() {
        int iterations = 0;
        for (Statement<BooleanType, BooleanType, Type> next : getParameters()) {
            if (next != null) {
                iterations++;
                Value<BooleanType> value = next.calculateResult();
                if (!Value.toBooleanValue(value).valueProperty().get()) {
                    return new BooleanValue(null);
                }
            }
        }
        if (iterations < getMinimumParameterAmount()) {
            //specific code
            return new BooleanValue(null);
            //end specific code
        }
        return new BooleanValue(true, null);
    }

}
