package sharknoon.dualide.ui.browsers;/*
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

import javafx.scene.Node;
import sharknoon.dualide.logic.items.Class;
import sharknoon.dualide.logic.statements.Statement;

import java.util.function.Consumer;

public class CallPopupBuilder {
    
    
    public static CallPopupBuilder create(Node ownerNode, Consumer<Statement> statementConsumer) {
        return new CallPopupBuilder(ownerNode, statementConsumer);
    }
    
    private final Node ownerNode;
    private final Consumer<Statement> statementConsumer;
    private Statement parent = null;
    private Class allowedItems = null;
    
    public CallPopupBuilder(Node ownerNode, Consumer<Statement> statementConsumer) {
        this.ownerNode = ownerNode;
        this.statementConsumer = statementConsumer;
    }
    
    public CallPopupBuilder setParent(Statement parent) {
        this.parent = parent;
        return this;
    }
    
    public CallPopupBuilder setAllowedItems(Class allowedItems) {
        this.allowedItems = allowedItems;
        return this;
    }
    
    
    public CallPopUp showCallPopUp() {
        return new CallPopUp(ownerNode, statementConsumer, parent, allowedItems);
    }
    
}
