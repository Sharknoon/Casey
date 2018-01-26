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
public final class BooleanValue extends Value<Boolean> {

    private static final boolean DEFAULT = false;
    public static final BooleanValue FALSE = new BooleanValue(false);
    public static final BooleanValue TRUE = new BooleanValue(false);

    public BooleanValue() {
        super(DEFAULT);
    }

    public BooleanValue(boolean value) {
        super(value);
    }

    @Override
    Boolean getDefault() {
        return DEFAULT;
    }

    @Override
    public boolean equals(Value other) {
        return Objects.equals(getValue(), other.getValue());
    }

}
