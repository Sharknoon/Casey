package sharknoon.casey.ide.ui.bodies;
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
import javafx.beans.binding.BooleanExpression;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.ObjectExpression;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableDoubleValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import sharknoon.casey.ide.logic.ValueReturnable;
import sharknoon.casey.ide.logic.items.Class;
import sharknoon.casey.ide.logic.items.Class.ObjectType;
import sharknoon.casey.ide.logic.statements.Statement;
import sharknoon.casey.ide.logic.statements.calls.Call;
import sharknoon.casey.ide.logic.statements.calls.CallItem;
import sharknoon.casey.ide.logic.types.Type;
import sharknoon.casey.ide.ui.misc.Icon;
import sharknoon.casey.ide.ui.misc.Icons;
import sharknoon.casey.ide.ui.styles.StyleClasses;
import sharknoon.casey.ide.utils.javafx.BindUtils;

import java.util.function.Consumer;

public class CallBody extends Body<Call<?>> {
    
    private static final int DEFAULT_MARGIN = 5;
    private static final ObjectProperty<Type> UNDEFINED = new SimpleObjectProperty<>(Type.UNDEFINED);
    private ObservableList<Node> content;
    
    public CallBody(Call<?> statement) {
        super(statement);
        var contentNode = createContentNode();
        onChildChange(statement, contentNode);
        setContent(contentNode);
    }
    
    private HBox createContentNode() {
        var call = getStatement();
        
        var hBoxContent = new HBox();
        hBoxContent.setPrefSize(0, 0);
        hBoxContent.setAlignment(Pos.CENTER_LEFT);
        
        var isUndefinedExpected = call.getExpectedReturnType() == Type.UNDEFINED;
        bindError(Bindings.createBooleanBinding(() -> isUndefinedExpected)
                .or(call
                        .returnTypeProperty()
                        .isEqualTo(call.getExpectedReturnType()))
                .not()
        );
        
        //contains nulls for empty parameter
        content = getNodes(call);
        
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
        Call<?> c = getStatement();
        if (c == null) {
            return Bindings.createBooleanBinding(() -> false);
        }
        return c.isExtensible();
    }
    
    @Override
    public void extend() {
        getStatement().getChilds().add(null);
    }
    
    @Override
    public BooleanExpression isReducingAllowed() {
        return getStatement().isReducible();
    }
    
    @Override
    public void reduce() {
        ObservableList<Statement<Type, Type, Type>> childs = getStatement().getChilds();
        childs.remove(childs.size() - 1);
    }
    
    @Override
    public ObservableList<Text> toText() {
        ObservableList<ObservableList<Text>> texts = FXCollections.observableArrayList();
        ObservableList<Statement<Type, Type, Type>> childs = getStatement().getChilds();
        BindUtils.addListener(childs, c -> {
            texts.clear();
            for (int i = 0; i < childs.size(); i++) {
                Statement<Type, Type, Type> statement = childs.get(i);
                if (statement == null) {
                    texts.add(FXCollections.observableArrayList(new Text("null")));
                    return;
                }
                CallItem<?> callItem = ((CallItem) statement);
                ObservableList<Text> callItemTexts = callItem.getBody().toText();
                texts.add(callItemTexts);
                if (i < childs.size() - 1) {
                    Text textArrow = new Text(" -> ");
                    textArrow.getStyleClass().add(StyleClasses.textStatementCallItemArrow.name());
                    texts.add(FXCollections.observableArrayList(textArrow));
                }
            }
        });
        return BindUtils.concatFromList(texts);
    }
    
    private ObservableList<Node> getNodes(Call<?> o) {
        ObservableList<Node> listNode = FXCollections.observableArrayList();
        ReadOnlyListProperty<Statement<Type, Type, Type>> callItems = o.childsProperty();
        BindUtils.addListener(callItems, c -> onCallChanged(listNode, callItems));
        return listNode;
    }
    
