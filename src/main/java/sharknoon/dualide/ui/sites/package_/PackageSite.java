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
package sharknoon.dualide.ui.sites.package_;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import javafx.collections.SetChangeListener;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import sharknoon.dualide.logic.items.Item;
import sharknoon.dualide.ui.sites.Site;
import sharknoon.dualide.logic.items.Package;
import sharknoon.dualide.ui.ItemTreeView;
import sharknoon.dualide.ui.misc.Icon;
import sharknoon.dualide.ui.misc.Icons;
import sharknoon.dualide.ui.dialogs.Dialogs;
import sharknoon.dualide.ui.dialogs.Dialogs.Confirmations;
import sharknoon.dualide.ui.dialogs.Dialogs.TextInputs;
import sharknoon.dualide.utils.language.Word;
import sharknoon.dualide.logic.items.Class;
import sharknoon.dualide.logic.items.Function;
import sharknoon.dualide.logic.items.ItemType;
import sharknoon.dualide.logic.items.Variable;

/**
 *
 * @author Josua Frank
 */
public class PackageSite extends Site<Package> {

    private BorderPane borderPaneRoot;
    private final GridPane gridPaneChildren = new GridPane();

    private void init() {
        borderPaneRoot = new BorderPane();
        gridPaneChildren.setVgap(20);
        gridPaneChildren.setHgap(20);
        gridPaneChildren.setAlignment(Pos.TOP_CENTER);
        gridPaneChildren.setPadding(new Insets(50));

        getItem().childrenProperty().addListener((SetChangeListener.Change<? extends Item<? extends Item, Package, ? extends Item>> change) -> {
            setContent();
        });
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
        gridPaneChildren.getColumnConstraints().addAll(colIcon, colText, colButtonComments, colButtonRename, colButtonDelete);

        GridPane gridPanePackageButtons = new GridPane();
        gridPanePackageButtons.setHgap(20);
        gridPanePackageButtons.setPadding(new Insets(50));

        Button buttonAddPackage = createButton(Word.PACKAGE_SITE_ADD_PACKAGE_BUTTON_TEXT, Icon.PLUSPACKAGE, (t) -> {
            Optional<String> name = Dialogs.showTextInputDialog(TextInputs.NEW_PACKAGE_DIALOG, getForbittenChildNames());
            if (name.isPresent()) {
                Package package_ = Item.createItem(ItemType.PACKAGE, getItem(), name.get());
                ItemTreeView.selectItem(package_);
            }
        });
        Button buttonAddClass = createButton(Word.PACKAGE_SITE_ADD_CLASS_BUTTON_TEXT, Icon.PLUSCLASS, (t) -> {
            Optional<String> name = Dialogs.showTextInputDialog(TextInputs.NEW_CLASS_DIALOG, getForbittenChildNames());
            if (name.isPresent()) {
                Class clazz = Item.createItem(ItemType.CLASS, getItem(), name.get());
                ItemTreeView.selectItem(clazz);
            }
        });
        Button buttonAddFunction = createButton(Word.PACKAGE_SITE_ADD_FUNCTION_BUTTON_TEXT, Icon.PLUSFUNCTION, (t) -> {
            Optional<String> name = Dialogs.showTextInputDialog(TextInputs.NEW_FUNCTION_DIALOG, getForbittenChildNames());
            if (name.isPresent()) {
                Function fun = Item.createItem(ItemType.FUNCTION, getItem(), name.get());
                ItemTreeView.selectItem(fun);
            }
        });
        Button buttonAddVariable = createButton(Word.PACKAGE_SITE_ADD_VARIABLE_BUTTON_TEXT, Icon.PLUSVARIABLE, (t) -> {
            Optional<String> name = Dialogs.showTextInputDialog(TextInputs.NEW_VARIABLE_DIALOG, getForbittenChildNames());
            if (name.isPresent()) {
                Variable var = Item.createItem(ItemType.VARIABLE, getItem(), name.get());
                ItemTreeView.selectItem(var);
            }
        });
        Button buttonComment = createButton(Word.PACKAGE_SITE_COMMENT_BUTTON_TEXT, Icon.COMMENTS, (t) -> {
            Optional<String> comments = Dialogs.showTextEditorDialog(Dialogs.TextEditors.COMMENT_PACKAGE_DIALOG, getItem().getComments());
            if (comments.isPresent()) {
                getItem().setComments(comments.get());
            }
        }, false, true);
        Button buttonRename = createButton(Word.PACKAGE_SITE_RENAME_BUTTON_TEXT, Icon.RENAME, (t) -> {
            Optional<String> name = Dialogs.showTextInputDialog(Dialogs.TextInputs.RENAME_PACKAGE_DIALOG, getItem().getName(), getItem().getParent().getSite().getForbittenChildNames());
            if (name.isPresent()) {
                getItem().setName(name.get());
            }
        }, false, true);
        Button buttonDelete = createButton(Word.PACKAGE_SITE_DELETE_BUTTON_TEXT, Icon.TRASH, (t) -> {
            Optional<Boolean> confirmed = Dialogs.showConfirmationDialog(Confirmations.DELETE_PACKAGE_DIALOG, "#PACKAGE", getItem().getName());
            if (confirmed.isPresent() && confirmed.get()) {
                getItem().destroy();
            }
        }, false, true);

        gridPanePackageButtons.addRow(0,
                buttonAddPackage,
                buttonAddClass,
                buttonAddFunction,
                buttonAddVariable,
                buttonComment,
                buttonRename,
                buttonDelete
        );

        ColumnConstraints colAddPackage = new ColumnConstraints();
        colAddPackage.setHalignment(HPos.LEFT);
        ColumnConstraints colAddClass = new ColumnConstraints();
        colAddClass.setHalignment(HPos.LEFT);
        ColumnConstraints colAddFunction = new ColumnConstraints();
        colAddFunction.setHalignment(HPos.LEFT);
        ColumnConstraints colAddVariable = new ColumnConstraints();
        colAddVariable.setHalignment(HPos.LEFT);
        colAddVariable.setFillWidth(true);
        colAddVariable.setHgrow(Priority.ALWAYS);
        ColumnConstraints colComment = new ColumnConstraints();
        colComment.setHalignment(HPos.RIGHT);
        ColumnConstraints colRename = new ColumnConstraints();
        colRename.setHalignment(HPos.RIGHT);
        ColumnConstraints colDelete = new ColumnConstraints();
        colDelete.setHalignment(HPos.RIGHT);
        gridPanePackageButtons.getColumnConstraints().addAll(colAddPackage, colAddClass, colAddFunction, colAddVariable, colComment, colRename, colDelete);

        ScrollPane scrollPaneChildren = new ScrollPane(gridPaneChildren);
        scrollPaneChildren.setFitToHeight(true);
        scrollPaneChildren.setFitToWidth(true);
        borderPaneRoot.setCenter(scrollPaneChildren);
        borderPaneRoot.setBottom(gridPanePackageButtons);
    }

