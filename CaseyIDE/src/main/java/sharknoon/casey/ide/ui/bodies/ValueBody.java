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
package sharknoon.casey.ide.ui.bodies;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanExpression;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import sharknoon.casey.ide.logic.items.Class.ObjectType;
import sharknoon.casey.ide.logic.statements.values.ObjectValue;
import sharknoon.casey.ide.logic.statements.values.PrimitiveValue;
import sharknoon.casey.ide.logic.statements.values.PrimitiveValue.BooleanValue;
import sharknoon.casey.ide.logic.statements.values.PrimitiveValue.NumberValue;
import sharknoon.casey.ide.logic.statements.values.PrimitiveValue.TextValue;
import sharknoon.casey.ide.logic.statements.values.Value;
import sharknoon.casey.ide.logic.types.PrimitiveType;
import sharknoon.casey.ide.logic.types.Type;

/**
 * @author Josua Frank
 */
public class ValueBody extends Body<Value> {
    
    private static final Insets MARGIN = new Insets(10);
    
    private static Node createContentNode(Value value) {
        Type returnType = value.getReturnType();
        if (returnType == PrimitiveType.BOOLEAN) {
            BooleanValue val = (BooleanValue) value;
            CheckBox checkBoxValue = new CheckBox();
            StackPane.setMargin(checkBoxValue, MARGIN);
            checkBoxValue.setPadding(new Insets(5, 0, 5, 5));//Don't ask me why, it works! (makes a non-text checkbox a sqare pane
            checkBoxValue.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            checkBoxValue.selectedProperty().bindBidirectional(val.valueProperty());
            Platform.runLater(checkBoxValue::requestFocus);
            return checkBoxValue;
        } else if (returnType == PrimitiveType.NUMBER) {
            NumberValue val2 = (NumberValue) value;
            Spinner<Double> spinnerValue = new Spinner<>(-Double.MAX_VALUE, Double.MAX_VALUE, Double.NaN);
            StackPane.setMargin(spinnerValue, MARGIN);
            spinnerValue.getValueFactory().setConverter(new StringConverter<>() {
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
    
                        value = value.replaceAll("[^0-9.-]", "");
    
                        // Perform the requested parsing
                        return Double.parseDouble(value);
                    } catch (NumberFormatException ex) {
                        return 0.0;
                    }
                }
            });
            spinnerValue.getEditor().setPrefWidth(14);
            spinnerValue.setMinWidth(24);
            spinnerValue.setEditable(true);
            spinnerValue.getValueFactory().valueProperty().bindBidirectional(val2.valueProperty());
            BodyUtils.bindWidthToText(spinnerValue.getEditor(), 24);
            Platform.runLater(spinnerValue::requestFocus);
            return spinnerValue;
        } else if (returnType == PrimitiveType.TEXT) {
            TextValue val3 = (TextValue) value;
            TextField textFieldValue = new TextField();
            StackPane.setMargin(textFieldValue, MARGIN);
            textFieldValue.textProperty().bindBidirectional(val3.valueProperty());
            BodyUtils.bindWidthToText(textFieldValue, 0);
            Platform.runLater(textFieldValue::requestFocus);
            return textFieldValue;
        } else if (returnType instanceof ObjectType) {
            ObjectValue val4 = (ObjectValue) value;
            Text textType = new Text();
            StackPane.setMargin(textType, MARGIN);
            textType.textProperty().bind(val4.getReturnType().simpleNameProperty());
            textType.setFill(Color.BLACK);
            return textType;
        }
        Text errorText = new Text("Error");
        errorText.setFill(Color.RED);
        return errorText;
    }
    
    ValueBody(Value value) {
        super(value);
        Node content = createContentNode(value);
        setContent(content);
    }
    
    @Override
    public BooleanExpression isClosingAllowed() {
        return Bindings.createBooleanBinding(() -> true);
    }
    
    @Override
    public BooleanExpression isExtendingAllowed() {
        return Bindings.createBooleanBinding(() -> false);
    }
    
    @Override
    public BooleanExpression isReducingAllowed() {
        return Bindings.createBooleanBinding(() -> false);
    }
    
    @Override
    public ObservableList<Text> toText() {
        ObservableList<Text> text = FXCollections.observableArrayList();
        Text par = new Text();
        Value value = getStatement();
        if (value.getReturnType().isPrimitive()) {
            PrimitiveValue pv = (PrimitiveValue) value;
            par.textProperty().bind(pv.valueProperty().asString());
        } else if (value.getReturnType().isObject()) {
            ObjectValue ov = (ObjectValue) value;
            par.textProperty().bind(ov.returnTypeProperty().asString());
        } else {
            par.setText("ERROR");
        }
        par.setFill(Color.LIGHTBLUE);
        text.add(par);
        return text;
    }
}
