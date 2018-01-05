package sharknoon.dualide.ui.flowchart;

import java.util.List;
import java.util.stream.Collectors;
import javafx.scene.input.KeyEvent;
import sharknoon.dualide.ui.flowchart.blocks.Block;
import sharknoon.dualide.ui.flowchart.blocks.Blocks;
import sharknoon.dualide.ui.flowchart.blocks.block.Start;
import sharknoon.dualide.ui.flowchart.lines.Line;
import sharknoon.dualide.ui.flowchart.lines.Lines;

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
            case ESCAPE:
                onESCAPE();
                break;
        }
    }

    private void onDELETE() {
        List<Block> blocksToDelete = Blocks
                .getAllBlocks(flowchart)
                .stream()
                .filter(Block::isSelected)
                .filter(b -> b.getClass() != Start.class)
                .collect(Collectors.toList());
        blocksToDelete.forEach(Block::remove);//Maybe a concurrentmodificationexception
        List<Line> linesToDelete = Lines.getAllLines(flowchart)
                .stream()
                .filter(Line::isSelected)
                .collect(Collectors.toList());
        linesToDelete.forEach(Line::remove);

    }

    private void onESCAPE() {
        if (Lines.isLineDrawing()) {
            Lines.getDrawingLine().remove();
        }
    }
}
