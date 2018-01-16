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
package sharknoon.dualide.ui.sites.project;

import java.util.Optional;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import sharknoon.dualide.logic.Project;
import sharknoon.dualide.ui.ItemTreeView;
import sharknoon.dualide.ui.sites.Site;
import sharknoon.dualide.utils.language.Language;
import sharknoon.dualide.utils.language.Word;
import sharknoon.dualide.logic.Package;
import sharknoon.dualide.ui.misc.Icon;
import sharknoon.dualide.ui.misc.Icons;
import sharknoon.dualide.ui.sites.Dialogs;

/**
 *
 * @author Josua Frank
 */
public class ProjectSite extends Site<Project> {

    private final BorderPane borderPaneRoot = new BorderPane();

    {
        VBox vBoxPackages = new VBox(20);
        vBoxPackages.setPadding(new Insets(50));

        refresh(vBoxPackages);

        HBox hBoxProjectButtons = new HBox(20);
        hBoxProjectButtons.setPadding(new Insets(50));

        Button buttonAddPackage = new Button();
        Icons.set(buttonAddPackage, Icon.PLUS);
        Language.set(Word.PROJECT_SITE_ADD_PACKAGE_BUTTON_TEXT, buttonAddPackage);
        buttonAddPackage.setOnAction((event) -> {
            Optional<String> name = Dialogs.showTextInputDialog(Dialogs.TextInputs.NEW_PACKAGE_DIALOG);
            if (name.isPresent()) {
                Package package_ = new Package(getItem(), name.get());
                refresh(vBoxPackages);
                ItemTreeView.selectItem(package_);
            }
        });
        hBoxProjectButtons.getChildren().add(buttonAddPackage);

        Button buttonDeleteProject = new Button();
        Icons.set(buttonDeleteProject, Icon.TRASH);
        Language.set(Word.PROJECT_SIDE_DELETE_PROJECT_BUTTON_TEXT, buttonDeleteProject);
        buttonDeleteProject.setOnAction((event) -> {
            Optional<Boolean> confirmed = Dialogs.showConfirmationDialog(Dialogs.Confirmations.DELETE_PROJECT_DIALOG, "#PROJECT", getItem().getName());
            if (confirmed.isPresent() && confirmed.get()) {
                getItem().destroy();
            }
        });
        hBoxProjectButtons.getChildren().add(buttonDeleteProject);

        ScrollPane scrollPanePackages = new ScrollPane(vBoxPackages);
        borderPaneRoot.setCenter(scrollPanePackages);
        borderPaneRoot.setBottom(hBoxProjectButtons);
    }

    private void refresh(VBox vBoxPackages) {
        vBoxPackages.getChildren().clear();
        getItem().getChildren().forEach(p -> {
            BorderPane borderPanePackageEntry = new BorderPane();
            ImageView icon = Icons.get(Icon.PACKAGE);
            borderPanePackageEntry.setLeft(icon);

            VBox vBoxNameAndComments = new VBox(10);
            Text textPackageName = new Text();
            textPackageName.textProperty().bindBidirectional(p.nameProperty());
            Text textPackageComments = new Text();
            textPackageComments.textProperty().bindBidirectional(p.commentsProperty());
            vBoxNameAndComments.getChildren().addAll(textPackageName, textPackageComments);
            borderPanePackageEntry.setCenter(vBoxNameAndComments);

            Button buttonRenamePackage = new Button();
            Icons.set(buttonRenamePackage, Icon.RENAME);
            Language.set(Word.PROJECT_SITE_RENAME_PACKAGE_BUTTON_TEXT, buttonRenamePackage);
            buttonRenamePackage.setOnAction((event) -> {
                Optional<String> name = Dialogs.showTextInputDialog(Dialogs.TextInputs.RENAME_PACKAGE_DIALOG);
                if (name.isPresent()) {
                    p.setName(name.get());
                }
            });

            Button buttonDeletePackage = new Button();
            Icons.set(buttonDeletePackage, Icon.TRASH);
            Language.set(Word.PROJECT_SITE_DELETE_PACKAGE_BUTTON_TEXT, buttonDeletePackage);
            buttonDeletePackage.setOnAction((event) -> {
                Optional<Boolean> confirmed = Dialogs.showConfirmationDialog(Dialogs.Confirmations.DELETE_PACKAGE_DIALOG, "#PACKAGE", p.getName());
                if (confirmed.isPresent() && confirmed.get()) {
                    p.destroy();
                    refresh(vBoxPackages);
                }
            });

            HBox hBoxButtons = new HBox(10);
            hBoxButtons.getChildren().addAll(buttonRenamePackage, buttonDeletePackage);
            borderPanePackageEntry.setRight(hBoxButtons);

            vBoxPackages.getChildren().add(borderPanePackageEntry);
        });
    }

    public ProjectSite(Project item) {
        super(item);
    }

    @Override
    public Pane getTabContentPane() {
        return borderPaneRoot;
    }

    @Override
    public Icon getTabIcon() {
        return Icon.PROJECT;
    }

}
