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

import io.reactivex.rxjavafx.observables.JavaFxObservable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.DoubleExpression;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import sharknoon.casey.ide.logic.statements.Statement;
import sharknoon.casey.ide.logic.statements.operators.Operator;
import sharknoon.casey.ide.logic.types.PrimitiveType;
import sharknoon.casey.ide.logic.types.Type;
import sharknoon.casey.ide.ui.misc.Icons;
import sharknoon.casey.ide.utils.javafx.BindUtils;
import sharknoon.casey.ide.utils.javafx.FXUtils;

import java.util.List;
import java.util.function.Consumer;

/**
 * @author Josua Frank
 */
public class OperatorBody extends Body<Operator<PrimitiveType, Type>> {
    
    private static final int DEFAULT_MARGIN = 5;
    
    private ObservableList<Node> content;
    
    public OperatorBody(Operator operator) {
        super(operator);
        HBox contentNode = createContentNode();
        onChildChange(operator, contentNode);
        setContent(contentNode);
    }
    
    private HBox createContentNode() {
        Operator<?, ?> operator = getStatement();
        
        HBox hBoxContent = new HBox();
        hBoxContent.setPrefSize(0, 0);
        hBoxContent.setAlignment(Pos.CENTER_LEFT);
        
        //contains nulls for empty parameter
        ObservableList<Body> bodies = statementsToBody(operator);
        content = operator.setOperatorsBetweenParameters(bodies, () -> Icons.get(operator.getOperatorType().getIcon(), 50));
        for (int i = 0; i < content.size(); i++) {
            Node node = content.get(i);
            if (node == null) {
                content.set(i, createPlaceholder());
            } else if (node instanceof Body) {
                Body body = (Body) node;
                int index = i;
                body.setOnBodyDestroyed(() -> {
                    content.set(index, createPlaceholder());
                });
            }
        }
        hBoxContent.setSpacing(DEFAULT_MARGIN);
        Bindings.bindContentBidirectional(hBoxContent.getChildren(), content);
        
        return hBoxContent;
    }
    
    private ValuePlaceholderBody createPlaceholder() {
        Operator<?, ?> operator = getStatement();
        Type parameterType = operator.getParameterType();
        ValuePlaceholderBody body = ValuePlaceholderBody.createValuePlaceholderBody(parameterType, operator);
        
        Consumer<Statement> statementConsumer = s -> {
            if (content.contains(body)) {
                int index = content.indexOf(body);
                content.set(index, s.getBody());
                operator.setParameter(operator.indexWithOperatorsToRegularIndex(index), s);
                s.getBody().setOnBodyDestroyed(() -> {
                    content.set(index, body);
                    operator.setParameter(operator.indexWithOperatorsToRegularIndex(index), null);
                });
            }
        };
        body.setStatementConsumer(statementConsumer);
        return body;
    }
    
    @Override
    public BooleanExpression isClosingAllowed() {
        return Bindings.createBooleanBinding(() -> true);
    }
    
    @Override
    public BooleanExpression isExtendingAllowed() {
        return Bindings.createBooleanBinding(
                () -> getStatement().isExtensible()
        );
    }
    
    @Override
    public void extend() {
        Operator<?, ?> operator = getStatement();
        List<Node> extension = operator.extend(() -> Icons.get(operator.getOperatorType().getIcon(), 50));
        for (Node node : extension) {
            content.add(node == null ? createPlaceholder() : node);
        }
    }
    
    @Override
    public BooleanExpression isReducingAllowed() {
        Operator<?, ?> operator = getStatement();
        return operator.parameterAmountProperty()
                .greaterThan(operator.getMinimumParameterAmount());
    }
    
    @Override
    public void reduce() {
        Operator<?, ?> operator = getStatement();
        int toReduce = operator.reduce();
        content.remove(content.size() - toReduce, content.size());
    }
    
    @Override
    public ObservableList<Text> toText() {
        ObservableList<ObservableList<Text>> text = FXCollections.observableArrayList();
        ObservableList<Statement<PrimitiveType, Type, Type>> childs = getStatement().getChilds();
        Text bracketOpen = new Text("(");
        Text bracketClose = new Text(")");
        Color random = FXUtils.getRandomDifferentColor();
        bracketOpen.setFill(random);
        bracketClose.setFill(random);
        text.add(FXCollections.observableArrayList(bracketOpen));
        text.add(FXCollections.observableArrayList(bracketClose));
        if (childs.size() > 1) {//infix
            BindUtils.addListener(childs, c -> {
                text.remove(1, text.size() - 1);
                if (childs.size() > 0) {
                    var child = childs.get(0);
                    ObservableList<Text> par = child != null ? child.getBody().toText() : FXCollections.observableArrayList(new Text("null"));
                    text.add(text.size() - 1, par);
                }
                for (int i = 1; i < childs.size(); i++) {
                    Text op = new Text(
                            getStatement().getOperatorType().toString()
                    );
                    text.add(text.size() - 1, FXCollections.observableArrayList(op));
                    var child = childs.get(i);
                    ObservableList<Text> par = child != null ? child.getBody().toText() : FXCollections.observableArrayList(new Text("null"));
                    text.add(text.size() - 1, par);
                }
            });
        } else {//op text
            BindUtils.addListener(childs, c -> {
                text.remove(1, text.size() - 1);
                Text op = new Text(
                        getStatement().getOperatorType().toString()
                );
                text.add(text.size() - 1, FXCollections.observableArrayList(op));
                Statement<PrimitiveType, Type, Type> child = childs.size() > 0 ? childs.get(0) : null;
                ObservableList<Text> par = child != null ? child.getBody().toText() : FXCollections.observableArrayList(new Text("null"));
                text.add(text.size() - 1, par);
            });
        }
        return BindUtils.concatFromList(text);
    }
    
