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

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;

/**
 *
 * @author Josua Frank
 */
public abstract class Value<V> {
    
    private final ReadOnlyObjectWrapper<V> value = new ReadOnlyObjectWrapper<>();
    
    protected Value(V value){
        this.value.set(value);
    }
    
    public abstract V getDefaultValue();

    public ReadOnlyObjectProperty<V> valueProperty() {
        return value.getReadOnlyProperty();
    }
    
    public V getValue(){
        return valueProperty().get();
    }
    
}
