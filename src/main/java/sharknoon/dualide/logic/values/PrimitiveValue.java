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

import java.util.Objects;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import sharknoon.dualide.logic.Statement;
import sharknoon.dualide.logic.types.PrimitiveType;
import sharknoon.dualide.logic.types.PrimitiveType.BooleanType;
import sharknoon.dualide.logic.types.PrimitiveType.NumberType;
import sharknoon.dualide.logic.types.PrimitiveType.TextType;

/**
 *
 * @author Josua Frank
 * @param <T>
 */
public abstract class PrimitiveValue<T extends PrimitiveType, O> extends Value<PrimitiveType> {

    public PrimitiveValue(T type, Statement parent) {
        super(type, parent);
    }

    @Override
    public Value<PrimitiveType> calculateResult() {
        return this;
    }

    public abstract O getValue();

    public abstract ObjectProperty<O> valueProperty();

    public static NumberValue createNewNumberValue(Double value, Statement parent) {
        return new NumberValue(value, parent);
    }

    public static class NumberValue<Void> extends PrimitiveValue<NumberType, Double> {

        private final ObjectProperty<Double> number = new SimpleObjectProperty<>(0.0);

        public NumberValue(Double number, Statement parent) {
            super(PrimitiveType.NUMBER, parent);
            if (number != null) {
                this.number.set(number);
            }
            this.number.addListener((observable, oldValue, newValue) -> {
                onChange();
            });
        }

        public NumberValue(Statement parent) {
            this(0.0, parent);
        }

        @Override
        public String toString() {
            return String.valueOf(number.get());
        }

        @Override
        public Double getValue() {
            return number.get();
        }

        @Override
        public ObjectProperty<Double> valueProperty() {
            return number;
        }

        @Override
        public int hashCode() {
            return number.get().hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final NumberValue<?> other = (NumberValue<?>) obj;
            return Objects.equals(this.number.get(), other.number.get());
        }

        

    }

    public static TextValue createNewTextalue(String value, Statement parent) {
        return new TextValue(value, parent);
    }

    public static class TextValue<Void> extends PrimitiveValue<TextType, String> {

        private final ObjectProperty<String> text = new SimpleObjectProperty<>("");

        public TextValue(String text, Statement parent) {
            super(PrimitiveType.TEXT, parent);
            if (text != null) {
                this.text.set(text);
            }
            this.text.addListener((observable, oldValue, newValue) -> {
                onChange();
            });
        }

        public TextValue(Statement parent) {
            this("", parent);
        }

        @Override
        public String toString() {
            return String.valueOf(text.get());
        }

        @Override
        public String getValue() {
            return text.get();
        }

        @Override
        public ObjectProperty<String> valueProperty() {
            return text;
        }
        @Override
        public int hashCode() {
            return text.get().hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final TextValue<?> other = (TextValue<?>) obj;
            return Objects.equals(this.text.get(), other.text.get());
        }
    }

    public static BooleanValue createNewBooleanValue(Boolean value, Statement parent) {
        return new BooleanValue(value, parent);
    }

    public static class BooleanValue<Void> extends PrimitiveValue<BooleanType, Boolean> {

        private final ObjectProperty<Boolean> bool = new SimpleObjectProperty<>(false);

        public BooleanValue(Boolean bool, Statement parent) {
            super(PrimitiveType.BOOLEAN, parent);
            if (bool != null) {
                this.bool.set(bool);
            }
            this.bool.addListener((observable, oldValue, newValue) -> {
                onChange();
            });
        }

        public BooleanValue(Statement parent) {
            this(false, parent);
        }

        @Override
        public String toString() {
            return String.valueOf(bool.get());
        }

        @Override
        public Boolean getValue() {
            return bool.get();
        }

        @Override
        public ObjectProperty<Boolean> valueProperty() {
            return bool;
        }
        @Override
        public int hashCode() {
            return bool.get().hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final BooleanValue<?> other = (BooleanValue<?>) obj;
            return Objects.equals(this.bool.get(), other.bool.get());
        }
    }

}
