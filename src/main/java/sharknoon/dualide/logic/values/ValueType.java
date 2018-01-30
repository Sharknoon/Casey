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
import sharknoon.dualide.logic.creations.CreationType;
import static sharknoon.dualide.logic.creations.CreationType.*;
import sharknoon.dualide.logic.operations.OperationType;
import static sharknoon.dualide.logic.operations.OperationType.*;
import sharknoon.dualide.utils.collection.Collections;

/**
 *
 * @author Josua Frank
 */
public enum ValueType {
    NUMBER(NumberValue.class, NUMBER_CREATION, EnumSet.of(ADD, SUBTRACT, MULTIPLY, DIVIDE, MODULO)),
    BOOLEAN(BooleanValue.class, BOOLEAN_CREATION, EnumSet.of(EQUALS, NOT_EQUALS, GREATER_THAN, LESS_THAN, GREATER_OR_EQUAL_THAN, LESS_OR_EQUAL_THAN, AND, OR, NOT)),
    TEXT(TextValue.class, TEXT_CREATION, EnumSet.of(CONCAT)),
    OBJECT(ObjectValue.class, OBJECT_CREATION, EnumSet.noneOf(OperationType.class));

    private final Class<? extends Value> type;
    private final EnumSet<OperationType> operations;
    private final CreationType creation;

    private ValueType(Class<? extends Value> type, CreationType creation, EnumSet<OperationType> operations) {
        this.type = type;
        this.operations = operations;
        this.creation = creation;
    }

    public Set<OperationType> getOperationTypes() {
        return Collections.silentUnmodifiableSet(operations);
    }

    public CreationType getCreationType() {
        return creation;
    }

    public static Set<ValueType> getAll() {
        return EnumSet.allOf(ValueType.class);
    }

}
