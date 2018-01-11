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
package sharknoon.dualide.ui.function.blocks;

import sharknoon.dualide.ui.function.blocks.block.Start;
import sharknoon.dualide.ui.function.blocks.block.Process;
import sharknoon.dualide.ui.function.blocks.block.End;
import sharknoon.dualide.ui.function.blocks.block.Decision;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import sharknoon.dualide.ui.function.Function;
import sharknoon.dualide.ui.function.UISettings;

/**
 * This is the general class for all blocks, it has handy funtions to create new
 * blocks and manages the behaviour between the blocks
 *
 * @author Josua Frank
 */
public class Blocks {

    public static Block createStartBlock(Function flowchart) {
        return new Start(flowchart);
    }

    public static Block createEndBlock(Function flowchart) {
        return new End(flowchart);
    }

    public static Block createDecisionBlock(Function flowchart) {
        return new Decision(flowchart);
    }

    public static Block createProcessBlock(Function flowchart) {
        return new Process(flowchart);
    }

    private static final Map<Function, Set<Block>> BLOCKS = new HashMap<>();
    private static final Map<Function, Block> MOVING_BLOCK = new HashMap<>();
    private static final Set<Block> EMPTY = new HashSet<>();

    static void registerBlock(Function flowchart, Block block) {
        if (BLOCKS.containsKey(flowchart)) {
            BLOCKS.get(flowchart).add(block);
        } else {
            Set<Block> list = new HashSet<>();
            list.add(block);
            BLOCKS.put(flowchart, list);
        }
    }

    static void unregisterBlock(Function flowchart, Block block) {
        if (BLOCKS.containsKey(flowchart)) {
            BLOCKS.get(flowchart).remove(block);
        }
    }

    public static void unselectAll(Function flowchart) {
        if (BLOCKS.containsKey(flowchart)) {
            BLOCKS.get(flowchart).forEach(b -> b.unselect());
        }
    }

    public static Block getMovingBlock(Function flowchart) {
        return MOVING_BLOCK.get(flowchart);
    }

    public static void setMovingBlock(Function flowchart, Block block) {
        MOVING_BLOCK.put(flowchart, block);
    }

    public static Collection<Block> getAllBlocks(Function flowchart) {
        return BLOCKS.getOrDefault(flowchart, EMPTY);
    }

    public static Collection<Block> getSelectedBlocks(Function flowchart) {
        return BLOCKS
                .getOrDefault(flowchart, EMPTY)
                .stream()
                .filter(Block::isSelected)
                .collect(Collectors.toList());
    }

    public static BlockGroup getSelectedBlocksGroup(Function flowchart) {
        return new BlockGroup(BLOCKS
                .getOrDefault(flowchart, EMPTY)
                .stream()
                .filter(Block::isSelected)
                .collect(Collectors.toList()));
    }

    public static boolean isSpaceFree(Block block, double x, double y) {
        boolean isSpaceFree = block.canMoveTo(x, y);
        return isSpaceFree && isInsideWorkspace(block, x, y);
    }

    public static boolean isInsideWorkspace(Block b, double x, double y) {
        return !(x < 0 + UISettings.paddingInsideWorkSpace
                || y < 0 + UISettings.paddingInsideWorkSpace
                || x + b.getWidth() > UISettings.maxWorkSpaceX - UISettings.paddingInsideWorkSpace
                || y + b.getHeight() > UISettings.maxWorkSpaceY - UISettings.paddingInsideWorkSpace);
    }

    private static Block mouseOverBlock = null;

    public static void setMouseOverBlock(Block block) {
        mouseOverBlock = block;
    }

    public static boolean isMouseOverBlock() {
        return mouseOverBlock != null;
    }

    public static void removeMouseOverBlock() {
        mouseOverBlock = null;
    }

}
