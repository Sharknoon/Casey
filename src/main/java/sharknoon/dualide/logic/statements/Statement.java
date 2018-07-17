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

import com.fasterxml.jackson.databind.JsonNode;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sharknoon.dualide.logic.ValueReturnable;
import sharknoon.dualide.logic.statements.operators.Operator;
import sharknoon.dualide.logic.statements.values.Value;
import sharknoon.dualide.logic.types.Type;
import sharknoon.dualide.ui.bodies.Body;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The statement is the base class for values, operators, ...
 *
 * @param <PT> The parent type of this statement
 * @param <T>  The type of this statement
 * @param <CT> The child type of this statement
 * @author Josua Frank
 */
public abstract class Statement<PT extends Type, T extends Type, CT extends Type> implements ValueReturnable<T> {
    
    protected final ReadOnlyListWrapper<Statement<T, CT, Type>> childs = new ReadOnlyListWrapper<>(FXCollections.observableArrayList());
    private final transient ReadOnlyObjectWrapper<Statement<Type, PT, T>> parent = new ReadOnlyObjectWrapper<>();
    private final ReadOnlyObjectWrapper<Body<Statement>> body = new ReadOnlyObjectWrapper<>();
    private final List<Runnable> changeListeners = new ArrayList<>();
    
    public void initParent(Statement<Type, PT, T> parent, boolean addChildToParent) {
        if (parent != null) {
            this.parent.set(parent);
            if ((parentProperty().get() instanceof Value) && addChildToParent) {//Operators are managing their childs itself
                parentProperty().get().childs.add((Statement) this);
            }
        }
        childs.addListener((observable, oldValue, newValue) -> onChange());
    }
    
    public ReadOnlyObjectProperty<Statement<Type, PT, T>> parentProperty() {
        return parent.getReadOnlyProperty();
    }
    
    public ListProperty<Statement<T, CT, Type>> childsProperty() {
        return childs;
    }
    
    public ObservableList<Statement<T, CT, Type>> getChilds() {
        return childsProperty();
    }
    
    public Body<Statement> getBody() {
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
        Body body = getBody();
        if (body != null) {
            body.destroy();
        }
    }
    
    protected void onChange() {
        changeListeners.forEach(Runnable::run);
        if (parentProperty().get() != null) {
            parentProperty().get().onChange();
        }
    }
    
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
    
    public abstract Map<String, JsonNode> getAdditionalProperties();
    
    //to be overridden
    public void setAdditionalProperties(Map<String, JsonNode> properties) {
    
    }
    
}
