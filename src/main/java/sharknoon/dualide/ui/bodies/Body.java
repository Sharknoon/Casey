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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import sharknoon.dualide.logic.statements.Statement;
import sharknoon.dualide.logic.types.Type;
import sharknoon.dualide.logic.statements.operators.Operator;
import sharknoon.dualide.logic.statements.values.Value;
import sharknoon.dualide.logic.types.PrimitiveType.BooleanType;
import sharknoon.dualide.logic.types.PrimitiveType.NumberType;
import sharknoon.dualide.logic.types.PrimitiveType.TextType;
import sharknoon.dualide.ui.misc.Icon;
import sharknoon.dualide.ui.misc.Icons;
import sharknoon.dualide.utils.javafx.FXUtils;

/**
 * Helper class
 *
 * @param <S> The Statement this body corresponds to
 * @author Josua Frank
 */
public abstract class Body<S extends Statement> extends Group {

    private final S statement;
    private final StackPane contentPane = new StackPane();
    private final Shape backgroundShape;

    public static final Body createBody(Statement statement) {
        if (statement instanceof Operator) {
            return new OperatorBody((Operator) statement);
        } else if (statement instanceof Value) {
            return new ValueBody((Value) statement);
        }
        return null;
    }

    public Body(S statement) {
        super();
        this.statement = statement;
        this.backgroundShape = createOuterShape(statement.getReturnType());
        init();
        super.getChildren().addAll(backgroundShape, contentPane);
    }

    public Body(Collection<? extends Type> types) {
        super();
        this.statement = null;
        this.backgroundShape = createOuterShape(types);
        init();
        super.getChildren().addAll(backgroundShape, contentPane);
    }

    private void init() {
        contentPane.setMinSize(57, 57);
        initCloseButton();
        initPlusButton();
        initMinusButton();
        initListeners();
    }

    private void initCloseButton() {
        Node closeIcon = Icons.get(Icon.CLOSEROUND, 25);
        closeIcon.setOnMouseClicked((event) -> {
            if (statement != null) {
                statement.destroy();
            }
        });
        closeIcon.layoutXProperty().bind(contentPane.widthProperty().subtract(25));
        closeIcon.setLayoutY(0);
        closeIcon.setVisible(false);
        getChildren().add(closeIcon);
        onMouseEntered((event) -> {
            closeIcon.setVisible(true);
            closeIcon.toFront();
        });
        onMouseExited((event) -> {
            closeIcon.setVisible(false);
        });
    }

    private void initPlusButton() {
        if (this instanceof OperatorBody) {
            Operator operator = (Operator) getStatement().get();
            if (operator.isExtensible()) {
                Node addIcon = Icons.get(Icon.PLUSROUND, 25);
                addIcon.setOnMouseClicked((event) -> {
                    ((OperatorBody) this).extend();
                });
                addIcon.layoutXProperty().bind(contentPane.widthProperty().subtract(25));
                addIcon.layoutYProperty().bind(contentPane.heightProperty().divide(2).subtract(12));
                addIcon.setVisible(false);
                getChildren().add(addIcon);
                onMouseEntered((event) -> {
                    addIcon.setVisible(true);
                    addIcon.toFront();
                });
                onMouseExited((event) -> {
                    addIcon.setVisible(false);
                });
            }
        }
    }

    private void initMinusButton() {
        if (this instanceof OperatorBody) {
            Operator operator = (Operator) getStatement().get();
            if (operator.isExtensible()) {
                Node removeIcon = Icons.get(Icon.MINUSROUND, 25);
                removeIcon.setOnMouseClicked((event) -> {
                    ((OperatorBody) this).reduce();
                });
                removeIcon.layoutXProperty().bind(contentPane.widthProperty().subtract(25));
                removeIcon.layoutYProperty().bind(contentPane.heightProperty().subtract(25));
                removeIcon.setVisible(false);
                getChildren().add(removeIcon);
                onMouseEntered((event) -> {
                    removeIcon.visibleProperty().bind(operator.parameterAmountProperty().greaterThan(operator.getMinimumParameterAmount()));
                    removeIcon.toFront();
                });
                onMouseExited((event) -> {
                    removeIcon.visibleProperty().unbind();
                    removeIcon.setVisible(false);
                });
            }
        }
    }

    private Node previousCloseIcon;

    private void setCurrentCloseIcon(Node newCloseIcon) {
        if (previousCloseIcon != null) {
            previousCloseIcon.setVisible(false);
        }
        previousCloseIcon = newCloseIcon;
    }

    private final List<Consumer<MouseEvent>> onMouseEntered = new ArrayList<>();

    private void onMouseEntered(Consumer<MouseEvent> event) {
        onMouseEntered.add(event);
    }

    private final List<Consumer<MouseEvent>> onMouseExited = new ArrayList<>();

    private void onMouseExited(Consumer<MouseEvent> event) {
        onMouseExited.add(event);
    }

    private void initListeners() {
        setOnMouseEntered((event) -> {
            onMouseEntered.forEach(c -> c.accept(event));
        });
        setOnMouseExited((event) -> {
            onMouseExited.forEach(c -> c.accept(event));
        });
    }

