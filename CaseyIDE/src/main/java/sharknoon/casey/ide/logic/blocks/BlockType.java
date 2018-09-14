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
package sharknoon.casey.ide.logic.blocks;

import sharknoon.casey.ide.ui.misc.Icon;
import sharknoon.casey.ide.utils.language.Word;

/**
 * @author frank
 */
public enum BlockType {
    START(false, Word.EMTPY, Icon.STARTBLOCK),
    END(true, Word.FUNCTION_SITE_ADD_NEW_END_BLOCK, Icon.ENDBLOCK),
    CALL(true, Word.FUNCTION_SITE_ADD_NEW_CALL_BLOCK, Icon.CALLBLOCK),
    ASSIGNMENT(true, Word.FUNCTION_SITE_ADD_NEW_ASSIGNMENT_BLOCK, Icon.ASSIGNMENTBLOCK),
    DECISION(true, Word.FUNCTION_SITE_ADD_NEW_DECISION_BLOCK, Icon.DECISIONBLOCK),
    INPUT(true, Word.FUNCTION_SITE_ADD_NEW_INPUT_BLOCK, Icon.INPUTBLOCK),
    OUTPUT(true, Word.FUNCTION_SITE_ADD_NEW_OUTPUT_BLOCK, Icon.OUTPUTBLOCK);
    
    public static BlockType forName(String name) {
        return valueOf(name.trim().toUpperCase());
    }
    
    private final boolean creatableByUser;
    private final Word wordContextMenuAddBlock;
    private final Icon iconContextMenuAddBlock;
    
    BlockType(boolean creatableByUser, Word wordContextMenuAddBlock, Icon icon) {
        this.creatableByUser = creatableByUser;
        this.wordContextMenuAddBlock = wordContextMenuAddBlock;
        this.iconContextMenuAddBlock = icon;
    }
    
    public boolean isCreatableByUser() {
        return creatableByUser;
    }
    
    public Word getContextMenuAddBlockWord() {
        return wordContextMenuAddBlock;
    }
    
    public Icon getIconContextMenuAddBlock() {
        return iconContextMenuAddBlock;
    }
}
