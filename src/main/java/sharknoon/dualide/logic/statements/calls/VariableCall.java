package sharknoon.dualide.logic.statements.calls;
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

import sharknoon.dualide.logic.items.Variable;
import sharknoon.dualide.logic.statements.Statement;
import sharknoon.dualide.logic.statements.values.Value;
import sharknoon.dualide.logic.types.Type;

public class VariableCall extends Call<Variable> {
    
    private final Variable variable;
    
    public VariableCall(Statement<Type, Type, Type> parent, Variable startCall, Type expectedReturnType) {
        super(parent, startCall, expectedReturnType);
        this.variable = startCall;
    }
    
    @Override
    public Value<Type> calculateResult() {
        return variable.getReturnType().createEmptyValue(parentProperty().get());
    }
}
