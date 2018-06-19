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
import sharknoon.dualide.logic.statements.values.Value;
import sharknoon.dualide.logic.types.PrimitiveType;
import sharknoon.dualide.logic.types.PrimitiveType.BooleanType;
import sharknoon.dualide.logic.types.PrimitiveType.NumberType;
import sharknoon.dualide.logic.types.Type;
import sharknoon.dualide.logic.statements.values.PrimitiveValue.BooleanValue;

/**
 *
 * @author Josua Frank
 */
public class LessThanOperator extends Operator<BooleanType, NumberType> {

    public LessThanOperator(Statement parent) {
        super(parent, 2, -1, true, PrimitiveType.BOOLEAN, PrimitiveType.NUMBER);
    }

    @Override
    public BooleanValue calculateResult() {
        Statement<BooleanType, NumberType, Type> previous = null;
        int iterations = 0;
        for (Statement<BooleanType, NumberType, Type> next : getParameters()) {
            if (next != null) {
                iterations++;
                if (previous != null) {
                    //specific code
                    double previousValue = Value.toNumberValue(previous.calculateResult()).getValue();
                    double nextValue = Value.toNumberValue(next.calculateResult()).getValue();
                    if (!(previousValue < nextValue)) {
                        return new BooleanValue(null);
                    }
                    //end specific code
                }
                previous = next;
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
