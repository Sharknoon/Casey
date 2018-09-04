package sharknoon.casey.compiler.general.parser

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

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import sharknoon.casey.compiler.general.parser.beans.*
import sharknoon.casey.compiler.general.parser.beans.Item.ItemType
import java.nio.file.*
import java.util.*

private var NAME_TO_ITEM = mutableMapOf<String, Item>()
private var ITEM_TO_NAME = mutableMapOf<Item, String>()
private var NAME_TO_BLOCK = mutableMapOf<UUID, Block>()
private var STATIC_VARIABLES = mutableListOf<Item>()
private var STATIC_FUNCTIONS = mutableListOf<Item>()

fun getItem(name: String): Item? = NAME_TO_ITEM[name]

fun getItemFullName(item: Item): String? = ITEM_TO_NAME[item]

fun getBlock(id: UUID): Block? = NAME_TO_BLOCK[id]

fun isValidItemName(name: String): Boolean = NAME_TO_ITEM.containsKey(name)

fun isStaticVariable(item: Item): Boolean = STATIC_VARIABLES.contains(item)

fun isStaticFunction(item: Item): Boolean = STATIC_FUNCTIONS.contains(item)

fun parseCasey(path: Path): Item? {
    if (!Files.exists(path)) {
        System.err.println("Could not find file: $path")
        return null
    }
    val newItem = getItem(path) ?: return null
    buildNameDirectories(newItem, null, "")
    return newItem
}

private fun getItem(path: Path): Item? {
    val mapper = ObjectMapper().registerKotlinModule()
    try {
        return mapper.readValue<Item>(Files.newInputStream(path), Item::class.java)
    } catch (e: Exception) {
        System.err.println("Could not parse item: $e")
    }
    return null
}

private fun buildNameDirectories(i: Item, p: Item?, currentPath: String) {
    val newCurrentPath = if (currentPath.isEmpty()) i.name else currentPath + "." + i.name
    NAME_TO_ITEM[newCurrentPath] = i
    ITEM_TO_NAME[i] = newCurrentPath
    if (p != null && p.item === ItemType.PACKAGE) {
        if (i.item === ItemType.VARIABLE) {
            STATIC_VARIABLES.add(i)
        } else if (i.item === ItemType.FUNCTION) {
            STATIC_FUNCTIONS.add(i)
        }
    }
    for (c in i.children) {
        buildNameDirectories(c, i, newCurrentPath)
    }
    for (block in i.blocks) {
        val id = block.blockid
        NAME_TO_BLOCK[id] = block
    }

}
