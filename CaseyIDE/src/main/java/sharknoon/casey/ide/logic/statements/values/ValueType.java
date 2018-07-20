package sharknoon.casey.ide.logic.statements.values;/*
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

import sharknoon.casey.ide.logic.statements.Statement;

import java.util.function.BiFunction;

public enum ValueType {
    NUMBER((val, par) -> PrimitiveValue.createNewNumberValue((double) val, par)),
    TEXT((val, par) -> PrimitiveValue.createNewTextValue((String) val, par)),
    BOOLEAN((val, par) -> PrimitiveValue.createNewBooleanValue((boolean) val, par)),
    VOID((val, par) -> PrimitiveValue.createNewVoidValue(par)),
    OBJECT((val, par) -> ObjectValue.createObject((String) val, par));
    
    BiFunction<Object, Statement<?, ?, ?>, Value<?>> creator;
    
    ValueType(BiFunction<Object, Statement<?, ?, ?>, Value<?>> creator) {
        this.creator = creator;
    }
    
    public boolean isPrimitive() {
        return this != OBJECT;
    }
    
    public boolean isObject() {
        return this == OBJECT;
    }
    
    /**
     * Creates a new Value
     *
     * @param value  e.g. A Double or a Boolean, null for void
     * @param parent The parent of this statement
     * @return The newly created statement
     */
    public Value<?> create(Object value, Statement<?, ?, ?> parent) {
        return creator.apply(value, parent);
    }
}
