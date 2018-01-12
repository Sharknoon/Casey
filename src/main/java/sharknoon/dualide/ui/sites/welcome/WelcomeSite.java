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

import com.sun.org.apache.xml.internal.security.Init;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import java.util.Optional;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import org.controlsfx.glyphfont.FontAwesome;
import sharknoon.dualide.logic.Item;
import sharknoon.dualide.logic.Project;
import sharknoon.dualide.logic.Welcome;
import sharknoon.dualide.ui.ItemTreeView;
import sharknoon.dualide.utils.language.Language;
import sharknoon.dualide.utils.language.Word;
import sharknoon.dualide.ui.sites.Site;
import sharknoon.dualide.utils.javafx.RecursiveTreeItem;

/**
 *
 * @author Josua Frank
 */
public class WelcomeSite extends Site<Welcome> {

    private final BorderPane borderPaneRoot = new BorderPane();

    {
        GridPane gridPaneContent = new GridPane();
        gridPaneContent.addColumn(0);

        Button buttonCreateNewProject = new Button();
        GlyphsDude.setIcon(buttonCreateNewProject, FontAwesomeIcon.PLUS);
        Language.set(Word.WELCOME_SITE_CREATE_NEW_PROJECT_BUTTON_TEXT, buttonCreateNewProject);
        buttonCreateNewProject.setOnAction((event) -> {
            Optional<Project> project = new NewProjectDialog().show();
            if (project.isPresent()) {
                ItemTreeView.update();
                ItemTreeView.selectItem(project.get().getSite());
            }
        });

        Button buttonLoadProject = new Button();
        GlyphsDude.setIcon(buttonLoadProject, FontAwesomeIcon.FOLDER_OPEN/*, "6em"*/);
        Language.set(Word.WELCOME_SITE_LOAD_PROJECT_BUTTON_TEXT, buttonLoadProject);

        gridPaneContent.addColumn(1, buttonCreateNewProject, buttonLoadProject);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        gridPaneContent.getColumnConstraints().addAll(col1, col2);

        borderPaneRoot.setCenter(gridPaneContent);

    }

    public WelcomeSite(Welcome item) {
        super(item);
    }

    @Override
    public Pane getTabContentPane() {
        return borderPaneRoot;
    }

    @Override
    public String getTabName() {
        return Language.get(Word.WELCOME_SITE_TAB_TITLE);
    }

    @Override
    public Node getTabIcon() {
        return GlyphsDude.createIcon(FontAwesomeIcon.HAND_ALT_UP);
    }

}
