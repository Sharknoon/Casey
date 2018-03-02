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

import java.util.Collection;
import java.util.Optional;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import sharknoon.dualide.logic.items.Class.ObjectType;

/**
 *
 * @author Josua Frank
 */
public abstract class PrimitiveType implements Type<PrimitiveType> {

    public static Optional<PrimitiveType> forName(String asText) {
        return Optional.ofNullable(TYPESMAP.get(asText.toUpperCase()));
    }

    private static final ObservableList<PrimitiveType> TYPESLIST = FXCollections.observableArrayList();
    private static final ObservableMap<String, PrimitiveType> TYPESMAP = FXCollections.observableHashMap();

    public static Collection<? extends String> getForbiddenNames() {
        return TYPESMAP.keySet();
    }

    private final StringProperty name = new SimpleStringProperty();

    protected PrimitiveType() {
        String nameWithType = getClass().getSimpleName().toUpperCase();
        name.set(nameWithType.substring(0, nameWithType.length() - 4));
        TYPESLIST.add(this);
        TYPESMAP.put(name.get(), this);
    }

    @Override
    public boolean isPrimitive() {
        return true;
    }

    @Override
    public PrimitiveType getPrimitiveType() {
        return this;
    }

    @Override
    public ObjectType getClassType() {
        return null;
    }

    public static ObservableList<PrimitiveType> getAll() {
        return TYPESLIST;
    }

    @Override
    public StringProperty getSimpleName() {
        return name;
    }

    @Override
    public StringProperty getFullName() {
        return getSimpleName();
    }

    public static BooleanType BOOLEAN = new BooleanType();

    public static class BooleanType extends PrimitiveType {

        private BooleanType() {
        }

    }

    public static NumberType NUMBER = new NumberType();

    public static class NumberType extends PrimitiveType {

        private NumberType() {
        }

    }

    public static TextType TEXT = new TextType();

    public static class TextType extends PrimitiveType {

        private TextType() {
        }

    }

}
