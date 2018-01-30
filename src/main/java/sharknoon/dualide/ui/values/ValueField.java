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
package sharknoon.dualide.ui.values;

import sharknoon.dualide.logic.values.ValueType;
import java.util.LinkedHashSet;
import java.util.Set;
import javafx.beans.property.SetProperty;
import javafx.beans.property.SimpleSetProperty;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

/**
 *
 * @author Josua Frank
 */
public class ValueField extends Pane {

    private final SetProperty<ValueType> allowedValues = new SimpleSetProperty<>(FXCollections.observableSet(new LinkedHashSet<>()));

    public ValueField() {
        setAllowedValues(ValueType.getAll());
        initGraphic();
    }

    public ValueField(Set<ValueType> allowedValue) {
        setAllowedValues(allowedValue);
        initGraphic();
    }

    public SetProperty<ValueType> allowedValuesProperty() {
        return allowedValues;
    }

    public Set<ValueType> getAllowedValues() {
        return allowedValuesProperty().get();
    }

    public void setAllowedValues(Set<ValueType> allowedValues) {
        allowedValuesProperty().get().clear();
        if (allowedValues.isEmpty()) {
            allowedValues = ValueType.getAll();
        }
        allowedValuesProperty().get().addAll(allowedValues);
    }

    private void initGraphic() {
        setPrefSize(200, 50);
        final Shape shape;
        if (allowedValues.size() < 2) {
            double width = getWidth();
            double height = getHeight();
            switch (allowedValuesProperty().iterator().next()) {
                case BOOLEAN:
                    Polygon poly = new Polygon();
                    poly.getPoints().addAll(
                            0.0, height / 2,
                            height / 2, 0.0,
                            width - (height / 2), 0.0,
                            width, height / 2,
                            width - (height / 2), height,
                            height / 2, height
                    );
                    shape = poly;
                    break;
                case NUMBER:
                    Rectangle rect = new Rectangle();
                    rect.arcHeightProperty().bind(rect.heightProperty());
                    rect.arcWidthProperty().bind(rect.heightProperty());
                    rect.heightProperty().bind(heightProperty());
                    rect.widthProperty().bind(widthProperty());
                    shape = rect;
                    break;
                case OBJECT:
                    Polygon poly2 = new Polygon();
                    poly2.getPoints().addAll(
                            0.0, height / 3,
                            height / 3, 0.0,
                            width - (height / 3), 0.0,
                            width, height / 3,
                            width, (height / 3) * 2,
                            width - (height / 3), height,
                            height / 3, height,
                            0.0, (height / 3) * 2
                    );
                    shape = poly2;
                    break;
                case TEXT:
                    Rectangle rect2 = new Rectangle();
                    rect2.heightProperty().addListener((observable, oldValue, newValue) -> {
                        rect2.setArcHeight(newValue.doubleValue() / 2);
                        rect2.setArcWidth(newValue.doubleValue() / 2);
                    });
                    rect2.heightProperty().bind(heightProperty());
                    rect2.widthProperty().bind(widthProperty());
                    shape = rect2;
                    break;
                default:
                    //Should never occur
                    shape = new Rectangle();
            }
        } else {
            Rectangle rect = new Rectangle();
            rect.heightProperty().bind(heightProperty());
            rect.widthProperty().bind(widthProperty());
            shape = rect;
        }
        shape.setOnMouseClicked(e -> ValueSelectionPopUp.showValueSelectionPopUp(shape, getAllowedValues()));
        shape.setFill(Color.WHITE);
        shape.setStroke(Color.BLACK);
        getChildren().add(shape);
        //Polygons cant be resized :(
        heightProperty().addListener((observable, oldValue, newValue) -> {
            redrawShape();
        });
        widthProperty().addListener((observable, oldValue, newValue) -> {
            redrawShape();
        });
    }

    /**
     * Only for the polygons
     */
    private void redrawShape() {
        Node node = getChildren().get(0);
        if (node != null && node instanceof Polygon) {
            Polygon poly = (Polygon) node;
            double height = getHeight();
            double width = getWidth();
            switch (allowedValuesProperty().iterator().next()) {
                case BOOLEAN:
                    poly.getPoints().clear();
                    poly.getPoints().addAll(
                            0.0, height / 2,
                            height / 2, 0.0,
                            width - (height / 2), 0.0,
                            width, height / 2,
                            width - (height / 2), height,
                            height / 2, height
                    );
                    break;
                case OBJECT:
                    poly.getPoints().clear();
                    poly.getPoints().addAll(
                            0.0, height / 3,
                            height / 3, 0.0,
                            width - (height / 3), 0.0,
                            width, height / 3,
                            width, (height / 3) * 2,
                            width - (height / 3), height,
                            height / 3, height,
                            0.0, (height / 3) * 2
                    );
                    break;
            }
        }
    }
    
}
