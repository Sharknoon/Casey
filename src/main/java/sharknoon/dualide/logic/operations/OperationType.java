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

import java.util.EnumSet;
import java.util.Set;

/**
 *
 * @author Josua Frank
 */
public enum OperationType {
    ADD(AddOperator.class),
    SUBTRACT(SubtractOperator.class),
    MULTIPLY(MultiplyOperator.class),
    DIVIDE(DivideOperator.class),
    MODULO(ModuloOperator.class),
    EQUALS(EqualsOperator.class);

    private final Class<? extends Operator> type;

    private OperationType(Class<? extends Operator> type) {
        this.type = type;
    }

    public static Set<OperationType> getAll() {
        return EnumSet.allOf(OperationType.class);
    }
}
