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
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.effect.Bloom;
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
import javafx.stage.Window;
import org.hildan.fxgson.FxGson;
import sharknoon.dualide.logic.Project;
import sharknoon.dualide.logic.Welcome;
import sharknoon.dualide.serial.ClassTypeAdapter;
import sharknoon.dualide.ui.ItemTabPane;
import sharknoon.dualide.ui.ItemTreeView;
import sharknoon.dualide.ui.misc.Icon;
import sharknoon.dualide.ui.misc.Icons;
import sharknoon.dualide.ui.sites.Dialogs;
import sharknoon.dualide.utils.language.Language;
import sharknoon.dualide.utils.language.Word;
import sharknoon.dualide.ui.sites.Site;

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
        vBoxRecentProjects.getChildren().add(textRecentProjects);

        //TODO Projectname \n last used date
        gridPaneContent.addColumn(0, vBoxRecentProjects);

        VBox vBoxProjectButtons = new VBox(20);
        vBoxProjectButtons.setMinHeight(600);

        Button buttonCreateNewProject = new Button();
        Icons.set(buttonCreateNewProject, Icon.PLUS);
        Language.set(Word.WELCOME_SITE_CREATE_NEW_PROJECT_BUTTON_TEXT, buttonCreateNewProject);
        buttonCreateNewProject.setOnAction((event) -> {
            Optional<String> name = Dialogs.showTextInputDialog(Dialogs.TextInputs.NEW_PROJECT_DIALOG);
            if (name.isPresent()) {
                Project project = new Project(Welcome.getWelcome(), name.get());
                ItemTreeView.selectItem(project);
                ItemTreeView.hideRootItem();
                ItemTabPane.hideRootTab();
            }
        });
        vBoxProjectButtons.getChildren().add(buttonCreateNewProject);

        Button buttonLoadProject = new Button();
        Icons.set(buttonLoadProject, Icon.LOAD);
        Language.set(Word.WELCOME_SITE_LOAD_PROJECT_BUTTON_TEXT, buttonLoadProject);
        buttonLoadProject.setOnAction((event) -> {
            FileChooser chooser = new FileChooser();
            chooser.setInitialDirectory(new File(System.getProperty("user.home")));//TODO last directory
            chooser.setTitle(Language.get(Word.OPEN_DIALOG_TITLE));
            chooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter(Language.get(Word.SAVE_DIALOG_EXTENSION_FILTER_DUALIDE_PROJECT), "*.dip")
            );
            File file = chooser.showOpenDialog(Window.impl_getWindows().next());
            if (file != null) {
                try {
                    Project project = FxGson
                            .fullBuilder()
                            .setPrettyPrinting()
                            .registerTypeHierarchyAdapter(sharknoon.dualide.logic.Class.class, new ClassTypeAdapter())
                            .create()
                            .fromJson(Files.lines(file.toPath()).collect(Collectors.joining("\n")), Project.class);
                    System.out.println(project);
                } catch (IOException ex) {
                    Logger.getLogger(WelcomeSite.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        vBoxProjectButtons.getChildren().add(buttonLoadProject);

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
    public Pane getTabContentPane() {
        return borderPaneRoot;
    }

    @Override
    public Icon getTabIcon() {
        return Icon.WELCOME;
    }

}
