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
import sharknoon.casey.ide.logic.statements.values.PrimitiveValue.TextValue;
import sharknoon.casey.ide.logic.types.PrimitiveType;
import sharknoon.casey.ide.logic.types.PrimitiveType.TextType;
import sharknoon.casey.ide.logic.types.Type;

import java.util.stream.Collectors;

/**
 *
 * @author Josua Frank
 */
public class ConcatOperator extends Operator<TextType, Type> {

    public ConcatOperator(Statement parent) {
        super(parent, 2, -1, true, PrimitiveType.TEXT);
    }

    @Override
    public TextValue calculateResult() {
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
