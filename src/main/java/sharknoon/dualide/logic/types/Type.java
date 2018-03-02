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

import java.util.Optional;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import sharknoon.dualide.logic.items.Class.ObjectType;
import sharknoon.dualide.utils.javafx.BindUtils;

/**
 *
 * @author Josua Frank
 * @param <T>
 */
public interface Type<T extends Type> {

    public default Type getReturnType() {
        return this;
    }

    public boolean isPrimitive();

    public PrimitiveType getPrimitiveType();

    public ObjectType getClassType();

    public static ListProperty<Type> getAllTypes() {
        return new SimpleListProperty<>(BindUtils.concat(PrimitiveType.getAll(), ObjectType.getAll()));
    }

    public static Optional<Type> valueOf(String name) {
        return PrimitiveType.forName(name).map(p -> (Type) p)
                .or(() -> ObjectType.forName(name));
    }

    public StringProperty getSimpleName();

    public StringProperty getFullName();

}
