package sharknoon.dualide.ui.flowchart.blocks.block;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javafx.geometry.Side;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import sharknoon.dualide.ui.flowchart.BlockEventHandler;
import sharknoon.dualide.ui.flowchart.Flowchart;
import sharknoon.dualide.ui.flowchart.blocks.Block;

/**
 *
 * @author Josua Frank
 */
public class Process extends Block {

    private static final double PROCESS_HIGHT = 100;
    private static final double PROCESS_WIDTH = 200;
    private static final Side[] SIDES = new Side[]{Side.TOP, Side.BOTTOM};

    public Process(Flowchart flowchart) {
        super(flowchart, createProcessShapeSupplier(), SIDES);
    }

    private static Supplier<Shape> createProcessShapeSupplier() {
        return () -> {
            Rectangle rectangle = new Rectangle(PROCESS_WIDTH, PROCESS_HIGHT);
            rectangle.setFill(Color.WHITE);
            return rectangle;
        };
    }

}
