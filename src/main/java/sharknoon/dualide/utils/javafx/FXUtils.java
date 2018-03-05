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
package sharknoon.dualide.utils.javafx;

import java.util.function.Function;
import javafx.beans.binding.Bindings;
import javafx.beans.property.StringProperty;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.Tooltip;

/**
 *
 * @author Josua Frank
 */
public class FXUtils {

    public static <T> void fixComboBoxText(ComboBox<T> comboBox, Function<T,StringProperty> listText, Function<T,StringProperty> tooltipText) {
        comboBox.setCellFactory((param) -> {
            return new ListCell<T>() {
                @Override
                protected void updateItem(T item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item != null) {
                        textProperty().bind(listText.apply(item));
                        Tooltip tooltip = new Tooltip();
                        tooltip.textProperty().bind(tooltipText.apply(item));
                        tooltipProperty().bind(Bindings.createObjectBinding(() -> tooltip, tooltip.textProperty()));
                    }
                }
            };
        });
        comboBox.setButtonCell(new ListCell<T>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    textProperty().bind(listText.apply(item));
                    Tooltip tooltip = new Tooltip();
                    tooltip.textProperty().bind(tooltipText.apply(item));
                    tooltipProperty().bind(Bindings.createObjectBinding(() -> tooltip, tooltip.textProperty()));
                }
            }

        });
    }

}
