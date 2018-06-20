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
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import sharknoon.dualide.logic.Returnable;
import sharknoon.dualide.logic.statements.Statement;
import sharknoon.dualide.logic.statements.values.ObjectValue;
import sharknoon.dualide.logic.types.PrimitiveType;
import sharknoon.dualide.logic.types.Type;
import sharknoon.dualide.ui.bodies.TypeBrowser;
import sharknoon.dualide.ui.dialogs.Dialogs;
import sharknoon.dualide.ui.misc.Icon;
import sharknoon.dualide.utils.javafx.BindUtils;
import sharknoon.dualide.utils.language.Language;
import sharknoon.dualide.utils.language.Word;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Josua Frank
 */
public class Class extends Item<Class, Package, Item<? extends Item, Class, ? extends Item>> implements Returnable<Class.ObjectType> {

    private transient static final ListProperty<Class> CLASSES = new SimpleListProperty<>(FXCollections.observableArrayList());

    private transient final ObjectType type = new ObjectType(this);
    private transient final FilteredList<Item<? extends Item, Class, ? extends Item>> variables = new FilteredList<>(childrenProperty(), c -> c.getType() == ItemType.VARIABLE);
    private transient final FilteredList<Item<? extends Item, Class, ? extends Item>> functions = new FilteredList<>(childrenProperty(), c -> c.getType() == ItemType.FUNCTION);

    protected Class(Package parent, String name) {
        super(parent, name);
        CLASSES.add(this);
    }

    private static ListProperty<Class> classesProperty() {
        return CLASSES;
    }

    /**
     * case sensitive!
     *
     * @param fullName The full name of the class, e.g. MyProject.package.Class
     * @return A optional holding eventuelly the class type
     */
    public static Optional<Class> forName(String fullName) {
        if (fullName.isEmpty()) {
            return Optional.empty();
        }
        return CLASSES.stream()
                .filter(c -> c.getFullName().equals(fullName))
                .findFirst();
    }

    @Override
    public void destroy() {
        List<String> usagesList = new ArrayList<>();
        if (Variable.getAllVariables().containsKey(type) && !Variable.getAllVariables().get(type).isEmpty()) {
            usagesList.add(Language.get(Word.VARIABLE) + ":");
            Variable.getAllVariables().get(type).stream().map(Item::getFullName).forEachOrdered(usagesList::add);
        }
        if (Parameter.getAllParameters().containsKey(type) && !Parameter.getAllParameters().get(type).isEmpty()) {
            usagesList.add(Language.get(Word.FUNCTION_SITE_FUNCTION_PARAMETERS) + ":");
            Parameter.getAllParameters().get(type).stream().map(Item::getFullName).forEachOrdered(usagesList::add);
        }
        if (Function.getAllReturnTypes().containsKey(type) && !Function.getAllReturnTypes().get(type).isEmpty()) {
            usagesList.add(Language.get(Word.FUNCTION_SITE_FUNCTION_RETURNTYPE) + ":");
            Function.getAllReturnTypes().get(type).stream().map(Item::getFullName).forEachOrdered(usagesList::add);
        }
        if (usagesList.isEmpty()) {
            super.destroy();
            CLASSES.remove(this);
            type.onDelete.forEach(Runnable::run);
        } else {
            Dialogs.showErrorDialog(Dialogs.Errors.TYPE_IN_USE_DIALOG, null, Map.of("LIST", usagesList.stream().collect(Collectors.joining("\n"))));
        }
    }

    public ObservableList<Item<? extends Item, Class, ? extends Item>> getVariables() {
        return variables;
    }

    public ObservableList<Item<? extends Item, Class, ? extends Item>> getFunctions() {
        return functions;
    }

    @Override
    public Map<String, JsonNode> getAdditionalProperties() {
        return super.getAdditionalProperties();
    }

    @Override
    public void setAdditionalProperties(Map<String, JsonNode> properties) {
        super.setAdditionalProperties(properties);
    }

    @Override
    public ObjectType getReturnType() {
        return type;
    }

    public static class ObjectType extends Type<ObjectType, ObjectValue> {

