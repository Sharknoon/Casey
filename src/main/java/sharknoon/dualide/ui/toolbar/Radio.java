package sharknoon.dualide.ui.toolbar;

import javafx.scene.control.ToolBar;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;

/**
 *
 * @author Josua Frank
 */
public class Radio {

    public static void init(ToolBar toolbar) {
        StackPane root = new StackPane();
        Circle circle = new Circle(13, Color.WHITE);
        Polygon play = new Polygon(10,2,
        16,11,10,24);
        play.setFill(Color.BLACK);
        root.getChildren().addAll(circle, play);
        toolbar.getItems().add(root);
    }

}
