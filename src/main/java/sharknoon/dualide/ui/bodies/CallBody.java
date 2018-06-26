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

import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.DoubleExpression;
import javafx.beans.binding.ObjectExpression;
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
import sharknoon.dualide.logic.items.Class.ObjectType;
import sharknoon.dualide.logic.items.Function;
import sharknoon.dualide.logic.items.Item;
import sharknoon.dualide.logic.statements.Statement;
import sharknoon.dualide.logic.statements.calls.Call;
import sharknoon.dualide.logic.statements.calls.FunctionCall;
import sharknoon.dualide.logic.statements.calls.VariableCall;
import sharknoon.dualide.logic.types.PrimitiveType;
import sharknoon.dualide.logic.types.PrimitiveType.BooleanType;
import sharknoon.dualide.logic.types.PrimitiveType.NumberType;
import sharknoon.dualide.logic.types.PrimitiveType.TextType;
import sharknoon.dualide.logic.types.Type;
import sharknoon.dualide.ui.misc.Icon;
import sharknoon.dualide.ui.misc.Icons;
import sharknoon.dualide.utils.javafx.BindUtils;

import java.util.List;
import java.util.function.Consumer;

public class CallBody extends Body<Call<?>> {
    
    private static final int DEFAULT_MARGIN = 5;
    
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
    
    public void extend() {
    
    }
    
    public void reduce() {
    
    }
    
    private ObservableList<Node> callsToNodes(Call<?> o) {
        ObservableList<Node> listNode = FXCollections.observableArrayList();
        ObservableList<? extends ValueReturnable> calls = o.getCalls();
        for (int i = 0; i < calls.size(); i++) {
            ValueReturnable call = calls.get(i);
            
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
    
            listNode.addAll(icon, label);
    
            if (call instanceof Function) {
                Function f = (Function) call;
                if (f.hasParameter()) {
                    f.getParameter().forEach(p -> {
                        listNode.add(createPlaceholder(p.getReturnType()));
                    });
                }
            }
    
            if (i < calls.size() - 1) {
                Node arrow = Icons.get(Icon.BACKGROUND, 40);
                listNode.add(arrow);
            } else if (i == calls.size() - 1) {
                //BooleanProperty isVisible = BindUtils.getLast(calls).
            }
        }
        return listNode;
    }
    
    private PlaceholderBody createPlaceholder(Type allowedType) {
        Call<?> call = getStatement().get();
        PlaceholderBody body = PlaceholderBody.createValuePlaceholderBody(List.of(allowedType), call);
        
        Consumer<Statement> statementConsumer = s -> {
            if (content.contains(body)) {
                int index = content.indexOf(body);
                content.set(index, s.getBody());
                //call.setParameter(call.indexWithOperatorsToRegularIndex(index), s);
                s.getBody().setOnBodyDestroyed(() -> {
                    content.set(index, body);
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
        ObjectExpression<Insets> padding = getPadding(
                call.returnTypeProperty(),
                heightProperty()
        );
        ObservableValue<Insets> ov = BindUtils.map(padding, p -> new Insets(p.getTop() + DEFAULT_MARGIN, p.getRight() + DEFAULT_MARGIN, p.getBottom() + DEFAULT_MARGIN, p.getLeft() + DEFAULT_MARGIN));
        hBoxContent.paddingProperty().bind(ov);
    }
    
    private ObjectExpression<Insets> getPadding(ObjectExpression<Type> parent, DoubleExpression height) {
        DoubleBinding padding = calculateDistance(parent, height);
        return Bindings.createObjectBinding(() -> new Insets(0, padding.get(), 0, padding.get()), padding);
    }
    
    private DoubleBinding calculateDistance(ObjectExpression<Type> parentOE, DoubleExpression childheightOE) {
        return Bindings.createDoubleBinding(() -> {
            Type parent = parentOE.get();
            double childheight = childheightOE.get();
            if (parent instanceof PrimitiveType.VoidType) {
                return childheight * (1.0 / 2.0);
            } else if (parent instanceof PrimitiveType.BooleanType) {
                return childheight * (1.0 / 3.0);
            } else if (parent instanceof PrimitiveType.NumberType) {
                return childheight * (1.0 / 4.0);
            } else if (parent instanceof ObjectType) {
                return childheight * (1.0 / 4.0);
            } else if (parent instanceof PrimitiveType.TextType) {
                return childheight * (1.0 / 8.0);
            }
            return 0.0;
        }, parentOE, childheightOE);
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
                Function function = c.getCalls().get(i);
                Text callText = new Text(function.getName());
                if (function.hasParameter()) {
                    //TODO
                }
                text.add(callText);
            }
        } else if (statement instanceof VariableCall) {
            //TODO
        }
        return text;
    }
}
