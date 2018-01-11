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
package sharknoon.dualide.ui;

import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import sharknoon.dualide.logic.Project;
import sharknoon.dualide.utils.language.Language;
import sharknoon.dualide.utils.language.Word;

/**
 *
 * @author Josua Frank
 */
public class ProjectTree {

    public static void update() {
        TreeView view = MainController.getTreeView();
        final TreeItem root;
        if (view.getRoot() == null) {
            root = new TreeItem();
            Language.setCustom(Word.TREE_VIEW_ROOT_ITEM_NAME, s -> root.setValue(s));
            root.setGraphic(GlyphsDude.createIcon(FontAwesomeIcon.ANDROID));
            view.setRoot(root);
        } else {
            root = view.getRoot();
        }
        root.getChildren().clear();
        Project.getAllProjects().forEach((p) -> {
            root.getChildren().add(p.createTreeItem());
        });
        view.setOnMouseClicked((event) -> {

        });
    }

}
