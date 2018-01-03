package sharknoon.dualide.ui.flowchart.blocks.block;

import java.util.function.Supplier;
import javafx.geometry.Side;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;
import sharknoon.dualide.ui.flowchart.Flowchart;
import sharknoon.dualide.ui.flowchart.blocks.Block;

/**
 *
 * @author Josua Frank
 */
public class Decision extends Block {

    private static final double DECISION_HIGHT = 100;
    private static final double DECISION_WIDTH = 200;
    private static final Side[] SIDES = new Side[]{Side.TOP, Side.BOTTOM, Side.RIGHT};

    public Decision(Flowchart flowchart) {
        super(flowchart, createDecisionShapeSupplier(), SIDES);
    }

    private static Supplier<Shape> createDecisionShapeSupplier() {
        return () -> {
            Polygon polygon = new Polygon(
                    DECISION_WIDTH / 2, 0,//oben mitte
                    DECISION_WIDTH, DECISION_HIGHT / 2,
                    DECISION_WIDTH / 2, DECISION_HIGHT,
                    0, DECISION_HIGHT / 2);
            polygon.setFill(Color.rgb(0, 0, 0, 0.5));
            return polygon;
        };
    }

}
