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
package sharknoon.dualide.logic.statements.values;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import sharknoon.dualide.logic.statements.values.creations.CreationType;
import static sharknoon.dualide.logic.statements.values.creations.CreationType.*;
import sharknoon.dualide.logic.statements.operators.OperatorType;
import static sharknoon.dualide.logic.statements.operators.OperatorType.*;
import sharknoon.dualide.ui.misc.Icon;
import sharknoon.dualide.utils.collection.Collections;
import sharknoon.dualide.utils.language.Language;
import sharknoon.dualide.utils.language.Word;

/**
 *
 * @author Josua Frank
 */
public enum ValueType {
    BOOLEAN(new BooleanValue(null), Word.BOOLEAN, Icon.BOOLEAN, BOOLEAN_CREATION, EnumSet.of(EQUALS, NOT_EQUALS, GREATER_THAN, LESS_THAN, GREATER_OR_EQUAL_THAN, LESS_OR_EQUAL_THAN, AND, OR, NOT)),
    NUMBER(new NumberValue(null), Word.NUMBER, Icon.NUMBER, NUMBER_CREATION, EnumSet.of(ADD, SUBTRACT, MULTIPLY, DIVIDE, MODULO)),
    OBJECT(new ObjectValue(null, null, null), Word.OBJECT, Icon.CLASS, OBJECT_CREATION, EnumSet.noneOf(OperatorType.class)),
    TEXT(new TextValue(null), Word.TEXT, Icon.TEXT, TEXT_CREATION, EnumSet.of(CONCAT));

    private static Map<Class<? extends Value>, ValueType> TYPES;
    public static Set<String> forbiddenNames;
    private final Value defaultValue;
    private final Word name;
    private final Icon icon;
    private final EnumSet<OperatorType> operations;
    private final CreationType creation;

    private ValueType(Value defaultValue, Word name, Icon icon, CreationType creation, EnumSet<OperatorType> operations) {
        init(defaultValue.getClass());
        this.defaultValue = defaultValue;
        this.name = name;
        this.icon = icon;
        this.operations = operations;
        this.creation = creation;
    }

    private void init(Class<? extends Value> type) {
        if (TYPES == null) {
            TYPES = new HashMap<>();
        }
        TYPES.put(type, this);
    }

    public String getName() {
        return Language.get(name);
    }

    public Icon getIcon() {
        return icon;
    }

    public Value getDefault() {
        return defaultValue;
    }

    public CreationType getCreationType() {
        return creation;
    }

    public Set<OperatorType> getOperationTypes() {
        return Collections.silentUnmodifiableSet(operations);
    }

    public static Set<ValueType> getAll() {
        return EnumSet.allOf(ValueType.class);
    }

    public static <V extends Value> ValueType valueOf(V value) {
        return TYPES.get(value.getClass());
    }


    public static Set<String> getForbiddenNames() {
        if (forbiddenNames == null) {
            forbiddenNames = new HashSet<>();
            for (ValueType value : values()) {
                forbiddenNames.add(value.name());
            }
        }
        return forbiddenNames;
    }

}
