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

import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import sharknoon.dualide.logic.ValueReturnable;
import sharknoon.dualide.logic.statements.operators.Operator;
import sharknoon.dualide.logic.statements.values.Value;
import sharknoon.dualide.logic.types.Type;
import sharknoon.dualide.ui.bodies.Body;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The statement is the base class for values, operators, ...
 *
 * @author Josua Frank
 * @param <PT> The parent type of this statement
 * @param <T> The type of this statement
 * @param <CT> The child type of this statement
 */
public abstract class Statement<PT extends Type, T extends Type, CT extends Type> implements ValueReturnable<T> {

    private final transient ReadOnlyObjectWrapper<Statement<Type, Type, T>> parent = new ReadOnlyObjectWrapper<>();
    protected final ReadOnlyListWrapper<Statement<T, CT, Type>> childs = new ReadOnlyListWrapper<>(FXCollections.observableArrayList());
    private final ReadOnlyObjectWrapper<Body> body = new ReadOnlyObjectWrapper<>();

    public Statement(Statement<Type, Type, T> parent) {
        if (parent != null) {
            this.parent.set(parent);
            if (!(parentProperty().get() instanceof Operator)) {//Operators are managing their childs itself
                parentProperty().get().childs.add((Statement) this);
            }
        }
        childs.addListener((observable, oldValue, newValue) -> onChange());
    }

    public ReadOnlyObjectProperty<Statement<Type, Type, T>> parentProperty() {
        return parent.getReadOnlyProperty();
    }

    public ReadOnlyListProperty<Statement<T, CT, Type>> childsProperty() {
        return childs.getReadOnlyProperty();
    }

    public List<Statement<T, CT, Type>> getChilds(){
        return Collections.unmodifiableList(childs);
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
            parentProperty().get().childs.remove(this);
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
    public abstract Value<T> calculateResult();

    @Override
    public abstract String toString();

}
