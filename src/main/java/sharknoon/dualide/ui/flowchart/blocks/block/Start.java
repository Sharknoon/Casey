package sharknoon.dualide.ui.flowchart.blocks.block;

import java.util.function.Supplier;
import javafx.geometry.Side;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import sharknoon.dualide.ui.flowchart.Flowchart;
import sharknoon.dualide.ui.flowchart.blocks.Block;

/**
 *
 * @author Josua Frank
 */
public class Start extends Block {

    private static final double START_HIGHT = 100;
    private static final double START_WIDTH = 200;
    private static final Side[] SIDES = new Side[]{Side.BOTTOM};

    public Start(Flowchart flowchart) {
        super(flowchart, createStartShapeSupplier(), SIDES);
    }

    private static Supplier<Shape> createStartShapeSupplier() {
        return () -> {
            Rectangle rectangle = new Rectangle(START_WIDTH, START_HIGHT);
            rectangle.setArcWidth(START_HIGHT < START_WIDTH ? START_HIGHT : START_WIDTH);
            rectangle.setArcHeight(START_HIGHT < START_WIDTH ? START_HIGHT : START_WIDTH);
            rectangle.setFill(Color.GREEN);
            return rectangle;
        };
    }

}
