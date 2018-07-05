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

import io.reactivex.rxjavafx.observables.JavaFxObservable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import sharknoon.dualide.logic.ValueReturnable;
import sharknoon.dualide.logic.items.Item;
import sharknoon.dualide.logic.items.ItemType;
import sharknoon.dualide.logic.items.Parameter;
import sharknoon.dualide.logic.statements.Statement;
import sharknoon.dualide.logic.statements.values.Value;
import sharknoon.dualide.logic.types.Type;
import sharknoon.dualide.utils.javafx.BindUtils;

public class CallItem<I extends Item<Item, Item, Item> & ValueReturnable> extends Statement<Type, Type, Type> {
    
    private final I item;
    //Only for Funktions
    private final ObservableList<Item> parameter;
    
    public CallItem(Statement<Type, Type, Type> parent, I item) {
        super(parent, false);
        this.item = item;
        addNecessaryParameters();
        addToParent(parent);
        item.onDestroy(parent::destroy);
        parameter = item.getChildren().filtered(i -> i.getType() == ItemType.PARAMETER);
    }
    
    private void addToParent(Statement<Type, Type, Type> parent) {
        ReadOnlyListProperty<Statement<Type, Type, Type>> childs = parent.childsProperty();
        if (childs.size() == 0 || childs.get(childs.size() - 1) != null) {
            childs.add(this);
        } else {
            childs.set(childs.size() - 1, this);
        }
    }
    
    private void addNecessaryParameters() {
        BindUtils.addListener(item.getChildren(), c -> {
            item.getChildren().stream()
                    .filter(i -> i instanceof Parameter)
                    .forEach(p -> childs.add(null));
        });
    }
    
    public ObjectProperty<Type> getReturnTypePropertyForIndex(int index) {
        Parameter parameter = (Parameter) this.parameter.get(index);
        if (parameter != null){
            return parameter.returnTypeProperty();
        }
        return new SimpleObjectProperty<>();
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
    public ReadOnlyObjectProperty<Type> returnTypeProperty() {
        return item.returnTypeProperty();
    }
}