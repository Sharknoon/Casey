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

import com.sun.javafx.scene.control.skin.SpinnerSkin;
import javafx.application.Platform;
import javafx.beans.binding.DoubleBinding;
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
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
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
        Shape shape = createOuterShape(value.getValueType());
        Control content = createContentNode(value);
        getChildren().addAll(shape, content);
    }

    private Control createContentNode(Value value) {
        switch (value.getValueType()) {
            case BOOLEAN:
                BooleanValue val = (BooleanValue) value;
                CheckBox checkBoxValue = new CheckBox();
                int margin = 26;
                StackPane.setMargin(checkBoxValue, new Insets(0, margin, 0, margin));
                widthProperty.bind(checkBoxValue.prefWidthProperty().add(margin * 2));
                checkBoxValue.setPadding(new Insets(0, 0, 10, 5));//Don't ask me why, it works! (makes a non-text checkbox a sqare pane
                checkBoxValue.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                checkBoxValue.selectedProperty().bindBidirectional(val.valueProperty());
                return checkBoxValue;
            case NUMBER:
                NumberValue val2 = (NumberValue) value;
                Spinner<Double> spinnerValue = new Spinner<>(Double.MIN_VALUE, Double.MAX_VALUE, Double.MIN_VALUE);
                int margin2 = 15;
                StackPane.setMargin(spinnerValue, new Insets(0, margin2, 0, margin2));
                //Can be removed on JavaFX 9 TODO
                //http://hg.openjdk.java.net/openjfx/9-dev/rt/rev/4cc3cc9bc47d
                //https://bugs.java.com/bugdatabase/view_bug.do?bug_id=JDK-8140507
                spinnerValue.setSkin(new SpinnerSkin(spinnerValue) {
                    @Override
                    protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
                        return spinnerValue.getEditor().minWidth(height);
                    }
                });
                DoubleBinding spinnerWidth = minTextFieldWidthProperty(spinnerValue.getEditor()).add(23);
                widthProperty.addListener((observable, oldValue, newValue) -> {//Spinner does not resize automatically like textfield :(
                    spinnerValue.setPrefWidth(newValue.doubleValue());
                });
                widthProperty.bind(spinnerWidth.add(margin2 * 2));
                spinnerValue.setEditable(true);
                spinnerValue.getValueFactory().valueProperty().bindBidirectional(val2.valueProperty());
                return spinnerValue;
            case OBJECT:
                break;//TODO
            case TEXT:
                TextValue val4 = (TextValue) value;
                TextField textFieldValue = new TextField();
                int margin4 = 10;
                StackPane.setMargin(textFieldValue, new Insets(0, margin4, 0, margin4));
                widthProperty.bind(minTextFieldWidthProperty(textFieldValue).add(margin4 * 2));
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
        tf.setText(tf.getText());
        return minTextFieldWidthProperty.getReadOnlyProperty();
    }

}
