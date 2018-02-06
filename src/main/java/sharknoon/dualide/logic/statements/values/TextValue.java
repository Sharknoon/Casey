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
package sharknoon.dualide.logic.statements.values;

import java.util.Objects;
import sharknoon.dualide.logic.statements.Statement;

/**
 *
 * @author Josua Frank
 */
public final class TextValue extends Value<String, TextValue> {

    private static final String DEFAULT = "";

    public TextValue(Statement parent) {
        this(DEFAULT, parent);
    }

    public TextValue(String value, Statement parent) {
        super(value, parent);
    }

    @Override
    String getDefault() {
        return DEFAULT;
    }

    @Override
    public boolean equals(Value other) {
        return Objects.equals(getValue(), other.getValue());
    }

}
