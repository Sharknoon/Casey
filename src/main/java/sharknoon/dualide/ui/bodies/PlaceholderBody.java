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
package sharknoon.dualide.ui.bodies;

import java.util.Set;
import java.util.function.Consumer;
import javafx.scene.shape.Shape;
import sharknoon.dualide.logic.statements.Statement;
import sharknoon.dualide.logic.statements.operations.Operator;
import sharknoon.dualide.logic.statements.values.Value;
import sharknoon.dualide.logic.statements.values.ValueType;
import sharknoon.dualide.ui.misc.Icon;
import sharknoon.dualide.ui.misc.Icons;

/**
 *
 * @author Josua Frank
 */
public class PlaceholderBody extends Body {

    public static PlaceholderBody createValuePlaceholderBody(Set<ValueType> types, Statement parent, Consumer<Value> valueConsumer, Consumer<Operator> operatorConsumer) {
        return new PlaceholderBody(types, parent, valueConsumer, operatorConsumer);
    }

    public PlaceholderBody(Set<ValueType> types, Statement parent, Consumer<Value> valueConsumer, Consumer<Operator> operatorConsumer) {
        super(types);
        setOnMouseClicked((event) -> {
            StatementPopUp.showValueSelectionPopUp(this, types, parent, valueConsumer, operatorConsumer);
        });
        setOnMouseEntered((event) -> {
            setContent(Icons.get(Icon.PLUS, 50));
        });
        setOnMouseDragExited((event) -> {
            System.out.println("exited!!!!!");
            setContent();
        });
    }
}
