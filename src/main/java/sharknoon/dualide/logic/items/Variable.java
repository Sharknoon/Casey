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
package sharknoon.dualide.logic.items;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import sharknoon.dualide.ui.sites.Site;
import sharknoon.dualide.ui.sites.variable.VariableSite;

/**
 *
 * @author Josua Frank
 */
public class Variable extends Item<Variable, Item<? extends Item, ? extends Item, Variable>, Item<? extends Item, Variable, ? extends Item>> {

    private ObjectProperty<Class> clazz = new SimpleObjectProperty<>();
    private BooleanProperty modifiable = new SimpleBooleanProperty(true);

    private Variable() {
        super();
    }

    protected Variable(Item<? extends Item, ? extends Item, Variable> parent, String name) {
        super(parent, name);
    }

    public ObjectProperty<Class> classProperty() {
        return clazz;
    }

    public BooleanProperty modifiableProperty() {
        return modifiable;
    }

}