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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sharknoon.dualide.logic.ValueReturnable;
import sharknoon.dualide.logic.statements.Statement;
import sharknoon.dualide.logic.types.Type;
import sharknoon.dualide.utils.javafx.BindUtils;

import java.util.stream.Collectors;

public abstract class Call<VR extends ValueReturnable<Type>> extends Statement<Type, Type, Type> {
    
    private final ObservableList<ValueReturnable<Type>> calls = FXCollections.observableArrayList();
    private final ObjectBinding<ValueReturnable<Type>> lastCall;
    private final ObjectProperty<Type> returnType = new SimpleObjectProperty<>();
    
    public Call(Statement<Type, Type, Type> parent, VR startCall) {
        super(parent);
        lastCall = BindUtils.getLast(calls);
        lastCall.addListener((o, old, new_) -> {
            if (new_ != null) {
                returnType.unbind();
                returnType.bind(new_.returnTypeProperty());
            } else {
                returnType.unbind();
                returnType.set(null);
            }
        });
        
        calls.add(startCall);
    }
    
    public ObservableList<ValueReturnable<Type>> getCalls() {
        return calls;
    }
    
    public ValueReturnable<Type> getLastCall() {
        return lastCall.get();
    }
    
    public ObjectBinding<ValueReturnable<Type>> lastCallProperty() {
        return lastCall;
    }
    
    public BooleanExpression isExtensible() {
        return Bindings.createBooleanBinding(() -> {
            Type type = returnType.get();
            return type != null && type.getReturnType().isObject();
        }, returnType);
    }
    
    public BooleanExpression isReducible() {
        return Bindings.size(calls).greaterThan(1);
    }
    
    @Override
    public String toString() {
        return calls.stream().map(v -> v.getReturnType().getLanguageDependentName().get()).collect(Collectors.joining(" -> "));
    }
    
    @Override
    public Type getReturnType() {
        return returnTypeProperty().get();
    }
    
    @Override
    public ObjectProperty<Type> returnTypeProperty() {
        return returnType;
    }
    
    
}
