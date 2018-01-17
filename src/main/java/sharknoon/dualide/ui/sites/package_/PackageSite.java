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
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import sharknoon.dualide.logic.Item;
import sharknoon.dualide.ui.sites.Site;
import sharknoon.dualide.logic.Package;
import sharknoon.dualide.ui.ItemTreeView;
import sharknoon.dualide.ui.misc.Icon;
import sharknoon.dualide.ui.misc.Icons;
import sharknoon.dualide.ui.sites.Dialogs;
import sharknoon.dualide.ui.sites.Dialogs.Confirmations;
import sharknoon.dualide.ui.sites.Dialogs.TextInputs;
import sharknoon.dualide.utils.language.Language;
import sharknoon.dualide.utils.language.Word;
import sharknoon.dualide.logic.Class;
import sharknoon.dualide.logic.Function;
import sharknoon.dualide.logic.Variable;

/**
 *
 * @author Josua Frank
 */
public class PackageSite extends Site<Package> {

    private final BorderPane borderPaneRoot = new BorderPane();

    {
        GridPane gridPaneChildren = new GridPane();
        gridPaneChildren.setVgap(20);
        gridPaneChildren.setHgap(20);
        gridPaneChildren.setAlignment(Pos.TOP_CENTER);
        gridPaneChildren.setPadding(new Insets(50));

        refresh(gridPaneChildren);

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
            Optional<String> name = Dialogs.showTextInputDialog(TextInputs.NEW_PACKAGE_DIALOG, getForbittenValues());
            if (name.isPresent()) {
                Package package_ = Item.createItem(Package.class, getItem(), name.get());
                refresh(gridPaneChildren);
                ItemTreeView.selectItem(package_);
            }
        });
        Button buttonAddClass = createButton(Word.PACKAGE_SITE_ADD_CLASS_BUTTON_TEXT, Icon.PLUSCLASS, (t) -> {
            Optional<String> name = Dialogs.showTextInputDialog(TextInputs.NEW_CLASS_DIALOG, getForbittenValues());
            if (name.isPresent()) {
                Class clazz = Item.createItem(Class.class, getItem(), name.get());
                refresh(gridPaneChildren);
                ItemTreeView.selectItem(clazz);
            }
        });
        Button buttonAddFunction = createButton(Word.PACKAGE_SITE_ADD_FUNCTION_BUTTON_TEXT, Icon.PLUSFUNCTION, (t) -> {
            Optional<String> name = Dialogs.showTextInputDialog(TextInputs.NEW_FUNCTION_DIALOG, getForbittenValues());
            if (name.isPresent()) {
                Function fun = Item.createItem(Function.class, getItem(), name.get());
                refresh(gridPaneChildren);
                ItemTreeView.selectItem(fun);
            }
        });
        Button buttonAddVariable = createButton(Word.PACKAGE_SITE_ADD_VARIABLE_BUTTON_TEXT, Icon.PLUSVARIABLE, (t) -> {
            Optional<String> name = Dialogs.showTextInputDialog(TextInputs.NEW_VARIABLE_DIALOG, getForbittenValues());
            if (name.isPresent()) {
                Variable var = Item.createItem(Variable.class, getItem(), name.get());
                refresh(gridPaneChildren);
                ItemTreeView.selectItem(var);
            }
        });
        Button buttonComment = createButton(Word.PACKAGE_SITE_COMMENT_BUTTON_TEXT, Icon.COMMENTS, (t) -> {
            Optional<String> comments = Dialogs.showTextEditorDialog(Dialogs.TextEditors.COMMENT_PACKAGE_DIALOG, getItem().getComments());
            if (comments.isPresent()) {
                getItem().setComments(comments.get());
            }
        });
        Button buttonDelete = createButton(Word.PACKAGE_SITE_DELETE_BUTTON_TEXT, Icon.TRASH, (t) -> {
            Optional<Boolean> confirmed = Dialogs.showConfirmationDialog(Confirmations.DELETE_PACKAGE_DIALOG, "#PACKAGE", getItem().getName());
            if (confirmed.isPresent() && confirmed.get()) {
                getItem().destroy();
            }
        });

        gridPanePackageButtons.addRow(0,
                buttonAddPackage,
                buttonAddClass,
                buttonAddFunction,
                buttonAddVariable,
                buttonComment,
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
        ColumnConstraints colDelete = new ColumnConstraints();
        colDelete.setHalignment(HPos.RIGHT);
        gridPanePackageButtons.getColumnConstraints().addAll(colAddPackage, colAddClass, colAddFunction, colAddVariable, colComment, colDelete);

        ScrollPane scrollPanePackages = new ScrollPane(gridPaneChildren);
        scrollPanePackages.setFitToHeight(true);
        scrollPanePackages.setFitToWidth(true);
        borderPaneRoot.setCenter(scrollPanePackages);
        borderPaneRoot.setBottom(gridPanePackageButtons);
    }

    private Set<String> getForbittenValues() {
        return getForbittenValues(null);
    }

    private Set<String> getForbittenValues(String ignoreMe) {
        return getItem()
                .getChildren()
                .stream()
                .map(Item::getName)
                .filter(n -> ignoreMe == null || !n.equals(ignoreMe))
                .collect(Collectors.toSet());
    }

    private Button createButton(Word buttonText, Icon icon, Consumer<ActionEvent> onAction) {
        Button buttonAdd = new Button();
        Icons.set(buttonAdd, icon);
        Language.set(buttonText, buttonAdd);
        buttonAdd.setOnAction(e -> onAction.accept(e));
        return buttonAdd;
    }

    int rowCounter = 0;

    private void refresh(GridPane gridPanePackages) {
        gridPanePackages.getChildren().clear();
        rowCounter = 0;
        getItem().getChildren().forEach(p -> {
            ImageView icon = Icons.get(p.getSite().getTabIcon(), 50);
            gridPanePackages.add(icon, 0, rowCounter);

            Text textName = new Text();
            DropShadow shadowEffect = new DropShadow(10, Color.WHITESMOKE);
            shadowEffect.setSpread(0.5);
            textName.setEffect(shadowEffect);
            textName.textProperty().bindBidirectional(p.nameProperty());
            gridPanePackages.add(textName, 1, rowCounter);

            Button buttonComment = new Button();
            Icons.set(buttonComment, Icon.COMMENTS);
            Language.set(Word.PROJECT_SITE_COMMENT_PACKAGE_BUTTON_TEXT, buttonComment);//TODO
            buttonComment.setOnAction((event) -> {
                Optional<String> comments = Dialogs.showTextEditorDialog(Dialogs.TextEditors.COMMENT_PACKAGE_DIALOG, p.getComments());//TODO
                if (comments.isPresent()) {
                    p.setComments(comments.get());
                }
            });
            gridPanePackages.add(buttonComment, 2, rowCounter);

            Button buttonRename = new Button();
            Icons.set(buttonRename, Icon.RENAME);
            Language.set(Word.PROJECT_SITE_RENAME_PACKAGE_BUTTON_TEXT, buttonRename);//TODO
            buttonRename.setOnAction((event) -> {
                Set<String> forbiddenValues = p
                        .getParent()
                        .getChildren()
                        .stream()
                        .map(Item::getName)
                        .filter(s -> !s.equals(p.getName()))
                        .collect(Collectors.toSet());
                Optional<String> name = Dialogs.showTextInputDialog(Dialogs.TextInputs.RENAME_PACKAGE_DIALOG, forbiddenValues);//TODO
                if (name.isPresent()) {
                    p.setName(name.get());
                }
            });
            gridPanePackages.add(buttonRename, 3, rowCounter);

            Button buttonDelete = new Button();
            Icons.set(buttonDelete, Icon.TRASH);
            Language.set(Word.PROJECT_SITE_DELETE_PACKAGE_BUTTON_TEXT, buttonDelete);//TODO
            buttonDelete.setOnAction((event) -> {
                Optional<Boolean> confirmed = Dialogs.showConfirmationDialog(Dialogs.Confirmations.DELETE_PACKAGE_DIALOG, "#PACKAGE", p.getName());//TODO
                if (confirmed.isPresent() && confirmed.get()) {
                    p.destroy();
                    refresh(gridPanePackages);
                }
            });
            gridPanePackages.add(buttonDelete, 4, rowCounter);

            rowCounter++;
        });
    }

    public PackageSite(Package item) {
        super(item);
    }

    @Override
    public Pane getTabContentPane() {
        return borderPaneRoot;
    }

    @Override
    public Icon getTabIcon() {
        return Icon.PACKAGE;
    }

}
