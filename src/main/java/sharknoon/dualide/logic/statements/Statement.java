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
package sharknoon.dualide.logic.statements;

import java.util.HashMap;
import java.util.Map;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import sharknoon.dualide.logic.items.ItemType;
import sharknoon.dualide.logic.statements.operations.Operator;
import sharknoon.dualide.logic.statements.values.Value;
import sharknoon.dualide.logic.statements.values.ValueType;
import sharknoon.dualide.ui.bodies.Body;

/**
 * The statement is the base class for values, operators, ...
 *
 * @author Josua Frank
 * @param <PV> The parent Value of this statement
 * @param <RV> The return Value of this statement
 * @param <CV> The child Value of this statement
 */
public abstract class Statement<PV extends Value, RV extends Value, CV extends Value> {

    private final transient ReadOnlyObjectWrapper<Statement<Value, Value, RV>> parent = new ReadOnlyObjectWrapper<>();
    protected final ReadOnlyListWrapper<Statement<RV, CV, Value>> childs = new ReadOnlyListWrapper<>(FXCollections.observableArrayList());
    private final ReadOnlyObjectWrapper<Body> body = new ReadOnlyObjectWrapper<>();

    public Statement(Statement<Value, Value, RV> parent) {
        if (parent != null) {
            this.parent.set(parent);
            this.parent.get().childs.add((Statement) this);
        }
    }

    public ReadOnlyObjectProperty<Statement<Value, Value, RV>> parentProperty() {
        return parent.getReadOnlyProperty();
    }

    public ReadOnlyListProperty<Statement<RV, CV, Value>> childsProperty() {
        return childs.getReadOnlyProperty();
    }

    public Body getBody() {
        if (body.get() == null) {
            body.set(Body.createBody(this));
        }
        return body.get();
    }

    public void destroy() {
        childs.forEach(c -> c.destroy());
        parent.get().childs.remove((Statement) this);
        getBody().destroy();
    }

    public abstract RV calculateResult();

    public abstract StatementType getStatementType();

    public abstract ValueType getReturnType();

    @Override
    public abstract String toString();

    public enum StatementType {
        OPERATOR(Operator.class),
        VALUE(Value.class);

        private static Map<Class<? extends Statement>, StatementType> TYPES;
        private Class<? extends Statement> type;

        private StatementType(Class<? extends Statement> type) {
            this.type = type;
            init();
        }

        private void init() {
            if (TYPES == null) {
                TYPES = new HashMap<>();
            }
            TYPES.put(type, this);
        }

        public StatementType valueOf(Statement statement) {
            return TYPES.get(statement.getClass());
        }
    }

}
