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

import sharknoon.dualide.utils.settings.Logger;
import sharknoon.dualide.utils.settings.Resources;

import java.nio.file.Path;
import java.util.Optional;

/**
 * @author Josua Frank
 */
public enum Icon {

    AND,
    ARROWRIGHT,
    BACKGROUND,
    VOID,
    BOOLEAN,
    CLASS,
    CLOSE,
    CLOSEROUND,
    COG,
    COMMENTS,
    CONCAT,
    DARK,
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
    LIGHT,
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
    RUN,
    SAVE,
    STYLE,
    TEXT,
    TRASH,
    VARIABLE,
    VARIABLEBOOLEAN,
    VARIABLECLASS,
    VARIABLENUMBER,
    VARIABLETEXT,
    WELCOME,
    PARAMETER,
    PLUSPARAMETER,
    PARAMETERCLASS,
    PARAMETERBOOLEAN,
    PARAMETERNUMBER,
    PARAMETERTEXT,
    FUNCTIONBOOLEAN,
    FUNCTIONCLASS,
    FUNCTIONTEXT,
    FUNCTIONNUMBER, FUNCTIONVOID;
    
    private String path;
    private boolean isSearched = false;
    
    public static Icon forName(String name) {
        return Enum.valueOf(Icon.class, name.toUpperCase());
    }
    
    Icon() {
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

}
