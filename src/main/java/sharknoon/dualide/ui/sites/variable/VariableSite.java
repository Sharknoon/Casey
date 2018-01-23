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
package sharknoon.dualide.ui.sites.variable;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import sharknoon.dualide.logic.Item;
import sharknoon.dualide.logic.Variable;
import sharknoon.dualide.logic.Class;
import sharknoon.dualide.ui.ItemTabPane;
import sharknoon.dualide.ui.ItemTreeView;
import sharknoon.dualide.ui.misc.Icon;
import sharknoon.dualide.ui.sites.Dialogs;
import sharknoon.dualide.ui.sites.Site;
import sharknoon.dualide.utils.language.Language;
import sharknoon.dualide.utils.language.Word;

/**
 *
 * @author Josua Frank
 */
public class VariableSite extends Site<Variable> {

    private BorderPane borderPaneRoot;
    private final GridPane gridPaneContent = new GridPane();

    private void init() {
        borderPaneRoot = new BorderPane();
        gridPaneContent.setVgap(20);
        gridPaneContent.setHgap(20);
        gridPaneContent.setAlignment(Pos.TOP_CENTER);
        gridPaneContent.setPadding(new Insets(50));

        setContent();

        ColumnConstraints colClass = new ColumnConstraints();
        colClass.setHalignment(HPos.LEFT);
        ColumnConstraints colName = new ColumnConstraints();
        colName.setHalignment(HPos.LEFT);
        colName.setFillWidth(true);
        colName.setHgrow(Priority.ALWAYS);
        gridPaneContent.getColumnConstraints().addAll(colClass, colName);

        GridPane gridPaneVariableButtons = new GridPane();
        gridPaneVariableButtons.setHgap(20);
        gridPaneVariableButtons.setPadding(new Insets(50));

        Button buttonComment = createButton(Word.VARIABLE_SITE_COMMENT_BUTTON_TEXT, Icon.COMMENTS, (t) -> {
            Optional<String> comments = Dialogs.showTextEditorDialog(Dialogs.TextEditors.COMMENT_VARIABLE_DIALOG, getItem().getComments());
            if (comments.isPresent()) {
                getItem().setComments(comments.get());
            }
        }, false, true);
        Button buttonRename = createButton(Word.VARIABLE_SITE_RENAME_BUTTON_TEXT, Icon.RENAME, (t) -> {
            Optional<String> name = Dialogs.showTextInputDialog(Dialogs.TextInputs.RENAME_VARIABLE_DIALOG);
            if (name.isPresent()) {
                getItem().setName(name.get());
            }
        }, false, true);
        Button buttonDelete = createButton(Word.VARIABLE_SITE_DELETE_BUTTON_TEXT, Icon.TRASH, (t) -> {
            Optional<Boolean> confirmed = Dialogs.showConfirmationDialog(Dialogs.Confirmations.DELETE_VARIABLE_DIALOG, "#VARIABLE", getItem().getName());
            if (confirmed.isPresent() && confirmed.get()) {
                getItem().destroy();
                ItemTabPane.closeAllTabs();
                ItemTreeView.closeAllItems();
            }
        }, false, true);

        gridPaneVariableButtons.addRow(0,
                buttonComment,
                buttonRename,
                buttonDelete
        );

        ColumnConstraints colComment = new ColumnConstraints();
        colComment.setHalignment(HPos.RIGHT);
        colComment.setFillWidth(true);
        colComment.setHgrow(Priority.ALWAYS);
        colComment.setHalignment(HPos.RIGHT);
        ColumnConstraints colRename = new ColumnConstraints();
        colRename.setHalignment(HPos.RIGHT);
        ColumnConstraints colDelete = new ColumnConstraints();
        colDelete.setHalignment(HPos.RIGHT);
        gridPaneVariableButtons.getColumnConstraints().addAll(colComment, colRename, colDelete);

        ScrollPane scrollPanePackages = new ScrollPane(gridPaneContent);
        scrollPanePackages.setFitToHeight(true);
        scrollPanePackages.setFitToWidth(true);
        borderPaneRoot.setCenter(scrollPanePackages);
        borderPaneRoot.setBottom(gridPaneVariableButtons);
    }

    int rowCounter = 0;

    private void onClicked(Item item) {
        ItemTreeView.selectItem(item);
    }

    public VariableSite(Variable item) {
        super(item);
    }

    private void setContent() {
        Label labelClass = new Label();
        Language.set(Word.VARIABLE_SITE_CLASS_LABEL_TEXT, labelClass);

        Label labelName = new Label();
        Language.set(Word.VARIABLE_SITE_NAME_LABEL_TEXT, labelClass);

        gridPaneContent.addRow(0, labelClass, labelName);

        ComboBox<Class> comboBoxClasses = new ComboBox<>();
        comboBoxClasses.itemsProperty().bindBidirectional(Class.classesProperty());
        comboBoxClasses.valueProperty().bindBidirectional(getItem().classProperty());
        getItem().nameProperty().addListener((observable, oldValue, newValue) -> {
            comboBoxClasses.itemsProperty().bindBidirectional(Class.classesProperty());
        });

        TextField textFieldName = new TextField();
        textFieldName.textProperty().bindBidirectional(getItem().nameProperty());

        gridPaneContent.addRow(1, comboBoxClasses, textFieldName);
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
        return Icon.VARIABLE;
    }

    @Override
    public Icon getAddIcon() {
        return Icon.PLUSVARIABLE;
    }

}