    private ObservableList<Body> statementsToBody(Operator<?, ?> o) {
        ObservableList<Body> listBody = FXCollections.observableArrayList();
        o.getParameters().stream()
                .map((parameter) -> parameter == null ? null : parameter.getBody())
                .forEachOrdered((nodePar) -> {
                    listBody.add(nodePar);
                });
        return listBody;
    }
    
    private void onChildChange(Operator<?, ?> operator, HBox hBoxContent) {
        DoubleBinding defaultHeight = Bindings.createDoubleBinding(() -> 57.0);
        
        ObjectProperty<Type> firstParameterType = new SimpleObjectProperty<>();
        ObjectProperty<Type> lastParameterType = new SimpleObjectProperty<>();
        
        JavaFxObservable.valuesOf(operator.childsProperty())
                .map(l -> {
                    if (l.size() < 1 || l.get(0) == null) {
                        return operator.parameterTypeProperty();
                    } else {
                        return l.get(0).returnTypeProperty();
                    }
                })
                .subscribe(objectProperty -> {
                    firstParameterType.unbind();
                    firstParameterType.bind(objectProperty);
                });
        
        JavaFxObservable.valuesOf(operator.childsProperty())
                .map(l -> {
                    if (l.size() < 1 || l.get(l.size() - 1) == null) {
                        return operator.parameterTypeProperty();
                    } else {
                        return l.get(l.size() - 1).returnTypeProperty();
                    }
                })
                .subscribe(objectProperty -> {
                    lastParameterType.unbind();
                    lastParameterType.bind(objectProperty);
                });
        
        
        ObjectProperty<Insets> padding = getPadding(
                operator.startsWithParameter(),
                operator.endsWithParameter(),
                operator.returnTypeProperty(),
                firstParameterType,
                lastParameterType,
                operator.getFirstParameter().map(s -> s.getBody().heightProperty().add(0)).orElse(defaultHeight),
                operator.getLastParameter().map(s -> s.getBody().heightProperty().add(0)).orElse(defaultHeight)
        );
        ObservableValue<Insets> ov = BindUtils.map(padding, p -> new Insets(p.getTop() + DEFAULT_MARGIN, p.getRight() + DEFAULT_MARGIN, p.getBottom() + DEFAULT_MARGIN, p.getLeft() + DEFAULT_MARGIN));
        hBoxContent.paddingProperty().bind(ov);
    }
    
    private ObjectProperty<Insets> getPadding(boolean leftParameter,
                                              boolean rightParameter,
                                              ObservableValue<Type> parent,
                                              ObservableValue<Type> leftType,
                                              ObservableValue<Type> rightType,
                                              DoubleExpression leftHeight,
                                              DoubleExpression rightHeight) {
        DoubleExpression leftPadding = Bindings.createDoubleBinding(() -> 0.0);
        DoubleExpression rightPadding = Bindings.createDoubleBinding(() -> 0.0);
        if (leftParameter) {
            leftPadding = BodyUtils.calculateDistance(parent, leftType, leftHeight);
        }
        if (rightParameter) {
            rightPadding = BodyUtils.calculateDistance(parent, rightType, rightHeight);
        }
        ObjectProperty<Insets> insets = new SimpleObjectProperty<>();
        insets.set(new Insets(0, rightPadding.get(), 0, leftPadding.get()));
        leftPadding.addListener((observable, oldValue, newValue) -> {
            Insets oldInsets = insets.get();
            Insets newInsets = new Insets(oldInsets.getTop(), oldInsets.getRight(), oldInsets.getBottom(), newValue.doubleValue());
            insets.set(newInsets);
        });
        rightPadding.addListener((observable, oldValue, newValue) -> {
            Insets oldInsets = insets.get();
            Insets newInsets = new Insets(oldInsets.getTop(), newValue.doubleValue(), oldInsets.getBottom(), oldInsets.getLeft());
            insets.set(newInsets);
        });
        return insets;
    }
}