    public Optional<S> getStatement() {
        return Optional.ofNullable(statement);
    }

    /**
     * @param polygon
     * @param isOctagon    wether this is a octagon(true) or a hexagon(false)
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

    private Shape createOuterShape(Type type) {
        return createOuterShape(List.of(type));
    }

    private Shape createOuterShape(Collection<? extends Type> types) {
        final Shape shape;
        double height = contentPane.getHeight();
        double width = contentPane.getWidth();
        if (types != null && types.size() == 1) {
            Type type = types.iterator().next();
            if (type.isPrimitive()) {
                if (type instanceof BooleanType) {
                    Polygon poly = new Polygon();
                    redrawPolygon(poly, false, height, width);
                    contentPane.heightProperty().addListener((observable, oldValue, newValue) -> {
                        redrawPolygon(poly, false, contentPane.heightProperty().get(), contentPane.widthProperty().get());
                    });
                    contentPane.widthProperty().addListener((observable, oldValue, newValue) -> {
                        redrawPolygon(poly, false, contentPane.heightProperty().get(), contentPane.widthProperty().get());
                    });
                    shape = poly;
                } else if (type instanceof NumberType) {
                    Rectangle rect = new Rectangle();
                    rect.arcHeightProperty().bind(rect.heightProperty());
                    rect.arcWidthProperty().bind(rect.heightProperty());
                    rect.heightProperty().bind(contentPane.heightProperty());
                    rect.widthProperty().bind(contentPane.widthProperty());
                    shape = rect;
                } else if (type instanceof TextType) {
                    Rectangle rect2 = new Rectangle();
                    rect2.arcHeightProperty().bind(rect2.heightProperty().divide(2));
                    rect2.arcWidthProperty().bind(rect2.heightProperty().divide(2));
                    rect2.heightProperty().bind(contentPane.heightProperty());
                    rect2.widthProperty().bind(contentPane.widthProperty());
                    shape = rect2;
                } else {
                    //Should never occur
                    shape = new Rectangle();
                }
            } else {
                Polygon poly2 = new Polygon();
                redrawPolygon(poly2, true, height, width);
                contentPane.heightProperty().addListener((observable, oldValue, newValue) -> {
                    redrawPolygon(poly2, true, contentPane.heightProperty().get(), contentPane.widthProperty().get());
                });
                contentPane.widthProperty().addListener((observable, oldValue, newValue) -> {
                    redrawPolygon(poly2, true, contentPane.heightProperty().get(), contentPane.widthProperty().get());
                });
                shape = poly2;
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
        //shape.setStrokeType(StrokeType.INSIDE);
        return shape;
    }

    public void setContent(Node... node) {
        contentPane.getChildren().clear();
        contentPane.getChildren().addAll(node);
    }

    public List<Node> getContent() {
        return contentPane.getChildren();
    }

    public ReadOnlyDoubleProperty heightProperty() {
        return contentPane.heightProperty();
    }

    private List<Runnable> onDestroy;

    public void setOnBodyDestroyed(Runnable runnable) {
        if (onDestroy == null) {
            onDestroy = new ArrayList<>();
        }
        onDestroy.add(runnable);
    }

    /**
     * Do not call directly! call getStatement().ifPresent(s -> s.destroy());
     */
    public void destroy() {
        if (onDestroy != null) {
            onDestroy.forEach(Runnable::run);
        }
        contentPane.getChildren().clear();
        getChildren().clear();
    }

    public ObservableList<Text> toText() {
        ObservableList<Text> text = FXCollections.observableArrayList();
        if (statement instanceof Operator) {
            List<Statement> childs = statement.getChilds();
            Text bracketOpen = new Text("(");
            Text bracketClose = new Text(")");
            Color random = FXUtils.getRandomDifferentColor();
            bracketOpen.setFill(random);
            bracketClose.setFill(random);
            text.add(bracketOpen);
            if (childs.size() > 1) {//infix
                childs.stream().forEach((child) -> {
                    List<Text> par = child != null?child.getBody().toText():List.of(new Text(String.valueOf((Object)null)));
                    text.addAll(par);
                    Text op = new Text(String.valueOf(((Operator) statement).getOperatorType()));
                    text.add(op);
                });
                if (childs.size() > 0) {
                    text.remove(text.size() - 1);
                }
            } else {//op text
                Text op = new Text(String.valueOf(((Operator) statement).getOperatorType()));
                text.add(op);
                if (childs.size() > 0) {
                    Text par = new Text(String.valueOf(childs.get(0)));
                    par.setFill(Color.LIGHTBLUE);
                    text.add(par);
                }
            }
            text.add(bracketClose);
        } else if (statement instanceof Value) {
            Text par = new Text(String.valueOf(String.valueOf(statement)));
            par.setFill(Color.LIGHTBLUE);
            text.add(par);
        } else {
            //TODO funktionen
        }
        return text;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + String.valueOf(statement);
    }

}
