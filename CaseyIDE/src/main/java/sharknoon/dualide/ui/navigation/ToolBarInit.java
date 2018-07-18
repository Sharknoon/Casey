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
package sharknoon.dualide.ui.navigation;

import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import sharknoon.dualide.logic.items.Project;
import sharknoon.dualide.ui.misc.Icon;
import sharknoon.dualide.ui.misc.Icons;
import sharknoon.dualide.utils.language.Language;
import sharknoon.dualide.utils.language.Word;

/**
 * @author Josua Frank
 */
public class ToolBarInit {
    
    private static ToolBar toolBar;
    
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
        toolBar.getItems().add(buttonSave);
    }
    
    private static void initRunButton() {
        Button buttonRun = new Button();
        Icons.set(Icon.RUN, buttonRun);
        Language.set(Word.TOOLBAR_BUTTON_RUN_TEXT, buttonRun);
        buttonRun.setOnAction(e -> {
        });
        toolBar.getItems().add(buttonRun);
    }
    
}
