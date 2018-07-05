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
import sharknoon.dualide.logic.ValueReturnable;
import sharknoon.dualide.logic.items.Class;
import sharknoon.dualide.logic.items.Class.ObjectType;
import sharknoon.dualide.logic.statements.Statement;
import sharknoon.dualide.logic.statements.calls.Call;
import sharknoon.dualide.logic.statements.calls.CallItem;
import sharknoon.dualide.logic.types.Type;
import sharknoon.dualide.ui.misc.Icon;
import sharknoon.dualide.ui.misc.Icons;
import sharknoon.dualide.ui.styles.StyleClasses;
import sharknoon.dualide.utils.javafx.BindUtils;

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
        ObservableList<Text> text = FXCollections.observableArrayList();
        getStatement().getChilds().forEach(statement -> {
            if (statement == null) {
                return;
            }
            CallItem<?> callItem = ((CallItem) statement);
        
            text.addAll(callItem.getBody().toText());
        
            if (!callItem.equals(getStatement().lastChildProperty().getValue())) {
                Text textArrow = new Text(" -> ");
                textArrow.getStyleClass().add(StyleClasses.textStatementCallItemArrow.name());
                text.add(textArrow);
            }
        });
        return text;
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
        call.lastChildProperty().addListener((observable, oldValue, newValue) -> {
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
