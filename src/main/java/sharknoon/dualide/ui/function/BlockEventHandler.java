/*
 * Copyright 2018 Shark Industries.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package sharknoon.dualide.ui.function;

import java.util.function.Consumer;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author Josua Frank
 */
public final class BlockEventHandler {

    private final Consumer<MouseEvent> EMPTY = e -> {
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
