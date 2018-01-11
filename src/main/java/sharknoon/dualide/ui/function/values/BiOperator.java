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
package sharknoon.dualide.ui.function.values;

/**
 *
 * @author Josua Frank
 */
abstract class BiOperator<R extends Returnable, A1 extends Returnable, A2 extends Returnable> extends Operator<R> {

    public A1 firstArgument;
    public A2 secondArgument;

    public A1 getFirstArgument() {
        return firstArgument;
    }

    public A2 getSecondArgument() {
        return secondArgument;
    }

    public void setFirstArgument(A1 firstArgument) {
        this.firstArgument = firstArgument;
    }

    public void setSecondArgument(A2 secondArgument) {
        this.secondArgument = secondArgument;
    }

}
