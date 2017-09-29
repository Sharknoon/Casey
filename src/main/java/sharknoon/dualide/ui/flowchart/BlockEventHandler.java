package sharknoon.dualide.ui.flowchart;

import java.util.function.Consumer;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author Josua Frank
 */
public final class BlockEventHandler {

    private final Consumer EMPTY = e -> {
    };

    public final Consumer<MouseEvent> onMousePressed;
    public final Consumer<MouseEvent> onMouseReleased;
    public final Consumer<MouseEvent> onMouseDragged;
    public final Consumer<MouseEvent> onMouseEntered;
    public final Consumer<MouseEvent> onMouseExited;

    public BlockEventHandler(Consumer<MouseEvent> onMousePressed,
            Consumer<MouseEvent> onMouseReleased,
            Consumer<MouseEvent> onMouseDragged,
            Consumer<MouseEvent> onMouseEntered,
            Consumer<MouseEvent> onMouseExited) {
        this.onMousePressed = onMousePressed;
        this.onMouseReleased = onMouseReleased;
        this.onMouseDragged = onMouseDragged;
        this.onMouseEntered = onMouseEntered;
        this.onMouseExited = onMouseExited;
    }

    public BlockEventHandler(Consumer<MouseEvent> onMousePressed, Consumer<MouseEvent> onMouseReleased, Consumer<MouseEvent> onMouseDragged) {
        this.onMousePressed = onMousePressed;
        this.onMouseReleased = onMouseReleased;
        this.onMouseDragged = onMouseDragged;
        this.onMouseEntered = EMPTY;
        this.onMouseExited = EMPTY;
    }

    public BlockEventHandler(Consumer<MouseEvent> onMouseDragged) {
        this.onMousePressed = EMPTY;
        this.onMouseReleased = EMPTY;
        this.onMouseDragged = onMouseDragged;
        this.onMouseEntered = EMPTY;
        this.onMouseExited = EMPTY;
    }

}
