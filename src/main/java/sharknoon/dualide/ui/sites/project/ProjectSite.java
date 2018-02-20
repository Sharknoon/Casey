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

import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import javafx.collections.SetChangeListener;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.fxmisc.easybind.EasyBind;
import sharknoon.dualide.logic.items.Item;
import sharknoon.dualide.logic.items.Project;
import sharknoon.dualide.ui.ItemTreeView;
import sharknoon.dualide.ui.sites.Site;
import sharknoon.dualide.utils.language.Word;
import sharknoon.dualide.logic.items.Package;
import sharknoon.dualide.logic.items.ItemType;
import sharknoon.dualide.logic.items.Welcome;
import sharknoon.dualide.ui.ItemTabPane;
import sharknoon.dualide.ui.misc.Icon;
import sharknoon.dualide.ui.misc.Icons;
import sharknoon.dualide.ui.dialogs.Dialogs;

/**
 *
 * @author Josua Frank
 */
public class ProjectSite extends Site<Project> {

    private BorderPane borderPaneRoot;
    private GridPane gridPanePackages;

    private void init() {
        borderPaneRoot = new BorderPane();
        gridPanePackages = new GridPane();
        gridPanePackages.setVgap(20);
        gridPanePackages.setHgap(20);
        gridPanePackages.setAlignment(Pos.TOP_CENTER);
        gridPanePackages.setPadding(new Insets(50));

        setContent();

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

        GridPane gridPaneProjectButtons = new GridPane();
        gridPaneProjectButtons.setHgap(20);
        gridPaneProjectButtons.setPadding(new Insets(50));

        Button buttonAddPackage = createButton(Word.PROJECT_SITE_ADD_PACKAGE_BUTTON_TEXT, Icon.PLUSPACKAGE, (t) -> {
            Optional<String> name = Dialogs.showTextInputDialog(Dialogs.TextInputs.NEW_PACKAGE_DIALOG, getForbittenChildNames());
            if (name.isPresent()) {
                Package package_ = Item.createItem(ItemType.PACKAGE, getItem(), name.get(),true);
            }
        });

        Button buttonComment = createButton(Word.PROJECT_SITE_COMMENT_BUTTON_TEXT, Icon.COMMENTS, (t) -> {
            Optional<String> comments = Dialogs.showTextEditorDialog(Dialogs.TextEditors.COMMENT_PROJECT_DIALOG, getItem().getComments());
            if (comments.isPresent()) {
                getItem().setComments(comments.get());
            }
        }, false, true);
        Button buttonRename = createButton(Word.PROJECT_SITE_RENAME_BUTTON_TEXT, Icon.RENAME, (t) -> {
            Optional<String> name = Dialogs.showTextInputDialog(Dialogs.TextInputs.RENAME_PROJECT_DIALOG, getItem().getName(), Collections.emptySet());
            if (name.isPresent()) {
                getItem().setName(name.get());
            }
        }, false, true);
        Button buttonDelete = createButton(Word.PROJECT_SITE_DELETE_BUTTON_TEXT, Icon.TRASH, (t) -> {
            Optional<Boolean> confirmed = Dialogs.showConfirmationDialog(Dialogs.Confirmations.DELETE_PROJECT_DIALOG, "#PROJECT", getItem().getName());
            if (confirmed.isPresent() && confirmed.get()) {
                getItem().destroy();
                Welcome.getWelcome().getSite().select();
            }
        }, false, true);

        gridPaneProjectButtons.addRow(0,
                buttonAddPackage,
                buttonComment,
                buttonRename,
                buttonDelete
        );

        ColumnConstraints colAddPackage = new ColumnConstraints();
        colAddPackage.setHalignment(HPos.LEFT);
        colAddPackage.setFillWidth(true);
        colAddPackage.setHgrow(Priority.ALWAYS);
        ColumnConstraints colComment = new ColumnConstraints();
        colComment.setHalignment(HPos.RIGHT);
        ColumnConstraints colRename = new ColumnConstraints();
        colRename.setHalignment(HPos.RIGHT);
        ColumnConstraints colDelete = new ColumnConstraints();
        colDelete.setHalignment(HPos.RIGHT);
        gridPaneProjectButtons.getColumnConstraints().addAll(colAddPackage, colComment, colRename, colDelete);

        ScrollPane scrollPanePackages = new ScrollPane(gridPanePackages);
        scrollPanePackages.setFitToHeight(true);
        scrollPanePackages.setFitToWidth(true);
        borderPaneRoot.setCenter(scrollPanePackages);
        borderPaneRoot.setBottom(gridPaneProjectButtons);
    }

