package sharknoon.dualide.logic.statements.calls;/*
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
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import sharknoon.dualide.logic.ValueReturnable;
import sharknoon.dualide.logic.items.Item;
import sharknoon.dualide.logic.statements.Statement;
import sharknoon.dualide.logic.statements.values.Value;
import sharknoon.dualide.logic.types.Type;
import sharknoon.dualide.utils.javafx.BindUtils;

import java.util.stream.Collectors;

public class Call<I extends Item & ValueReturnable> extends Statement<Type, Type, Type> {
    
    private final ObjectBinding<Statement<Type, Type, Type>> lastChild;
    private final ObjectProperty<Type> returnType = new SimpleObjectProperty<>();
    private final Type expectedReturnType;
    
    public Call(Statement<Type, Type, Type> parent, Item<?, ?, ?> startCall, Type expectedReturnType) {
        super(parent);
        this.expectedReturnType = expectedReturnType;
        lastChild = BindUtils.getLast(childs);
        lastChild.addListener((o, old, new_) -> {
            if (new_ != null) {
                returnType.unbind();
                returnType.bind(new_.returnTypeProperty());
            } else {
                returnType.unbind();
                returnType.set(Type.UNDEFINED);
            }
        });
        addCallItem(startCall);
    }
    
    public void addCallItem(Item<?, ?, ?> item) {
        new CallItem(this, item);
    }
    
    public BooleanExpression isExtensible() {
        return Bindings.createBooleanBinding(() -> {
            Type type = returnType.get();
            return type != null && type.getReturnType().isObject();
        }, returnType);
    }
    
    public BooleanExpression isReducible() {
        return Bindings.size(childs).greaterThan(1);
    }
    
    public ObjectBinding<Statement<Type, Type, Type>> lastChildProperty() {
        return lastChild;
    }
    
    @Override
    public Type getReturnType() {
        return returnTypeProperty().get();
    }
    
    @Override
    public Value<Type> calculateResult() {
        return getReturnType().createEmptyValue(null);
    }
    
    public Type getExpectedReturnType() {
        return expectedReturnType;
    }
    
    public class CallItem<I extends Item<?, ?, ?> & ValueReturnable> extends Statement<Type, Type, Type> {
        
        private final I item;
        
        public CallItem(Statement<Type, Type, Type> parent, I item) {
            super(parent);
            this.item = item;
        }
        
        public I getItem() {
            return item;
        }
        
        @Override
        public Value<Type> calculateResult() {
            return item.getReturnType().createEmptyValue(parentProperty().get());
        }
        
        @Override
        public String toString() {
            return item.toString();
        }
        
        @Override
        public Type getReturnType() {
            return item.getReturnType();
        }
        
        @Override
        public ObjectProperty<Type> returnTypeProperty() {
            return item.returnTypeProperty();
        }
    }
    
    @Override
    public String toString() {
        return childs
                .stream()
                .map(Statement::toString)
                .collect(Collectors.joining(" -> "));
    }
    

    

    
    @Override
    public ObjectProperty<Type> returnTypeProperty() {
        return returnType;
    }
    
    
}
