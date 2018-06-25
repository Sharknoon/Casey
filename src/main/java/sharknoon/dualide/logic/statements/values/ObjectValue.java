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

import sharknoon.dualide.logic.items.Class.ObjectType;
import sharknoon.dualide.logic.statements.Statement;

import java.util.Objects;

/**
 * @author Josua Frank
 */
public class ObjectValue extends Value<ObjectType> {
    
    public static ObjectValue createObject(ObjectType type, Statement parent) {
        return new ObjectValue(type, parent);
    }
    
    public ObjectValue(ObjectType type, Statement parent) {
        super(type, parent);
    }
    
    @Override
    public Value<ObjectType> calculateResult() {
        return this;
    }
    
    @Override
    public String toString() {
        return getReturnType().simpleNameProperty().get();
    }
    
    @Override
    public int hashCode() {
        return getReturnType().hashCode();
        //TODO implement methods and paramteres
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
        final ObjectValue other = (ObjectValue) obj;
        return Objects.equals(this.getReturnType(), other.getReturnType());
        //TODO implement methods and paramteres
    }
    
}