    int rowCounter = 0;

    public void setContent() {
        gridPaneChildren.getChildren().clear();
        rowCounter = 0;
        getItem().getChildren().forEach(c -> {
            ImageView icon = Icons.get(c.getSite().getTabIcon(), 50);
            icon.setOnMouseClicked(e -> onClicked(c));

            Text textName = new Text();
            DropShadow shadowEffect = new DropShadow(10, Color.WHITESMOKE);
            shadowEffect.setSpread(0.5);
            textName.setEffect(shadowEffect);
            textName.setFont(Font.font(30));
            textName.textProperty().bindBidirectional(c.nameProperty());
            textName.setOnMouseClicked(e -> onClicked(c));

            Button buttonComment = createButton(Word.PACKAGE_SITE_COMMENT_CHILDREN_BUTTON_TEXT, Icon.COMMENTS, (t) -> {
                Optional<String> comments = Dialogs.showTextEditorDialog(Dialogs.TextEditors.COMMENT_PACKAGE_DIALOG, c.getComments());
                if (comments.isPresent()) {
                    c.setComments(comments.get());
                }
            }, false, true);

            Button buttonRename = createButton(Word.PACKAGE_SITE_RENAME_CHILDREN_BUTTON_TEXT, Icon.RENAME, (t) -> {
                Optional<String> name = Dialogs.showTextInputDialog(Dialogs.TextInputs.RENAME_PACKAGE_DIALOG, c.getName(), getForbittenChildNames(c.getName()));
                if (name.isPresent()) {
                    c.setName(name.get());
                }
            }, false, true);

            Button buttonDelete = createButton(Word.PACKAGE_SITE_DELETE_CHILDREN_BUTTON_TEXT, Icon.TRASH, (t) -> {
                Optional<Boolean> confirmed = Dialogs.showConfirmationDialog(Dialogs.Confirmations.DELETE_PACKAGE_DIALOG, "#PACKAGE", c.getName());
                if (confirmed.isPresent() && confirmed.get()) {
                    c.destroy();
                }
            }, false, true);

            gridPaneChildren.addRow(rowCounter,
                    icon,
                    textName,
                    buttonComment,
                    buttonRename,
                    buttonDelete
            );

            rowCounter++;
        });
    }

    private void onClicked(Item item) {
        ItemTreeView.selectItem(item);
    }

    public PackageSite(Package item) {
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
        return Icon.PACKAGE;
    }

    @Override
    public Icon getAddIcon() {
        return Icon.PLUSPACKAGE;
    }

}
