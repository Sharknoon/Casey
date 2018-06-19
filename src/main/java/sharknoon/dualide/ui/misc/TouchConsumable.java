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
package sharknoon.dualide.ui.misc;

import javafx.scene.Node;
import javafx.scene.input.TouchEvent;

/**
 *
 * @author Josua Frank
 */
public interface TouchConsumable {

    public default void onTouchMoved(TouchEvent event) {
    }

    public default void onTouchPressed(TouchEvent event) {
    }

    public default void onTouchReleased(TouchEvent event) {
    }

    public default void onTouchStationary(TouchEvent event) {
    }

    public static void registerListeners(Node node, TouchConsumable consumable) {
        node.setOnTouchMoved(consumable::onTouchMoved);
        node.setOnTouchPressed(consumable::onTouchPressed);
        node.setOnTouchReleased(consumable::onTouchReleased);
        node.setOnTouchStationary(consumable::onTouchStationary);
    }
}