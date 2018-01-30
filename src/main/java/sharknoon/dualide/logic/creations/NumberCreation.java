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
package sharknoon.dualide.logic.creations;

import java.util.Optional;
import sharknoon.dualide.logic.values.NumberValue;
import sharknoon.dualide.ui.dialogs.Dialogs;

/**
 *
 * @author Josua Frank
 */
public class NumberCreation extends Creation<NumberValue> {

    @Override
    public NumberValue create() {
        Optional<NumberValue> dialogResult = Dialogs
                .showNumberInputDialog(Dialogs.NumberInputs.NEW_NUMBER_VALUE)
                .map(d -> new NumberValue(d));
        return dialogResult.isPresent()
                ? dialogResult.get()
                : new NumberValue();
    }

}
