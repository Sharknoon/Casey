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
import sharknoon.dualide.logic.statements.Statement;
import sharknoon.dualide.logic.statements.values.Value;
import sharknoon.dualide.logic.types.PrimitiveType;
import sharknoon.dualide.logic.types.PrimitiveType.BooleanType;
import sharknoon.dualide.logic.types.Type;
import sharknoon.dualide.logic.statements.values.PrimitiveValue.BooleanValue;

/**
 *
 * @author Josua Frank
 */
public class NotOperator extends Operator<BooleanType, BooleanType> {

    public NotOperator(Statement parent) {
        super(parent, 1, 1, false, PrimitiveType.BOOLEAN, PrimitiveType.BOOLEAN);
    }

    @Override
    public BooleanValue calculateResult() {
        Collection<Statement<BooleanType, BooleanType, Type>> parameters = getParameters();
        if (getParameters().size() > 0) {
            Statement<BooleanType, BooleanType, Type> next = parameters.iterator().next();
            if (next != null) {
                return new BooleanValue(!Value.toBooleanValue(next.calculateResult()).getValue(), null);
            }
        }
        return new BooleanValue(null);
    }

    @Override
    public boolean startsWithParameter() {
        return false;
    }

}
