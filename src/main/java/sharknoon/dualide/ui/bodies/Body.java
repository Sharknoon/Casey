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

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.ObjectExpression;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableObjectValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import sharknoon.dualide.logic.statements.Statement;
import sharknoon.dualide.logic.statements.calls.Call;
import sharknoon.dualide.logic.statements.operators.Operator;
import sharknoon.dualide.logic.statements.values.Value;
import sharknoon.dualide.logic.types.PrimitiveType.BooleanType;
import sharknoon.dualide.logic.types.PrimitiveType.NumberType;
import sharknoon.dualide.logic.types.PrimitiveType.TextType;
import sharknoon.dualide.logic.types.PrimitiveType.VoidType;
import sharknoon.dualide.logic.types.Type;
import sharknoon.dualide.ui.misc.Icon;
import sharknoon.dualide.ui.misc.Icons;
import sharknoon.dualide.ui.misc.MouseConsumable;
import sharknoon.dualide.utils.javafx.BindUtils;
import sharknoon.dualide.utils.settings.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Helper class
 *
 * @param <S> The Statement this body corresponds to
 * @author Josua Frank
 */
public abstract class Body<S extends Statement> extends Group implements MouseConsumable {
    
    private static final ObservableList<Node> currentCloseButton = FXCollections.observableArrayList();
    private static final ObservableList<Node> currentExtendButton = FXCollections.observableArrayList();
    private static final ObservableList<Node> currentReduceButton = FXCollections.observableArrayList();
    
