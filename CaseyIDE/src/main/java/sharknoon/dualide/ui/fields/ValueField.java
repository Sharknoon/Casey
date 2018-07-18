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
package sharknoon.dualide.ui.fields;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectExpression;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import sharknoon.dualide.logic.statements.Statement;
import sharknoon.dualide.logic.types.PrimitiveType;
import sharknoon.dualide.logic.types.Type;
import sharknoon.dualide.ui.bodies.ValuePlaceholderBody;
import sharknoon.dualide.utils.javafx.BindUtils;

/**
 * @author Josua Frank
 */
public class ValueField extends Pane implements Field {
    
    public static ValueField DISABLED = new ValueField(true);
    
    private final ObjectProperty<Statement<?, ?, ?>> statement = new SimpleObjectProperty<>();
    ObjectExpression<Type> allowedType;
    private ObservableList<Text> texts;
    
    public ValueField() {
        this(Type.UNDEFINED);
    }
    
    public ValueField(Type allowedType) {
        this(new SimpleObjectProperty<>(allowedType), null);
    }
    
    public ValueField(Statement<?, ?, ?> statement) {
        this(new SimpleObjectProperty<>(Type.UNDEFINED), statement);
    }
    
    public ValueField(ObjectExpression<Type> allowedType) {
        this(allowedType, null);
    }
    
    public ValueField(ObjectExpression<Type> allowedType, Statement<?, ?, ?> statement) {
        this.allowedType = allowedType;
        ValuePlaceholderBody body = ValuePlaceholderBody.createValuePlaceholderBody(allowedType, null);
        
        statementProperty().addListener((observable, oldValue, s) -> {
            if (s == null) {
                getChildren().set(0, body);
                return;
            }
            getChildren().set(0, s.getBody());
            s.getBody().setOnBodyDestroyed(() -> {
                getChildren().set(0, body);
                this.statement.set(null);
            });
        });
        body.setStatementConsumer(this.statement::set);
        
        if (statement != null) {
            statementProperty().set(statement);
        }
        
        getChildren().add(body);
    }
    
    private ValueField(boolean disabled) {
        getChildren().add(ValuePlaceholderBody.DISABLED);
    }
    
    public Statement<?, ?, ?> getStatement() {
        return statement.get();
    }
    
    public ObjectProperty<Statement<?, ?, ?>> statementProperty() {
        return statement;
    }
    
    @Override
    public ObservableList<Text> toText() {
        ObservableList<Text> result = FXCollections.observableArrayList();
        BindUtils.addListener(statementProperty(), (observable, oldValue, newValue) -> {
            if (newValue != null) {
                texts = newValue.getBody().toText();
                Bindings.bindContent(result, texts);
            } else {
                if (texts != null) {
                    Bindings.unbindContent(result, texts);
                    texts = null;
                }
                result.clear();
                Text nullText = new Text("null");
                nullText.visibleProperty().bind(allowedType.isNotEqualTo(PrimitiveType.VOID));
                result.add(nullText);
            }
        });
        return result;
    }
}
