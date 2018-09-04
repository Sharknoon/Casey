package sharknoon.casey.compiler.general

import sharknoon.casey.compiler.general.parser.beans.Block
import sharknoon.casey.compiler.general.parser.getBlock

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

private val START_BLOCKS: MutableMap<Block, Skeleton> = mutableMapOf()

fun getSkeleton(startBlock: Block) = START_BLOCKS.getOrElse(startBlock) { createSkeleton(startBlock) }

private fun createSkeleton(startBlock: Block, visitedBlocks: MutableList<Block> = mutableListOf()): Skeleton? {
    val newSkeleton = Skeleton()

    var currentBlock = startBlock
    while (hasNextBlock(currentBlock)) {
        currentBlock = getNextBlock(currentBlock) ?: return null
        visitedBlocks.add(currentBlock)
        if (hasMultipleOutputs(currentBlock)) {
            for (output in getMultipleOutputs(currentBlock)) {

            }
        } else {

        }
    }

    START_BLOCKS[startBlock] = newSkeleton
    return newSkeleton

}


fun hasNextBlock(block: Block) = block.blockconnections.isNotEmpty()

fun getNextBlock(block: Block): Block? {
    if (block.blockconnections.isEmpty()) {
        System.err.println("Could not get the connections for the block $block")
        return null
    }
    val destination = block.blockconnections.values.iterator().next()
    if (destination.isEmpty()) {
        System.err.println("The destinations of this block connections are null, cant get next block $block")
        return null
    }
    val next = destination.keys.iterator().next()
    return getBlock(next)
}

fun hasMultipleOutputs(block: Block) = block.blockconnections.size > 1

fun getMultipleOutputs(block: Block) = block.blockconnections.values

data class Skeleton(val SubSkeletons: List<SubSkeleton> = listOf())

interface SubSkeleton

/**
 * A Single block with one output any one ore more inputs
 */
data class SingleBlock(val block: Block) : SubSkeleton

/**
 * A Decision: When the condition matches, the trueBlocks are exited, the falseBlocks otherwise.
 * Then the Decision exits.
 */
data class Decision(val condition: Block,
                    val trueBlocks: List<Block> = listOf(),
                    val falseBlocks: List<Block> = listOf()) : SubSkeleton

/**
 * A Loop: The blocks beforeCondition are executed first, then, when the condition matches, afterCondition blocks are
 * executed, then the beforeCondition block, then the condition is checked again, when the condition is false, the
 * Loop exits
 */
data class Loop(val beforeCondition: List<Block> = listOf(),
                val condition: Block,
                val afterCondition: List<Block> = listOf()) : SubSkeleton