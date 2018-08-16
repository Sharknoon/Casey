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
package sharknoon.casey.ide.ui.navigation;

import javafx.application.Platform;
import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import sharknoon.casey.ide.logic.CompileLanguage;
import sharknoon.casey.ide.logic.items.*;
import sharknoon.casey.ide.ui.fields.ValueField;
import sharknoon.casey.ide.ui.misc.*;
import sharknoon.casey.ide.ui.sites.Site;
import sharknoon.casey.ide.ui.styles.Styles;
import sharknoon.casey.ide.utils.language.*;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.*;

/**
 * @author Josua Frank
 */
public class ToolBarInit {
    
    private static BooleanProperty running = new SimpleBooleanProperty();
    private static StringProperty consoleText = new SimpleStringProperty();
    
    public static void init(ToolBar toolBar) {
        toolBar.getItems().addAll(
                initSaveButton(),
                initRunButton(),
                initLanguageSelectionChoiceBox(),
                //tmp(),
                initConsoleOutputLabel()
        );
        Platform.runLater(toolBar::requestFocus);
    }
    
    private static Button initSaveButton() {
        Button buttonSave = new Button();
        Icons.set(Icon.SAVE, buttonSave);
        Language.set(Word.TOOLBAR_BUTTON_SAVE_TEXT, buttonSave);
        buttonSave.setOnAction(e -> Project.getCurrentProject().ifPresent(Project::save));
        buttonSave.disableProperty().bind(Project.currentProjectProperty().isNull());
        buttonSave.setFocusTraversable(false);
        return buttonSave;
    }
    
    private static Button initRunButton() {
        Button buttonRun = new Button();
        Consumer<String> statusConsumer = s -> {
            Platform.runLater(() -> {
                if (consoleText != null) {
                    consoleText.set(s);
                }
            });
        };
        Icons.set(Icon.RUN, buttonRun);
        Language.set(Word.TOOLBAR_BUTTON_RUN_TEXT, buttonRun);
        Language.setCustom(Word.TOOLBAR_BUTTON_RUN_TOOLTIP, s -> buttonRun.setTooltip(new Tooltip(s)));
        buttonRun.setOnAction(e -> {
            running.set(true);
            if (consoleText != null) {
                consoleText.set("");
            }
            Optional<Project> currentProject = Project.getCurrentProject();
            if (currentProject.isPresent()) {
                Project p = currentProject.get();
                CompletableFuture<Void> finished = p.run(statusConsumer);
                finished.thenRun(() -> running.set(false));
            }
        });
        ObjectProperty<Item<?, ?, ?>> currentSite = Site.currentSelectedProperty();
        BooleanBinding enabledBinding =
                Bindings
                        .createBooleanBinding(
                                () -> currentSite.get() != null && currentSite.get().getType() == ItemType.FUNCTION,
                                currentSite
                        )
                        .and(
                                Bindings.createBooleanBinding(
                                        () -> currentSite.get() != null && currentSite.get().getParent().map(Item::getType).filter(t -> t == ItemType.PACKAGE).isPresent(),
                                        currentSite
                                )
                        )
                        .and(running.not());
        buttonRun.disableProperty().bind(enabledBinding.not());
        buttonRun.setFocusTraversable(false);
        return buttonRun;
    }
    
    private static ComboBox<CompileLanguage> initLanguageSelectionChoiceBox() {
        ComboBox<CompileLanguage> comboBox = new ComboBox<>();
        comboBox.getItems().addAll(EnumSet.allOf(CompileLanguage.class));
        Supplier<ListCell<CompileLanguage>> cellSupplier = () -> new ListCell<>() {
            @Override
            protected void updateItem(CompileLanguage item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    setGraphic(Icons.get(Icon.forName(item.name())));
                    setText(item.name());
                    setPadding(new Insets(4, 0, 4, 4));
                }
            }
        };
        comboBox.setCellFactory(listView -> cellSupplier.get());
        comboBox.setButtonCell(cellSupplier.get());
        comboBox.getSelectionModel().selectFirst();
        Project.currentCompileLanguageProperty().bind(comboBox.getSelectionModel().selectedItemProperty());
        comboBox.setFocusTraversable(false);
        return comboBox;
    }
    
    private static Button tmp() {
        Button buttonTMP = new Button("Debug");
        buttonTMP.setOnAction(e -> {
            ValueField vf = new ValueField();
            TextFlow t = new TextFlow();
            Bindings.bindContent(t.getChildren(), vf.toText());
            BorderPane root = new BorderPane(new Group(new VBox(vf, t)));
            Scene scene = new Scene(root, 1000, 500);
            Styles.bindStyleSheets(scene.getStylesheets());
            Stage s = new Stage();
            s.setScene(scene);
            s.show();
        });
        return buttonTMP;
    }
    
    private static Label initConsoleOutputLabel() {
        Label labelRunStatus = new Label();
        labelRunStatus.visibleProperty().bind(running);
        consoleText = labelRunStatus.textProperty();
        return labelRunStatus;
    }
}
