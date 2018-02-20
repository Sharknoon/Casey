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
package sharknoon.dualide.ui.sites.clazz;

import java.util.Optional;
import java.util.Set;
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
import sharknoon.dualide.ui.sites.Site;
import sharknoon.dualide.logic.items.Class;
import sharknoon.dualide.logic.items.Function;
import sharknoon.dualide.logic.items.Item;
import sharknoon.dualide.logic.items.ItemType;
import sharknoon.dualide.logic.items.Variable;
import sharknoon.dualide.ui.ItemTreeView;
import sharknoon.dualide.ui.misc.Icon;
import sharknoon.dualide.ui.misc.Icons;
import sharknoon.dualide.ui.dialogs.Dialogs;
import sharknoon.dualide.utils.language.Word;

/**
 *
 * @author Josua Frank
 */
public class ClassSite extends Site<Class> {

    private BorderPane borderPaneRoot;
    private GridPane gridPaneChildren;

    private void init() {
        borderPaneRoot = new BorderPane();
        gridPaneChildren = new GridPane();
        gridPaneChildren.setVgap(20);
        gridPaneChildren.setHgap(20);
        gridPaneChildren.setAlignment(Pos.TOP_CENTER);
        gridPaneChildren.setPadding(new Insets(50));

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

        Button buttonAddFunction = createButton(Word.CLASS_SITE_ADD_FUNCTION_BUTTON_TEXT, Icon.PLUSFUNCTION, (t) -> {
            Optional<String> name = Dialogs.showTextInputDialog(Dialogs.TextInputs.NEW_FUNCTION_DIALOG, getForbittenChildNames());
            if (name.isPresent()) {
                Function fun = Item.createItem(ItemType.FUNCTION, getItem(), name.get(), true);
            }
        });
        Button buttonAddVariable = createButton(Word.CLASS_SITE_ADD_VARIABLE_BUTTON_TEXT, Icon.PLUSVARIABLE, (t) -> {
            Optional<String> name = Dialogs.showTextInputDialog(Dialogs.TextInputs.NEW_VARIABLE_DIALOG, getForbittenChildNames());
            if (name.isPresent()) {
                Variable var = Item.createItem(ItemType.VARIABLE, getItem(), name.get(), true);
            }
        });
        Button buttonComment = createButton(Word.CLASS_SITE_COMMENT_BUTTON_TEXT, Icon.COMMENTS, (t) -> {
            Optional<String> comments = Dialogs.showTextEditorDialog(Dialogs.TextEditors.COMMENT_CLASS_DIALOG, getItem().getComments());
            if (comments.isPresent()) {
                getItem().setComments(comments.get());
            }
        }, false, true);
        Button buttonRename = createButton(Word.CLASS_SITE_RENAME_BUTTON_TEXT, Icon.RENAME, (t) -> {
            Optional<String> name = Dialogs.showTextInputDialog(Dialogs.TextInputs.RENAME_CLASS_DIALOG, getItem().getName(), getItem().getParent().map(p -> p.getSite().getForbittenChildNames()).orElse(Set.of()));
            if (name.isPresent()) {
                getItem().setName(name.get());
            }
        }, false, true);
        Button buttonDelete = createButton(Word.CLASS_SITE_DELETE_BUTTON_TEXT, Icon.TRASH, (t) -> {
            Optional<Boolean> confirmed = Dialogs.showConfirmationDialog(Dialogs.Confirmations.DELETE_CLASS_DIALOG, "#CLASS", getItem().getName());
            if (confirmed.isPresent() && confirmed.get()) {
                getItem().destroy();
            }
        }, false, true);

        gridPanePackageButtons.addRow(0,
                buttonAddFunction,
                buttonAddVariable,
                buttonComment,
                buttonRename,
                buttonDelete
        );

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
        gridPanePackageButtons.getColumnConstraints().addAll(colAddFunction, colAddVariable, colComment, colRename, colDelete);

        ScrollPane scrollPaneChildren = new ScrollPane(gridPaneChildren);
        scrollPaneChildren.setFitToHeight(true);
        scrollPaneChildren.setFitToWidth(true);
        borderPaneRoot.setCenter(scrollPaneChildren);
        borderPaneRoot.setBottom(gridPanePackageButtons);
    }

    int rowCounter = 0;

    public void setContent() {
        EasyBind.subscribe(getItem().childrenProperty(), childs -> {
            gridPaneChildren.getChildren().clear();
            rowCounter = 0;
            childs.forEach(c -> {
                Node icon = Icons.get(c.getSite().getTabIcon(), 50);
                icon.setOnMouseClicked(e -> onClicked(c));

                Text textName = new Text();
                DropShadow shadowEffect = new DropShadow(10, Color.WHITESMOKE);
                shadowEffect.setSpread(0.5);
                textName.setEffect(shadowEffect);
                textName.setFont(Font.font(30));
                textName.textProperty().bindBidirectional(c.nameProperty());
                textName.setOnMouseClicked(e -> onClicked(c));

                Button buttonComment = createButton(Word.CLASS_SITE_COMMENT_CHILDREN_BUTTON_TEXT, Icon.COMMENTS, (t) -> {
                    Optional<String> comments = Dialogs.showTextEditorDialog(Dialogs.TextEditors.COMMENT_CLASS_DIALOG, c.getComments());
                    if (comments.isPresent()) {
                        c.setComments(comments.get());
                    }
                }, false, true);

                Button buttonRename = createButton(Word.CLASS_SITE_RENAME_CHILDREN_BUTTON_TEXT, Icon.RENAME, (t) -> {
                    Optional<String> name = Dialogs.showTextInputDialog(Dialogs.TextInputs.RENAME_CLASS_DIALOG, c.getName(), getForbittenChildNames(c.getName()));
                    if (name.isPresent()) {
                        c.setName(name.get());
                    }
                }, false, true);

                Button buttonDelete = createButton(Word.CLASS_SITE_DELETE_CHILDREN_BUTTON_TEXT, Icon.TRASH, (t) -> {
                    Optional<Boolean> confirmed = Dialogs.showConfirmationDialog(Dialogs.Confirmations.DELETE_CLASS_DIALOG, "#CLASS", c.getName());
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
        });
    }

    private void onClicked(Item item) {
        item.getSite().select();
    }

    public ClassSite(Class item) {
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
        return Icon.CLASS;
    }

    @Override
    public Icon getAddIcon() {
        return Icon.PLUSCLASS;
    }

}
