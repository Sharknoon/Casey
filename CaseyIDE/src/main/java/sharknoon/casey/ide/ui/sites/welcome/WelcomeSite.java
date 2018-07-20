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
package sharknoon.casey.ide.ui.sites.welcome;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import sharknoon.casey.ide.logic.items.Item;
import sharknoon.casey.ide.logic.items.ItemType;
import sharknoon.casey.ide.logic.items.Project;
import sharknoon.casey.ide.logic.items.Welcome;
import sharknoon.casey.ide.serial.Serialisation;
import sharknoon.casey.ide.ui.dialogs.Dialogs;
import sharknoon.casey.ide.ui.misc.Icon;
import sharknoon.casey.ide.ui.sites.Site;
import sharknoon.casey.ide.ui.sites.SiteUtils;
import sharknoon.casey.ide.ui.styles.StyleClasses;
import sharknoon.casey.ide.utils.language.Language;
import sharknoon.casey.ide.utils.language.Word;
import sharknoon.casey.ide.utils.settings.Props;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 *
 * @author Josua Frank
 */
public class WelcomeSite extends Site<Welcome> {

    private BorderPane borderPaneRoot;
    private ScrollPane scrollPaneRecentProjects;
    private static final String lastDirectoryKey = "lastProjectDirectory";
    private static Optional<String> lastDirectory = Optional.empty();

    private static final ObjectProperty<Icon> icon = new SimpleObjectProperty<>(Icon.WELCOME);

    private void init() {
        borderPaneRoot = new BorderPane();
        scrollPaneRecentProjects = new ScrollPane();
        GridPane gridPaneContent = new GridPane();
        gridPaneContent.setAlignment(Pos.CENTER);
        gridPaneContent.setMaxSize(1000, 600);
        gridPaneContent.setPadding(new Insets(20));
        gridPaneContent.setHgap(20);

        VBox vBoxRecentProjects = new VBox(20);
        vBoxRecentProjects.setMinHeight(600);

        Text textRecentProjects = new Text();
        Language.setCustom(Word.WELCOMESITE_RECENT_PROJECTS, textRecentProjects::setText);
        textRecentProjects.getStyleClass().add(StyleClasses.textWelcomeSiteRecentlyUsed.name());

        RecentProject.recentProjectsProperty().addListener((observable, oldValue, newValue) -> {
            refreshRecentProjects();
        });
        refreshRecentProjects();

        scrollPaneRecentProjects.setFitToHeight(true);
        scrollPaneRecentProjects.setFitToWidth(true);
        vBoxRecentProjects.getChildren().addAll(textRecentProjects, scrollPaneRecentProjects);
        gridPaneContent.addColumn(0, vBoxRecentProjects);

        VBox vBoxProjectButtons = new VBox(20);
        vBoxProjectButtons.setMinHeight(600);

        Button buttonCreateNewProject = SiteUtils.createButton(Word.WELCOME_SITE_CREATE_NEW_PROJECT_BUTTON_TEXT, Icon.PLUS, (t) -> {
            Optional<String> name = Dialogs.showTextInputDialog(Dialogs.TextInputs.NEW_PROJECT_DIALOG);
            name.ifPresent(this::createProject);
        });

        Props.get(lastDirectoryKey).thenAccept(o -> lastDirectory = o);

        Button buttonLoadProject = SiteUtils.createButton(Word.WELCOME_SITE_LOAD_PROJECT_BUTTON_TEXT, Icon.LOAD, (t) -> {
            FileChooser chooser = new FileChooser();
            if (lastDirectory.isPresent() && Files.exists(Paths.get(lastDirectory.get()))) {
                chooser.setInitialDirectory(new File(lastDirectory.get()));
            } else {
                chooser.setInitialDirectory(new File(System.getProperty("user.home")));
            }
            chooser.setTitle(Language.get(Word.OPEN_DIALOG_TITLE));
            chooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter(Language.get(Word.SAVE_DIALOG_EXTENSION_FILTER_CASEY_PROJECT), "*.casey")
            );
            File file = chooser.showOpenDialog(borderPaneRoot.getScene().getWindow());
            if (file != null) {
                lastDirectory = Optional.of(file.getParentFile().getAbsolutePath());
                Props.set(lastDirectoryKey, lastDirectory.get());
                loadProject(file.toPath());
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

    private void refreshRecentProjects() {
        VBox vBoxLastProjects = new VBox(10);
        RecentProject.getAllProjects()
                .thenAccept((rpc) -> {
                    rpc
                            .stream()
                            .sorted((p1, p2) -> p2.getTime().compareTo(p1.getTime()))
                            .forEach((lastProject) -> {
                                VBox vBoxLastProject = new VBox(10);
                                vBoxLastProject.setPadding(new Insets(5));

                                Text textRecentProjectName = new Text();
                                textRecentProjectName.getStyleClass().add(StyleClasses.textWelcomeSiteProjectListTitle.name());
                                textRecentProjectName.setText(lastProject.getName());

                                Text textRecentProjectDate = new Text();
                                textRecentProjectDate.getStyleClass().add(StyleClasses.textWelcomeSiteProjectListDate.name());
                                textRecentProjectDate.setText(lastProject.getTime().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)));

                                vBoxLastProject.getChildren().addAll(textRecentProjectName, textRecentProjectDate);

                                vBoxLastProject.setOnMouseClicked((event) -> {
                                    String pathString = lastProject.getPath();
                                    if (pathString.isEmpty()) {
                                        RecentProject.removeProject(lastProject);
                                        return;
                                    }
                                    Path pathToFile = Paths.get(lastProject.getPath());
                                    if (Files.exists(pathToFile)) {
                                        loadProject(pathToFile);
                                    } else {
                                        RecentProject.removeProject(lastProject);
                                    }
                                });

                                vBoxLastProjects.getChildren().add(vBoxLastProject);
                            });
                }).thenRun(() -> {
            Platform.runLater(() -> {
                scrollPaneRecentProjects.setContent(vBoxLastProjects);
            });
        });
    }

    private static void loadProject(Path path) {
        Optional<Project> project = Serialisation.loadProject(path);
        if (project.isPresent()) {
            RecentProject.updateProject(project.get());
            project.get().getSite().select();
        }
    }

    private void createProject(String name) {
        Project project = Item.createItem(ItemType.PROJECT, null, name);
        RecentProject.updateProject(project);
        project.getSite().select();
    }

    public WelcomeSite(Welcome item) {
        super(item);
    }

    @Override
    public CompletableFuture<Node> getTabContentPane() {
        return CompletableFuture.supplyAsync(() -> {
            if (borderPaneRoot == null) {
                init();
            }
            return borderPaneRoot;
        });
    }

    @Override
    public ObjectProperty<Icon> tabIconProperty() {
        return icon;
    }

    @Override
    public Icon getAddIcon() {
        return tabIconProperty().get();
    }

}
