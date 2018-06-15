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
package sharknoon.dualide.ui.sites;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import sharknoon.dualide.logic.items.Function;
import sharknoon.dualide.logic.items.Item;
import sharknoon.dualide.logic.items.ItemType;
import sharknoon.dualide.logic.items.Variable;
import sharknoon.dualide.ui.dialogs.Dialogs;
import sharknoon.dualide.ui.misc.Icon;
import sharknoon.dualide.ui.misc.Icons;
import sharknoon.dualide.utils.javafx.BindUtils;
import sharknoon.dualide.utils.language.Language;
import sharknoon.dualide.utils.language.Word;

/**
 *
 * @author Josua Frank
 */
public class SiteUtils {

    public static Word getCommentWord(ItemType type) {
        switch (type) {
            case CLASS:
                return Word.CLASS_SITE_COMMENT_BUTTON_TEXT;
            case FUNCTION:
                return Word.FUNCTION_SITE_COMMENT_BUTTON_TEXT;
            case PACKAGE:
                return Word.PACKAGE_SITE_COMMENT_BUTTON_TEXT;
            case PROJECT:
                return Word.PROJECT_SITE_COMMENT_BUTTON_TEXT;
            case VARIABLE:
                return Word.VARIABLE_SITE_COMMENT_BUTTON_TEXT;
            case WELCOME:
            default:
                return Word.PROJECT_SITE_COMMENT_CHILDREN_BUTTON_TEXT;
        }
    }

    public static Word getRenameWord(ItemType type) {
        switch (type) {
            case CLASS:
                return Word.CLASS_SITE_RENAME_BUTTON_TEXT;
            case FUNCTION:
                return Word.FUNCTION_SITE_RENAME_BUTTON_TEXT;
            case PACKAGE:
                return Word.PACKAGE_SITE_RENAME_BUTTON_TEXT;
            case PROJECT:
                return Word.PROJECT_SITE_RENAME_BUTTON_TEXT;
            case VARIABLE:
                return Word.VARIABLE_SITE_RENAME_BUTTON_TEXT;
            case WELCOME:
            default:
                return Word.PROJECT_SITE_RENAME_CHILDREN_BUTTON_TEXT;
        }
    }

    public static Word getDeleteWord(ItemType type) {
        switch (type) {
            case CLASS:
                return Word.CLASS_SITE_DELETE_BUTTON_TEXT;
            case FUNCTION:
                return Word.FUNCTION_SITE_DELETE_BUTTON_TEXT;
            case PACKAGE:
                return Word.PACKAGE_SITE_DELETE_BUTTON_TEXT;
            case PROJECT:
                return Word.PROJECT_SITE_DELETE_BUTTON_TEXT;
            case VARIABLE:
                return Word.VARIABLE_SITE_DELETE_BUTTON_TEXT;
            case WELCOME:
            default:
                return Word.PROJECT_SITE_DELETE_CHILDREN_BUTTON_TEXT;
        }
    }

    /**
     * todo
     *
     * @param type
     * @return
     */
    public static Word getAddChildrenWord(ItemType type) {
        switch (type) {
            case CLASS:
                return Word.CLASS_SITE_DELETE_BUTTON_TEXT;
            case FUNCTION:
                return Word.FUNCTION_SITE_DELETE_BUTTON_TEXT;
            case PACKAGE:
                return Word.PACKAGE_SITE_DELETE_BUTTON_TEXT;
            case PROJECT:
                return Word.PROJECT_SITE_DELETE_BUTTON_TEXT;
            case VARIABLE:
                return Word.VARIABLE_SITE_DELETE_BUTTON_TEXT;
            case WELCOME:
            default:
                return Word.PROJECT_SITE_DELETE_CHILDREN_BUTTON_TEXT;
        }
    }

    public static Dialogs.TextInputs getAddChildrenDialog(ItemType type) {
        switch (type) {
            case CLASS:
                return Dialogs.TextInputs.NEW_CLASS_DIALOG;
            case FUNCTION:
                return Dialogs.TextInputs.NEW_FUNCTION_DIALOG;
            case PACKAGE:
                return Dialogs.TextInputs.NEW_PACKAGE_DIALOG;
            case PROJECT:
                return Dialogs.TextInputs.NEW_PROJECT_DIALOG;
            case VARIABLE:
                return Dialogs.TextInputs.NEW_VARIABLE_DIALOG;
            case WELCOME:
            default:
                return Dialogs.TextInputs.NEW_TEXT_VALUE;
        }
    }

    public static Dialogs.TextEditors getCommentDialog(ItemType type) {
        switch (type) {
            case CLASS:
                return Dialogs.TextEditors.COMMENT_CLASS_DIALOG;
            case FUNCTION:
                return Dialogs.TextEditors.COMMENT_FUNCTION_DIALOG;
            case PACKAGE:
                return Dialogs.TextEditors.COMMENT_PACKAGE_DIALOG;
            case PROJECT:
                return Dialogs.TextEditors.COMMENT_PROJECT_DIALOG;
            case VARIABLE:
                return Dialogs.TextEditors.COMMENT_VARIABLE_DIALOG;
            case WELCOME:
            default:
                return Dialogs.TextEditors.COMMENT_PROJECT_DIALOG;
        }
    }

