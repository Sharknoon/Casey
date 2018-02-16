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

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.Skin;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.util.StringConverter;

/**
 *
 * @author Josua Frank
 */
public class NumberField extends Spinner<Double> {

    public NumberField() {
        super();
        setValueFactory(new MyDoubleConverter());
        setEditable(true);
        //Do nothing on enter pressed
        getEditor().textProperty().addListener(e -> commit());
        getEditor().focusedProperty().addListener((observable, oldValue, newValue) -> {
            Platform.runLater(() -> {
                getEditor().selectAll();
            });
        });
    }

    private void commit() {
        String text = getEditor().getText();
        if (text.matches("[0-9\\.\\+-]*")) {
            SpinnerValueFactory<Double> valueFactory = getValueFactory();
            if (valueFactory != null) {
                StringConverter<Double> converter = valueFactory.getConverter();
                if (converter != null) {
                    Double value = converter.fromString(text);
                    valueFactory.setValue(value);
                }
            }
        } else {
            getEditor().setText(text.replaceAll("[^0-9\\.\\+-]", ""));
        }

    }

    public NumberField(double initialValue) {
        this();
        getValueFactory().setValue(initialValue);
    }

    private NumberField(int min, int max, int initialValue) {
    }

    private NumberField(int min, int max, int initialValue, int amountToStepBy) {
    }

    private NumberField(double min, double max, double initialValue) {
    }

    private NumberField(double min, double max, double initialValue, double amountToStepBy) {
    }

    private NumberField(ObservableList<Double> items) {
    }

    private NumberField(SpinnerValueFactory<Double> valueFactory) {
    }

//    @Override
//    protected Skin<?> createDefaultSkin() {
//        return new FixedSpinnerSkin<>(this);
//    }

    public class MyDoubleConverter extends SpinnerValueFactory<Double> {

        public MyDoubleConverter() {
            setConverter(new StringConverter<Double>() {

                double previousValue = 0.0;

                /**
                 * Converts the specified {@link String} into its {@link Double}
                 * value. A {@code null}, empty, or otherwise invalid argument
                 * returns zero.
                 *
                 * @param s the {@link String} to convert
                 * @return the {@link Double} value of {@code s}
                 * @see #setReset
                 */
                @Override
                public Double fromString(String s) {
                    if (s == null || s.isEmpty()) {
                        previousValue = 0.0;
                    }

                    try {
                        previousValue = Double.valueOf(s);
                    } catch (NumberFormatException e) {
                    }
                    return previousValue;
                }

                /**
                 * Converts the specified {@link Double} into its {@link String}
                 * form. A {@code null} argument is converted into the literal
                 * string "0".
                 *
                 * @param value the {@link Double} to convert
                 * @return the {@link String} form of {@code value}
                 */
                @Override
                public String toString(Double value) {
                    if (value == null) {
                        return "0";
                    }
                    String string = value.toString();
                    return string.endsWith(".0") ? string.substring(0, string.length() - 2) : string;
                }
            });
        }

        @Override
        public void decrement(int steps) {
            Double value = valueProperty().get();
            if (value - ((double) steps) == Double.POSITIVE_INFINITY) {
                value = Double.MAX_VALUE;
            } else if (value - ((double) steps) == Double.NEGATIVE_INFINITY) {
                value = Double.MIN_VALUE;
            } else {
                value = value - ((double) steps);
            }
            valueProperty().set(value);
        }

        @Override
        public void increment(int steps) {
            Double value = valueProperty().get();
            if (value + ((double) steps) == Double.POSITIVE_INFINITY) {
                value = Double.MAX_VALUE;
            } else if (value + ((double) steps) == Double.NEGATIVE_INFINITY) {
                value = Double.MIN_VALUE;
            } else {
                value = value + ((double) steps);
            }
            valueProperty().set(value);
        }

    }

}
