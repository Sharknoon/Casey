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
package sharknoon.dualide.logic.operations;

import java.util.List;
import sharknoon.dualide.logic.values.BooleanValue;
import sharknoon.dualide.logic.values.NumberValue;

/**
 *
 * @author Josua Frank
 */
public class GreaterOrEqualThanOperator extends Operator<BooleanValue, NumberValue> {

    @Override
    public BooleanValue calculateResult() {
        List<NumberValue> parameters = getParameters();
        if (parameters.size() < 2) {
            return BooleanValue.FALSE;
        }
        boolean result = parameters.get(0).getValue() >= parameters.get(1).getValue();
        return new BooleanValue(result);
    }

}
