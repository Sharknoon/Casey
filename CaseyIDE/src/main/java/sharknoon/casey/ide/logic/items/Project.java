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
package sharknoon.casey.ide.logic.items;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;
import javafx.application.Platform;
import javafx.beans.binding.ObjectExpression;
import javafx.beans.property.*;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import sharknoon.casey.ide.misc.Executor;
import sharknoon.casey.ide.serial.Serialisation;
import sharknoon.casey.ide.ui.MainApplication;
import sharknoon.casey.ide.ui.dialogs.Dialogs;
import sharknoon.casey.ide.ui.misc.Icon;
import sharknoon.casey.ide.ui.misc.Icons;
import sharknoon.casey.ide.ui.sites.Site;
import sharknoon.casey.ide.ui.sites.welcome.RecentProject;
import sharknoon.casey.ide.ui.styles.Styles;
import sharknoon.casey.ide.utils.language.Language;
import sharknoon.casey.ide.utils.language.Word;
import sharknoon.casey.ide.utils.settings.Resources;

import java.io.File;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author Josua Frank
 */
public class Project extends Item<Project, Item, Package> {
    
    private static final String ID = "id";
    private static ObjectProperty<Project> currentProject = new SimpleObjectProperty<>();
    
    public static Optional<Project> getCurrentProject() {
        return Optional.ofNullable(currentProject.get());
    }
    
    public static ObjectProperty<Project> currentProjectProperty() {
        return currentProject;
    }
    
    private final ObjectProperty<Path> saveFile = new SimpleObjectProperty<>();
    private String id;
    private Runnable onFinish;
    
    protected Project(Welcome parent, String name) {
        superInit(parent, name);
        init();
    }
    
    /**
     * Executes the compiled version of this item
     *
     * @param basePath The base path where the .casey file is in
     * @param mainItem The item which should be executed
     * @return
     */
    public boolean execute(Path basePath, Item mainItem) {
        Path workingDirectory = basePath.toAbsolutePath();
        String mainClass = mainItem.getFullName();
        ObjectProperty<Text> errorAndOutputProperty = new SimpleObjectProperty<>();
        Consumer<String> error = s -> {
            Text t = new Text(s + "\n");
            t.setFill(Color.RED);
            errorAndOutputProperty.set(t);
        };
        Consumer<String> output = s -> {
            Text t = new Text(s + "\n");
            errorAndOutputProperty.set(t);
        };
        StringProperty inputProperty = new SimpleStringProperty();
        Consumer<String> input = inputProperty::set;
        BooleanProperty abortProperty = new SimpleBooleanProperty();
        Platform.runLater(() -> onFinish = showInputOutputWindow(mainItem.getName(), mainItem.getSite().getTabIcon(), input, errorAndOutputProperty, abortProperty::set));
        int status = Executor.runClass(workingDirectory, mainClass, output, error, inputProperty, abortProperty).join();
        if (onFinish != null) {
            onFinish.run();
        }
        return status == 0;
    }
    