    int rowCounter = 0;

    public void setContent() {
        EasyBind.subscribe(getItem().childrenProperty(), childs -> {
            gridPanePackages.getChildren().clear();
            rowCounter = 0;
            getItem()
                    .getChildren()
                    .stream()
                    .sorted((c1, c2) -> c1.getName().compareTo(c2.getName()))
                    .forEach(p -> {
                        Node icon = Icons.get(Icon.PACKAGE, 50);
                        icon.setOnMouseClicked(e -> onClicked(p));

                        Text textPackageName = new Text();
                        DropShadow shadowEffect = new DropShadow(10, Color.WHITESMOKE);
                        shadowEffect.setSpread(0.5);
                        textPackageName.setEffect(shadowEffect);
                        textPackageName.setFont(Font.font(30));
                        textPackageName.textProperty().bindBidirectional(p.nameProperty());
                        textPackageName.setOnMouseClicked(e -> onClicked(p));

                        Button buttonCommentPackage = createButton(Word.PROJECT_SITE_COMMENT_CHILDREN_BUTTON_TEXT, Icon.COMMENTS, (t) -> {
                            Optional<String> comments = Dialogs.showTextEditorDialog(Dialogs.TextEditors.COMMENT_PACKAGE_DIALOG, p.getComments());
                            if (comments.isPresent()) {
                                p.setComments(comments.get());
                            }
                        }, false, true);

                        Button buttonRenamePackage = createButton(Word.PROJECT_SITE_RENAME_CHILDREN_BUTTON_TEXT, Icon.RENAME, (t) -> {
                            Optional<String> name = Dialogs.showTextInputDialog(Dialogs.TextInputs.RENAME_PACKAGE_DIALOG, p.getName(), getForbittenChildNames(p.getName()));
                            if (name.isPresent()) {
                                p.setName(name.get());
                            }
                        }, false, true);

                        Button buttonDeletePackage = createButton(Word.PROJECT_SITE_DELETE_CHILDREN_BUTTON_TEXT, Icon.TRASH, (t) -> {
                            Optional<Boolean> confirmed = Dialogs.showConfirmationDialog(Dialogs.Confirmations.DELETE_PACKAGE_DIALOG, "#PACKAGE", p.getName());
                            if (confirmed.isPresent() && confirmed.get()) {
                                p.destroy();
                            }
                        }, false, true);

                        gridPanePackages.addRow(rowCounter,
                                icon,
                                textPackageName,
                                buttonCommentPackage,
                                buttonRenamePackage,
                                buttonDeletePackage
                        );

                        rowCounter++;
                    });
        });
    }

    private void onClicked(Item item) {
        item.getSite().select();
    }

    public ProjectSite(Project item) {
        super(item);
    }

    @Override
    public CompletableFuture<Pane> getTabContentPane() {
        return CompletableFuture.supplyAsync(() -> {
            if (borderPaneRoot == null) {
                init();
            }
            return borderPaneRoot;
        });
    }

    @Override
    public Icon getTabIcon() {
        return Icon.PROJECT;
    }

    @Override
    public Icon getAddIcon() {
        return Icon.PLUSPROJECT;
    }

}