    public static Body createBody(Statement statement) {
        if (statement instanceof Operator) {
            return new OperatorBody((Operator) statement);
        } else if (statement instanceof Value) {
            return new ValueBody((Value) statement);
        } else if (statement instanceof Call) {
            return new CallBody((Call) statement);
        }
        return null;
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
    
    private static void redrawPath(Path path, double parentHeight, double parentWidth) {
        path.getElements().clear();
        path.getElements().addAll(
                new MoveTo(0, parentHeight / 2),
                new ArcTo(parentHeight / 2, parentHeight / 2, 0, parentHeight / 2, 0, false, false),
                new HLineTo(parentWidth - (parentHeight / 2)),
                new ArcTo(parentHeight / 2, parentHeight / 2, 0, parentWidth, parentHeight / 2, false, false),
                new ArcTo(parentHeight / 2, parentHeight / 2, 0, parentWidth - (parentHeight / 2), parentHeight, false, false),
                new HLineTo(parentHeight / 2),
                new ArcTo(parentHeight / 2, parentHeight / 2, 0, 0, parentHeight / 2, false, false)
        );
    }
    
    private final S statement;
    private final StackPane contentPane = new StackPane();
    private final ObjectBinding<Shape> backgroundShape;
    private BooleanProperty errorProperty = new SimpleBooleanProperty(false);
    private List<Runnable> onDestroy;
    private Node closeIcon;
    private Node extendIcon;
    private Node reduceIcon;
    
    Body(S statement) {
        super();
        this.statement = statement;
        this.backgroundShape = typeToShapeBinding(statement.returnTypeProperty());
        init();
        getChildren().addAll(backgroundShape.get(), contentPane);
        backgroundShape.addListener((observable, oldValue, newValue) -> {
            getChildren().remove(oldValue);
            getChildren().add(0, newValue);
        });
    }
    
    Body(Type type) {
        super();
        this.statement = null;
        this.backgroundShape = typeToShapeBinding(Bindings.createObjectBinding(() -> type));
        init();
        getChildren().addAll(backgroundShape.get(), contentPane);
        backgroundShape.addListener((observable, oldValue, newValue) -> {
            getChildren().remove(oldValue);
            getChildren().add(0, newValue);
        });
    }
    
    Body(ObjectExpression<Type> type) {
        super();
        this.statement = null;
        this.backgroundShape = typeToShapeBinding(type);
        init();
        getChildren().addAll(backgroundShape.get(), contentPane);
        backgroundShape.addListener((observable, oldValue, newValue) -> {
            getChildren().remove(oldValue);
            getChildren().add(0, newValue);
        });
    }
    
    private void init() {
        contentPane.setMinSize(57, 57);
        initCloseButton();
        initPlusButton();
        initMinusButton();
        MouseConsumable.registerListeners(this, this);
    }
    
    protected void bindError(BooleanExpression error) {
        errorProperty.unbind();
        errorProperty.bind(error);
    }
    
    @Override
    public void onMouseEntered(MouseEvent event) {
        currentCloseButton.add(closeIcon);
        currentExtendButton.add(extendIcon);
        currentReduceButton.add(reduceIcon);
    }
    
    @Override
    public void onMouseExited(MouseEvent event) {
        currentCloseButton.remove(currentCloseButton.size() - 1);
        currentExtendButton.remove(currentExtendButton.size() - 1);
        currentReduceButton.remove(currentReduceButton.size() - 1);
    }
    
    public abstract BooleanExpression isClosingAllowed();
    
    private void initCloseButton() {
        closeIcon = Icons.get(Icon.CLOSEROUND, 25);
        closeIcon.setOnMouseClicked((event) -> {
            if (statement != null) {
                statement.destroy();
            }
        });
        closeIcon.layoutXProperty().bind(contentPane.widthProperty().subtract(25));
        closeIcon.setLayoutY(0);
        closeIcon.setVisible(false);
        getChildren().add(closeIcon);
        closeIcon.visibleProperty().bind(hoverProperty().and(isClosingAllowed()).and(BindUtils.getLast(currentCloseButton).isEqualTo(closeIcon)));
        closeIcon.visibleProperty().addListener((ob, old, newV) -> closeIcon.toFront());
    }
    
    public abstract BooleanExpression isExtendingAllowed();
    
    private void initPlusButton() {
        extendIcon = Icons.get(Icon.PLUSROUND, 25);
        extendIcon.setOnMouseClicked((event) -> extend());
        extendIcon.layoutXProperty().bind(contentPane.widthProperty().subtract(25));
        extendIcon.layoutYProperty().bind(contentPane.heightProperty().divide(2).subtract(12));
        extendIcon.setVisible(false);
        getChildren().add(extendIcon);
        extendIcon.visibleProperty().bind(hoverProperty().and(isExtendingAllowed()).and(BindUtils.getLast(currentExtendButton).isEqualTo(extendIcon)));
        extendIcon.visibleProperty().addListener((ob, old, newV) -> extendIcon.toFront());
    }
    
    public void extend() {
    }
    
    public abstract BooleanExpression isReducingAllowed();
    
    private void initMinusButton() {
        reduceIcon = Icons.get(Icon.MINUSROUND, 25);
        reduceIcon.setOnMouseClicked((event) -> reduce());
        reduceIcon.layoutXProperty().bind(contentPane.widthProperty().subtract(25));
        reduceIcon.layoutYProperty().bind(contentPane.heightProperty().subtract(25));
        reduceIcon.setVisible(false);
        getChildren().add(reduceIcon);
        reduceIcon.visibleProperty().bind(hoverProperty().and(isReducingAllowed()).and(BindUtils.getLast(currentReduceButton).isEqualTo(reduceIcon)));
        reduceIcon.visibleProperty().addListener((ob, old, newV) -> reduceIcon.toFront());
    }
    
    public void reduce() {
    }
    
    public Optional<S> getStatement() {
        return Optional.ofNullable(statement);
    }
    
    private ObjectBinding<Shape> typeToShapeBinding(ObservableObjectValue<Type> type) {
        Body b = this;
        return new ObjectBinding<>() {
            
            {
                bind(type);
            }
            
            @Override
            public void dispose() {
                unbind(type);
            }
            
            @Override
            protected Shape computeValue() {
                final Shape shape;
                double height = contentPane.getHeight();
                double width = contentPane.getWidth();
                if (type != null && type.get() != null) {
                    Type t = type.get();
                    if (t.isPrimitive()) {
                        if (t instanceof BooleanType) {
                            Polygon poly = new Polygon();
                            redrawPolygon(poly, false, height, width);
                            contentPane.heightProperty().addListener((observable, oldValue, newValue) -> {
                                redrawPolygon(poly, false, contentPane.heightProperty().get(), contentPane.widthProperty().get());
                            });
                            contentPane.widthProperty().addListener((observable, oldValue, newValue) -> {
                                redrawPolygon(poly, false, contentPane.heightProperty().get(), contentPane.widthProperty().get());
                            });
                            shape = poly;
                        } else if (t instanceof NumberType) {
                            Rectangle rect = new Rectangle();
                            rect.arcHeightProperty().bind(rect.heightProperty());
                            rect.arcWidthProperty().bind(rect.heightProperty());
                            rect.heightProperty().bind(contentPane.heightProperty());
                            rect.widthProperty().bind(contentPane.widthProperty());
                            shape = rect;
                        } else if (t instanceof TextType) {
                            Rectangle rect2 = new Rectangle();
                            rect2.arcHeightProperty().bind(rect2.heightProperty().divide(2));
                            rect2.arcWidthProperty().bind(rect2.heightProperty().divide(2));
                            rect2.heightProperty().bind(contentPane.heightProperty());
                            rect2.widthProperty().bind(contentPane.widthProperty());
                            shape = rect2;
                        } else if (t instanceof VoidType) {
                            Path path = new Path();
                            redrawPath(path, height, width);
                            contentPane.heightProperty().addListener((observable, oldValue, newValue) -> {
                                redrawPath(path, contentPane.heightProperty().get(), contentPane.widthProperty().get());
                            });
                            contentPane.widthProperty().addListener((observable, oldValue, newValue) -> {
                                redrawPath(path, contentPane.heightProperty().get(), contentPane.widthProperty().get());
                            });
                            shape = path;
                        } else {
                            //Should never occur
                            shape = new Rectangle();
                        }
                        shape.setStroke(Color.BLACK);
                    } else if (t.isObject()) {
                        Polygon poly2 = new Polygon();
                        redrawPolygon(poly2, true, height, width);
                        contentPane.heightProperty().addListener((observable, oldValue, newValue) -> {
                            redrawPolygon(poly2, true, contentPane.heightProperty().get(), contentPane.widthProperty().get());
                        });
                        contentPane.widthProperty().addListener((observable, oldValue, newValue) -> {
                            redrawPolygon(poly2, true, contentPane.heightProperty().get(), contentPane.widthProperty().get());
                        });
                        shape = poly2;
                        shape.setStroke(Color.BLACK);
                    } else {
                        //Body is a undefined type
                        Rectangle rect = new Rectangle();
                        rect.heightProperty().bind(contentPane.heightProperty());
                        rect.widthProperty().bind(contentPane.widthProperty());
                        shape = rect;
                        shape.setStroke(Color.CRIMSON);
                    }
                    shape.setFill(Color.WHITE);
                    if (b instanceof ValuePlaceholderBody || b instanceof CallPlaceholderBody) {
                        shape.setStroke(Color.GREY);
                    }
                } else {
                    //No type allowed, but a type is necessary
                    Rectangle error = new Rectangle();
                    error.heightProperty().bind(contentPane.heightProperty());
                    error.widthProperty().bind(contentPane.widthProperty());
                    error.setFill(Color.RED);
                    error.setStroke(Color.CRIMSON);
                    shape = error;
                    Logger.error("Body is no type, should never occur");
                }
                Paint strokeColor = shape.getStroke();
                errorProperty.addListener((observable, oldValue, newValue) -> {
                    if (newValue) {
                        shape.setStroke(Color.CRIMSON);
                    } else {
                        shape.setStroke(strokeColor);
                    }
                });
                shape.setStrokeWidth(3);
                //shape.setStrokeType(StrokeType.INSIDE);
                return shape;
            }
        };
    }
    
    public List<Node> getContent() {
        return contentPane.getChildren();
    }
    
    public void setContent(Node... node) {
        contentPane.getChildren().clear();
        contentPane.getChildren().addAll(node);
    }
    
    ReadOnlyDoubleProperty heightProperty() {
        return contentPane.heightProperty();
    }
    
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
    
    public abstract ObservableList<Text> toText();
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + String.valueOf(statement);
    }
    
}
