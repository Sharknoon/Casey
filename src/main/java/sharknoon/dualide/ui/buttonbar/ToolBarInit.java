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
package sharknoon.dualide.ui.buttonbar;

import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import sharknoon.dualide.logic.Project;
import sharknoon.dualide.ui.misc.Icon;
import sharknoon.dualide.ui.misc.Icons;
import sharknoon.dualide.utils.language.Language;
import sharknoon.dualide.utils.language.Word;

/**
 *
 * @author Josua Frank
 */
public class ToolBarInit {

    private static ToolBar buttonbar;

    public static void init(ToolBar buttonBar) {
        buttonbar = buttonBar;
        initSaveButton();
    }

    public static void initSaveButton() {
        Button buttonSave = new Button();
        Icons.set(buttonSave, Icon.SAVE);
        Language.set(Word.TOOLBAR_BUTTON_SAVE_TEXT, buttonSave);
        buttonSave.setOnAction(e -> Project.getCurrentProject().ifPresent(Project::save));
        buttonbar.getItems().add(buttonSave);
    }

}