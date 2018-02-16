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

/**
 *
 * @author Josua Frank
 * @param <T> The type of the value
 * @param <U> This value itself
 */
public abstract class Value<T, U extends Value> extends Statement<Value, U, Value> {

    private final ObjectProperty<T> value = new SimpleObjectProperty<>();

    protected Value(T value, Statement parent) {
        super(parent);
        setValue(value);
        valueProperty().addListener((observable, oldValue, newValue) -> {
            onChange();
        });
    }

    public T getValue() {
        return value.get();
    }

    public void setValue(T value) {
        this.value.set(value);
    }

    public ObjectProperty<T> valueProperty() {
        return value;
    }

    public ValueType getValueType() {
        return ValueType.valueOf(this);
    }

    @Override
    public StatementType getStatementType() {
        return StatementType.VALUE;
    }

    @Override
    public U calculateResult() {
        return (U) this;
    }

    @Override
    public ValueType getReturnType() {
        return getValueType();
    }

    abstract T getDefault();

    public abstract boolean equals(Value other);

    @Override
    public String toString() {
        return String.valueOf(value.get());
    }

}
