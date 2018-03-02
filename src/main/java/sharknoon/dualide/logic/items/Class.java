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
package sharknoon.dualide.logic.items;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import sharknoon.dualide.logic.operators.OperatorType;
import sharknoon.dualide.logic.types.PrimitiveType;
import sharknoon.dualide.logic.types.Type;
import sharknoon.dualide.ui.misc.Icon;
import sharknoon.dualide.utils.javafx.BindUtils;

/**
 *
 * @author Josua Frank
 */
public class Class extends Item<Class, Package, Item<? extends Item, Class, ? extends Item>> {

    private transient static final ListProperty<Class> CLASSES = new SimpleListProperty<>(FXCollections.observableArrayList());

    private transient final ObjectType type = new ObjectType(this);
    private transient final FilteredList<Item<? extends Item, Class, ? extends Item>> variables = new FilteredList<>(childrenProperty(), c -> c.getType() == ItemType.VARIABLE);
    private transient final FilteredList<Item<? extends Item, Class, ? extends Item>> functions = new FilteredList<>(childrenProperty(), c -> c.getType() == ItemType.FUNCTION);

    protected Class(Package parent, String name) {
        super(parent, name);
        CLASSES.add(this);
    }

    public static ListProperty<Class> classesProperty() {
        return CLASSES;
    }

    /**
     * case sensitive!
     *
     * @param fullName
     * @return
     */
    public static Optional<Class> forName(String fullName) {
        return CLASSES.stream()
                .filter(c -> c.getFullName().equals(fullName))
                .findFirst();
    }

    public ObservableList<Item<? extends Item, Class, ? extends Item>> getVariables() {
        return variables;
    }

    public ObservableList<Item<? extends Item, Class, ? extends Item>> getFunctions() {
        return functions;
    }

    @Override
    public void setAdditionalProperties(Map<String, JsonNode> properties) {
        super.setAdditionalProperties(properties);
    }

    @Override
    public Map<String, JsonNode> getAdditionalProperties() {
        return super.getAdditionalProperties();
    }

    public static class ObjectType implements Type<ObjectType> {

        private final Class clazz;

        public ObjectType(Class clazz) {
            this.clazz = clazz;
        }

        @Override
        public boolean isPrimitive() {
            return false;
        }

        @Override
        public PrimitiveType getPrimitiveType() {
            return null;
        }

        @Override
        public ObjectType getClassType() {
            return this;
        }

        public static ObservableList<ObjectType> getAll() {
            return BindUtils.map((ObservableList<Class>) classesProperty(), c -> c.type);
        }

        @Override
        public StringProperty getSimpleName() {
            return clazz.nameProperty();
        }

        /**
         * unidirectional binding only
         *
         * @return
         */
        @Override
        public StringProperty getFullName() {
            StringProperty sp = new SimpleStringProperty();
            sp.bind(clazz.fullNameProperty());
            return sp;
        }

        public static Optional<ObjectType> forName(String name) {
            return Class.forName(name).map(c -> c.type);
        }

    }

}
