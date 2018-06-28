package sharknoon.dualide.ui.bodies;
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

import io.reactivex.rxjavafx.observables.JavaFxObservable;
import javafx.beans.binding.*;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import sharknoon.dualide.logic.ValueReturnable;
import sharknoon.dualide.logic.items.Function;
import sharknoon.dualide.logic.items.Item;
import sharknoon.dualide.logic.items.ItemType;
import sharknoon.dualide.logic.items.Parameter;
import sharknoon.dualide.logic.statements.Statement;
import sharknoon.dualide.logic.statements.calls.Call;
import sharknoon.dualide.logic.statements.calls.FunctionCall;
import sharknoon.dualide.logic.statements.calls.VariableCall;
import sharknoon.dualide.logic.types.PrimitiveType;
import sharknoon.dualide.logic.types.Type;
import sharknoon.dualide.ui.misc.Icon;
import sharknoon.dualide.ui.misc.Icons;
import sharknoon.dualide.utils.javafx.BindUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class CallBody extends Body<Call<?>> {
    
    private static final int DEFAULT_MARGIN = 5;
    //The left shape is an icon, i use the rounded corners of the text shape to complement the icon
    private static final ObjectProperty<Type> leftShape = new SimpleObjectProperty<>(PrimitiveType.TEXT);
    private ObservableList<Node> content;
    
    public CallBody(Call<?> statement) {
        super(statement);
        HBox contentNode = createContentNode();
        onChildChange(statement, contentNode);
        setContent(contentNode);
    }
    
    private HBox createContentNode() {
        Call<?> call = getStatement().get();
        
        HBox hBoxContent = new HBox();
        hBoxContent.setPrefSize(0, 0);
        hBoxContent.setAlignment(Pos.CENTER_LEFT);
        
        //contains nulls for empty parameter
        content = callsToNodes(call);
        
        hBoxContent.setSpacing(DEFAULT_MARGIN);
        Bindings.bindContentBidirectional(hBoxContent.getChildren(), content);
        
        return hBoxContent;
    }
    
    @Override
    public BooleanExpression isClosingAllowed() {
        return Bindings.createBooleanBinding(() -> true);
    }
    
    @Override
    public BooleanExpression isExtendingAllowed() {
        Call<?> c = getStatement().orElse(null);
        if (c == null) {
            return Bindings.createBooleanBinding(() -> false);
        }
        return c.isExtensible();
    }
    
    @Override
    public void extend() {
        getStatement().map(Call::getCalls).ifPresent(c -> c.add(null));
    }
    
    @Override
    public BooleanExpression isReducingAllowed() {
        Call<?> c = getStatement().orElse(null);
        if (c == null) {
            return Bindings.createBooleanBinding(() -> false);
        }
        return c.isReducible();
    }
    
    @Override
    public void reduce() {
    
    }
    
    @Override
    public ObservableList<Text> toText() {
        ObservableList<Text> text = FXCollections.observableArrayList();
        Statement statement = getStatement().orElse(null);
        if (statement == null) {
            text.add(new Text("ERROR"));
            return text;
        }
        if (statement instanceof FunctionCall) {
            FunctionCall c = (FunctionCall) statement;
            for (int i = 0; i < c.getCalls().size(); i++) {
                //ValueReturnable function = c.getCalls().get(i);
                //Text callText = new Text(function.getName());
                //TODO parameter
                //text.add(callText);
            }
        } else if (statement instanceof VariableCall) {
            //TODO
        }
        return text;
    }
    
    private ObservableList<Node> callsToNodes(Call<?> o) {
        ObservableList<ObservableList<Node>> listNode = FXCollections.observableArrayList();
        ObservableList<? extends ValueReturnable> calls = o.getCalls();
        BindUtils.addListener(calls, c -> onCallChanged(listNode, calls));
        return BindUtils.concatFromList(listNode);
    }
    
    private void onCallChanged(ObservableList<ObservableList<Node>> listNode, ObservableList<? extends ValueReturnable> calls) {
        listNode.clear();
        for (int i = 0; i < calls.size(); i++) {
            ValueReturnable call = calls.get(i);
            if (call != null) {
                listNode.add(callToNode(call));
            } else {
                ObservableList<Node> list = FXCollections.observableArrayList();
                //TODO very unsafe
                PlaceholderBody placeholder = createPlaceholder(calls.get(i - 1).returnTypeProperty(), list);
                list.add(placeholder);
                listNode.add(list);
            }
            
            if (i < calls.size() - 1) {
                ObservableList<Node> list = FXCollections.observableArrayList();
                Node arrow = Icons.get(Icon.ARROWRIGHT, 40);
                list.add(arrow);
                listNode.add(list);
            } else if (i == calls.size() - 1) {
                //BooleanProperty isVisible = BindUtils.getLast(calls).
            }
            //BooleanProperty rightType = BindUtils.getLast(calls);
        }
    }
    
    private ObservableList<Node> callToNode(ValueReturnable call) {
        ObservableList<Node> nodeList = FXCollections.observableArrayList();
        ObjectProperty<Image> image = getIcon(call);
        ImageView icon = new ImageView();
        icon.setPreserveRatio(true);
        Image im = image.get();
        if (im.getWidth() > im.getHeight()) {
            icon.setFitWidth(50);
        } else {
            icon.setFitHeight(50);
        }
        icon.imageProperty().bind(image);
        
        StringProperty name = getName(call);
        Label label = new Label();
        label.setMinWidth(Region.USE_PREF_SIZE);
        label.setTextFill(Color.BLACK);
        label.setFont(Font.font(25));
        label.textProperty().bind(name);
        
        nodeList.addAll(icon, label);
        
        if (call instanceof Function) {
            Map<Parameter, PlaceholderBody> bodies = new HashMap<>();
            Function f = (Function) call;
            f.getChildren()
                    .stream()
                    .filter(i -> i instanceof Parameter)
                    .map(i -> (Parameter) i)
                    .forEach(p -> {
                        PlaceholderBody placeholder = createPlaceholder(p.returnTypeProperty(), nodeList);
                        bodies.put(p, placeholder);
                        nodeList.add(placeholder);
                    });
            
            JavaFxObservable.changesOf(f.getChildren())
                    .filter(c -> c.getValue().getType() == ItemType.PARAMETER)
                    .subscribe(c -> {
                        switch (c.getFlag()) {
                            case ADDED:
                                Parameter p = (Parameter) c.getValue();
                                PlaceholderBody placeholder = createPlaceholder(p.returnTypeProperty(), nodeList);
                                bodies.put(p, placeholder);
                                nodeList.add(placeholder);
                                break;
                            case REMOVED:
                                nodeList.remove(bodies.get(c.getValue()));
                                break;
                        }
                    });
        }
        return nodeList;
    }
    
    private PlaceholderBody createPlaceholder(ObjectProperty<Type> allowedType, ObservableList<Node> contentList) {
        Call<?> call = getStatement().get();
        PlaceholderBody body = PlaceholderBody.createValuePlaceholderBody(allowedType, call);
        
        Consumer<Statement> statementConsumer = s -> {
            if (contentList.contains(body)) {
                int index = contentList.indexOf(body);
                contentList.set(index, s.getBody());
                //call.setParameter(call.indexWithOperatorsToRegularIndex(index), s);
                s.getBody().setOnBodyDestroyed(() -> {
                    contentList.set(index, body);
                    //call.setParameter(call.indexWithOperatorsToRegularIndex(index), null);
                });
            }
        };
        body.setStatementConsumer(statementConsumer);
        return body;
    }
    
    private ObjectProperty<Image> getIcon(ValueReturnable vr) {
        ObjectProperty<Icon> icon;
        if (vr instanceof Item) {
            icon = ((Item) vr).getSite().tabIconProperty();
        } else {
            icon = new SimpleObjectProperty<>(Icon.VOID);
        }
        //evtl platform.runlater
        return Icons.iconToImageProperty(icon);
    }
    
    private StringProperty getName(ValueReturnable vr) {
        if (vr instanceof Item) {
            return ((Item) vr).nameProperty();
        }
        return new SimpleStringProperty("ERROR");
    }
    
    private void onChildChange(Call<?> call, HBox hBoxContent) {
        ObjectProperty<Type> lastParameterType = new SimpleObjectProperty<>();
        ObjectBinding<ValueReturnable<Type>> lastCallProperty = call.lastCallProperty();
        BindUtils.addListener(lastCallProperty, (observable, oldValue, newValue) -> {
            if (newValue != null && newValue instanceof Function) {
                Function f = (Function) newValue;
                ObservableList<Item<? extends Item, Function, ? extends Item>> children = f.getChildren();
                BindUtils.addListener(children, c -> {
                    Optional<ObjectProperty<Type>> type = children
                            .stream()
                            .filter(i -> i.getType() == ItemType.PARAMETER)
                            .map(i -> (Parameter) i)
                            .reduce((a, b) -> b)
                            .map(p -> p.returnTypeProperty());
                    if (type.isPresent()) {
                        lastParameterType.bind(type.get());
                    } else {
                        lastParameterType.unbind();
                        lastParameterType.set(null);
                    }
                });
            } else {
                lastParameterType.unbind();
                lastParameterType.set(null);
            }
        });
        ObjectExpression<Insets> padding = getPadding(
                call.returnTypeProperty(),
                heightProperty(),
                lastParameterType
        );
        ObservableValue<Insets> ov = BindUtils.map(padding, p -> new Insets(p.getTop() + DEFAULT_MARGIN, p.getRight() + DEFAULT_MARGIN, p.getBottom() + DEFAULT_MARGIN, p.getLeft() + DEFAULT_MARGIN));
        hBoxContent.paddingProperty().bind(ov);
    }
    
    private ObjectExpression<Insets> getPadding(ObjectExpression<Type> parent, DoubleExpression height, ObjectExpression<Type> lastParameter) {
        DoubleBinding leftPadding = BodyUtils.calculateDistance(parent, leftShape, height);
        lastParameter = lastParameter == null ? leftShape : lastParameter;
        DoubleBinding rightPadding = BodyUtils.calculateDistance(parent, lastParameter, height);
        return Bindings.createObjectBinding(() -> new Insets(0, rightPadding.get(), 0, leftPadding.get()), rightPadding);
    }
}
