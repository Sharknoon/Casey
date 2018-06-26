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
import javafx.beans.binding.DoubleBinding;
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
import sharknoon.dualide.logic.items.Class.ObjectType;
import sharknoon.dualide.logic.statements.Statement;
import sharknoon.dualide.logic.statements.operators.Operator;
import sharknoon.dualide.logic.statements.operators.OperatorType;
import sharknoon.dualide.logic.types.PrimitiveType.BooleanType;
import sharknoon.dualide.logic.types.PrimitiveType.NumberType;
import sharknoon.dualide.logic.types.PrimitiveType.TextType;
import sharknoon.dualide.logic.types.Type;
import sharknoon.dualide.ui.misc.Icons;
import sharknoon.dualide.utils.javafx.BindUtils;
import sharknoon.dualide.utils.javafx.FXUtils;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author Josua Frank
 */
public class OperatorBody extends Body<Operator<Type, Type>> {
    
    private static final int DEFAULT_MARGIN = 5;
    
    public static OperatorBody createOperatorBody(Operator operator) {
        return new OperatorBody(operator);
    }
    
    private static int getWeight(Type type) {
        if (type instanceof BooleanType) {
            return 0;
        } else if (type instanceof NumberType) {
            return 1;
        } else if (type instanceof ObjectType) {
            return 2;
        } else if (type instanceof TextType) {
            return 3;
        }
        //placeholder rectangle
        return 4;
    }
    
    private ObservableList<Node> content;
    
    public OperatorBody(Operator operator) {
        super(operator);
        HBox contentNode = createContentNode();
        onChildChange(operator, contentNode);
        setContent(contentNode);
    }
    
    private HBox createContentNode() {
        Operator<?, ?> operator = getStatement().get();
        
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
    
    private PlaceholderBody createPlaceholder() {
        Operator<?, ?> operator = getStatement().get();
        Set<Type> parameterTypes = operator.getParameterTypes();
        PlaceholderBody body = PlaceholderBody.createValuePlaceholderBody(parameterTypes, operator);
        
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
    
    public void extend() {
        Operator<?, ?> operator = getStatement().get();
        if (operator.isExtensible()) {
            List<Node> extension = getStatement().get().extend(() -> Icons.get(operator.getOperatorType().getIcon(), 50));
            for (int i = 0; i < extension.size(); i++) {
                Node node = extension.get(i);
                if (node == null) {
                    content.add(createPlaceholder());
                } else {
                    content.add(node);
                }
            }
        }
    }
    
    public void reduce() {
        Operator<?, ?> operator = getStatement().get();
        if (operator.isExtensible() && operator.getParameterAmount() > operator.getMinimumParameterAmount()) {
            int toReduce = getStatement().get().reduce();
            content.remove(content.size() - toReduce, content.size());
        }
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
        ObjectProperty<Insets> padding = getPadding(
                operator.startsWithParameter(),
                operator.endsWithParameter(),
                operator.getReturnType(),
                operator.getFirstParameter().map(p -> Set.of(p.getReturnType())).orElse(operator.getParameterTypes()),
                operator.getLastParameter().map(p -> Set.of(p.getReturnType())).orElse(operator.getParameterTypes()),
                operator.getFirstParameter().map(s -> s.getBody().heightProperty().add(0)).orElse(defaultHeight),
                operator.getLastParameter().map(s -> s.getBody().heightProperty().add(0)).orElse(defaultHeight)
        );
        ObservableValue<Insets> ov = BindUtils.map(padding, p -> new Insets(p.getTop() + DEFAULT_MARGIN, p.getRight() + DEFAULT_MARGIN, p.getBottom() + DEFAULT_MARGIN, p.getLeft() + DEFAULT_MARGIN));
        hBoxContent.paddingProperty().bind(ov);
    }
    
    private ObjectProperty<Insets> getPadding(boolean leftParameter,
                                              boolean rightParameter,
                                              Type parent,
                                              Set<Type> leftType,
                                              Set<Type> rightType,
                                              DoubleBinding leftHeight,
                                              DoubleBinding rightHeight) {
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
    
    /**
     * @param parent
     * @param childs
     */
    private DoubleBinding calculateDistance(Type parent, Set<Type> childs, DoubleBinding childheight) {
        int childvalue = 4;
        if (childs != null && childs.size() == 1) {
            childvalue = getWeight(childs.iterator().next());
        }
        int parentvalue = getWeight(parent);
        if (parentvalue >= childvalue) {
            return childheight.multiply(0);
        }
        //Parent is a boolean
        if (parent instanceof BooleanType) {
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
        } else if (parent instanceof NumberType) {
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
        } else if (parent instanceof ObjectType) {
            switch (childvalue) {
                //Child is a text
                case 3:
                    return childheight.multiply(2.0 / 9.0);
                //Child can be everything
                case 4:
                    return childheight.multiply(1.0 / 3.0);
            }
        } else if (parent instanceof TextType) {
            //Child can be everything
            return childheight.multiply(1.0 / 4.0);
        }
        return childheight.multiply(0);
    }
    
    @Override
    public ObservableList<Text> toText() {
        ObservableList<Text> text = FXCollections.observableArrayList();
        List<Statement<Type, Type, Type>> childs = getStatement().map(Statement::getChilds).orElse(List.of());
        Text bracketOpen = new Text("(");
        Text bracketClose = new Text(")");
        Color random = FXUtils.getRandomDifferentColor();
        bracketOpen.setFill(random);
        bracketClose.setFill(random);
        text.add(bracketOpen);
        if (childs.size() > 1) {//infix
            childs.forEach((child) -> {
                List<Text> par = child != null ? child.getBody().toText() : List.of(new Text(String.valueOf((Object) null)));
                text.addAll(par);
                Text op = new Text(
                        getStatement()
                                .map(Operator::getOperatorType)
                                .map(OperatorType::toString)
                                .orElse("ERROR")
                );
                text.add(op);
            });
            if (childs.size() > 0) {
                text.remove(text.size() - 1);
            }
        } else {//op text
            Text op = new Text(
                    getStatement()
                            .map(Operator::getOperatorType)
                            .map(OperatorType::toString)
                            .orElse("ERROR")
            );
            text.add(op);
            if (childs.size() > 0) {
                Text par = new Text(String.valueOf(childs.get(0)));
                par.setFill(Color.LIGHTBLUE);
                text.add(par);
            }
        }
        text.add(bracketClose);
        return text;
    }
}
