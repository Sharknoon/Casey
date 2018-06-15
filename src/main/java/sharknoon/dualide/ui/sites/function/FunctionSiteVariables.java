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
package sharknoon.dualide.ui.sites.function;

import java.util.Optional;
import java.util.Set;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import sharknoon.dualide.logic.items.Function;
import sharknoon.dualide.logic.items.Item;
import sharknoon.dualide.logic.items.ItemType;
import sharknoon.dualide.logic.items.Variable;
import sharknoon.dualide.ui.dialogs.Dialogs;
import sharknoon.dualide.ui.misc.Icon;
import sharknoon.dualide.ui.sites.SiteUtils;
import sharknoon.dualide.utils.language.Word;

/**
 *
 * @author Josua Frank
 */
public class FunctionSiteVariables {

    private final FunctionSite functionSite;
    private BorderPane borderPaneRoot;
    private GridPane gridPaneChildren;

    public FunctionSiteVariables(FunctionSite functionSite) {
        this.functionSite = functionSite;
    }

    private void init() {
        borderPaneRoot = new BorderPane();
        gridPaneChildren = new GridPane();
        gridPaneChildren.setVgap(20);
        gridPaneChildren.setHgap(20);
        gridPaneChildren.setAlignment(Pos.TOP_CENTER);
        gridPaneChildren.setPadding(new Insets(50));

        SiteUtils.setChildContent(functionSite.getItem(), gridPaneChildren);

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

        Button buttonAddVariable = SiteUtils.createButton(Word.FUNCTION_SITE_ADD_VARIABLE_BUTTON_TEXT, Icon.PLUSVARIABLE, (t) -> {
            Optional<String> name = Dialogs.showTextInputDialog(Dialogs.TextInputs.NEW_VARIABLE_DIALOG, functionSite.getForbittenChildNames());
            if (name.isPresent()) {
                Variable var = Item.createItem(ItemType.VARIABLE, functionSite.getItem(), name.get(), true);
            }
        });
        Button buttonComment = SiteUtils.createButton(Word.FUNCTION_SITE_COMMENT_BUTTON_TEXT, Icon.COMMENTS, (t) -> {
            Optional<String> comments = Dialogs.showTextEditorDialog(Dialogs.TextEditors.COMMENT_CLASS_DIALOG, functionSite.getItem().getComments());
            if (comments.isPresent()) {
                functionSite.getItem().setComments(comments.get());
            }
        }, false, true);
        Button buttonRename = SiteUtils.createButton(Word.FUNCTION_SITE_RENAME_BUTTON_TEXT, Icon.RENAME, (t) -> {
            Optional<String> name = Dialogs.showTextInputDialog(Dialogs.TextInputs.RENAME_CLASS_DIALOG, functionSite.getItem().getName(), functionSite.getItem().getParent().map(p -> p.getSite().getForbittenChildNames()).orElse(Set.of()));
            if (name.isPresent()) {
                functionSite.getItem().setName(name.get());
            }
        }, false, true);
        Button buttonDelete = SiteUtils.createButton(Word.FUNCTION_SITE_DELETE_BUTTON_TEXT, Icon.TRASH, (t) -> {
            Optional<Boolean> confirmed = Dialogs.showConfirmationDialog(Dialogs.Confirmations.DELETE_CLASS_DIALOG, "#CLASS", functionSite.getItem().getName());
            if (confirmed.isPresent() && confirmed.get()) {
                functionSite.getItem().destroy();
            }
        }, false, true);

        gridPanePackageButtons.addRow(0,
                buttonAddVariable,
                buttonComment,
                buttonRename,
                buttonDelete
        );

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
        gridPanePackageButtons.getColumnConstraints().addAll(colAddVariable, colComment, colRename, colDelete);

        ScrollPane scrollPaneChildren = new ScrollPane(gridPaneChildren);
        scrollPaneChildren.setFitToHeight(true);
        scrollPaneChildren.setFitToWidth(true);
        borderPaneRoot.setCenter(scrollPaneChildren);
        borderPaneRoot.setBottom(gridPanePackageButtons);
    }

    public Pane getTabContentPane() {
        if (borderPaneRoot == null) {
            init();
        }
        return borderPaneRoot;
    }

}
