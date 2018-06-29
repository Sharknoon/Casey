package sharknoon.dualide.logic.statements.calls;

import sharknoon.dualide.logic.items.Function;
import sharknoon.dualide.logic.statements.Statement;
import sharknoon.dualide.logic.statements.values.Value;
import sharknoon.dualide.logic.types.Type;

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
public class FunctionCall extends Call<Function> {
    
    private final Function function;
    
    public FunctionCall(Statement<Type, Type, Type> parent, Function startCall, Type expectedReturnType) {
        super(parent, startCall, expectedReturnType);
        this.function = startCall;
    }
    
    @Override
    public Value<Type> calculateResult() {
        return function.getReturnType().createEmptyValue(parentProperty().get());
    }
    
    
}
