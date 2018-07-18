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

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import sharknoon.dualide.logic.statements.Statement;
import sharknoon.dualide.logic.statements.values.PrimitiveValue.BooleanValue;
import sharknoon.dualide.logic.statements.values.PrimitiveValue.NumberValue;
import sharknoon.dualide.logic.statements.values.PrimitiveValue.TextValue;
import sharknoon.dualide.logic.types.PrimitiveType.BooleanType;
import sharknoon.dualide.logic.types.PrimitiveType.NumberType;
import sharknoon.dualide.logic.types.PrimitiveType.TextType;
import sharknoon.dualide.logic.types.Type;

/**
 * @param <T> the type of the value
 * @author Josua Frank
 */
public abstract class Value<T extends Type> extends Statement<Type, T, Type> {
    
    public static NumberValue toNumberValue(Value<NumberType> value) {
        return value instanceof NumberValue ? (NumberValue) value : new NumberValue(null);
    }
    
    public static TextValue toTextValue(Value<TextType> value) {
        return value instanceof TextValue ? (TextValue) value : new TextValue(null);
    }
    
    public static BooleanValue toBooleanValue(Value<BooleanType> value) {
        return value instanceof BooleanValue ? (BooleanValue) value : new BooleanValue(null);
    }
    
    //returnType
    private final T type;
    
    public Value(T type, Statement parent) {
        initParent(parent, false);
        this.type = type;
    }
    
    @Override
    public T getReturnType() {
        return type;
    }
    
    @Override
    public ObjectProperty returnTypeProperty() {
        return new SimpleObjectProperty<>(type);
    }
    
    @Override
    public abstract int hashCode();
    
    @Override
    public abstract boolean equals(Object obj);
    
}
