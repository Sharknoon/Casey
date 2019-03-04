package sharknoon.casey.compiler.general

import sharknoon.casey.compiler.general.parser.beans.Block
import sharknoon.casey.compiler.general.parser.beans.Item
import sharknoon.casey.compiler.general.parser.beans.startBlock
import sharknoon.casey.compiler.general.parser.getBlock
import sharknoon.casey.compiler.utils.Result
import sharknoon.casey.compiler.utils.Result.Error
import sharknoon.casey.compiler.utils.Result.Success

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


private val FUNCTIONS: MutableMap<Item, Skeleton> = mutableMapOf()

fun getSkeleton(function: Item): Skeleton? {
    if (FUNCTIONS.containsKey(function)) return FUNCTIONS[function]
    val skeleton = createSkeleton(function)
    if (skeleton == null) {
        System.err.println("Could not create skeleton of function $function")
        return null
    }
    FUNCTIONS[function] = skeleton
    return skeleton
}

private fun createSkeleton(function: Item): Skeleton? {
    val s = Skeleton(function)
    s.buildMergeMap()
    s.buildDecisionMap()

    return s
}

class Skeleton(val function: Item) {

    private var mergeMap = mutableMapOf<Block, MutableList<Block>>()//Key= Block itself, value= merges of other blocks into
    private var decisionMap = mutableMapOf<Block, Pair<Block, Block>>()//Key= Decision itself, value= merge with another line (end of the decision)
    var skeletons = mutableListOf<SubSkeleton>()

    fun buildMergeMap() {
        for (block in function.blocks) {
            block
                    .blockconnections
                    .values
                    .stream()
                    .map { it.keys }
                    .flatMap { it.stream() }
                    .map { getBlock(it) }//Stream of destination blocks
                    .forEach { destinationBlock ->
                        if (destinationBlock == null) {
                            return@forEach
                        }
                        mergeMap.getOrPut(destinationBlock) { mutableListOf() }.add(block)
                    }
        }
    }

    fun Block.hasMultipleInputs() = mergeMap[this]?.size ?: 0 > 1

    private var currentBlock = function.startBlock
        set(value) {
            visitedBlocks.add(field)
            field = value
        }
    private var visitedBlocks = mutableSetOf<Block>()
    fun Block.alreadyVisited() = visitedBlocks.contains(this)

    /**
     * Runs both arms until the meet a block with two ore more inputs, if both arms met at the same input, this is a if
     */
    fun isIf(decisionBlock: Block): Boolean {
        val oldCurrentBlock = currentBlock
        val oldVisitedBlocks = visitedBlocks.toMutableSet()
        currentBlock = decisionBlock
        val trueBlock = getTrueBlock(currentBlock) ?: return false
        val falseBlock = getFalseBlock(currentBlock) ?: return false
        var mergeBlock: Block? = null
        if (trueBlock.alreadyVisited() || falseBlock.alreadyVisited()) {
            currentBlock = oldCurrentBlock
            visitedBlocks = oldVisitedBlocks
            return false
        }
        currentBlock = trueBlock
        val trueSkeletons = mutableListOf<SubSkeleton>()
        while (true) {
            if (currentBlock.isDecisionBlock()) {
                trueSkeletons.add(resolveDecisionBlock(currentBlock))
            }
            if (currentBlock.alreadyVisited()) {
                currentBlock = oldCurrentBlock
                visitedBlocks = oldVisitedBlocks
                return false
            }
            if (currentBlock.hasMultipleInputs()) {
                mergeBlock = currentBlock
                break
            }
            if (currentBlock.hasNextBlock()) {
                currentBlock = currentBlock.getNextBlock() ?: return false
            } else {
                break
            }
        }
        currentBlock = falseBlock
        while (true) {
            if (currentBlock.isDecisionBlock()) {
                resolveDecisionBlock(currentBlock)
            }
            if (currentBlock.alreadyVisited() && currentBlock != mergeBlock) {
                currentBlock = oldCurrentBlock
                visitedBlocks = oldVisitedBlocks
                return false
            }
            if (currentBlock == mergeBlock) {
                //val decision = Decision(decisionBlock,trueBlocks ,falseBlocks)
                return true
            }
            if (currentBlock.hasNextBlock()) {
                currentBlock = currentBlock.getNextBlock() ?: return false
            } else {
                break
            }
        }
        currentBlock = oldCurrentBlock
        visitedBlocks = oldVisitedBlocks
        return false
    }

