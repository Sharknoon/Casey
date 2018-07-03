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

import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import sharknoon.dualide.logic.ValueReturnable;
import sharknoon.dualide.logic.items.Item;
import sharknoon.dualide.logic.statements.Statement;
import sharknoon.dualide.logic.statements.values.Value;
import sharknoon.dualide.logic.types.Type;

public class CallItem<I extends Item<?, ?, ?> & ValueReturnable> extends Statement<Type, Type, Type> {
    
    private final I item;
    
    public CallItem(Statement<Type, Type, Type> parent, I item) {
        super(parent, false);
        this.item = item;
        ReadOnlyListProperty<Statement<Type, Type, Type>> childs = parent.childsProperty();
        if (childs.size() == 0 || childs.get(childs.size() - 1) != null) {
            childs.add(this);
        } else {
            childs.set(childs.size() - 1, this);
        }
        item.onDestroy(() -> parent.childsProperty().remove(this));
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