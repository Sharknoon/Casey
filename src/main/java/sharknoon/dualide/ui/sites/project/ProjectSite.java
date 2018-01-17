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
import java.util.Set;
import java.util.stream.Collectors;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import sharknoon.dualide.logic.Item;
import sharknoon.dualide.logic.Project;
import sharknoon.dualide.ui.ItemTreeView;
import sharknoon.dualide.ui.sites.Site;
import sharknoon.dualide.utils.language.Language;
import sharknoon.dualide.utils.language.Word;
import sharknoon.dualide.logic.Package;
import sharknoon.dualide.ui.ItemTabPane;
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
        GridPane gridPanePackages = new GridPane();
        gridPanePackages.setVgap(20);
        gridPanePackages.setHgap(20);
        gridPanePackages.setAlignment(Pos.TOP_CENTER);
        gridPanePackages.setPadding(new Insets(50));

        refresh(gridPanePackages);

        ColumnConstraints colIcon = new ColumnConstraints();
        colIcon.setHalignment(HPos.LEFT);
        ColumnConstraints colText = new ColumnConstraints();
        colText.setHalignment(HPos.LEFT);
        colText.setFillWidth(true);
        colText.setHgrow(Priority.ALWAYS);
        ColumnConstraints colButtonComments = new ColumnConstraints();
        colButtonComments.setHalignment(HPos.RIGHT);
        ColumnConstraints colButtonRename = new ColumnConstraints();
        colButtonRename.setHalignment(HPos.RIGHT);
        ColumnConstraints colButtonDelete = new ColumnConstraints();
        colButtonDelete.setHalignment(HPos.RIGHT);
        gridPanePackages.getColumnConstraints().addAll(colIcon, colText, colButtonComments, colButtonRename, colButtonDelete);

        HBox hBoxProjectButtons = new HBox(20);
        hBoxProjectButtons.setPadding(new Insets(50));

        Button buttonAddPackage = new Button();
        Icons.set(buttonAddPackage, Icon.PLUSPACKAGE);
        Language.set(Word.PROJECT_SITE_ADD_PACKAGE_BUTTON_TEXT, buttonAddPackage);
        buttonAddPackage.setOnAction((event) -> {
            Set<String> forbiddenValues = getItem()
                    .getChildren()
                    .stream()
                    .map(Item::getName)
                    .collect(Collectors.toSet());
            Optional<String> name = Dialogs.showTextInputDialog(Dialogs.TextInputs.NEW_PACKAGE_DIALOG, forbiddenValues);
            if (name.isPresent()) {
                Package package_ = Item.createItem(Package.class, getItem(), name.get());
                refresh(gridPanePackages);
                ItemTreeView.selectItem(package_);
            }
        });
        hBoxProjectButtons.getChildren().add(buttonAddPackage);

        Button buttonDeleteProject = new Button();
        Icons.set(buttonDeleteProject, Icon.TRASH);
        Language.set(Word.PROJECT_SIDE_DELETE_BUTTON_TEXT, buttonDeleteProject);
        buttonDeleteProject.setOnAction((event) -> {
            Optional<Boolean> confirmed = Dialogs.showConfirmationDialog(Dialogs.Confirmations.DELETE_PROJECT_DIALOG, "#PROJECT", getItem().getName());
            if (confirmed.isPresent() && confirmed.get()) {
                getItem().destroy();
                ItemTabPane.closeAllTabs();
                ItemTreeView.closeAllItems();
            }
        });
        hBoxProjectButtons.getChildren().add(buttonDeleteProject);

        Button buttonCommentProject = new Button();
        Icons.set(buttonCommentProject, Icon.COMMENTS);
        Language.set(Word.PROJECT_SIDE_COMMENT_BUTTON_TEXT, buttonCommentProject);
        buttonCommentProject.setOnAction((event) -> {
            Optional<String> comments = Dialogs.showTextEditorDialog(Dialogs.TextEditors.COMMENT_PROJECT_DIALOG, getItem().getComments());
            if (comments.isPresent()) {
                getItem().setComments(comments.get());
            }
        });
        hBoxProjectButtons.getChildren().add(buttonCommentProject);

        ScrollPane scrollPanePackages = new ScrollPane(gridPanePackages);
        scrollPanePackages.setFitToHeight(true);
        scrollPanePackages.setFitToWidth(true);
        borderPaneRoot.setCenter(scrollPanePackages);
        borderPaneRoot.setBottom(hBoxProjectButtons);
    }

    int rowCounter = 0;

    private void refresh(GridPane gridPanePackages) {
        gridPanePackages.getChildren().clear();
        rowCounter = 0;
        getItem().getChildren().forEach(p -> {
            ImageView icon = Icons.get(Icon.PACKAGE, 50);
            gridPanePackages.add(icon, 0, rowCounter);

            Text textPackageName = new Text();
            DropShadow shadowEffect = new DropShadow(10, Color.WHITESMOKE);
            shadowEffect.setSpread(0.5);
            textPackageName.setEffect(shadowEffect);
            textPackageName.textProperty().bindBidirectional(p.nameProperty());
            gridPanePackages.add(textPackageName, 1, rowCounter);

            Button buttonCommentPackage = new Button();
            Icons.set(buttonCommentPackage, Icon.COMMENTS);
            Language.set(Word.PROJECT_SITE_COMMENT_PACKAGE_BUTTON_TEXT, buttonCommentPackage);
            buttonCommentPackage.setOnAction((event) -> {
                Optional<String> comments = Dialogs.showTextEditorDialog(Dialogs.TextEditors.COMMENT_PACKAGE_DIALOG, p.getComments());
                if (comments.isPresent()) {
                    p.setComments(comments.get());
                }
            });
            gridPanePackages.add(buttonCommentPackage, 2, rowCounter);

            Button buttonRenamePackage = new Button();
            Icons.set(buttonRenamePackage, Icon.RENAME);
            Language.set(Word.PROJECT_SITE_RENAME_PACKAGE_BUTTON_TEXT, buttonRenamePackage);
            buttonRenamePackage.setOnAction((event) -> {
                Set<String> forbiddenValues = p
                        .getParent()
                        .getChildren()
                        .stream()
                        .map(Item::getName)
                        .filter(s -> !s.equals(p.getName()))
                        .collect(Collectors.toSet());
                Optional<String> name = Dialogs.showTextInputDialog(Dialogs.TextInputs.RENAME_PACKAGE_DIALOG, forbiddenValues);
                if (name.isPresent()) {
                    p.setName(name.get());
                }
            });
            gridPanePackages.add(buttonRenamePackage, 3, rowCounter);

            Button buttonDeletePackage = new Button();
            Icons.set(buttonDeletePackage, Icon.TRASH);
            Language.set(Word.PROJECT_SITE_DELETE_PACKAGE_BUTTON_TEXT, buttonDeletePackage);
            buttonDeletePackage.setOnAction((event) -> {
                Optional<Boolean> confirmed = Dialogs.showConfirmationDialog(Dialogs.Confirmations.DELETE_PACKAGE_DIALOG, "#PACKAGE", p.getName());
                if (confirmed.isPresent() && confirmed.get()) {
                    p.destroy();
                    refresh(gridPanePackages);
                }
            });
            gridPanePackages.add(buttonDeletePackage, 4, rowCounter);

            rowCounter++;
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