    private void onCallChanged(ObservableList<Node> listNode, ObservableList<Statement<Type, Type, Type>> calls) {
        listNode.clear();
        for (int i = 0; i < calls.size(); i++) {
            CallItem<?> callItem = (CallItem<?>) calls.get(i);
            if (callItem != null) {
                listNode.add(callItem.getBody());
            } else {
                Call<?> call = getStatement();
                CallPlaceholderBody placeholder = createCallPlaceholder(
                        listNode,
                        b -> {
                        },
                        call);
                listNode.add(placeholder);
            }
            
            if (i < calls.size() - 1) {
                Node arrow = Icons.get(Icon.ARROWRIGHT, 40);
                listNode.add(arrow);
            }
        }
    }
    
    /**
     * @param contentList        The contentList in which the placeholder is being set and replaced by a statement body
     * @param onStatementChanged a body consumer, optional
     * @return
     */
    private CallPlaceholderBody createCallPlaceholder(
            ObservableList<Node> contentList,
            Consumer<Body> onStatementChanged,
            Call parent) {
        CallPlaceholderBody placeholderBody;
        
        CallItem callItem = (CallItem) parent.getChilds().get(parent.getChilds().size() - 2);
        if (callItem != null && callItem.getItem() instanceof ValueReturnable) {
            Type returnType = ((ValueReturnable) callItem.getItem()).getReturnType();
            ObjectType objectType = returnType.getObjectType();
            Class c = objectType.toItem();
            placeholderBody = CallPlaceholderBody.createCallPlaceholderBody(c, parent);
        } else {
            return CallPlaceholderBody.DISABLED;
        }
        
        
        Consumer<Statement> statementConsumer = statement -> {
            Body statementBody = statement.getBody();
            onStatementChanged.accept(statementBody);
            if (contentList.contains(placeholderBody)) {
                contentList.set(contentList.indexOf(placeholderBody), statement.getBody());
            }
        };
        placeholderBody.setStatementConsumer(statementConsumer);
        return placeholderBody;
    }
    
    private void onChildChange(Call<?> call, HBox hBoxContent) {
        //On destroy
        if (call.getChilds().isEmpty()) {
            return;
        }
        ObservableValue<Type> firstChildReturnTypeProperty = BindUtils.map(call.firstChildProperty(), p -> p != null ? p.returnTypeProperty().get() : Type.UNDEFINED);
        SimpleObjectProperty<Type> lastChildReturnTypeProperty = new SimpleObjectProperty<>();
        BindUtils.addListener(call.lastChildProperty(), (observable, oldValue, newValue) -> {
            if (newValue != null) {
                lastChildReturnTypeProperty.bind(newValue.returnTypeProperty());
            } else {
                lastChildReturnTypeProperty.bind(UNDEFINED);
            }
        });
    
        ObjectExpression<Insets> padding = getPadding(
                call.returnTypeProperty(),
                heightProperty(),
                firstChildReturnTypeProperty,
                lastChildReturnTypeProperty
        );
        ObservableValue<Insets> ov = BindUtils.map(padding, p -> new Insets(p.getTop() + DEFAULT_MARGIN, p.getRight() + DEFAULT_MARGIN, p.getBottom() + DEFAULT_MARGIN, p.getLeft() + DEFAULT_MARGIN));
        hBoxContent.paddingProperty().bind(ov);
    }
    
    private ObjectExpression<Insets> getPadding(ObservableValue<Type> parent, ObservableDoubleValue height, ObservableValue<Type> firstCallItem, ObservableValue<Type> lastCallItem) {
        DoubleBinding leftPadding = BodyUtils.calculateDistance(parent, firstCallItem, height);
        DoubleBinding rightPadding = BodyUtils.calculateDistance(parent, lastCallItem, height);
        return Bindings.createObjectBinding(() -> new Insets(0, rightPadding.get(), 0, leftPadding.get()), rightPadding);
    }
}
