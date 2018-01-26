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

import java.util.Objects;

/**
 *
 * @author Josua Frank
 */
public final class NumberValue extends Value<Double> {

    private static final double DEFAULT = 0.0;

    public NumberValue() {
        super(DEFAULT);
    }

    public NumberValue(double value) {
        super(value);
    }

    @Override
    Double getDefault() {
        return DEFAULT;
    }

    @Override
    public boolean equals(Value other) {
        return Objects.equals(getValue(), other.getValue());
    }

}
