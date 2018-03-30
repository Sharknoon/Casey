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
import javafx.beans.property.ListProperty;
import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import sharknoon.dualide.logic.Statement;
import sharknoon.dualide.logic.items.Class.ObjectType;
import sharknoon.dualide.logic.values.PrimitiveValue;
import sharknoon.dualide.logic.values.PrimitiveValue.BooleanValue;
import sharknoon.dualide.logic.values.PrimitiveValue.NumberValue;
import sharknoon.dualide.logic.values.PrimitiveValue.TextValue;
import sharknoon.dualide.logic.values.Value;
import sharknoon.dualide.ui.dialogs.Dialogs;
import sharknoon.dualide.ui.misc.Icon;
import sharknoon.dualide.utils.language.Language;
import sharknoon.dualide.utils.language.Word;

/**
 *
 * @author Josua Frank
 * @param <T>
 * @param <V>
 */
public abstract class PrimitiveType<T extends PrimitiveType, V extends PrimitiveValue> implements Type<T, V> {

    public static Optional<PrimitiveType> forName(String asText) {
        return Optional.ofNullable(TYPESMAP.get(asText.toUpperCase()));
    }

    private static final ListProperty<PrimitiveType> TYPESLIST = new SimpleListProperty<>(FXCollections.observableArrayList());
    private static final MapProperty<String, PrimitiveType> TYPESMAP = new SimpleMapProperty<>(FXCollections.observableHashMap());

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

    public static ListProperty<PrimitiveType> getAll() {
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

    @Override
    public void onDelete(Runnable runnable) {
        //Do nothing, primitive types cant be deleted
    }

    
    
    public static BooleanType BOOLEAN = new BooleanType();

    public static class BooleanType extends PrimitiveType<BooleanType, BooleanValue> {

        private BooleanType() {
        }

        @Override
        public Icon getIcon() {
            return Icon.BOOLEAN;
        }

        @Override
        public Optional<BooleanValue> createValue(Statement parent) {
            return Dialogs
                    .showBooleanInputDialog(Dialogs.BooleanInputs.NEW_BOOLEAN_VALUE)
                    .map(b -> new BooleanValue(b, parent));
        }

        @Override
        public Icon getCreationIcon() {
            return Icon.PLUSBOOLEAN;
        }
        private StringProperty creationText;

        @Override
        public StringProperty getCreationText() {
            if (creationText == null) {
                creationText = new SimpleStringProperty();
                Language.setCustom(Word.BOOLEAN_CREATION, creationText::set);

            }
            return creationText;
        }
        private StringProperty name;

        @Override
        public StringProperty getName() {
            if (name == null) {
                name = new SimpleStringProperty();
                Language.setCustom(Word.BOOLEAN, name::set);
            }
            return name;
        }

    }

    public static NumberType NUMBER = new NumberType();

    public static class NumberType extends PrimitiveType<NumberType, NumberValue> {

        private NumberType() {
        }

        @Override
        public Icon getIcon() {
            return Icon.NUMBER;
        }

        @Override
        public Optional<NumberValue> createValue(Statement parent) {
            return Dialogs
                    .showNumberInputDialog(Dialogs.NumberInputs.NEW_NUMBER_VALUE)
                    .map(d -> new NumberValue(d, parent));
        }

        @Override
        public Icon getCreationIcon() {
            return Icon.PLUSNUMBER;
        }

        private StringProperty creationText;

        @Override
        public StringProperty getCreationText() {
            if (creationText == null) {
                creationText = new SimpleStringProperty();
                Language.setCustom(Word.NUMBER_CREATION, creationText::set);

            }
            return creationText;
        }
        private StringProperty name;

        @Override
        public StringProperty getName() {
            if (name == null) {
                name = new SimpleStringProperty();
                Language.setCustom(Word.NUMBER, name::set);
            }
            return name;
        }

    }

    public static TextType TEXT = new TextType();

    public static class TextType extends PrimitiveType<TextType, TextValue> {

        private TextType() {
        }

        @Override
        public Icon getIcon() {
            return Icon.TEXT;
        }

        @Override
        public Optional<TextValue> createValue(Statement parent) {
            return Dialogs
                    .showTextInputDialog(Dialogs.TextInputs.NEW_TEXT_VALUE)
                    .map(t -> new TextValue(t, parent));
        }

        @Override
        public Icon getCreationIcon() {
            return Icon.PLUSTEXT;
        }

        private StringProperty creationText;

        @Override
        public StringProperty getCreationText() {
            if (creationText == null) {
                creationText = new SimpleStringProperty();
                Language.setCustom(Word.TEXT_CREATION, creationText::set);

            }
            return creationText;
        }
        private StringProperty name;

        @Override
        public StringProperty getName() {
            if (name == null) {
                name = new SimpleStringProperty();
                Language.setCustom(Word.TEXT, name::set);
            }
            return name;
        }

    }

}
