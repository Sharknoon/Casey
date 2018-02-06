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

import java.util.HashSet;
import java.util.Set;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeType;
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
public abstract class Body<S extends Statement> extends Group {

    private final StackPane contentPane = new StackPane();

    public static final Body createBody(Statement statement) {
        switch (statement.getStatementType()) {
            case OPERATOR:
                return new OperatorBody((Operator) statement);
            case VALUE:
                return new ValueBody((Value) statement);
        }
        return null;
    }

    public Body(ValueType valueType) {
        super();
        contentPane.setMinSize(57, 57);
        super.getChildren().addAll(createOuterShape(valueType), contentPane);
    }

    public Body(Set<ValueType> valueTypes) {
        super();
        contentPane.setMinSize(57, 57);
        super.getChildren().addAll(createOuterShape(valueTypes), contentPane);
    }

    /**
     *
     * @param polygon
     * @param isOctagon wehter this is a octagon(true) or a hexagon(false)
     * @param parentHeight
     * @param parentWidth
     */
    private static void redrawPolygon(Polygon polygon, boolean isOctagon, double parentHeight, double parentWidth) {
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

    private Shape createOuterShape(ValueType valueType) {
        //To be replaced by Set.of(...); TODO
        Set<ValueType> valueTypes = new HashSet<>();
        valueTypes.add(valueType);
        return createOuterShape(valueTypes);
    }

    private Shape createOuterShape(Set<ValueType> valueTypes) {
        final Shape shape;
        double height = contentPane.getHeight();
        double width = contentPane.getWidth();
        if (valueTypes.size() == 1) {
            switch (valueTypes.iterator().next()) {
                case BOOLEAN:
                    Polygon poly = new Polygon();
                    redrawPolygon(poly, false, height, width);
                    contentPane.heightProperty().addListener((observable, oldValue, newValue) -> {
                        redrawPolygon(poly, false, contentPane.heightProperty().get(), contentPane.widthProperty().get());
                    });
                    contentPane.widthProperty().addListener((observable, oldValue, newValue) -> {
                        redrawPolygon(poly, false, contentPane.heightProperty().get(), contentPane.widthProperty().get());
                    });
                    shape = poly;
                    break;
                case NUMBER:
                    Rectangle rect = new Rectangle();
                    rect.arcHeightProperty().bind(rect.heightProperty());
                    rect.arcWidthProperty().bind(rect.heightProperty());
                    rect.heightProperty().bind(contentPane.heightProperty());
                    rect.widthProperty().bind(contentPane.widthProperty());
                    shape = rect;
                    break;
                case OBJECT:
                    Polygon poly2 = new Polygon();
                    redrawPolygon(poly2, true, height, width);
                    contentPane.heightProperty().addListener((observable, oldValue, newValue) -> {
                        redrawPolygon(poly2, true, contentPane.heightProperty().get(), contentPane.widthProperty().get());
                    });
                    contentPane.widthProperty().addListener((observable, oldValue, newValue) -> {
                        redrawPolygon(poly2, true, contentPane.heightProperty().get(), contentPane.widthProperty().get());
                    });
                    shape = poly2;
                    break;
                case TEXT:
                    Rectangle rect2 = new Rectangle();
                    rect2.arcHeightProperty().bind(rect2.heightProperty().divide(2));
                    rect2.arcWidthProperty().bind(rect2.heightProperty().divide(2));
                    rect2.heightProperty().bind(contentPane.heightProperty());
                    rect2.widthProperty().bind(contentPane.widthProperty());
                    shape = rect2;
                    break;
                default:
                    //Should never occur
                    shape = new Rectangle();
            }
        } else {
            Rectangle rect = new Rectangle();
            rect.heightProperty().bind(contentPane.heightProperty());
            rect.widthProperty().bind(contentPane.widthProperty());
            shape = rect;
        }
        shape.setFill(Color.WHITE);
        if (this instanceof PlaceholderBody) {
            shape.setStroke(Color.GREY);
        } else {
            shape.setStroke(Color.BLACK);
        }
        shape.setStrokeWidth(3);
        shape.setStrokeType(StrokeType.INSIDE);
        return shape;
    }

    public void setContent(Node... node) {
        contentPane.getChildren().addAll(node);
    }

}
