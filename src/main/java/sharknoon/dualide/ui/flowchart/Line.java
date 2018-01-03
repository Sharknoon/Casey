package sharknoon.dualide.ui.flowchart;

import javafx.scene.layout.Pane;
import sharknoon.dualide.ui.flowchart.blocks.Block;

/**
 *
 * @author Josua Frank
 */
public class Line {

    private  javafx.scene.shape.Line line;
    private final Block startBlock;
    private Block endBlock;

    public Line(Block startBlock) {
        this.startBlock = startBlock;
        
    }
    
    public void addTo(Pane pane){
        //pane.getChildren().add(pane)
    }

}
