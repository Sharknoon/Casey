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
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import sharknoon.casey.ide.logic.items.Item;
import sharknoon.casey.ide.logic.items.ItemType;
import sharknoon.casey.ide.logic.items.Project;
import sharknoon.casey.ide.ui.fields.ValueField;
import sharknoon.casey.ide.ui.misc.Icon;
import sharknoon.casey.ide.ui.misc.Icons;
import sharknoon.casey.ide.ui.sites.Site;
import sharknoon.casey.ide.ui.styles.Styles;
import sharknoon.casey.ide.utils.language.Language;
import sharknoon.casey.ide.utils.language.Word;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * @author Josua Frank
 */
public class ToolBarInit {
    
    private static ToolBar toolBar;
    private static BooleanProperty running = new SimpleBooleanProperty();
    
    public static void init(ToolBar toolBar) {
        ToolBarInit.toolBar = toolBar;
        initSaveButton();
        initRunButton();
        tmp();
    }
    
    private static void initSaveButton() {
        Button buttonSave = new Button();
        Icons.set(Icon.SAVE, buttonSave);
        Language.set(Word.TOOLBAR_BUTTON_SAVE_TEXT, buttonSave);
        buttonSave.setOnAction(e -> Project.getCurrentProject().ifPresent(Project::save));
        buttonSave.disableProperty().bind(Project.currentProjectProperty().isNull());
        toolBar.getItems().add(buttonSave);
    }
    
    private static void initRunButton() {
        Button buttonRun = new Button();
        Label labelRunStatus = new Label();
        labelRunStatus.visibleProperty().bind(running);
        Consumer<String> statusConsumer = s -> {
            Platform.runLater(() -> {
                labelRunStatus.setText(s);
            });
        };
        Icons.set(Icon.RUN, buttonRun);
        Language.set(Word.TOOLBAR_BUTTON_RUN_TEXT, buttonRun);
        Language.setCustom(Word.TOOLBAR_BUTTON_RUN_TOOLTIP, s -> buttonRun.setTooltip(new Tooltip(s)));
        buttonRun.setOnAction(e -> {
            running.set(true);
            labelRunStatus.setText("");
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
        toolBar.getItems().addAll(buttonRun, labelRunStatus);
    }
    
    public static void tmp() {
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
        toolBar.getItems().add(buttonTMP);
    }
    
}
