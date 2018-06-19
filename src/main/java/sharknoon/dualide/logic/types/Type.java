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
import sharknoon.dualide.logic.statements.Statement;
import sharknoon.dualide.logic.items.Class.ObjectType;
import sharknoon.dualide.logic.statements.values.Value;
import sharknoon.dualide.ui.misc.Icon;
import sharknoon.dualide.utils.javafx.BindUtils;

/**
 *
 * @author Josua Frank
 * @param <T>
 * @param <V>
 */
public abstract class Type<T extends Type, V extends Value> {

    public Type getReturnType() {
        return this;
    }

    public abstract boolean isPrimitive();

    public abstract PrimitiveType getPrimitiveType();

    public abstract ObjectType getObjectType();

    public static ListProperty<Type> getAllTypes() {
        return new SimpleListProperty<>(BindUtils.concat(PrimitiveType.getAll(), ObjectType.getAll()));
    }

    public static Optional<Type> valueOf(String name) {
        return PrimitiveType.forName(name).map(p -> (Type) p)
                .or(() -> ObjectType.forName(name));
    }

    public abstract StringProperty getSimpleName();

    public abstract StringProperty getFullName();

    public abstract Icon getIcon();

    public abstract Icon getCreationIcon();

    public abstract StringProperty getCreationText();

    /**
     * Language dependent name, e.g. Nummer in german for Number
     *
     * @return
     */
    public abstract StringProperty getLanguageDependentName();

    public abstract Optional<V> createValue(Statement parent);

    public abstract void onDelete(Runnable runnable);

}
