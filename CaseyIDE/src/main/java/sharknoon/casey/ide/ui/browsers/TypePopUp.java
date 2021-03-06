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
package sharknoon.casey.ide.ui.browsers;

import javafx.scene.Node;
import javafx.scene.layout.VBox;
import org.controlsfx.control.PopOver;
import sharknoon.casey.ide.logic.types.Type;
import sharknoon.casey.ide.ui.styles.Styles;
import sharknoon.casey.ide.utils.language.Language;
import sharknoon.casey.ide.utils.language.Word;

import java.util.Collection;
import java.util.function.Consumer;

/**
 * @author frank
 */
public class TypePopUp extends PopOver {
    
    public static void showTypeSelectionPopUp(Node ownerNode, Consumer<Type> typeConsumer) {
        showTypeSelectionPopUp(ownerNode, typeConsumer, null);
    }
    
    public static void showTypeSelectionPopUp(Node ownerNode, Consumer<Type> typeConsumer, Collection<? extends Type> allowedTypes) {
        new TypePopUp(ownerNode, typeConsumer, allowedTypes, false);
    }
    
    public static void showTypeSelectionPopUp(Node ownerNode, Consumer<Type> typeConsumer, Collection<? extends Type> allowedTypes, boolean withVoid) {
        new TypePopUp(ownerNode, typeConsumer, allowedTypes, withVoid);
    }
    
    private TypePopUp(Node ownerNode, Consumer<Type> typeConsumer, Collection<? extends Type> allowedTypes, boolean withVoid) {
        super();
        Consumer<Type> newTypeConsumer = t -> {
            hide();
            if (typeConsumer != null) {
                typeConsumer.accept(t);
            }
        };
        VBox vBoxRoot = withVoid ? TypeBrowser.createTypeBrowserWithVoid(newTypeConsumer, allowedTypes) : TypeBrowser.createTypeBrowser(newTypeConsumer, allowedTypes);
        Styles.bindStyleSheets(getRoot().getStylesheets());
        setContentNode(vBoxRoot);
        setArrowLocation(PopOver.ArrowLocation.BOTTOM_CENTER);
        setTitle(Language.get(Word.TYPE_SELECTION_POPUP_TITLE));
        
        show(ownerNode);
    }
    
}
