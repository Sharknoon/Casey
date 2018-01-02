package sharknoon.dualide.ui.flowchart;

import java.util.List;
import java.util.stream.Collectors;
import javafx.scene.input.KeyEvent;
import sharknoon.dualide.ui.flowchart.blocks.Block;
import sharknoon.dualide.ui.flowchart.blocks.Blocks;
import sharknoon.dualide.ui.flowchart.blocks.block.Start;

/**
 *
 * @author Josua Frank
 */
public class Keyboard {

    private final Flowchart flowchart;

    public Keyboard(Flowchart flowchart) {
        this.flowchart = flowchart;
    }

    public void init() {
    }

    public void onKeyReleased(KeyEvent event) {
        switch (event.getCode()) {
            case DELETE:
                onDELETE();
                break;
        }
    }

    private void onDELETE() {
        List<Block> selectedBlocks = Blocks
                .getAllBlocks(flowchart)
                .stream()
                .filter(Block::isSelected)
                .filter(b -> b.getClass() != Start.class)
                .collect(Collectors.toList());
        selectedBlocks
                .forEach(Block::remove);
    }
}
