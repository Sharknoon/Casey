package sharknoon.dualide.ui.bodies;/*
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

import javafx.beans.binding.*;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
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
import sharknoon.dualide.logic.items.Item;
import sharknoon.dualide.logic.items.ItemType;
import sharknoon.dualide.logic.statements.Statement;
import sharknoon.dualide.logic.statements.calls.CallItem;
import sharknoon.dualide.logic.types.PrimitiveType;
import sharknoon.dualide.logic.types.Type;
import sharknoon.dualide.ui.misc.Icons;
import sharknoon.dualide.ui.styles.StyleClasses;
import sharknoon.dualide.utils.javafx.BindUtils;
import sharknoon.dualide.utils.javafx.bindings.AggregatedObservableList;
import sharknoon.dualide.utils.settings.Logger;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class CallItemBody extends Body<CallItem<?>> {
    
    private static final int DEFAULT_MARGIN = 5;
    //The left shape is an icon, i use the rounded corners of the text shape to complement the icon
    private static final ObjectProperty<Type> leftShape = new SimpleObjectProperty<>(PrimitiveType.TEXT);
    
    public CallItemBody(CallItem<?> statement) {
        super(statement);
        setContent(getBodyContent());
    }
    
    private Node getBodyContent() {
        var hBoxContent = new HBox();
        hBoxContent.setPrefSize(0, 0);
        hBoxContent.setAlignment(Pos.CENTER_LEFT);
        hBoxContent.setSpacing(DEFAULT_MARGIN);
        hBoxContent.paddingProperty().bind(bindPadding(getStatement()));
        Bindings.bindContent(hBoxContent.getChildren(), getNodes());
        return hBoxContent;
    }
    
    private ObservableList<Node> getNodes() {
        ObservableList<Node> nodeList = FXCollections.observableArrayList();
        
        CallItem<?> callItem = getStatement();
        
        ObjectProperty<Image> image = getIcon();
        ImageView icon = new ImageView();
        icon.setPreserveRatio(true);
        Image im = image.get();
        if (im.getWidth() > im.getHeight()) {
            icon.setFitWidth(50);
        } else {
            icon.setFitHeight(50);
        }
        icon.imageProperty().bind(image);
        
        StringProperty name = getName();
        Label label = new Label();
        label.setMinWidth(Region.USE_PREF_SIZE);
        label.setTextFill(Color.BLACK);
        label.setFont(Font.font(25));
        label.textProperty().bind(name);
        
        nodeList.addAll(icon, label);
        
        BiConsumer<Statement, Integer> parameterConsumer;
        parameterConsumer = (p, index) -> {
            Body b;
            if (p == null) {
                b = createParameterPlaceholder(
                        callItem.getReturnTypePropertyForIndex(index),
                        nodeList,
                        callItem
                );
            } else {
                b = p.getBody();
            }
            //+ 2 because the icon and the name are using the first two spots
            if (nodeList.size() <= index + 2) {
                nodeList.add(b);
            } else {
                nodeList.set(index + 2, b);
            }
        };
        
        for (int i = 0; i < callItem.getChilds().size(); i++) {
            parameterConsumer.accept(callItem.getChilds().get(i), i);
        }
        
        callItem.getChilds().addListener((ListChangeListener<Statement<Type, Type, Type>>) c -> {
            while (c.next()) {
                if (c.wasReplaced()) {
                    return;
                }
                if (c.wasAdded()) {
                    for (int i = c.getFrom(); i < c.getTo(); i++) {
                        parameterConsumer.accept(c.getList().get(i), i);
                    }
                }
                if (c.wasRemoved()) {
                    nodeList.remove(c.getFrom() + 2, c.getTo() + 3);
                }
            }
        });
        return nodeList;
    }
    
    /**
     * @param allowedType The allowed Type the user should be able to insert
     * @param contentList The contentList in which the placeholder is being set and replaced by a statement body
     * @return
     */
    private ValuePlaceholderBody createParameterPlaceholder(
            ObjectExpression<Type> allowedType,
            ObservableList<Node> contentList,
            Statement parent) {
        ValuePlaceholderBody pb = ValuePlaceholderBody.createValuePlaceholderBody(allowedType, parent);
        
        Consumer<Statement> statementConsumer = s -> {
            Body sb = s.getBody();
            if (contentList.contains(pb)) {
                int index = contentList.indexOf(pb);
                contentList.set(index, s.getBody());
                parent.getChilds().set(index - 2, s);
                sb.setOnBodyDestroyed(() -> {
                    if (contentList.contains(sb)) {
                        int index2 = contentList.indexOf(sb);
                        contentList.set(index2, pb);
                        parent.getChilds().set(index2 - 2, null);
                    }
                });
            }
        };
        pb.setStatementConsumer(statementConsumer);
        return pb;
    }
    
    private ObjectProperty<Image> getIcon() {
        return Icons.iconToImageProperty(getStatement().getItem().getSite().tabIconProperty());
    }
    
    private StringProperty getName() {
        return getStatement().getItem().nameProperty();
    }
    
    private ObservableValue<Insets> bindPadding(CallItem<?> callItem) {
    
        ObjectExpression<Type> rightType;
        if (callItem.getItem().getType() == ItemType.VARIABLE) {
            rightType = new SimpleObjectProperty<>(PrimitiveType.TEXT);
        } else {//Funktion
            rightType = callItem.lastParameterTypeProperty();
        }
        
        ReadOnlyObjectProperty<Statement<Type, Type, Type>> parent = callItem.parentProperty();
        ObjectProperty<Type> parentReturnType = new SimpleObjectProperty<>();
        BindUtils.addListener(parent, (observable, oldValue, newValue) -> {
            parentReturnType.bind(newValue.returnTypeProperty());
        });
    
        return getPadding(
                parentReturnType,
                heightProperty(),
                leftShape,
                rightType
        );
    }
    
    private ObjectExpression<Insets> getPadding(ObjectExpression<Type> parent, DoubleExpression height, ObjectExpression<Type> leftType, ObjectExpression<Type> rightType) {
        Logger.debug("GETTING PADDING FOR parent:" + parent.get() + ", left:" + leftType.get() + ", right:" + rightType.get());
        DoubleBinding leftPadding = BodyUtils.calculateDistance(parent, leftType, height);
        DoubleBinding rightPadding = BodyUtils.calculateDistance(parent, rightType, height);
        return Bindings.createObjectBinding(() -> new Insets(0, rightPadding.get(), 0, leftPadding.get()), rightPadding, leftPadding);
    }
    
    @Override
    public BooleanExpression isClosingAllowed() {
        return Bindings.createBooleanBinding(() -> false);
    }
    
    @Override
    public BooleanExpression isExtendingAllowed() {
        return Bindings.createBooleanBinding(() -> false);
    }
    
    @Override
    public BooleanExpression isReducingAllowed() {
        return Bindings.createBooleanBinding(() -> false);
    }
    
    @Override
    public ObservableList<Text> toText() {
        CallItem<?> callItem = getStatement();
        
        Item<?, ?, ?> item = callItem.getItem();
        ObservableList<ObservableList<Text>> result = FXCollections.observableArrayList();
        
        Text textName = new Text();
        textName.textProperty().bind(item.nameProperty());
        textName.getStyleClass().add(StyleClasses.textStatementCallItemName.name());
        result.add(FXCollections.observableArrayList(textName));
        
        ObservableList<Statement<Type, Type, Type>> childs = callItem.childsProperty();
        BindUtils.addListener(childs, c -> {
            if (item.getType() == ItemType.FUNCTION) {
                result.remove(1, result.size());
                Text textOpenBracket = new Text("(");
                textOpenBracket.getStyleClass().add(StyleClasses.textStatementCallItemFunctionBrackets.name());
                result.add(FXCollections.observableArrayList(textOpenBracket));
    
                AggregatedObservableList<Text> aggTexts2 = childs.stream()
                        .map(p -> p != null ? p.getBody().toText() : FXCollections.observableArrayList(new Text("null")))
                        .collect(AggregatedObservableList::new, AggregatedObservableList::appendList, AggregatedObservableList::mergeWith);
    
                AggregatedObservableList<Text> aggTexts = new AggregatedObservableList<>();
                for (Statement<Type, Type, Type> p : childs) {
                    ObservableList<Text> texts = p != null ? p.getBody().toText() : FXCollections.observableArrayList(new Text("null"));
                    aggTexts.appendList(texts);
                }
                result.add(aggTexts2);
                
                Text textCloseBracket = new Text(")");
                textCloseBracket.getStyleClass().add(StyleClasses.textStatementCallItemFunctionBrackets.name());
                result.add(FXCollections.observableArrayList(textCloseBracket));
            }
        });
    
        return BindUtils.concatFromList(result);
    }
}
