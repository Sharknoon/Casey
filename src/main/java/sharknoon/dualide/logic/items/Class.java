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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import sharknoon.dualide.logic.Statement;
import sharknoon.dualide.logic.operators.OperatorType;
import sharknoon.dualide.logic.types.PrimitiveType;
import sharknoon.dualide.logic.types.Type;
import sharknoon.dualide.logic.values.ObjectValue;
import sharknoon.dualide.ui.dialogs.Dialogs;
import sharknoon.dualide.ui.misc.Icon;
import sharknoon.dualide.utils.javafx.BindUtils;
import sharknoon.dualide.utils.javafx.FXUtils;
import sharknoon.dualide.utils.language.Language;
import sharknoon.dualide.utils.language.Word;

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

    @Override
    public void destroy() {
        super.destroy();
        CLASSES.remove(this);
        type.onDelete.forEach(Runnable::run);
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

    public ObjectType toType() {
        return type;
    }

    public static class ObjectType implements Type<ObjectType, ObjectValue> {

        private final Class clazz;
        private static final ListProperty<ObjectType> TYPES = new SimpleListProperty<>(BindUtils.map((ObservableList<Class>) classesProperty(), c -> c.type));

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

        public static ListProperty<ObjectType> getAll() {
            return TYPES;
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

        @Override
        public Icon getIcon() {
            return Icon.CLASS;
        }

        @Override
        public Optional<ObjectValue> createValue(Statement parent) {
            ComboBox<ObjectType> types = new ComboBox<>();
            types.itemsProperty().bindBidirectional(ObjectType.getAll());
            FXUtils.fixComboBoxText(types, ObjectType::getSimpleName, ObjectType::getFullName);
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setMaxWidth(Double.MAX_VALUE);
            grid.setAlignment(Pos.CENTER_LEFT);
            grid.add(types, 0, 0);
            return Dialogs
                    .showCustomInputDialog(
                            Word.NEW_OBJECT_VALUE_DIALOG_TITLE,
                            Word.NEW_OBJECT_VALUE_DIALOG_HEADER_TEXT,
                            Word.NEW_OBJECT_VALUE_DIALOG_CONTENT_TEXT,
                            Icon.CLASS,
                            grid,
                            p -> ((ComboBox<ObjectType>) p.getChildren().get(0)).getSelectionModel().getSelectedItem()
                    )
                    .map(o -> new ObjectValue(o, parent));
        }

        @Override
        public Icon getCreationIcon() {
            return Icon.PLUSCLASS;
        }

        private StringProperty creationText;

        @Override
        public StringProperty getCreationText() {
            if (creationText == null) {
                creationText = new SimpleStringProperty();
                Language.setCustom(Word.OBJECT_CREATION, creationText::set);

            }
            return creationText;
        }
        private StringProperty name;

        @Override
        public StringProperty getName() {
            if (name == null) {
                name = new SimpleStringProperty();
                Language.setCustom(Word.OBJECT, name::set);
            }
            return name;
        }

        @Override
        public String toString() {
            return getSimpleName().get();
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

        List<Runnable> onDelete = new ArrayList();

        @Override
        public void onDelete(Runnable runnable) {
            onDelete.add(runnable);
        }

    }

}
