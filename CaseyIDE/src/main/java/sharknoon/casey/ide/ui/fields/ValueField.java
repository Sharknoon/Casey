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
package sharknoon.casey.ide.ui.fields;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectExpression;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import sharknoon.casey.ide.logic.statements.Statement;
import sharknoon.casey.ide.logic.types.PrimitiveType;
import sharknoon.casey.ide.logic.types.Type;
import sharknoon.casey.ide.ui.bodies.ValuePlaceholderBody;
import sharknoon.casey.ide.utils.javafx.BindUtils;

import java.util.Objects;

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
    
        //In the case the allowed type changes but there is a statement associated to it, delete it
        allowedType.addListener((observable, oldValue, at) -> {
            if (at == null) {//Whatever is at the value, it can stay there (Undefined Type)
                return;
            }
            Statement<?, ?, ?> currentS = statementProperty().get();
            if (currentS != null && !Objects.equals(currentS.returnTypeProperty().get(), at)) {
                statementProperty().set(null);
            }
        });
        
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
