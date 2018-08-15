package sharknoon.casey.compiler.general.beans

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

class Item {

    //The name of the item (required)
    var name = ""

    //Comments of this item (required)
    var comments = ""

    //The type of this item (required)
    var item: ItemType? = null

    //The list of children of this item (required)
    var children = listOf<Item>()

    //The id of this project (only for projects)
    var id: UUID? = null

    //The type of this variable or parameter (only for variables and parameters)
    var type: String? = null

    //The returntype of this function (only for functions)
    var returntype: String? = null

    //The list of blocks of this function (only for functions)
    var blocks = listOf<Block>()


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Item

        if (name != other.name) return false
        if (children != other.children) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + children.hashCode()
        return result
    }

    override fun toString(): String {
        return "Item(name='$name', comments='$comments', item=$item, children=$children, id=$id, type=$type, returntype=$returntype, blocks=$blocks)"
    }


    enum class ItemType {
        FUNCTION,
        PROJECT,
        PACKAGE,
        VARIABLE,
        PARAMETER,
        CLASS
    }
}
