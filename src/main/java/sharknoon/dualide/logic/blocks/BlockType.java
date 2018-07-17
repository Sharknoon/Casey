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
package sharknoon.dualide.logic.blocks;

import sharknoon.dualide.utils.language.Word;

/**
 *
 * @author frank
 */
public enum BlockType {
    START(false, Word.EMTPY),
    END(true, Word.FUNCTION_SITE_ADD_NEW_END_BLOCK),
    CALL(true, Word.FUNCTION_SITE_ADD_NEW_CALL_BLOCK),
    ASSIGNMENT(true, Word.FUNCTION_SITE_ADD_NEW_ASSIGNMENT_BLOCK),
    DECISION(true, Word.FUNCTION_SITE_ADD_NEW_DECISION_BLOCK),
    INPUT(true, Word.FUNCTION_SITE_ADD_NEW_INPUT_BLOCK),
    OUTPUT(true, Word.FUNCTION_SITE_ADD_NEW_OUTPUT_BLOCK);

    private final boolean creatableByUser;
    private final Word wordContextMenuAddBlock;
    
    BlockType(boolean creatableByUser, Word wordContextMenuAddBlock) {
        this.creatableByUser = creatableByUser;
        this.wordContextMenuAddBlock = wordContextMenuAddBlock;
    }

    public boolean isCreatableByUser() {
        return creatableByUser;
    }

    public Word getContextMenuAddBlockWord() {
        return wordContextMenuAddBlock;
    }

    public static BlockType forName(String name) {
        return valueOf(name.trim().toUpperCase());
    }
}
