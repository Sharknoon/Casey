package sharknoon.dualide.ui.bodies;
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

import sharknoon.dualide.logic.statements.functions.FunctionCall;
import sharknoon.dualide.logic.types.Type;

import java.util.Collection;

public class FunctionBody extends Body<FunctionCall<Type,Type>> {

    public FunctionBody(FunctionCall<Type, Type> statement) {
        super(statement);
    }

    public FunctionBody(Collection<? extends Type> types) {
        super(types);
    }
}