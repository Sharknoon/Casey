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
package sharknoon.dualide.ui.sites.function.blocks;

import sharknoon.dualide.ui.sites.function.blocks.block.Start;
import sharknoon.dualide.ui.sites.function.blocks.block.Process;
import sharknoon.dualide.ui.sites.function.blocks.block.End;
import sharknoon.dualide.ui.sites.function.blocks.block.Decision;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import sharknoon.dualide.ui.sites.function.FunctionSite;
import sharknoon.dualide.ui.sites.function.UISettings;

/**
 * This is the general class for all blocks, it has handy funtions to create new
 * blocks and manages the behaviour between the blocks
 *
 * @author Josua Frank
 */
public class Blocks {

    public static Block createStartBlock(FunctionSite functionSite) {
        return new Start(functionSite);
    }

    public static Block createEndBlock(FunctionSite functionSite) {
        return new End(functionSite);
    }

    public static Block createDecisionBlock(FunctionSite functionSite) {
        return new Decision(functionSite);
    }

    public static Block createProcessBlock(FunctionSite functionSite) {
        return new Process(functionSite);
    }

    private static final Map<FunctionSite, Set<Block>> BLOCKS = new HashMap<>();
    private static final Map<FunctionSite, Block> MOVING_BLOCK = new HashMap<>();
    private static final Set<Block> EMPTY = new HashSet<>();

    static void registerBlock(FunctionSite functionSite, Block block) {
        if (BLOCKS.containsKey(functionSite)) {
            BLOCKS.get(functionSite).add(block);
        } else {
            Set<Block> list = new HashSet<>();
            list.add(block);
            BLOCKS.put(functionSite, list);
        }
    }

    static void unregisterBlock(FunctionSite functionSite, Block block) {
        if (BLOCKS.containsKey(functionSite)) {
            BLOCKS.get(functionSite).remove(block);
        }
    }

    public static void unselectAll(FunctionSite functionSite) {
        if (BLOCKS.containsKey(functionSite)) {
            BLOCKS.get(functionSite).forEach(b -> b.unselect());
        }
    }

    public static Block getMovingBlock(FunctionSite functionSite) {
        return MOVING_BLOCK.get(functionSite);
    }

    public static void setMovingBlock(FunctionSite functionSite, Block block) {
        MOVING_BLOCK.put(functionSite, block);
    }

    public static Collection<Block> getAllBlocks(FunctionSite functionSite) {
        return BLOCKS.getOrDefault(functionSite, EMPTY);
    }

    public static Collection<Block> getSelectedBlocks(FunctionSite functionSite) {
        return BLOCKS
                .getOrDefault(functionSite, EMPTY)
                .stream()
                .filter(Block::isSelected)
                .collect(Collectors.toList());
    }

    public static BlockGroup getSelectedBlocksGroup(FunctionSite functionSite) {
        return new BlockGroup(BLOCKS
                .getOrDefault(functionSite, EMPTY)
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