    public static Dialogs.TextInputs getRenameDialog(ItemType type) {
        switch (type) {
            case CLASS:
                return Dialogs.TextInputs.RENAME_CLASS_DIALOG;
            case FUNCTION:
                return Dialogs.TextInputs.RENAME_FUNCTION_DIALOG;
            case PACKAGE:
                return Dialogs.TextInputs.RENAME_PACKAGE_DIALOG;
            case PROJECT:
                return Dialogs.TextInputs.RENAME_PROJECT_DIALOG;
            case VARIABLE:
                return Dialogs.TextInputs.RENAME_VARIABLE_DIALOG;
            case WELCOME:
            default:
                return Dialogs.TextInputs.RENAME_PROJECT_DIALOG;
        }
    }

    public static Dialogs.Confirmations getDeleteDialog(ItemType type) {
        switch (type) {
            case CLASS:
                return Dialogs.Confirmations.DELETE_CLASS_DIALOG;
            case FUNCTION:
                return Dialogs.Confirmations.DELETE_FUNCTION_DIALOG;
            case PACKAGE:
                return Dialogs.Confirmations.DELETE_PACKAGE_DIALOG;
            case PROJECT:
                return Dialogs.Confirmations.DELETE_PROJECT_DIALOG;
            case VARIABLE:
                return Dialogs.Confirmations.DELETE_VARIABLE_DIALOG;
            case WELCOME:
            default:
                return Dialogs.Confirmations.DELETE_PROJECT_DIALOG;
        }
    }

    public static Icon getAddChildrenIcon(ItemType type) {
        switch (type) {
            case CLASS:
                return Icon.PLUSCLASS;
            case FUNCTION:
                return Icon.PLUSFUNCTION;
            case PACKAGE:
                return Icon.PLUSPACKAGE;
            case PROJECT:
                return Icon.PLUSPROJECT;
            case VARIABLE:
                return Icon.PLUSVARIABLE;
            case WELCOME:
            default:
                return Icon.PLUS;
        }
    }

    public static Pane getItemContent(Item<?, ?, ?> item, ItemType... allowedChildrenTypes) {
          var borderPaneRoot = new BorderPane();
          var gridPaneChildren = new GridPane();
        gridPaneChildren.setVgap(20);
        gridPaneChildren.setHgap(20);
        gridPaneChildren.setAlignment(Pos.TOP_CENTER);
        gridPaneChildren.setPadding(new Insets(50));

        setChildContent(item, gridPaneChildren);

          var colIcon = new ColumnConstraints();
        colIcon.setHalignment(HPos.LEFT);
          var colText = new ColumnConstraints();
        colText.setHalignment(HPos.LEFT);
        colText.setFillWidth(true);
        colText.setHgrow(Priority.ALWAYS);
          var colButtonComments = new ColumnConstraints();
        colButtonComments.setHalignment(HPos.RIGHT);
          var colButtonRename = new ColumnConstraints();
        colButtonRename.setHalignment(HPos.RIGHT);
          var colButtonDelete = new ColumnConstraints();
        colButtonDelete.setHalignment(HPos.RIGHT);
        gridPaneChildren.getColumnConstraints().addAll(colIcon, colText, colButtonComments, colButtonRename, colButtonDelete);

          var gridPanePackageButtons = new GridPane();
        gridPanePackageButtons.setHgap(20);
        gridPanePackageButtons.setPadding(new Insets(50));

        List<Button> buttonRow = new ArrayList<>();
        for (ItemType allowedChildrenType : allowedChildrenTypes) {
              var wordButtonText = getAddChildrenWord(allowedChildrenType);
              var iconButtonGraphic = getAddChildrenIcon(allowedChildrenType);
              var dialogAddChildren = getAddChildrenDialog(allowedChildrenType);
              var buttonAddChildren = createButton(wordButtonText, iconButtonGraphic, (t) -> {
                Optional<String> name = Dialogs.showTextInputDialog(dialogAddChildren, item.getSite().getForbittenChildNames());
                if (name.isPresent()) {
                    Item.createItem(allowedChildrenType, item, name.get(), true);
                }
            });
            buttonRow.add(buttonAddChildren);
        }

          var wordComment = getCommentWord(item.getType());
          var dialogComment = getCommentDialog(item.getType());
          var buttonComment = createButton(wordComment, Icon.COMMENTS, (t) -> {
            Optional<String> comments = Dialogs.showTextEditorDialog(dialogComment, item.getComments());
            if (comments.isPresent()) {
                item.setComments(comments.get());
            }
        }, false, true);
        buttonRow.add(buttonComment);
          var wordRename = getRenameWord(item.getType());
          var dialogRename = getRenameDialog(item.getType());
          var buttonRename = createButton(wordRename, Icon.RENAME, (t) -> {
            Optional<String> name = Dialogs.showTextInputDialog(dialogRename, item.getName(), item.getParent().map(p -> p.getSite().getForbittenChildNames()).orElse(Set.of()));
            if (name.isPresent()) {
                item.setName(name.get());
            }
        }, false, true);
        buttonRow.add(buttonRename);
          var wordDelete = getDeleteWord(item.getType());
          var dialogDelete = getDeleteDialog(item.getType());
          var buttonDelete = createButton(wordDelete, Icon.TRASH, (t) -> {
            Optional<Boolean> confirmed = Dialogs.showConfirmationDialog(dialogDelete, "(#[^\\s]+)", item.getName());
            if (confirmed.isPresent() && confirmed.get()) {
                item.destroy();
            }
        }, false, true);
        buttonRow.add(buttonDelete);

        for (  var i = 0; i < buttonRow.size(); i++) {
            gridPanePackageButtons.add(buttonRow.get(i), i, 0);
              var colAddChildren = new ColumnConstraints();
            colAddChildren.setHalignment(HPos.LEFT);
            if (i + 1 == allowedChildrenTypes.length) {
                colAddChildren.setFillWidth(true);
                colAddChildren.setHgrow(Priority.ALWAYS);
            }
            gridPanePackageButtons.getColumnConstraints().add(colAddChildren);
        }

          var colComment = new ColumnConstraints();
        colComment.setHalignment(HPos.RIGHT);
          var colRename = new ColumnConstraints();
        colRename.setHalignment(HPos.RIGHT);
          var colDelete = new ColumnConstraints();
        colDelete.setHalignment(HPos.RIGHT);
        gridPanePackageButtons.getColumnConstraints().addAll(colComment, colRename, colDelete);

          var scrollPaneChildren = new ScrollPane(gridPaneChildren);
        scrollPaneChildren.setFitToHeight(true);
        scrollPaneChildren.setFitToWidth(true);
        borderPaneRoot.setCenter(scrollPaneChildren);
        borderPaneRoot.setBottom(gridPanePackageButtons);
        return borderPaneRoot;
    }

