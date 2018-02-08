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
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import sharknoon.dualide.logic.statements.Statement;
import sharknoon.dualide.logic.statements.operations.Operator;
import sharknoon.dualide.logic.statements.values.Value;
import sharknoon.dualide.logic.statements.values.ValueType;
import sharknoon.dualide.ui.misc.Icons;

/**
 *
 * @author Josua Frank
 */
public class OperatorBody extends Body<Operator<Value, Value>> {

    public static OperatorBody createOperatorBody(Operator operator) {
        return new OperatorBody(operator);
    }

    public OperatorBody(Operator operator) {
        super(operator);
        Node content = createContentNode();
        setContent(content);
    }

    private static final int DEFAULT_MARGIN = 5;

    private Node createContentNode() {
        Operator<Value, Value> operator = getStatement().get();

        HBox hBoxContent = new HBox();
        hBoxContent.setPrefSize(0, 0);
        hBoxContent.setAlignment(Pos.CENTER_LEFT);

        //contains nulls for empty parameter
        ObservableList<Body> bodies = statementsToBody(operator);
        ObservableList<Node> content = operator.setOperatorsBetweenParameters(bodies, () -> Icons.get(operator.getOperatorType().getIcon(), 50));
        for (int i = 0; i < content.size(); i++) {
            Node node = content.get(i);
            if (node == null) {
                final int index = i;
                PlaceholderBody pb = PlaceholderBody.createValuePlaceholderBody(operator, s -> {
                    content.set(index, s.getBody());
                });
                content.set(i, pb);
            } else if (node instanceof Body) {
                Body body = (Body) node;
                int index = i;
                body.setOnBodyDestroyed(() -> {
                    PlaceholderBody pb = PlaceholderBody.createValuePlaceholderBody(operator, s -> {
                        content.set(index, s.getBody());
                    });
                    content.set(index, pb);
                });
            }
        }
        content.addListener((Change<? extends Node> c) -> {
            System.out.println(c);
            while (c.next()) {
                if (c.wasReplaced()) {
                    for (int i = 0; i < c.getAddedSize(); i++) {
                        Node a = c.getAddedSubList().get(i);
                        if (a instanceof Body) {
                            Body body = (Body) a;
                            int index = c.getFrom() + i;
                            body.setOnBodyDestroyed(() -> {
                                PlaceholderBody pb = PlaceholderBody.createValuePlaceholderBody(operator, s -> {
                                    content.set(index, s.getBody());
                                });
                                content.set(index, pb);
                            });
                        }
                    }
                }
            }
        });
        hBoxContent.setSpacing(DEFAULT_MARGIN);
        hBoxContent.setPadding(new Insets(DEFAULT_MARGIN));
        Bindings.bindContentBidirectional(hBoxContent.getChildren(), content);

        return hBoxContent;
    }

    private ObservableList<Body> statementsToBody(Operator<Value, Value> o) {
        ObservableList<Body> listBody = FXCollections.observableArrayList();
        for (int i = 0; i < Math.max(o.getParameterAmount(), o.getMinimumParameterAmount()); i++) {
            //can be null
            Statement<Value, Value, Value> statementPar = o.getParameterIndexMap().get(i);
            //Not null
            Body nodePar = getBody(statementPar);
            listBody.add(nodePar);
        }
        return listBody;
    }

    private Body getBody(Statement statement) {
        if (statement != null) {
            return statement.getBody();
        }
        return null;
    }

