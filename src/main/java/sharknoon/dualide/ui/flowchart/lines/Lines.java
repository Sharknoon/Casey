package sharknoon.dualide.ui.flowchart.lines;

import java.util.HashMap;
import java.util.Map;
import sharknoon.dualide.ui.flowchart.Flowchart;
import sharknoon.dualide.ui.flowchart.Line;
import sharknoon.dualide.ui.flowchart.blocks.Block;


/**
 *
 * @author Josua Frank
 */
public class Lines {
    
    public static Line createLine(Flowchart flowchart, Block block){
        return new Line(block);
    }
    
    private static final Map<Flowchart, Line> LINES = new HashMap<>();
    
    public static void setCurrentLine(Flowchart flowchart, Line line){
        LINES.put(flowchart, line);
    }
    
    public static Line getCurrentLine(Flowchart flowchart){
        return LINES.get(flowchart);
    }
    
}
