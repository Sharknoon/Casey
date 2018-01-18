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

import java.io.File;
import java.util.Optional;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import sharknoon.dualide.logic.Item;
import sharknoon.dualide.logic.Welcome;
import sharknoon.dualide.ui.ItemTabPane;
import sharknoon.dualide.ui.ItemTreeView;
import sharknoon.dualide.ui.misc.Icon;
import sharknoon.dualide.ui.sites.Dialogs;
import sharknoon.dualide.utils.language.Language;
import sharknoon.dualide.utils.language.Word;
import sharknoon.dualide.ui.sites.Site;

import sharknoon.dualide.logic.Project;
import sharknoon.dualide.serial.Serialisation;

/**
 *
 * @author Josua Frank
 */
public class WelcomeSite extends Site<Welcome> {

    private final BorderPane borderPaneRoot = new BorderPane();

    {
        GridPane gridPaneContent = new GridPane();
        gridPaneContent.setAlignment(Pos.CENTER);
        gridPaneContent.setMaxSize(1000, 600);
        gridPaneContent.setPadding(new Insets(20));
        gridPaneContent.setHgap(20);

        VBox vBoxRecentProjects = new VBox(20);
        vBoxRecentProjects.setMinHeight(600);

        Text textRecentProjects = new Text();
        Language.setCustom(Word.WELCOMESITE_RECENT_PROJECTS, s -> textRecentProjects.setText(s));
        textRecentProjects.setFont(Font.font(40));
        DropShadow shadowEffect = new DropShadow(10, Color.WHITESMOKE);
        shadowEffect.setSpread(0.5);
        textRecentProjects.setEffect(shadowEffect);

        //TODO Projectname \n last used date
        ScrollPane scrollPaneRecentProjects = new ScrollPane();
        scrollPaneRecentProjects.setFitToHeight(true);
        scrollPaneRecentProjects.setFitToWidth(true);
        vBoxRecentProjects.getChildren().addAll(textRecentProjects, scrollPaneRecentProjects);
        gridPaneContent.addColumn(0, vBoxRecentProjects);

        VBox vBoxProjectButtons = new VBox(20);
        vBoxProjectButtons.setMinHeight(600);

        Button buttonCreateNewProject = createButton(Word.WELCOME_SITE_CREATE_NEW_PROJECT_BUTTON_TEXT, Icon.PLUS, (t) -> {
            Optional<String> name = Dialogs.showTextInputDialog(Dialogs.TextInputs.NEW_PROJECT_DIALOG);
            if (name.isPresent()) {
                Project project = Item.createItem(Project.class, Welcome.getWelcome(), name.get());
                ItemTreeView.selectItem(project);
                ItemTreeView.hideRootItem();
                ItemTabPane.hideRootTab();
            }
        });

        Button buttonLoadProject = createButton(Word.WELCOME_SITE_LOAD_PROJECT_BUTTON_TEXT, Icon.LOAD, (t) -> {
            FileChooser chooser = new FileChooser();
            chooser.setInitialDirectory(new File(System.getProperty("user.home")));//TODO last directory
            chooser.setTitle(Language.get(Word.OPEN_DIALOG_TITLE));
            chooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter(Language.get(Word.SAVE_DIALOG_EXTENSION_FILTER_DUALIDE_PROJECT), "*.dip")
            );
            File file = chooser.showOpenDialog(borderPaneRoot.getScene().getWindow());
            if (file != null) {
                Optional<Project> project = Serialisation.loadProject(file.toPath());
                if (project.isPresent()) {
                    ItemTreeView.selectItem(project.get());
                    ItemTreeView.hideRootItem();
                    ItemTabPane.hideRootTab();
                }
            }
        });

        vBoxProjectButtons.getChildren().addAll(buttonCreateNewProject, buttonLoadProject);
        gridPaneContent.addColumn(1, vBoxProjectButtons);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(65);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(35);
        gridPaneContent.getColumnConstraints().addAll(col1, col2);

        borderPaneRoot.setCenter(gridPaneContent);

    }

    public WelcomeSite(Welcome item) {
        super(item);
    }

    @Override
    public void refresh() {
    }

    @Override
    public Pane getTabContentPane() {
        return borderPaneRoot;
    }

    @Override
    public Icon getTabIcon() {
        return Icon.WELCOME;
    }

}
