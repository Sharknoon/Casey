package sharknoon.dualide.ui.flowchart.blocks;

import java.util.ArrayList;
import sharknoon.dualide.ui.flowchart.blocks.block.Start;
import sharknoon.dualide.ui.flowchart.blocks.block.Process;
import sharknoon.dualide.ui.flowchart.blocks.block.End;
import sharknoon.dualide.ui.flowchart.blocks.block.Decision;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import sharknoon.dualide.ui.flowchart.Flowchart;

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

    private static final Map<Flowchart, List<Block>> BLOCKS = new HashMap<>();
    private static final Map<Flowchart, Block> CURRENT_BLOCK = new HashMap<>();
    private static final List<Block> EMPTY = new ArrayList<>();

    static void registerBlock(Flowchart flowchart, Block block) {
        if (BLOCKS.containsKey(flowchart)) {
            BLOCKS.get(flowchart).add(block);
        } else {
            ArrayList list = new ArrayList();
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

    public static Block getCurrentBlock(Flowchart flowchart) {
        return CURRENT_BLOCK.get(flowchart);
    }
    
    public static void setCurrentBlock(Flowchart flowchart, Block block){
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

}
