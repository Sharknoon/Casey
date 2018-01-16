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
package sharknoon.dualide.ui.sites;

import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.web.HTMLEditor;

/**
 *
 * @author frank
 */
public class TextEditorDialog extends Dialog<String>{
    
        private final GridPane grid;
        private final HTMLEditor editor;
    private final String defaultValue;

    public TextEditorDialog(GridPane grid, HTMLEditor editor, String defaultValue) {
        this.grid = grid;
        this.editor = editor;
        this.defaultValue = defaultValue;
    }
    
    
    
}
