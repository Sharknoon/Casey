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

import javafx.beans.binding.ObjectExpression;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.Pane;
import sharknoon.dualide.logic.statements.Statement;
import sharknoon.dualide.logic.types.Type;
import sharknoon.dualide.ui.bodies.ValuePlaceholderBody;

/**
 * @author Josua Frank
 */
public class ValueField extends Pane {
    
    public static ValueField DISABLED = new ValueField(true);
    
    private final ReadOnlyObjectWrapper<Statement<Type, Type, Type>> statement = new ReadOnlyObjectWrapper<>();
    
    public ValueField() {
        this(Type.UNDEFINED);
    }
    
    public ValueField(Type allowedType) {
        this(new SimpleObjectProperty<>(allowedType));
    }
    
    public ValueField(ObjectExpression<Type> allowedType) {
        ValuePlaceholderBody body = ValuePlaceholderBody.createValuePlaceholderBody(allowedType, null);
        
        body.setStatementConsumer(s -> {
            getChildren().set(0, s.getBody());
            s.getBody().setOnBodyDestroyed(() -> {
                getChildren().set(0, body);
                statement.set(null);
            });
            statement.set(s);
        });
        
        getChildren().add(body);
    }
    
    private ValueField(boolean disabled) {
        getChildren().add(ValuePlaceholderBody.DISABLED);
    }
    
    public Statement<Type, Type, Type> getStatement() {
        return statement.get();
    }
    
    public ReadOnlyObjectProperty<Statement<Type, Type, Type>> statementProperty() {
        return statement.getReadOnlyProperty();
    }
}
