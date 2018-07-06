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

import sharknoon.dualide.logic.items.Parameter;
import sharknoon.dualide.logic.statements.Statement;
import sharknoon.dualide.logic.statements.values.Value;
import sharknoon.dualide.logic.types.Type;

public class ParameterCall extends Call<Parameter> {
    
    private final Parameter parameter;
    
    public ParameterCall(Statement<Type, Type, Type> parent, Parameter startCall, Type expectedReturnType) {
        super(parent, startCall, expectedReturnType);
        this.parameter = startCall;
    }
    
    @Override
    public Value<Type> calculateResult() {
        return parameter.getReturnType().createEmptyValue(parentProperty().get());
    }
}
