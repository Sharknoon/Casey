package sharknoon.dualide.ui.flowchart.blocks;

import java.util.ArrayList;
import sharknoon.dualide.ui.flowchart.blocks.block.Start;
import sharknoon.dualide.ui.flowchart.blocks.block.Process;
import sharknoon.dualide.ui.flowchart.blocks.block.End;
import sharknoon.dualide.ui.flowchart.blocks.block.Decision;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import sharknoon.dualide.ui.flowchart.Flowchart;
import sharknoon.dualide.ui.flowchart.UISettings;

/**
 *
 * @author Josua Frank
 */
public class Blocks {

    public static Block createStartBlock(Flowchart flowchart) {
        return new Start(flowchart);
    }

    public static Block createEndBlock(Flowchart flowchart) {
        return new End(flowchart);
    }

    public static Block createDecisionBlock(Flowchart flowchart) {
        return new Decision(flowchart);
    }

    public static Block createProcessBlock(Flowchart flowchart) {
        return new Process(flowchart);
    }

    private static final Map<Flowchart, Set<Block>> BLOCKS = new HashMap<>();
    private static final Map<Flowchart, Block> CURRENT_BLOCK = new HashMap<>();
    private static final Set<Block> EMPTY = new HashSet<>();

    static void registerBlock(Flowchart flowchart, Block block) {
        if (BLOCKS.containsKey(flowchart)) {
            BLOCKS.get(flowchart).add(block);
        } else {
            Set<Block> list = new HashSet<>();
            list.add(block);
            BLOCKS.put(flowchart, list);
        }
    }

    static void unregisterBlock(Flowchart flowchart, Block block) {
        if (BLOCKS.containsKey(flowchart)) {
            BLOCKS.get(flowchart).remove(block);
        }
    }

    public static void unselectAll(Flowchart flowchart) {
        if (BLOCKS.containsKey(flowchart)) {
            BLOCKS.get(flowchart).forEach(b -> b.unselect());
        }
    }

    public static Block getWorkingBlock(Flowchart flowchart) {
        return CURRENT_BLOCK.get(flowchart);
    }

    public static void setWorkingBlock(Flowchart flowchart, Block block) {
        CURRENT_BLOCK.put(flowchart, block);
    }

    public static Collection<Block> getAllBlocks(Flowchart flowchart) {
        return BLOCKS.getOrDefault(flowchart, EMPTY);
    }

    public static Collection<Block> getSelectedBlocks(Flowchart flowchart) {
        return BLOCKS
                .getOrDefault(flowchart, EMPTY)
                .stream()
                .filter(Block::isSelected)
                .collect(Collectors.toList());
    }

    public static BlockGroup getSelectedBlocksGroup(Flowchart flowchart) {
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
