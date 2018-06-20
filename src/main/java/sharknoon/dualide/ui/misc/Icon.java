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

import java.nio.file.Path;
import java.util.Optional;

import sharknoon.dualide.utils.settings.Logger;
import sharknoon.dualide.utils.settings.Resources;

/**
 * @author Josua Frank
 */
public enum Icon {

    AND,
    BACKGROUND,
    BANNED,
    BOOLEAN,
    CLASS,
    CLOSE,
    CLOSEROUND,
    COG,
    COMMENTS,
    CONCAT,
    DIVIDE,
    DURATION,
    ENGLISH,
    EQUAL,
    ERROR,
    FUNCTION,
    FUNCTIONFLOWCHART,
    FUNCTIONPARAMETER,
    FUNCTIONRETURN,
    FUNCTIONVARIABLEPARAMETER,
    GERMAN,
    GREATEROREQUALTHAN,
    GREATERTHAN,
    LANGUAGE,
    LENGTH,
    LESSOREQUALTHAN,
    LESSTHAN,
    LOAD,
    LOGO,
    MINUS,
    MINUSROUND,
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
    PLUSROUND,
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
        this.path = name().toLowerCase();
    }

    public String getPath(boolean asSVG) {
        if (!isSearched) {
            Optional<Path> fullPath = Resources.search(path, true, false, true);
            if (fullPath.isPresent()) {
                path = fullPath.get().toString();
                int index = path.lastIndexOf(".");
                if (index != -1) {
                    path = path.substring(0, path.lastIndexOf("."));
                } else {
                    Logger.error("Path " + path + " should contain a file ending!");
                }
            } else {
                path = "";
            }
            isSearched = true;
        }
        return path.isEmpty() ? path : path + (asSVG ? ".svg" : ".png");
    }

    public static Icon forName(String name) {
        return Enum.valueOf(Icon.class, name.toUpperCase());
    }

}
