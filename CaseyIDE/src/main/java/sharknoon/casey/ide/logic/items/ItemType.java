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
package sharknoon.casey.ide.logic.items;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import sharknoon.casey.ide.utils.language.Language;
import sharknoon.casey.ide.utils.language.Word;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Josua Frank
 */
public enum ItemType {
    CLASS(Class.class),
    FUNCTION(Function.class),
    PACKAGE(Package.class),
    PROJECT(Project.class),
    VARIABLE(Variable.class),
    WELCOME(Welcome.class),
    PARAMETER(Parameter.class);

    private final java.lang.Class<? extends Item> type;
    //Language dependent name, e.g. 'Klasse' in german, 'class' in english
    private final transient StringProperty name = new SimpleStringProperty();
    private static final transient Map<java.lang.Class<? extends Item>, ItemType> TYPES = new HashMap<>();
    
    ItemType(java.lang.Class<? extends Item> type) {
        this.type = type;
        setTypeName();
    }

    static {
        for (ItemType value : ItemType.values()) {
            TYPES.put(value.type, value);
        }
    }

    public static <O extends Item> ItemType valueOf(O item) {
        return TYPES.get(item.getClass());
    }

    private void setTypeName() {
        Word WordTypeName = Word.valueOf(name());
        Language.setCustom(WordTypeName, s -> nameProperty().set(s));
    }

    public StringProperty nameProperty() {
        return name;
    }

}