    public static void setChildContent(Item<?, ?, ?> item, GridPane gridPaneChildren) {
        BindUtils.addListener(item.childrenProperty(), (prop, oldChilds, childs) -> {
            gridPaneChildren.getChildren().clear();
              var rowCounter = 0;
            for (Item<?, ?, ?> c : childs) {
                Runnable onClick = () -> c.getSite().select();

                  var icon = Icons.get(c.getSite().getTabIcon(), 50);
                icon.setOnMouseClicked(e -> onClick.run());

                  var textName = new Text();
                  var shadowEffect = new DropShadow(10, Color.WHITESMOKE);
                shadowEffect.setSpread(0.5);
                textName.setEffect(shadowEffect);
                textName.setFont(Font.font(30));
                textName.textProperty().bindBidirectional(c.nameProperty());
                textName.setOnMouseClicked(e -> onClick.run());

                  var wordComment = SiteUtils.getCommentWord(c.getType());
                  var wordRename = SiteUtils.getRenameWord(c.getType());
                  var wordDelete = SiteUtils.getDeleteWord(c.getType());

                  var dialogComment = SiteUtils.getCommentDialog(c.getType());
                  var dialogRename = SiteUtils.getRenameDialog(c.getType());
                  var dialogDelete = SiteUtils.getDeleteDialog(c.getType());

                  var buttonComment = createButton(wordComment, Icon.COMMENTS, (t) -> {
                      var comments = Dialogs.showTextEditorDialog(dialogComment, c.getComments());
                    if (comments.isPresent()) {
                        c.setComments(comments.get());
                    }
                }, false, true);

                  var buttonRename = createButton(wordRename, Icon.RENAME, (t) -> {
                    Optional<String> name = Dialogs.showTextInputDialog(dialogRename, c.getName(), item.getSite().getForbittenChildNames(c.getName()));
                    if (name.isPresent()) {
                        c.setName(name.get());
                    }
                }, false, true);

                  var buttonDelete = createButton(wordDelete, Icon.TRASH, (t) -> {
                      var confirmed = Dialogs.showConfirmationDialog(dialogDelete, "(#[^\\s]+)", c.getName());
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
            }
        });
    }

    public static Button createButton(Word buttonText, Consumer<ActionEvent> onAction) {
        return createButton(buttonText, null, onAction);
    }

    public static Button createButton(Icon icon, Consumer<ActionEvent> onAction) {
        return createButton(null, icon, onAction);
    }

    public static Button createButton(Word buttonText, Icon icon, Consumer<ActionEvent> onAction) {
        return createButton(buttonText, icon, onAction, true, true);
    }

    public static Button createButton(Word buttonText, Icon icon, Consumer<ActionEvent> onAction, boolean withText, boolean withTooltip) {
          var buttonAdd = new Button();
        if (buttonText != null) {
            if (withText) {
                Language.set(buttonText, buttonAdd);
            } else if (withTooltip) {
                Language.setCustom(buttonText, s -> buttonAdd.setTooltip(new Tooltip(s)));
            }
        }
        if (icon != null) {
            Icons.set(icon, buttonAdd);
        }
        if (onAction != null) {
            buttonAdd.setOnAction(e -> onAction.accept(e));
        }
        return buttonAdd;
    }
}
