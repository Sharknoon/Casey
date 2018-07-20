package sharknoon.casey.ide.ui.browsers;/*
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
import javafx.scene.layout.VBox;
import org.controlsfx.control.PopOver;
import sharknoon.casey.ide.logic.items.Class;
import sharknoon.casey.ide.logic.statements.Statement;
import sharknoon.casey.ide.ui.styles.Styles;
import sharknoon.casey.ide.utils.language.Language;
import sharknoon.casey.ide.utils.language.Word;

import java.util.function.Consumer;

public class CallPopUp extends PopOver {
    
    
    CallPopUp(Node ownerNode, Consumer<Statement> statementConsumer, Statement parent, Class allowedItems) {
        super();
        Consumer<Statement> newStatementConsumer = t -> {
            hide();
            if (statementConsumer != null) {
                statementConsumer.accept(t);
            }
        };
        VBox vBoxRoot = new CallBrowser(newStatementConsumer, parent, allowedItems);
        Styles.bindStyleSheets(getRoot().getStylesheets());
        setContentNode(vBoxRoot);
        setArrowLocation(PopOver.ArrowLocation.BOTTOM_CENTER);
        setTitle(Language.get(Word.VALUE_SELECTION_POPUP_TITLE));
        
        show(ownerNode);
    }
    
}
