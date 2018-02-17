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
package sharknoon.dualide.ui.bodies;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.util.StringConverter;
import sharknoon.dualide.logic.statements.values.BooleanValue;
import sharknoon.dualide.logic.statements.values.NumberValue;
import sharknoon.dualide.logic.statements.values.TextValue;
import sharknoon.dualide.logic.statements.values.Value;

/**
 *
 * @author Josua Frank
 */
public class ValueBody extends Body<Value> {

    public static ValueBody createValueBody(Value value) {
        return new ValueBody(value);
    }

    public ValueBody(Value value) {
        super(value);
        Control content = createContentNode(value);
        setContent(content);
    }

    private static Control createContentNode(Value value) {
        switch (value.getValueType()) {
            case BOOLEAN:
                BooleanValue val = (BooleanValue) value;
                CheckBox checkBoxValue = new CheckBox();
                int margin = 10;
                StackPane.setMargin(checkBoxValue, new Insets(margin));
                checkBoxValue.setPadding(new Insets(5, 0, 5, 5));//Don't ask me why, it works! (makes a non-text checkbox a sqare pane
                checkBoxValue.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                checkBoxValue.selectedProperty().bindBidirectional(val.valueProperty());
                return checkBoxValue;
            case NUMBER:
                NumberValue val2 = (NumberValue) value;
                Spinner<Double> spinnerValue = new Spinner<>(-Double.MAX_VALUE, Double.MAX_VALUE, Double.NaN);
                int margin2 = 10;
                StackPane.setMargin(spinnerValue, new Insets(margin2));
                spinnerValue.getValueFactory().setConverter(new StringConverter<Double>() {
                    @Override
                    public String toString(Double value) {
                        // If the specified value is null, return a zero-length String
                        if (value == null) {
                            return "";
                        }

                        return value.toString();
                    }

                    @Override
                    public Double fromString(String value) {
                        try {
                            // If the specified value is null or zero-length, return null
                            if (value == null) {
                                return null;
                            }

                            value = value.trim();

                            if (value.length() < 1) {
                                return null;
                            }

                            // Perform the requested parsing
                            return Double.parseDouble(value);
                        } catch (NumberFormatException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                });
                spinnerValue.prefWidthProperty().bind(minTextFieldWidthProperty(spinnerValue.getEditor()).add(24));//Spinner does not resize automatically like textfield :(
                spinnerValue.getEditor().setMinWidth(14);
                spinnerValue.setEditable(true);
                spinnerValue.getValueFactory().valueProperty().bindBidirectional(val2.valueProperty());
                return spinnerValue;
            case OBJECT:
                break;//TODO
            case TEXT:
                TextValue val4 = (TextValue) value;
                TextField textFieldValue = new TextField();
                int margin4 = 10;
                StackPane.setMargin(textFieldValue, new Insets(margin4));
                textFieldValue.prefWidthProperty().bind(minTextFieldWidthProperty(textFieldValue));
                textFieldValue.textProperty().bindBidirectional(val4.valueProperty());
                return textFieldValue;
        }
        return new Label("Error");
    }

    private static ReadOnlyDoubleProperty minTextFieldWidthProperty(TextField tf) {
        ReadOnlyDoubleWrapper minTextFieldWidthProperty = new ReadOnlyDoubleWrapper();
        tf.textProperty().addListener((ov, prevText, currText) -> {
            // Do this in a Platform.runLater because of Textfield has no padding at first time and so on
            Platform.runLater(() -> {
                Text text = new Text(currText);
                text.setFont(tf.getFont()); // Set the same font, so the size is the same
                double width = text.getLayoutBounds().getWidth() // This big is the Text in the TextField
                        + tf.getPadding().getLeft() + tf.getPadding().getRight() // Add the padding of the TextField
                        + 2d; // Add some spacing
                minTextFieldWidthProperty.set(width); // Set the width
                tf.positionCaret(tf.getCaretPosition()); // If you remove this line, it flashes a little bit
            });
        });
        return minTextFieldWidthProperty.getReadOnlyProperty();
    }

}
