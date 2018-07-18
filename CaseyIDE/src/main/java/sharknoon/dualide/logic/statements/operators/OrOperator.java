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

import sharknoon.dualide.logic.statements.Statement;
import sharknoon.dualide.logic.statements.values.PrimitiveValue.BooleanValue;
import sharknoon.dualide.logic.statements.values.Value;
import sharknoon.dualide.logic.types.PrimitiveType;
import sharknoon.dualide.logic.types.PrimitiveType.BooleanType;
import sharknoon.dualide.logic.types.Type;

/**
 *
 * @author Josua Frank
 */
public class OrOperator extends Operator<BooleanType, BooleanType> {

    public OrOperator(Statement parent) {
        super(parent, 2, -1, true, PrimitiveType.BOOLEAN, PrimitiveType.BOOLEAN);
    }

    @Override
    public BooleanValue calculateResult() {
        int iterations = 0;
        boolean result = false;
        for (Statement<BooleanType, BooleanType, Type> next : getParameters()) {
            if (next != null) {
                iterations++;
                boolean nextValue = Value.toBooleanValue(next.calculateResult()).getValue();
                if (nextValue) {
                    result = true;
                    if (iterations >= getMinimumParameterAmount()) {
                        break;
                    }
                }
            }
        }
        if (iterations < getMinimumParameterAmount()) {
            //specific code
            return new BooleanValue(null);
            //end specific code
        }
        return new BooleanValue(result, null);
    }

}
