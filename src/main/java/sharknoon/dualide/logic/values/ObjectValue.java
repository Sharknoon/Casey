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
package sharknoon.dualide.logic.values;

import sharknoon.dualide.logic.Statement;
import sharknoon.dualide.logic.items.Class.ObjectType;

/**
 *
 * @author Josua Frank
 * @param <Void>
 */
public class ObjectValue<Void> extends Value<ObjectType> {

    public static ObjectValue createObject(ObjectType type, Statement parent) {
        return new ObjectValue(type, parent);
    }

    public ObjectValue(ObjectType type, Statement parent) {
        super(type, parent);
    }

    @Override
    public Value<ObjectType> calculateResult() {
        return this;
    }

    @Override
    public String toString() {
        return "Value:" + getReturnType().getSimpleName();
    }

}
