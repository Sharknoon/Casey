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
import java.util.stream.Collectors;
import sharknoon.dualide.logic.statements.Statement;
import sharknoon.dualide.logic.statements.values.TextValue;
import sharknoon.dualide.logic.statements.values.Value;
import sharknoon.dualide.logic.statements.values.ValueType;

/**
 *
 * @author Josua Frank
 */
public class ConcatOperator extends Operator<TextValue, Value> {

    public ConcatOperator(Statement parent) {
        super(parent, 2, -1, true, ValueType.TEXT);
    }

    @Override
    public TextValue calculateResult() {
        //TMP
        List<Statement<TextValue, Value, Value>> parameters = getParameters();
        List<Value> list = parameters.stream().filter(p -> p != null).map(p -> p.calculateResult()).collect(Collectors.toList());
        //TMP
        return new TextValue(
                getParameters()
                        .stream()
                        .filter(p -> p != null)
                        .map(p -> p.calculateResult())
                        .map(v -> v.toString())
                        .collect(Collectors.joining()),
                null
        );
    }

}
