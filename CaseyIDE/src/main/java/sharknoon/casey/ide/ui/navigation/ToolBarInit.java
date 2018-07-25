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
package sharknoon.casey.ide.ui.navigation;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import sharknoon.casey.ide.logic.items.Item;
import sharknoon.casey.ide.logic.items.ItemType;
import sharknoon.casey.ide.logic.items.Project;
import sharknoon.casey.ide.ui.misc.Icon;
import sharknoon.casey.ide.ui.misc.Icons;
import sharknoon.casey.ide.ui.sites.Site;
import sharknoon.casey.ide.utils.language.Language;
import sharknoon.casey.ide.utils.language.Word;

/**
 * @author Josua Frank
 */
public class ToolBarInit {
    
    private static ToolBar toolBar;
    private static BooleanProperty running = new SimpleBooleanProperty();
    
    public static void init(ToolBar buttonBar) {
        toolBar = buttonBar;
        initSaveButton();
        initRunButton();
    }
    
    private static void initSaveButton() {
        Button buttonSave = new Button();
        Icons.set(Icon.SAVE, buttonSave);
        Language.set(Word.TOOLBAR_BUTTON_SAVE_TEXT, buttonSave);
        buttonSave.setOnAction(e -> Project.getCurrentProject().ifPresent(Project::save));
        buttonSave.disableProperty().bind(Project.currentProjectProperty().isNull());
        toolBar.getItems().add(buttonSave);
    }
    
    private static void initRunButton() {
        Button buttonRun = new Button();
        Label labelRunStatus = new Label();
        StringProperty propertyRunStatusText = labelRunStatus.textProperty();
        Icons.set(Icon.RUN, buttonRun);
        Language.set(Word.TOOLBAR_BUTTON_RUN_TEXT, buttonRun);
        buttonRun.setOnAction(e -> {
            running.setValue(true);
            Project.getCurrentProject().ifPresent(p -> p.run(propertyRunStatusText));
            running.setValue(false);
        });
        ObjectProperty<Item<?, ?, ?>> currentSite = Site.currentSelectedProperty();
        BooleanBinding enabledBinding =
                Bindings
                        .createBooleanBinding(
                                () -> currentSite.get() != null && currentSite.get().getType() == ItemType.FUNCTION,
                                currentSite
                        )
                        .and(
                                Bindings.createBooleanBinding(
                                        () -> currentSite.get() != null && currentSite.get().getParent().map(Item::getType).filter(t -> t == ItemType.PACKAGE).isPresent(),
                                        currentSite
                                )
                        )
                        .and(running.not());
        buttonRun.disableProperty().bind(enabledBinding.not());
        toolBar.getItems().addAll(buttonRun, labelRunStatus);
    }
    
}
