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
import javafx.beans.binding.ObjectExpression;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.text.Text;
import sharknoon.dualide.logic.statements.Statement;
import sharknoon.dualide.logic.types.PrimitiveType;
import sharknoon.dualide.logic.types.Type;
import sharknoon.dualide.ui.browsers.ValuePopUpBuilder;
import sharknoon.dualide.ui.misc.Icon;
import sharknoon.dualide.ui.misc.Icons;

import java.util.function.Consumer;

/**
 * @author Josua Frank
 */
public class ValuePlaceholderBody extends Body {
    
    public static ValuePlaceholderBody DISABLED = new ValuePlaceholderBody();
    
    public static ValuePlaceholderBody createValuePlaceholderBody(Type type, Statement parent) {
        return createValuePlaceholderBody(type, parent, null);
    }
    
    public static ValuePlaceholderBody createValuePlaceholderBody(Type type, Statement parent, Consumer<Statement> statementConsumer) {
        return new ValuePlaceholderBody(type, parent, statementConsumer);
    }
    
    public static ValuePlaceholderBody createValuePlaceholderBody(ObjectExpression<Type> type, Statement parent) {
        return createValuePlaceholderBody(type, parent, null);
    }
    
    public static ValuePlaceholderBody createValuePlaceholderBody(ObjectExpression<Type> type, Statement parent, Consumer<Statement> statementConsumer) {
        return new ValuePlaceholderBody(type, parent, statementConsumer);
    }
    
    
    private Consumer<Statement> statementConsumer;
    
    public ValuePlaceholderBody(ObjectExpression<Type> type, Statement parent, Consumer<Statement> statementConsumer) {
        super(type);
        this.statementConsumer = statementConsumer;
        setOnMouseClicked((event) -> {
            ValuePopUpBuilder
                    .create(this, this.statementConsumer)
                    .setParent(parent)
                    .setAllowedType(type.get())
                    .showValuePopUp();
        });
        setOnMouseEntered((event) -> {
            setContent(Icons.get(Icon.PLUS, 50));
        });
        setOnMouseExited((event) -> {
            setContent();
        });
    
        visibleProperty().bind(type.isNotEqualTo(PrimitiveType.VOID));
    }
    
    public ValuePlaceholderBody(Type type, Statement parent, Consumer<Statement> statementConsumer) {
        super(type);
        this.statementConsumer = statementConsumer;
        setOnMouseClicked((event) -> {
            ValuePopUpBuilder
                    .create(this, this.statementConsumer)
                    .setParent(parent)
                    .setAllowedType(type)
                    .showValuePopUp();
        });
        setOnMouseEntered((event) -> {
            setContent(Icons.get(Icon.PLUS, 50));
        });
        setOnMouseExited((event) -> {
            setContent();
        });
    }
    
    private ValuePlaceholderBody() {
        super(Type.UNDEFINED);
        setOnMouseEntered((event) -> {
            setContent(Icons.get(Icon.VOID, 50));
        });
        setOnMouseExited((event) -> {
            setContent();
        });
    }
    
    public void setStatementConsumer(Consumer<Statement> consumer) {
        this.statementConsumer = consumer;
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
    
    /**
     * Cant use statement -> destroy, because a placeholder has no statement (yet)
     */
    @Override
    public void destroy() {
        super.destroy();
    }
    
    @Override
    public ObservableList<Text> toText() {
        return FXCollections.emptyObservableList();
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
