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
package sharknoon.casey.ide.logic.statements.values;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import sharknoon.casey.ide.logic.statements.Statement;
import sharknoon.casey.ide.logic.types.PrimitiveType;
import sharknoon.casey.ide.logic.types.PrimitiveType.BooleanType;
import sharknoon.casey.ide.logic.types.PrimitiveType.NumberType;
import sharknoon.casey.ide.logic.types.PrimitiveType.TextType;
import sharknoon.casey.ide.logic.types.PrimitiveType.VoidType;
import sharknoon.casey.ide.utils.jackson.JacksonUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @param <T>
 * @author Josua Frank
 */
public abstract class PrimitiveValue<T extends PrimitiveType, O> extends Value<T> {
    
    private static final String typeKey = "type";
    private static final String valueKey = "value";
    
    public static NumberValue createNewNumberValue(Double value, Statement parent) {
        return new NumberValue(value, parent);
    }
    
    public static TextValue createNewTextValue(String value, Statement parent) {
        return new TextValue(value, parent);
    }
    
    public static BooleanValue createNewBooleanValue(Boolean value, Statement parent) {
        return new BooleanValue(value, parent);
    }
    
    public static VoidValue createNewVoidValue(Statement parent) {
        return new VoidValue(parent);
    }
    
    public PrimitiveValue(T type, Statement parent) {
        super(type, parent);
    }
    @Override
    public Value<T> calculateResult() {
        return this;
    }
    
    @Override
    public Map<String, JsonNode> getAdditionalProperties() {
        Map<String, JsonNode> map = new HashMap<>();
        
        String type = getReturnType().fullNameProperty().get();
        JsonNode value = JacksonUtils.toNode(getValue());
        
        map.put(typeKey, new TextNode(type));
        map.put(valueKey, value);
        
        return map;
    }
    
    public abstract O getValue();
    
    public abstract ObjectProperty<O> valueProperty();
    
    public static class NumberValue extends PrimitiveValue<NumberType, Double> {
    
        private final ObjectProperty<Double> number = new SimpleObjectProperty<>(0.0);
    
        public NumberValue(Double number, Statement parent) {
            super(PrimitiveType.NUMBER, parent);
            if (number != null) {
                this.number.set(number);
            }
            if (parentProperty().get() != null && (parentProperty().get() instanceof Value)) {
                parentProperty().get().childsProperty().add(this);
            }
            this.number.addListener((observable, oldValue, newValue) -> {
                onChange();
            });
        }
    
        public NumberValue(Statement parent) {
            this(0.0, parent);
        }
    
        @Override
        public Double getValue() {
            return number.get();
        }
    
        @Override
        public int hashCode() {
            return number.get().hashCode();
        }
    
        @Override
        public String toString() {
            return String.valueOf(number.get());
        }
    
    
        @Override
        public ObjectProperty<Double> valueProperty() {
            return number;
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
            final NumberValue other = (NumberValue) obj;
            return Objects.equals(this.number.get(), other.number.get());
        }
    
    
    }
    
    public static class TextValue extends PrimitiveValue<TextType, String> {
        
        private final ObjectProperty<String> text = new SimpleObjectProperty<>("");
        
        public TextValue(String text, Statement parent) {
            super(PrimitiveType.TEXT, parent);
            if (text != null) {
                this.text.set(text);
            }
            if (parentProperty().get() != null && (parentProperty().get() instanceof Value)) {
                parentProperty().get().childsProperty().add(this);
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
            final TextValue other = (TextValue) obj;
            return Objects.equals(this.text.get(), other.text.get());
        }
    }
    
    public static class BooleanValue extends PrimitiveValue<BooleanType, Boolean> {
        
        private final ObjectProperty<Boolean> bool = new SimpleObjectProperty<>(false);
        
        public BooleanValue(Boolean bool, Statement parent) {
            super(PrimitiveType.BOOLEAN, parent);
            if (bool != null) {
                this.bool.set(bool);
            }
            if (parentProperty().get() != null && (parentProperty().get() instanceof Value)) {
                parentProperty().get().childsProperty().add(this);
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
            final BooleanValue other = (BooleanValue) obj;
            return Objects.equals(this.bool.get(), other.bool.get());
        }
    }
    
    public static class VoidValue extends PrimitiveValue<VoidType, Void> {
        
        private final ObjectProperty<Void> void_ = new SimpleObjectProperty<>(null);
        
        public VoidValue(Statement parent) {
            super(PrimitiveType.VOID, parent);
            if (parentProperty().get() != null && (parentProperty().get() instanceof Value)) {
                parentProperty().get().childsProperty().add(this);
            }
        }
        
        @Override
        public String toString() {
            return "void";
        }
        
        /**
         * returns null in the ObjectProperty
         *
         * @return
         */
        @Override
        public Void getValue() {
            return void_.get();
        }
        
        @Override
        public ObjectProperty<Void> valueProperty() {
            return void_;
        }
        
        @Override
        public int hashCode() {
            return 42;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            return getClass() == obj.getClass();
        }
    }
    
    
}
