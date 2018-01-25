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

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import sharknoon.dualide.logic.operations.OperationType;
import static sharknoon.dualide.logic.operations.OperationType.*;
import sharknoon.dualide.utils.collection.Collections;

/**
 *
 * @author Josua Frank
 */
public enum ValueType {
    NUMBER(NumberValue.class, ADD),
    BOOLEAN(BooleanValue.class),
    TEXT(TextValue.class),
    OBJECT(ObjectValue.class);

    private final Class<? extends Value> type;
    private final Set<OperationType> operations;

    private ValueType(Class<? extends Value> type, OperationType... operations) {
        this.type = type;
        this.operations = Collections.silentUnmodifiableSet(new HashSet<>(Arrays.asList(operations)));
    }

    public Set<OperationType> getOperationTypes() {
        return operations;
    }

    public static Set<ValueType> getAll() {
        return EnumSet.allOf(ValueType.class);
    }
}
