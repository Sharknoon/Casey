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
import sharknoon.dualide.logic.statements.values.Value;
import sharknoon.dualide.logic.items.Class.ObjectType;
import sharknoon.dualide.logic.types.PrimitiveType;
import sharknoon.dualide.logic.types.Type;
import sharknoon.dualide.logic.statements.values.PrimitiveValue.BooleanValue;
import sharknoon.dualide.logic.statements.values.PrimitiveValue.NumberValue;
import sharknoon.dualide.logic.statements.values.PrimitiveValue.TextValue;
import sharknoon.dualide.logic.statements.values.ObjectValue;

/**
 *
 * @author Josua Frank
 */
public class ValueBody extends Body<Value> {

    public static ValueBody createValueBody(Value value) {
        return new ValueBody(value);
    }

    private static final Insets MARGIN = new Insets(10);

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
            StackPane.setMargin(checkBoxValue, MARGIN);
            checkBoxValue.setPadding(new Insets(5, 0, 5, 5));//Don't ask me why, it works! (makes a non-text checkbox a sqare pane
            checkBoxValue.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            checkBoxValue.selectedProperty().bindBidirectional(val.valueProperty());
            return checkBoxValue;
        } else if (returnType == PrimitiveType.NUMBER) {
            NumberValue val2 = (NumberValue) value;
            Spinner<Double> spinnerValue = new Spinner<>(-Double.MAX_VALUE, Double.MAX_VALUE, Double.NaN);
            StackPane.setMargin(spinnerValue, MARGIN);
            spinnerValue.getValueFactory().setConverter(new StringConverter<Double>() {
                @Override
                public String toString(Double value) {
                    // If the specified value is null, return a zero-length String
                    if (value == null) {
                        return "";
                    }
                    String stringValue = value.toString();
                    if (stringValue.endsWith(".0")) {
                        stringValue = stringValue.substring(0, stringValue.length() - 2);
                    }
                    return stringValue;
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
                            return 0.0;
                        }

                        value = value.replaceAll("[^0-9\\.-]", "");
                        
                        // Perform the requested parsing
                        return Double.parseDouble(value);
                    } catch (NumberFormatException ex) {
                        return 0.0;
                    }
                }
            });
            spinnerValue.getEditor().setPrefWidth(14);
            spinnerValue.minWidthProperty().bind(minTextFieldWidthProperty(spinnerValue.getEditor()).add(24));//Spinner does not resize automatically like textfield :(
            spinnerValue.setEditable(true);
            spinnerValue.getValueFactory().valueProperty().bindBidirectional(val2.valueProperty());
            return spinnerValue;
        } else if (returnType == PrimitiveType.TEXT) {
            TextValue val3 = (TextValue) value;
            TextField textFieldValue = new TextField();
            StackPane.setMargin(textFieldValue, MARGIN);
            textFieldValue.prefWidthProperty().bind(minTextFieldWidthProperty(textFieldValue));
            textFieldValue.textProperty().bindBidirectional(val3.valueProperty());
            return textFieldValue;
        } else if (returnType instanceof ObjectType) {
            ObjectValue<?> val4 = (ObjectValue) value;
            Text textType = new Text();
            StackPane.setMargin(textType, MARGIN);
            textType.textProperty().bind(val4.getReturnType().getSimpleName());
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
