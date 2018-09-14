package sharknoon.casey.compiler.general.parser.beans

import java.util.*

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

data class Item(
        //The name of the item (required)
        var name: String,
        //The list of children of this item (required)
        var children: List<Item>) {

    //Comments of this item (required)
    var comments: String = ""
    //The type of this item (required)
    var item: ItemType = ItemType.PROJECT
    //The id of this project (only for projects)
    var id: UUID? = null
    //The type of this variable or parameter (only for variables and parameters)
    var type: String? = null
    //The returntype of this function (only for functions)
    var returntype: String? = null
    //The list of blocks of this function (only for functions)
    var blocks: List<Block> = listOf()

    enum class ItemType {
        FUNCTION,
        PROJECT,
        PACKAGE,
        VARIABLE,
        PARAMETER,
        CLASS
    }
}

private val START_BLOCKS = mutableMapOf<Item, Block>()
val Item.startBlock: Block
    get() {
        if (START_BLOCKS.containsKey(this)) {
            return START_BLOCKS.computeIfAbsent(this) { Block(UUID.randomUUID()) }
        }
        for (block in this.blocks) {
            if (block.blocktype == Block.BlockType.START) {
                START_BLOCKS[this] = block
                return block
            }
        }
        System.err.println("No start block for item $this")
        System.exit(2)
        return Block(UUID.randomUUID())//Never occurs
    }