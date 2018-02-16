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

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import sharknoon.dualide.logic.statements.Statement;
import sharknoon.dualide.logic.statements.values.ValueType;
import sharknoon.dualide.ui.misc.Icon;
import sharknoon.dualide.ui.misc.Icons;

/**
 *
 * @author Josua Frank
 */
public class PlaceholderBody extends Body {
    
    private Consumer<Statement> statementConsumer;

    public static PlaceholderBody createValuePlaceholderBody(Set<ValueType> types, Statement parent) {
        return createValuePlaceholderBody(types, parent, null);
    }
    
    /**
     * all types
     *
     * @param parent
     * @param statementConsumer
     * @return
     */
    public static PlaceholderBody createValuePlaceholderBody(Statement parent, Consumer<Statement> statementConsumer) {
        return new PlaceholderBody(EnumSet.allOf(ValueType.class), parent, statementConsumer);
    }

    public static PlaceholderBody createValuePlaceholderBody(ValueType type, Statement parent, Consumer<Statement> statementConsumer) {
        Set<ValueType> types = new HashSet<>();
        types.add(type);
        return new PlaceholderBody(types, parent, statementConsumer);
    }

    public static PlaceholderBody createValuePlaceholderBody(Set<ValueType> types, Statement parent, Consumer<Statement> statementConsumer) {
        return new PlaceholderBody(types, parent, statementConsumer);
    }

    public PlaceholderBody(Set<ValueType> types, Statement parent, Consumer<Statement> statementConsumer) {
        super(types);
        this.statementConsumer = statementConsumer;
        setOnMouseClicked((event) -> {
            StatementPopUp.showValueSelectionPopUp(this, types, parent, this.statementConsumer);
        });
        setOnMouseEntered((event) -> {
            setContent(Icons.get(Icon.PLUS, 50));
        });
        setOnMouseExited((event) -> {
            setContent();
        });
    }

    public void setStatementConsumer(Consumer<Statement> consumer){
        this.statementConsumer = consumer;
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    
    
}
