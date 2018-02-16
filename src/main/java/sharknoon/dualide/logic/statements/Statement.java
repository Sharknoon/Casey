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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import sharknoon.dualide.logic.statements.operators.Operator;
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
            if (!(parentProperty().get() instanceof Operator)) {//Operators are managing their childs itself
                parentProperty().get().childs.add((Statement) this);
            }
        }
        childs.addListener((observable, oldValue, newValue) -> {
            onChange();
        });
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
        destroy_impl();
        if (parentProperty().get() != null && !(parentProperty().get() instanceof Operator)) {
            parentProperty().get().childs.remove((Statement) this);
        }
    }

    private void destroy_impl() {
        childs.forEach(c -> {
            if (c != null) {
                c.destroy_impl();
            }
        });
        childs.clear();
        getBody().destroy();
    }

    protected void onChange() {
        changeListeners.forEach(Runnable::run);
        if (parentProperty().get() != null) {
            parentProperty().get().onChange();
        }
    }

    private final List<Runnable> changeListeners = new ArrayList<>();

    public void addChangeListener(Runnable onChange) {
        changeListeners.add(onChange);
    }

    /**
     * You shouldnt need this method, use the resultproperty instead
     *
     * @return
     */
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
