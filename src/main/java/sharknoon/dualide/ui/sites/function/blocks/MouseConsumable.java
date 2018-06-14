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
package sharknoon.dualide.ui.sites.function.blocks;

import javafx.scene.Node;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

/**
 * This interface is used to mark a type as eventconsumable
 *
 * @author Josua Frank
 */
public interface MouseConsumable {

    public abstract void onMousePressed(MouseEvent event);

    public abstract void onMouseDragged(MouseEvent event);

    public abstract void onMouseReleased(MouseEvent event);

    public abstract void onMouseClicked(MouseEvent event);

    public abstract void onContextMenuRequested(ContextMenuEvent event);

    public abstract void onMouseEntered(MouseEvent event);

    public abstract void onMouseExited(MouseEvent event);

    public abstract void onMouseMoved(MouseEvent event);

    public abstract void onScroll(ScrollEvent event);

    public static void registerListeners(Node node, MouseConsumable consumable) {
        node.setOnMousePressed(consumable::onMousePressed);
        node.setOnMouseDragged(consumable::onMouseDragged);
        node.setOnMouseReleased(consumable::onMouseReleased);
        node.setOnMouseClicked(consumable::onMouseClicked);
        node.setOnContextMenuRequested(consumable::onContextMenuRequested);
        node.setOnMouseEntered(consumable::onMouseEntered);
        node.setOnMouseExited(consumable::onMouseExited);
        node.setOnMouseMoved(consumable::onMouseMoved);
        node.setOnScroll(consumable::onScroll);
    }
}
