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
package sharknoon.dualide.ui.sites.welcome;

import java.util.Optional;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextInputDialog;
import sharknoon.dualide.logic.Project;
import sharknoon.dualide.logic.Welcome;
import sharknoon.dualide.utils.language.Language;
import sharknoon.dualide.utils.language.Word;

/**
 *
 * @author Josua Frank
 */
public class NewProjectDialog {

    private final Dialog<String> dialog = new TextInputDialog();

    public Optional<Project> show() {
        dialog.setTitle(Language.get(Word.NEW_PROJECT_DIALOG_TITLE));
        dialog.setHeaderText(Language.get(Word.NEW_PROJECT_DIALOG_HEADER_TEXT));
        dialog.setContentText(Language.get(Word.NEW_PROJECT_DIALOG_CONTENT_TEXT));
        Optional<String> name = dialog.showAndWait();
        if (!name.isPresent()) {
            return Optional.empty();
        }
        return Optional.of(new Project(Welcome.getWelcome(), name.get()));
    }

}
