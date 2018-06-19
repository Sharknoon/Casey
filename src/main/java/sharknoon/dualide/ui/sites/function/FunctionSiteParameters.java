package sharknoon.dualide.ui.sites.function;
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

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import sharknoon.dualide.logic.items.Function;
import sharknoon.dualide.logic.items.Item;
import sharknoon.dualide.logic.items.ItemType;
import sharknoon.dualide.logic.types.PrimitiveType;
import sharknoon.dualide.ui.dialogs.Dialogs;
import sharknoon.dualide.ui.fields.TypeField;
import sharknoon.dualide.ui.misc.Icon;
import sharknoon.dualide.ui.sites.SiteUtils;
import sharknoon.dualide.utils.javafx.BindUtils;
import sharknoon.dualide.utils.language.Word;

import java.util.*;
import java.util.stream.Collectors;

public class FunctionSiteParameters {

    private final FunctionSite functionSite;
    private Pane paneRoot;

    public FunctionSiteParameters(FunctionSite functionSite) {
        this.functionSite = functionSite;
    }

    public Pane init() {
        var borderPaneRoot = new BorderPane();

        var childContent = getContent();
        var footer = getFooter();

        borderPaneRoot.setCenter(childContent);
        borderPaneRoot.setBottom(footer);
        return borderPaneRoot;

    }

    private ScrollPane getContent() {
        var gridPaneChildren = new GridPane();
        gridPaneChildren.setVgap(20);
        gridPaneChildren.setHgap(20);
        gridPaneChildren.setAlignment(Pos.TOP_CENTER);
        gridPaneChildren.setPadding(new Insets(50));

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

        BindUtils.addListener(functionSite.getItem().parametersProperty(), (prop, oldpars, pars) -> {
            gridPaneChildren.getChildren().clear();
            var rowCounter = 0;
            for (var par : pars) {
                var typeField = new TypeField();
                typeField.typeProperty().bindBidirectional(par.typeProperty());

                var textName = new Text();
                var shadowEffect = new DropShadow(10, Color.WHITESMOKE);
                shadowEffect.setSpread(0.5);
                textName.setEffect(shadowEffect);
                textName.setFont(Font.font(30));
                textName.textProperty().bind(par.nameProperty());

                var wordComment = Word.FUNCTION_SITE_COMMENT_PARAMETER_BUTTON_TEXT;
                var wordRename = Word.FUNCTION_SITE_RENAME_PARAMETER_BUTTON_TEXT;
                var wordDelete = Word.FUNCTION_SITE_DELETE_PARAMETER_BUTTON_TEXT;

                var dialogComment = Dialogs.TextEditors.COMMENT_PROJECT_DIALOG;
                var dialogRename = Dialogs.TextInputs.RENAME_PARAMETER_DIALOG;
                var dialogDelete = Dialogs.Confirmations.DELETE_PARAMETER_DIALOG;

                var buttonComment = SiteUtils.createButton(wordComment, Icon.COMMENTS, (t) -> {
                    var comments = Dialogs.showTextEditorDialog(dialogComment, par.getComments());
                    if (comments.isPresent()) {
                        par.commentsProperty().set(comments.get());
                    }
                }, false, true);

                var buttonRename = SiteUtils.createButton(wordRename, Icon.RENAME, (t) -> {
                    Optional<String> name = Dialogs.showTextInputDialog(dialogRename, par.getName(), getForbittenParameterNames(par.getName()), null);
                    if (name.isPresent()) {
                        par.nameProperty().set(name.get());
                    }
                }, false, true);

                var buttonDelete = SiteUtils.createButton(wordDelete, Icon.TRASH, (t) -> {
                    var confirmed = Dialogs.showConfirmationDialog(dialogDelete, Map.of(par.getClass().getSimpleName().toUpperCase(), par.getName()));
                    if (confirmed.isPresent() && confirmed.get()) {
                        functionSite.getItem().getParameters().remove(par);
                    }
                }, false, true);

                gridPaneChildren.addRow(rowCounter,
                        typeField,
                        textName,
                        buttonComment,
                        buttonRename,
                        buttonDelete
                );

                rowCounter++;
            }
        });
        var scrollPaneChildren = new ScrollPane(gridPaneChildren);
        scrollPaneChildren.setFitToHeight(true);
        scrollPaneChildren.setFitToWidth(true);
        return scrollPaneChildren;
    }

    private Node getFooter() {
        var gridPanePackageButtons = new GridPane();
        gridPanePackageButtons.setHgap(20);
        gridPanePackageButtons.setPadding(new Insets(50));

        var wordAddParameter = Word.FUNCTION_SITE_ADD_PARAMETER_BUTTON_TEXT;
        var dialogAddParameter = Dialogs.TextInputs.NEW_PARAMETER_DIALOG;
        var buttonAddParamter = SiteUtils.createButton(wordAddParameter, Icon.PLUSVARIABLE, (t) -> {
            Optional<String> name = Dialogs.showTextInputDialog(dialogAddParameter, getForbittenParameterNames(), null);
            if (name.isPresent()) {
                Function.Parameter par = new Function.Parameter(name.get(), PrimitiveType.TEXT, "");
                functionSite.getItem().getParameters().add(par);
            }
        }, true, true);
        gridPanePackageButtons.add(buttonAddParamter, 0, 0);
        return gridPanePackageButtons;
    }

    private Set<String> getForbittenParameterNames() {
        return getForbittenParameterNames(null);
    }

    private Set<String> getForbittenParameterNames(String ignoreMe) {
        Set<String> set = (Set<String>) functionSite
                .getItem()
                .getParameters()
                .stream()
                .map(i -> i.getName())
                .filter(n -> ignoreMe == null || !n.equals(ignoreMe))
                .collect(Collectors.toSet());
        set.addAll(PrimitiveType.getForbiddenNames());
        return set;
    }

    public Pane getTabContentPane() {
        if (paneRoot == null) {
            paneRoot = init();

        }
        return paneRoot;
    }

}
