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
package sharknoon.dualide.ui.statements;

import java.util.HashSet;
import java.util.Set;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import sharknoon.dualide.logic.statements.Statement;
import sharknoon.dualide.logic.statements.operations.Operator;
import sharknoon.dualide.logic.statements.values.Value;
import sharknoon.dualide.logic.statements.values.ValueType;

/**
 * Helper class
 *
 * @author Josua Frank
 * @param <S> The Statement this body corresponds to
 */
public abstract class Body<S extends Statement> extends StackPane {

    //Controls the Shape because the shape is the biggest object in the stackpane
    //and the shape controls the overall size of the stackpane
    protected final ReadOnlyDoubleWrapper widthProperty = new ReadOnlyDoubleWrapper(75);
    protected final ReadOnlyDoubleWrapper heightProperty = new ReadOnlyDoubleWrapper(50);

    public static final Body createBody(Statement statement) {
        switch (statement.getStatementType()) {
            case OPERATOR:
                return new OperatorBody((Operator) statement);
            case VALUE:
                return new ValueBody((Value) statement);
        }
        return null;
    }

    private final S statement;

    public Body(S statement) {
        this.statement = statement;
        setPrefSize(0, 0);
    }

    public S getStatement() {
        return statement;
    }

    /**
     *
     * @param polygon
     * @param isOctagon wehter this is a octagon(true) or a hexagon(false)
     * @param parentHeight
     * @param parentWidth
     */
    public static void redrawPolygon(Polygon polygon, boolean isOctagon, double parentHeight, double parentWidth) {
        if (isOctagon) {
            polygon.getPoints().clear();
            polygon.getPoints().addAll(
                    0.0, parentHeight / 3,
                    parentHeight / 3, 0.0,
                    parentWidth - (parentHeight / 3), 0.0,
                    parentWidth, parentHeight / 3,
                    parentWidth, (parentHeight / 3) * 2,
                    parentWidth - (parentHeight / 3), parentHeight,
                    parentHeight / 3, parentHeight,
                    0.0, (parentHeight / 3) * 2
            );
        } else {
            polygon.getPoints().clear();
            polygon.getPoints().addAll(
                    0.0, parentHeight / 2,
                    parentHeight / 2, 0.0,
                    parentWidth - (parentHeight / 2), 0.0,
                    parentWidth, parentHeight / 2,
                    parentWidth - (parentHeight / 2), parentHeight,
                    parentHeight / 2, parentHeight
            );
        }
    }

    public Shape createOuterShape(ValueType valueType) {
        //To be replaced by Set.of(...); TODO
        Set<ValueType> valueTypes = new HashSet<>();
        valueTypes.add(valueType);
        return createOuterShape(valueTypes);
    }

    public Shape createOuterShape(Set<ValueType> valueTypes) {
        final Shape shape;
        double height = heightProperty.get();
        double width = widthProperty.get();
        if (valueTypes.size() == 1) {
            switch (valueTypes.iterator().next()) {
                case BOOLEAN:
                    Polygon poly = new Polygon();
                    redrawPolygon(poly, false, height, width);
                    heightProperty.addListener((observable, oldValue, newValue) -> {
                        redrawPolygon(poly, false, heightProperty.get(), widthProperty.get());
                    });
                    widthProperty.addListener((observable, oldValue, newValue) -> {
                        redrawPolygon(poly, false, heightProperty.get(), widthProperty.get());
                    });
                    shape = poly;
                    break;
                case NUMBER:
                    Rectangle rect = new Rectangle();
                    rect.arcHeightProperty().bind(rect.heightProperty());
                    rect.arcWidthProperty().bind(rect.heightProperty());
                    rect.heightProperty().bind(heightProperty);
                    rect.widthProperty().bind(widthProperty);
                    shape = rect;
                    break;
                case OBJECT:
                    Polygon poly2 = new Polygon();
                    redrawPolygon(poly2, true, height, width);
                    heightProperty.addListener((observable, oldValue, newValue) -> {
                        redrawPolygon(poly2, true, heightProperty.get(), widthProperty.get());
                    });
                    widthProperty.addListener((observable, oldValue, newValue) -> {
                        redrawPolygon(poly2, true, heightProperty.get(), widthProperty.get());
                    });
                    shape = poly2;
                    break;
                case TEXT:
                    Rectangle rect2 = new Rectangle();
                    rect2.heightProperty().addListener((observable, oldValue, newValue) -> {
                        rect2.setArcHeight(newValue.doubleValue() / 2);
                        rect2.setArcWidth(newValue.doubleValue() / 2);
                    });
                    rect2.heightProperty().bind(heightProperty);
                    rect2.widthProperty().bind(widthProperty);
                    shape = rect2;
                    break;
                default:
                    //Should never occur
                    shape = new Rectangle();
            }
        } else {
            Rectangle rect = new Rectangle();
            rect.heightProperty().bind(heightProperty);
            rect.widthProperty().bind(widthProperty);
            shape = rect;
        }
        shape.setFill(Color.WHITE);
        shape.setStroke(Color.BLACK);
        shape.setStrokeWidth(3);
        return shape;
    }
}
