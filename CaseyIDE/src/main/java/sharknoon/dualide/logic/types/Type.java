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
package sharknoon.dualide.logic.types;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import sharknoon.dualide.logic.items.Class.ObjectType;
import sharknoon.dualide.logic.statements.Statement;
import sharknoon.dualide.logic.statements.values.Value;
import sharknoon.dualide.ui.misc.Icon;
import sharknoon.dualide.utils.javafx.BindUtils;

import java.util.Optional;

/**
 * @param <T>
 * @param <V>
 * @author Josua Frank
 */
public abstract class Type<T extends Type, V extends Value> {
    
    public Type getReturnType() {
        return this;
    }
    
    public abstract boolean isPrimitive();
    
    public abstract boolean isObject();
    
    public abstract PrimitiveType getPrimitiveType();
    
    public abstract ObjectType getObjectType();
    
    /**
     * Excludes the placeholder Type for all types
     *
     * @return
     */
    public static ListProperty<Type> getAllTypes() {
        return new SimpleListProperty<>(BindUtils.concatAll(PrimitiveType.getAll(), ObjectType.getAll()));
    }
    
    public static Optional<Type> valueOf(String name) {
        return PrimitiveType.forName(name).map(p -> (Type) p)
                .or(() -> ObjectType.forName(name));
    }
    
    public abstract StringProperty simpleNameProperty();
    
    public abstract StringProperty fullNameProperty();
    
    public abstract Icon getIcon();
    
    public abstract Icon getCreationIcon();
    
    public abstract StringProperty creationTextProperty();
    
    /**
     * Language dependent name, e.g. Nummer in german for Number
     *
     * @return A property for type name, which changes when the language changes
     */
    public abstract StringProperty getLanguageDependentName();
    
    public abstract Optional<V> createValue(Statement parent);
    
    public abstract V createEmptyValue(Statement parent);
    
    public abstract void onDelete(Runnable runnable);
    
    public static UndefinedType UNDEFINED = new UndefinedType();
    
    /**
     * For internal use only, used for allowing all types to replace it, e.g. in a placeholder
     */
    public static class UndefinedType extends Type {
        
        private UndefinedType() {
        }
        
        @Override
        public boolean isPrimitive() {
            return false;
        }
        
        @Override
        public boolean isObject() {
            return false;
        }
        
        @Override
        public PrimitiveType getPrimitiveType() {
            return null;
        }
        
        @Override
        public ObjectType getObjectType() {
            return null;
        }
        
        private static StringProperty name = new SimpleStringProperty("UNDEFINED");
        
        @Override
        public StringProperty simpleNameProperty() {
            return name;
        }
        
        @Override
        public StringProperty fullNameProperty() {
            return name;
        }
        
        @Override
        public Icon getIcon() {
            return Icon.CLOSE;
        }
        
        @Override
        public Icon getCreationIcon() {
            return Icon.CLOSE;
        }
        
        private static StringProperty creationText = new SimpleStringProperty("DONT CREATE ME");
        
        @Override
        public StringProperty creationTextProperty() {
            return creationText;
        }
        
        @Override
        public StringProperty getLanguageDependentName() {
            return name;
        }
        
        @Override
        public Optional createValue(Statement parent) {
            return Optional.empty();
        }
        
        @Override
        public Value createEmptyValue(Statement parent) {
            return null;
        }
        
        @Override
        public void onDelete(Runnable runnable) {
        
        }
    }
    
}
