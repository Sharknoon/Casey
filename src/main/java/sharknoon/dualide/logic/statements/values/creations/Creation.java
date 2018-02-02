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
package sharknoon.dualide.logic.statements.values.creations;

import java.util.Optional;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import sharknoon.dualide.logic.statements.Statement;
import sharknoon.dualide.logic.statements.values.Value;

/**
 *
 * @author Josua Frank
 * @param <V> The type of the new value to be created
 */
public abstract class Creation<V extends Value> {

    private final ObjectProperty<V> value = new SimpleObjectProperty<>();

    public ObjectProperty<V> valueProperty() {
        return value;
    }

    public void setValue(V value) {
        valueProperty().set(value);
    }

    public Optional<V> getValue() {
        return Optional.ofNullable(valueProperty().get());
    }

    public abstract Optional<V> create(Statement parent);
    
}
