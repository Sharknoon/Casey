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
import javafx.beans.binding.IntegerBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sharknoon.dualide.logic.ValueReturnable;
import sharknoon.dualide.logic.statements.Statement;
import sharknoon.dualide.logic.statements.values.Value;
import sharknoon.dualide.logic.types.PrimitiveType;
import sharknoon.dualide.logic.types.Type;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class Call<VR extends ValueReturnable<Type>> extends Statement<Type, Type, Type> {
    
    private final ObservableList<VR> calls = FXCollections.observableArrayList();
    private final ObjectProperty<Type> returnType = new SimpleObjectProperty<>();
    
    public Call(Statement<Type, Type, Type> parent, VR startCall) {
        super(parent);
        calls.add(startCall);
        IntegerBinding lastIndexBinding = Bindings.size(calls).subtract(1);
        ObjectBinding<VR> lastCallBinding = Bindings.valueAt(calls, lastIndexBinding);
        Callable<Type> returnTypeCallable = () -> lastCallBinding.get().getReturnType();
        ObjectBinding<Type> lastCallReturnTypeBinding = Bindings.createObjectBinding(returnTypeCallable, lastCallBinding);
        returnType.bind(Bindings
                .when(
                        Bindings.isEmpty(calls)
                ).then(
                        (Type) null
                ).otherwise(
                        lastCallReturnTypeBinding
                )
        );
    }
    
    public List<VR> getCalls() {
        return calls;
    }
    
    @Override
    public Value<Type> calculateResult() {
        return (Value) PrimitiveType.VOID.createValue(parentProperty().get()).get();
    }
    
    @Override
    public String toString() {
        return calls.stream().map(v -> v.getReturnType().getLanguageDependentName().get()).collect(Collectors.joining(" -> "));
    }
    
    @Override
    public Type getReturnType() {
        if (calls.isEmpty()) {
            return null;
        }
        return calls.get(calls.size() - 1).getReturnType();
    }
    
    @Override
    public ObjectProperty returnTypeProperty() {
        return returnType;
    }
}
