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
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.StringConverter;
import sharknoon.dualide.logic.values.Value;
import sharknoon.dualide.logic.items.Class.ObjectType;
import sharknoon.dualide.logic.types.PrimitiveType;
import sharknoon.dualide.logic.types.Type;
import sharknoon.dualide.logic.values.PrimitiveValue.BooleanValue;
import sharknoon.dualide.logic.values.PrimitiveValue.NumberValue;
import sharknoon.dualide.logic.values.PrimitiveValue.TextValue;
import sharknoon.dualide.logic.values.ObjectValue;

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
        Node content = createContentNode(value);
        setContent(content);
    }

    private static Node createContentNode(Value value) {
        Type returnType = value.getReturnType();
        if (returnType == PrimitiveType.BOOLEAN) {
            BooleanValue val = (BooleanValue) value;
            CheckBox checkBoxValue = new CheckBox();
            int margin = 10;
            StackPane.setMargin(checkBoxValue, new Insets(margin));
            checkBoxValue.setPadding(new Insets(5, 0, 5, 5));//Don't ask me why, it works! (makes a non-text checkbox a sqare pane
            checkBoxValue.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            checkBoxValue.selectedProperty().bindBidirectional(val.valueProperty());
            return checkBoxValue;
        } else if (returnType == PrimitiveType.NUMBER) {
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
        } else if (returnType == PrimitiveType.TEXT) {
            TextValue val4 = (TextValue) value;
            TextField textFieldValue = new TextField();
            int margin4 = 10;
            StackPane.setMargin(textFieldValue, new Insets(margin4));
            textFieldValue.prefWidthProperty().bind(minTextFieldWidthProperty(textFieldValue));
            textFieldValue.textProperty().bindBidirectional(val4.valueProperty());
            return textFieldValue;
        } else if (returnType instanceof ObjectType) {
            ObjectValue<?> val3 = (ObjectValue) value;
            Text textType = new Text();
            textType.textProperty().bind(val3.getReturnType().getSimpleName());
            textType.setFill(Color.BLACK);
            return textType;
        }
        Text errorText = new Text("Error");
        errorText.setFill(Color.RED);
        return errorText;
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
