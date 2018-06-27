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

import javafx.beans.binding.ObjectExpression;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.text.Text;
import sharknoon.dualide.logic.statements.Statement;
import sharknoon.dualide.logic.types.Type;
import sharknoon.dualide.ui.misc.Icon;
import sharknoon.dualide.ui.misc.Icons;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author Josua Frank
 */
public class PlaceholderBody extends Body {
    
    public static PlaceholderBody DISABLED = new PlaceholderBody();
    
    public static PlaceholderBody createValuePlaceholderBody(Collection<? extends Type> types, Statement parent) {
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
        return createValuePlaceholderBody((Collection<? extends Type>) null, parent, statementConsumer);
    }
    
    public static PlaceholderBody createValuePlaceholderBody(Type type, Statement parent, Consumer<Statement> statementConsumer) {
        Set<Type> types = new HashSet<>();
        types.add(type);
        return createValuePlaceholderBody(types, parent, statementConsumer);
    }
    
    public static PlaceholderBody createValuePlaceholderBody(Collection<? extends Type> types, Statement parent, Consumer<Statement> statementConsumer) {
        return new PlaceholderBody(types, parent, statementConsumer);
    }
    
    public static PlaceholderBody createValuePlaceholderBody(ObjectExpression<Type> type, Statement parent) {
        return new PlaceholderBody(type, parent, null);
    }
    
    private Consumer<Statement> statementConsumer;
    
    public PlaceholderBody(ObjectExpression<Type> type, Statement parent, Consumer<Statement> statementConsumer) {
        super(type);
        this.statementConsumer = statementConsumer;
        setOnMouseClicked((event) -> {
            ValuePopUpBuilder
                    .create(this, this.statementConsumer)
                    .setParent(parent)
                    .setAllowedTypes(List.of(type.get()))
                    .showValuePopUp();
        });
        setOnMouseEntered((event) -> {
            setContent(Icons.get(Icon.PLUS, 50));
        });
        setOnMouseExited((event) -> {
            setContent();
        });
    }
    
    public PlaceholderBody(Collection<? extends Type> types, Statement parent, Consumer<Statement> statementConsumer) {
        super(types);
        this.statementConsumer = statementConsumer;
        setOnMouseClicked((event) -> {
            ValuePopUpBuilder
                    .create(this, this.statementConsumer)
                    .setParent(parent)
                    .setAllowedTypes(types)
                    .showValuePopUp();
        });
        setOnMouseEntered((event) -> {
            setContent(Icons.get(Icon.PLUS, 50));
        });
        setOnMouseExited((event) -> {
            setContent();
        });
    }
    
    private PlaceholderBody() {
        super((Collection<? extends Type>) null);
        setOnMouseEntered((event) -> {
            setContent(Icons.get(Icon.VOID, 50));
        });
        setOnMouseExited((event) -> {
            setContent();
        });
    }
    
    public void setStatementConsumer(Consumer<Statement> consumer) {
        this.statementConsumer = consumer;
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
    
    @Override
    public ObservableList<Text> toText() {
        return FXCollections.emptyObservableList();
    }
}