        private static final ListProperty<ObjectType> TYPES = new SimpleListProperty<>(BindUtils.map((ObservableList<Class>) classesProperty(), c -> c.type));
        public static ObjectType GENERAL = new ObjectType(null) {

            private StringProperty typeName = new SimpleStringProperty();

            @Override
            public void onDelete(Runnable runnable) {
                //general cant be deleted
            }

            @Override
            public boolean equals(Object obj) {
                return super.equals(obj);
            }

            @Override
            public int hashCode() {
                return -1;
            }

            @Override
            public String toString() {
                return "GENERIC, DO NOT USE!";
            }

            @Override
            public StringProperty getLanguageDependentName() {
                if (typeName.get() == null){
                    Language.setCustom(Word.OBJECT, typeName::set);
                }
                return typeName;
            }

            @Override
            public StringProperty creationTextProperty() {
                return super.creationTextProperty();
            }

            @Override
            public Icon getCreationIcon() {
                return super.getCreationIcon();
            }

            @Override
            public Optional<ObjectValue> createValue(Statement parent) {
                return super.createValue(parent);
            }

            @Override
            public Icon getIcon() {
                return super.getIcon();
            }

            @Override
            public StringProperty fullNameProperty() {
                return new SimpleStringProperty("GENERAL");
            }

            @Override
            public StringProperty simpleNameProperty() {
                return fullNameProperty();
            }

            @Override
            public ObjectType getObjectType() {
                return super.getObjectType();
            }

            @Override
            public PrimitiveType getPrimitiveType() {
                return super.getPrimitiveType();
            }

            @Override
            public boolean isPrimitive() {
                return super.isPrimitive();
            }

        };

        private final Class clazz;
        List<Runnable> onDelete = new ArrayList<>();
        private Type selectedType;
        private StringProperty creationText;
        private StringProperty name;

        ObjectType(Class clazz) {
            this.clazz = clazz;
        }

        public static ListProperty<ObjectType> getAll() {
            return TYPES;
        }

        public static Optional<ObjectType> forName(String name) {
            return Class.forName(name).map(Class::getReturnType);
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
        public ObjectType getObjectType() {
            return this;
        }

        @Override
        public StringProperty simpleNameProperty() {
            return clazz.nameProperty();
        }

        /**
         * unidirectional binding only
         *
         * @return Returns the full name of the class
         */
        @Override
        public StringProperty fullNameProperty() {
            StringProperty sp = new SimpleStringProperty();
            sp.bind(clazz.fullNameProperty());
            return sp;
        }

        @Override
        public Icon getIcon() {
            return Icon.CLASS;
        }

        @Override
        public Optional<ObjectValue> createValue(Statement parent) {
            TypeBrowser browser = TypeBrowser.createOnlyObjectTypebrowser(t -> selectedType = t, null);
            browser.setMinHeight(200);
            return Dialogs
                    .showCustomInputDialog(
                            Word.NEW_OBJECT_VALUE_DIALOG_TITLE,
                            Word.NEW_OBJECT_VALUE_DIALOG_HEADER_TEXT,
                            Word.NEW_OBJECT_VALUE_DIALOG_CONTENT_TEXT,
                            Icon.CLASS,
                            browser,
                            p -> selectedType
                    )
                    .map(o -> new ObjectValue(o.getObjectType(), parent));
        }

        @Override
        public Icon getCreationIcon() {
            return Icon.PLUSCLASS;
        }

        @Override
        public StringProperty creationTextProperty() {
            if (creationText == null) {
                creationText = new SimpleStringProperty();
                Language.setCustom(Word.OBJECT_CREATION, creationText::set);

            }
            return creationText;
        }

        @Override
        public StringProperty getLanguageDependentName() {
            return simpleNameProperty();
        }

        @Override
        public String toString() {
            return simpleNameProperty().get();
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 59 * hash + Objects.hashCode(this.clazz);
            return hash;
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
            final ObjectType other = (ObjectType) obj;
            return Objects.equals(this.clazz, other.clazz);
        }

        @Override
        public void onDelete(Runnable runnable) {
            onDelete.add(runnable);
        }
    }

}
