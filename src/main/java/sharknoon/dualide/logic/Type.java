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
package sharknoon.dualide.logic;

import java.util.HashMap;
import java.util.Map;
import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import sharknoon.dualide.utils.language.Language;
import sharknoon.dualide.utils.language.Word;

/**
 *
 * @author Josua Frank
 * @param <T>
 */
public enum Type {
    CLASS(Class.class),
    FUNCTION(Function.class),
    PACKAGE(Package.class),
    PROJECT(Project.class),
    VARIABLE(Variable.class),
    WELCOME(Welcome.class);

    private final java.lang.Class<? extends Item> type;
    private final transient StringProperty name = new SimpleStringProperty();
    private static final transient Map<java.lang.Class<? extends Item>, Type> TYPES = new HashMap<>();

    private Type(java.lang.Class<? extends Item> type) {
        this.type = type;
        setTypeName();
    }

    static {
        for (Type value : Type.values()) {
            TYPES.put(value.type, value);
        }
    }

    public static Type valueOf(java.lang.Class<? extends Item> item) {
        return TYPES.get(item);
    }

    private void setTypeName() {
        Word WordTypeName = Word.valueOf(name());
        Language.setCustom(WordTypeName, s -> nameProperty().set(s));
    }

    public StringProperty nameProperty() {
        return name;
    }

}
