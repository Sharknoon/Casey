package sharknoon.dualide.ui.blocks;

import java.util.Collection;
import java.util.HashMap;
import java.util.stream.Collectors;
import javafx.scene.Node;
import sharknoon.dualide.ui.BlockEventHandler;

/**
 *
 * @author Josua Frank
 */
public class Blocks {

    public static Block createStartBlock(BlockEventHandler handler) {
        return new Start(handler);
    }

    public static Block createEndBlock(BlockEventHandler handler) {
        return new End(handler);
    }

    public static Block createDecisionBlock(BlockEventHandler handler) {
        return new Decision(handler);
    }

    public static Block createProcessBlock(BlockEventHandler handler) {
        return new Process(handler);
    }

    private static final HashMap<Node, Block> SHAPE_TO_BLOCK = new HashMap<>();

    static void registerBlock(Block block) {
        SHAPE_TO_BLOCK.put(block.getShape(), block);
    }

    static void unregisterBlock(Block block) {
        SHAPE_TO_BLOCK.remove(block.getShape());
    }

    public static void unselectAll() {
        SHAPE_TO_BLOCK.values().forEach(b -> b.unselect());
    }

    public static Block getBlock(Node node) {
        return SHAPE_TO_BLOCK.get(node);
    }

    public static Collection<Block> getAllBlocks() {
        return SHAPE_TO_BLOCK.values();
    }

    public static Collection<Block> getSelectedBlocks() {
        return SHAPE_TO_BLOCK
                .values()
                .stream()
                .filter(Block::isSelected)
                .collect(Collectors.toList());
    }

    public static BlockGroup getSelectedBlocksGroup() {
        return new BlockGroup(SHAPE_TO_BLOCK
                .values()
                .stream()
                .filter(Block::isSelected)
                .collect(Collectors.toList()));
    }

}
