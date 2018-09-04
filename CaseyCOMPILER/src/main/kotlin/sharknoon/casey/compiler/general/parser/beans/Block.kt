package sharknoon.casey.compiler.general.parser.beans

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
 * See the License for the specific languageString governing permissions and
 * limitations under the License.
 */

import com.fasterxml.jackson.annotation.JsonIgnore
import java.util.*

data class Block(
        //The id of this block
        var blockid: UUID) {

    //The coordinates of this block (not used)
    @JsonIgnore
    var blockX: Double = 0.0
    @JsonIgnore
    var blockY: Double = 0.0
    //The type of this block
    var blocktype: BlockType = BlockType.START
    //The connections from this to other blocks
    var blockconnections: Map<ConnectionSide, Map<UUID, ConnectionSide>> = mapOf()
    //The statements of this block
    var blockcontent: BlockContent? = null

    enum class BlockType {
        START,
        END,
        DECISION,
        CALL,
        ASSIGNMENT,
        INPUT,
        OUTPUT
    }

    enum class ConnectionSide {
        TOP,
        BOTTOM,
        LEFT,
        RIGHT
    }


    data class BlockContent(
            //The statement of the block (only for assignment, call, decision, end, output)
            var statement: Statement? = null,
            //The variable the statement is assigned to or the input is being stored into (only for assignment, input)
            var variable: String? = null)
}
