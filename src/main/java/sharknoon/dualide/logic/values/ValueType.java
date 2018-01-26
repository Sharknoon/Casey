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
package sharknoon.dualide.logic.values;

import java.util.EnumSet;
import java.util.Set;
import sharknoon.dualide.logic.operations.OperationType;
import static sharknoon.dualide.logic.operations.OperationType.*;
import sharknoon.dualide.utils.collection.Collections;

/**
 *
 * @author Josua Frank
 */
public enum ValueType {
    NUMBER(NumberValue.class, EnumSet.of(ADD, SUBTRACT, MULTIPLY, DIVIDE, MODULO)),
    BOOLEAN(BooleanValue.class, EnumSet.of(EQUALS)),
    TEXT(TextValue.class, EnumSet.of(null)),
    OBJECT(ObjectValue.class, EnumSet.of(null));

    private final Class<? extends Value> type;
    private final EnumSet<OperationType> operations;

    private ValueType(Class<? extends Value> type, EnumSet<OperationType> operations) {
        this.type = type;
        this.operations = operations;
    }

    public Set<OperationType> getOperationTypes() {
        return Collections.silentUnmodifiableSet(operations);
    }

    public static Set<ValueType> getAll() {
        return EnumSet.allOf(ValueType.class);
    }
}
