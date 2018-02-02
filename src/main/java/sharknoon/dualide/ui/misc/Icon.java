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
package sharknoon.dualide.ui.misc;

import sharknoon.dualide.utils.settings.Ressources;

/**
 *
 * @author Josua Frank
 */
public enum Icon {

    AND,
    BACKGROUND,
    BOOLEAN,
    CLASS,
    CLOSE,
    COG,
    COMMENTS,
    CONCAT,
    DIVIDE,
    DURATION,
    ENGLISH,
    EQUAL,
    ERROR,
    FUNCTION,
    GERMAN,
    GREATEROREQUALTHAN,
    GREATERTHAN,
    LANGUAGE,
    LESSOREQUALTHAN,
    LESSTHAN,
    LOAD,
    MINUS,
    MODULO,
    MULTIPLY,
    NOT,
    NOTEQUAL,
    NUMBER,
    OR,
    PACKAGE,
    PLUS,
    PLUSBOOLEAN,
    PLUSCLASS,
    PLUSFUNCTION,
    PLUSNUMBER,
    PLUSPACKAGE,
    PLUSPROJECT,
    PLUSTEXT,
    PLUSVARIABLE,
    PROJECT,
    RENAME,
    SAVE,
    TEXT,
    TRASH,
    VARIABLE,
    VARIABLEBOOLEAN,
    VARIABLECLASS,
    VARIABLENUMBER,
    VARIABLETEXT,
    WELCOME;

    private String path;
    private boolean isSearched = false;

    private Icon() {
        this.path = name().toLowerCase() + ".png";
    }

    public String getPath() {
        if (!isSearched) {
            path = Ressources.search(path, true);
            isSearched = true;
        }
        return path;
    }

    public static Icon forName(String name) {
        return Enum.valueOf(Icon.class, name.toUpperCase());
    }

}
