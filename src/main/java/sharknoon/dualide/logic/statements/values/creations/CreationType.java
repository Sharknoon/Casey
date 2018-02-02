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
package sharknoon.dualide.logic.statements.values.creations;

import sharknoon.dualide.ui.misc.Icon;
import sharknoon.dualide.utils.language.Language;
import sharknoon.dualide.utils.language.Word;

/**
 *
 * @author Josua Frank
 */
public enum CreationType {
    NUMBER_CREATION(new NumberCreation(), Word.NUMBER_CREATION, Icon.PLUSNUMBER),
    BOOLEAN_CREATION(new BooleanCreation(), Word.BOOLEAN_CREATION, Icon.PLUSBOOLEAN),
    TEXT_CREATION(new TextCreation(), Word.TEXT_CREATION, Icon.PLUSTEXT),
    OBJECT_CREATION(new ObjectCreation(), Word.OBJECT_CREATION, Icon.PLUSCLASS);

    private final Creation instance;
    private final Word name;
    private final Icon icon;

    private <C extends Creation> CreationType(C instance, Word name, Icon icon) {
        this.instance = instance;
        this.name = name;
        this.icon = icon;
    }

    public Creation create() {
        return instance;
    }
    
    public String getName(){
        return Language.get(name);
    }
    
    public Icon getIcon(){
        return icon;
    }
}