    public Runnable showInputOutputWindow(String title, Icon icon, Consumer<String> inputLine, ObjectExpression<Text> errorAndOutputLine, Consumer<Boolean> abortProcess) {
        BorderPane root = new BorderPane();
        
        TextFlow outputs = new TextFlow();
        errorAndOutputLine.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                Platform.runLater(() -> outputs.getChildren().add(newValue));
            }
        });
        ScrollPane sc = new ScrollPane(outputs);
        sc.setPannable(true);
        outputs.heightProperty().addListener(o -> sc.setVvalue(1));
        root.setCenter(sc);
        
        TextField inputs = new TextField();
        inputs.setOnAction(ae -> {
            inputLine.accept(inputs.getText());
            inputLine.accept(null);
            inputs.clear();
        });
        root.setBottom(inputs);
        
        Scene newScene = new Scene(root, 650, 400);
        Styles.bindStyleSheets(newScene.getStylesheets());
        Stage newWindow = new Stage();
        newWindow.setTitle(title);
        newWindow.getIcons().add(Icons.getImage(icon).orElse(null));
        newWindow.setScene(newScene);
        newWindow.setOnCloseRequest(event -> {
            if (abortProcess != null) {
                abortProcess.accept(true);
            }
        });
        Text textProgramEnded = new Text(Language.get(Word.INPUT_OUTPUT_WINDOW_PROGRAM_ENDED));
        newWindow.show();
        return () -> Platform.runLater(() -> {
            outputs.getChildren().add(textProgramEnded);
            inputs.setOnAction(ae -> {
            });
            inputs.setOnKeyPressed(event -> newWindow.close());
        });
    }
    
    private void init() {
        currentProject.set(this);
        MainApplication.registerExitable(this::save);
    }
    
    public final String getID() {
        return id;
    }
    
    public void setID(String id) {
        this.id = id;
    }
    
    public Optional<Path> getSaveFile() {
        return Optional.ofNullable(saveFile.get());
    }
    
    public void setSaveFile(Path path) {
        if (path != null) {
            saveFile.set(path);
        }
    }
    
    @Override
    public Map<String, JsonNode> getAdditionalProperties() {
        Map<String, JsonNode> result = new HashMap<>();
        TextNode idNode = TextNode.valueOf(getID());
        result.put("id", idNode);
        return result;
    }
    
    @Override
    public void setAdditionalProperties(Map<String, JsonNode> properties) {
        properties.forEach((k, v) -> {
            switch (k) {
                case ID:
                    id = v.asText();
                    break;
            }
        });
    }
    
    public void close() {
        save();
        currentProject.set(null);
    }
    
    public void save() {
        requestSaveFile();
        if (saveFile.get() != null) {//If the user closes the project without saving
            RecentProject.updateProject(this);
            Serialisation.saveProject(this);
        }
    }
    
    public CompletableFuture<Void> run(Consumer<String> statusProperty) {
        save();
        Item<?, ?, ?> currentItem = Site.currentSelectedProperty().get();
        if (saveFile.get() != null && currentItem != null) {
            if (!(currentItem instanceof Function)) {
                //Shouldn't happen
                return CompletableFuture.completedFuture(null);
            }
            Function f = (Function) currentItem;
            Map<String, String> parameterValues = requestParameters(f);
            if (parameterValues == null) {
                //User canceled the input
                return CompletableFuture.completedFuture(null);
            }
            Path caseyFile = saveFile.get();
            Path basePath = saveFile.get().getParent();
            return CompletableFuture.runAsync(() -> {
                Optional<Path> optionalCaseyCompiler = Resources.getFile("sharknoon/casey/ide/CaseyCOMPILER.jar", true);
                if (!optionalCaseyCompiler.isPresent()) {
                    return;
                }
                List<String> commands = new ArrayList<>();
                commands.add("-l");
                commands.add("java");
                commands.add("-p");
                commands.add(String.valueOf(caseyFile));
                commands.add("-f");
                commands.add(currentItem.getFullName());
                if (parameterValues.size() > 0) {
                    commands.add("-pa");
                }
                parameterValues
                        .entrySet()
                        .stream()
                        .map(e -> e.getKey() + "=" + e.getValue())
                        .forEach(commands::add);
                boolean success = Executor.runJar(
                        optionalCaseyCompiler.get(),
                        statusProperty,
                        statusProperty,
                        null,
                        null,
                        commands.toArray(new String[0])
                ).join() == 0;
                if (success) {
                    execute(basePath, currentItem);
                }
            });
        }
        return CompletableFuture.completedFuture(null);
    }
    
    private Map<String, String> requestParameters(Function f) {
        GridPane root = new GridPane();
        root.setHgap(10);
        root.setVgap(5);
        
        List<TextField> textFields = f.getChildren().stream()
                .filter(c -> c.getType() == ItemType.PARAMETER)
                .map(p -> (Parameter) p)
                .map(p -> {
                    Label labelParameterName = new Label(p.getName());
                    TextField textFieldParameterValue = new TextField();
                    textFieldParameterValue.setId(p.getName());
                    textFieldParameterValue.setPromptText(p.getReturnType().createEmptyValue(null).toString());
                    root.addRow(root.getRowCount(), labelParameterName, textFieldParameterValue);
                    return textFieldParameterValue;
                })
                .collect(Collectors.toList());
    
        if (textFields.isEmpty()) {
            return Map.of();
        } else {
            Platform.runLater(() -> textFields.get(0).requestFocus());
        }
        
        Optional<GridPane> result = Dialogs.showCustomInputDialog(
                Word.COMPILER_ENTER_PARAMETER_VALUES_DIALOG_TITLE,
                Word.COMPILER_ENTER_PARAMETER_VALUES_DIALOG_HEADER_TEXT,
                Word.COMPILER_ENTER_PARAMETER_VALUES_DIALOG_CONTENT_TEXT,
                Icon.PARAMETER,
                root,
                gridPane -> gridPane);
        
        if (result.isPresent()) {
            return textFields.stream().collect(Collectors.toMap(Node::getId, textField -> textField.getText().isEmpty() ? textField.getPromptText() : textField.getText()));
        } else {
            return null;
        }
    }
    
    public void requestSaveFile() {
        if (saveFile.get() == null) {//If the programm has no path (hasnt been saved yet)
            FileChooser chooser = new FileChooser();
            chooser.setInitialDirectory(new File(System.getProperty("user.home")));
            chooser.setInitialFileName(nameProperty().get());
            chooser.setTitle(Language.get(Word.SAVE_DIALOG_TITLE));
            chooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter(Language.get(Word.SAVE_DIALOG_EXTENSION_FILTER_CASEY_PROJECT), "*.casey")
            );
            Window ownerWindow = Stage.getWindows().size() > 0 ? Stage.getWindows().get(0) : null;
            File file = chooser.showSaveDialog(ownerWindow);
            if (file != null) {
                saveFile.set(file.toPath());
            }
        }
    }
    
    
}