    private void onChildChange(Operator<Value, Value> operator, HBox hBoxContent) {
        DoubleBinding defaultHeight = Bindings.createDoubleBinding(() -> 57.0);
        ObjectProperty<Insets> padding = getPadding(
                operator.startsWithParameter(),
                operator.endsWithParameter(),
                operator.getReturnType(),
                operator.getFirstParameter().map(p -> (Set) new HashSet() {
            {
                add(p.getReturnType());
            }
        }).orElse(operator.getParameterTypes()),
                operator.getLastParameter().map(p -> (Set) new HashSet() {
            {
                add(p.getReturnType());
            }
        }).orElse(operator.getParameterTypes()),
                operator.getFirstParameter().map(s -> s.getBody().heightProperty().add(0)).orElse(defaultHeight),
                operator.getFirstParameter().map(s -> s.getBody().heightProperty().add(0)).orElse(defaultHeight)
        );
        hBoxContent.setPadding(padding.get());
        padding.addListener((observable, oldValue, newValue) -> {
            System.out.println("padding changed to: " + newValue);
            hBoxContent.setPadding(newValue);
        });
    }

    private ObjectProperty<Insets> getPadding(boolean leftParameter, boolean rightParameter, ValueType parent, Set<ValueType> leftType, Set<ValueType> rightType, DoubleBinding leftHeight, DoubleBinding rightHeight) {
        DoubleBinding leftPadding = Bindings.createDoubleBinding(() -> 0.0);
        DoubleBinding rightPadding = Bindings.createDoubleBinding(() -> 0.0);
        if (leftParameter) {
            leftPadding = calculateDistance(parent, leftType, leftHeight);
        }
        if (rightParameter) {
            rightPadding = calculateDistance(parent, rightType, rightHeight);
        }
        ObjectProperty<Insets> insets = new SimpleObjectProperty<>();
        insets.set(new Insets(0, rightPadding.get(), 0, leftPadding.get()));
        leftPadding.addListener((observable, oldValue, newValue) -> {
            System.out.println("leftpadding changed to: " + newValue);
            Insets oldInsets = insets.get();
            Insets newInsets = new Insets(oldInsets.getTop(), oldInsets.getRight(), oldInsets.getBottom(), newValue.doubleValue());
            insets.set(newInsets);
        });
        rightPadding.addListener((observable, oldValue, newValue) -> {
            System.out.println("rightpadding changed to: " + newValue);
            Insets oldInsets = insets.get();
            Insets newInsets = new Insets(oldInsets.getTop(), newValue.doubleValue(), oldInsets.getBottom(), oldInsets.getLeft());
            insets.set(newInsets);
        });
        return insets;
    }

    /**
     *
     * @param node
     * @param currentIndex index of the children in the hbox
     * @param lastIndex
     * @param parent
     * @param childs
     */
    private DoubleBinding calculateDistance(ValueType parent, Set<ValueType> childs, DoubleBinding childheight) {
        int childvalue = childs.size() > 1 ? 4 : childs.iterator().next().ordinal();
        int parentvalue = parent.ordinal();
        if (parentvalue >= childvalue) {
            return childheight.multiply(0);
        }
        //Parent is a boolean
        switch (parent) {
            case BOOLEAN:
                switch (childvalue) {
                    //Child is a number
                    case 1:
                        return childheight.multiply(1.0 / 4.0);
                    //Child is a object
                    case 2:
                        return childheight.multiply(1.0 / 6.0);
                    //Child is a text
                    case 3:
                        return childheight.multiply(3.0 / 8.0);
                    //Child can be everything
                    case 4:
                        return childheight.multiply(1.0 / 2.0);
                }
                break;
            case NUMBER:
                switch (childvalue) {
                    //Child is a object
                    case 2:
                        return childheight.multiply(3.0 / 20.0);
                    //Child is a text
                    case 3:
                        return childheight.multiply(1.0 / 4.0);
                    //Child can be everything
                    case 4:
                        return childheight.multiply(1.0 / 2.0);
                }
                break;
            case OBJECT:
                switch (childvalue) {
                    //Child is a text
                    case 3:
                        return childheight.multiply(2.0 / 9.0);
                    //Child can be everything
                    case 4:
                        return childheight.multiply(1.0 / 3.0);
                }
                break;
            case TEXT:
                //Child can be everything
                return childheight.multiply(1.0 / 4.0);
        }
        return childheight.multiply(0);
    }

}
