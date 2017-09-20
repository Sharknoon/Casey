package sharknoon.dualide.ui.blocks;

import java.util.Collection;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author Josua Frank
 */
public class Blocks {

    public static Block createStartBlock(Consumer<MouseEvent> onMouseDragged,
            Consumer<Boolean> mouseOverShape) {
        return new Start(onMouseDragged, mouseOverShape);
    }

    public static Block createEndBlock(Consumer<MouseEvent> onMouseDragged,
            Consumer<Boolean> mouseOverShape) {
        return new End(onMouseDragged, mouseOverShape);
    }

    public static Block createDecisionBlock(Consumer<MouseEvent> onMouseDragged,
            Consumer<Boolean> mouseOverShape) {
        return new Decision(onMouseDragged, mouseOverShape);
    }

    public static Block createProcessBlock(Consumer<MouseEvent> onMouseDragged,
            Consumer<Boolean> mouseOverShape) {
        return new Process(onMouseDragged, mouseOverShape);
    }

    private static final HashMap<Node, Block> SHAPE_TO_BLOCK = new HashMap<>();

    static void registerBlock(Block block) {
        SHAPE_TO_BLOCK.put(block.getShape(), block);
    }
    
    static void unregisterBlock(Block block){
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

    public static BlockGroup getSelectedBlocks() {
        return new BlockGroup(SHAPE_TO_BLOCK
                .values()
                .stream()
                .filter(Block::isSelected)
                .collect(Collectors.toList()));
    }

}