    fun isWhile(decisionBlock: Block): Boolean {
        val oldCurrentBlock = currentBlock
        val oldVisitedBlocks = visitedBlocks.toMutableSet()
        currentBlock = decisionBlock
        val trueBlock = getTrueBlock(currentBlock) ?: return false
        val falseBlock = getFalseBlock(currentBlock) ?: return false
        var numberOfVisitationsOfKnownBlocks = 0
        currentBlock = trueBlock
        while (true) {
            if (currentBlock.isDecisionBlock()) {
                resolveDecisionBlock(currentBlock)
            }
            if (currentBlock.alreadyVisited()) {
                numberOfVisitationsOfKnownBlocks++
                break
            }
            if (currentBlock.hasNextBlock()) {
                currentBlock = currentBlock.getNextBlock() ?: return false
            } else {
                break
            }
        }
        currentBlock = falseBlock
        while (true) {
            if (currentBlock.isDecisionBlock()) {
                resolveDecisionBlock(currentBlock)
            }
            if (currentBlock.alreadyVisited()) {
                numberOfVisitationsOfKnownBlocks++
                break
            }
            if (currentBlock.hasNextBlock()) {
                currentBlock = currentBlock.getNextBlock() ?: return false
            } else {
                break
            }
        }
        if (numberOfVisitationsOfKnownBlocks == 1) {
            println("while from ")
            return true
        }
        currentBlock = oldCurrentBlock
        visitedBlocks = oldVisitedBlocks
        return false
    }

    fun resolveDecisionBlock(decisionBlock: Block):SubSkeleton {
        if (isIf(decisionBlock)) {

        } else if (isWhile(decisionBlock)) {

        }
        //TODO isWhile, adding the skeletons
        return SingleBlock(decisionBlock)
    }

//    fun resolveNormalBlock(normalBlock: Block) {
//        val normal = SingleBlock(normalBlock)
//        //TODO Adding normal to the skeletonslist
//    }

    fun buildDecisionMap(): Result<Nothing> {
        while (currentBlock.hasNextBlock()) {
            currentBlock = currentBlock.getNextBlock() ?: return Error()//Add visited blocks TODO
            if (currentBlock.isDecisionBlock()) {
                resolveDecisionBlock(currentBlock)
                continue
            }
        }
        return Success()
    }

}

private fun getTrueBlock(block: Block): Block? {
    return getDecisionConditionBlock(block, Block.ConnectionSide.RIGHT)
}

private fun getFalseBlock(block: Block): Block? {
    return getDecisionConditionBlock(block, Block.ConnectionSide.LEFT)
}

private fun getDecisionConditionBlock(block: Block, side: Block.ConnectionSide): Block? {
    if (block.blockconnections.isEmpty()) {
        System.err.println("Could not get the connections for the block $block, tried to get the $side connection of the decision $block")
        return null
    }
    val destination = block.blockconnections[side]
    if (destination == null || destination.isEmpty()) {
        System.err.println("No destinations of this block connection, tried to get the $side connection of the decision $block")
        return null
    }
    val next = destination.keys.iterator().next()
    return getBlock(next)
}

fun Block.isDecisionBlock() = this.blocktype == Block.BlockType.DECISION

fun Block.hasNextBlock() = this.blockconnections.isNotEmpty() && this.blockconnections.values.iterator().next().isNotEmpty()

/**
 * Warning, can run into a loop when this is a while, check beforehand if this block is a decision block with a loop!
 */
fun Block.getNextBlock(): Block? {
    if (this.blockconnections.isEmpty()) {
        System.err.println("Could not get the connections for the block $this")
        return null
    }
    for (value in this.blockconnections.values) {
        for (destination in value) {
            return getBlock(destination.key)
        }
    }
    System.err.println("The destinations of this block connections are null, can't get next block $this")
    return null
}


interface SubSkeleton

/**
 * A Single block with one output and one or more inputs
 */
data class SingleBlock(val block: Block) : SubSkeleton

/**
 * A Decision: When the condition matches, the trueSkeletons are exited, the falseSkeletons otherwise.
 * Then the Decision exits.
 */
data class Decision(val condition: Block,
                    val trueSkeletons: List<SubSkeleton> = listOf(),
                    val falseSkeletons: List<SubSkeleton> = listOf()) : SubSkeleton

/**
 * A Loop: The blocks beforeCondition are executed first, then, when the condition matches, afterCondition blocks are
 * executed, then the beforeCondition block, then the condition is checked again, when the condition is false, the
 * Loop exits
 */
data class Loop(val beforeCondition: List<SubSkeleton> = listOf(),
                val condition: Block,
                val afterCondition: List<SubSkeleton> = listOf()) : SubSkeleton