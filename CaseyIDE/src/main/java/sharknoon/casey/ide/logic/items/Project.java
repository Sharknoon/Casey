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
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.*;
import sharknoon.casey.ide.MainApplication;
import sharknoon.casey.ide.logic.CompileLanguage;
import sharknoon.casey.ide.misc.Executor.ExecutorBuilder;
import sharknoon.casey.ide.serial.Serialisation;
import sharknoon.casey.ide.ui.dialogs.*;
import sharknoon.casey.ide.ui.misc.*;
import sharknoon.casey.ide.ui.sites.Site;
import sharknoon.casey.ide.ui.sites.welcome.RecentProject;
import sharknoon.casey.ide.ui.styles.Styles;
import sharknoon.casey.ide.utils.language.*;
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
    private static ObjectProperty<CompileLanguage> currentCompileLanguage = new SimpleObjectProperty<>(CompileLanguage.Java);
    
    public static Optional<Project> getCurrentProject() {
        return Optional.ofNullable(currentProject.get());
    }
    
    public static ObjectProperty<Project> currentProjectProperty() {
        return currentProject;
    }
    
    public static CompileLanguage getCurrentCompileLanguage() {
        return currentCompileLanguage.get();
    }
    
    public static ObjectProperty<CompileLanguage> currentCompileLanguageProperty() {
        return currentCompileLanguage;
    }
    
    private final ObjectProperty<Path> saveFile = new SimpleObjectProperty<>();
    private String id;
    private Runnable onFinish;
    
    protected Project(Welcome parent, String name) {
        superInit(parent, name);
        init();
    }
    
    private Runnable showInputOutputWindow(String title, Icon icon, Consumer<String> inputLine, ObjectExpression<Text> errorAndOutputLine, Consumer<Boolean> abortProcess) {
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
        Icons.getImage(icon).ifPresent(newWindow.getIcons()::add);
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
    
    public Path forceGetSaveFile() {
        requestSaveFile();
        return saveFile.get();
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
                commands.add(currentCompileLanguage.get().name().toLowerCase());
                commands.add("-p");
                commands.add(String.valueOf(caseyFile));
                commands.add("-f");
                commands.add(currentItem.getFullName());
                //For leaving out the comments
                //commands.add("-i");
                if (parameterValues.size() > 0) {
                    commands.add("-pa");
                }
                parameterValues
                        .entrySet()
                        .stream()
                        .map(e -> e.getKey() + "=" + e.getValue())
                        .forEach(commands::add);
                boolean success = ExecutorBuilder.executeJar(optionalCaseyCompiler.get())
                        .setOutputConsumer(statusProperty)
                        .setErrorConsumer(statusProperty)
                        .setArgs(commands)
                        .setExpectedExitValues(0)
                        .execute()
                        .join() == 0;
                if (success) {
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
                    Platform.runLater(() -> onFinish = showInputOutputWindow(currentItem.getName(), currentItem.getSite().getTabIcon(), input, errorAndOutputProperty, abortProperty::set));
                    ExecutorBuilder.executeClass(basePath.toAbsolutePath(), currentItem.getFullName())
                            .setOutputConsumer(output)
                            .setErrorConsumer(error)
                            .setInput(inputProperty)
                            .setAbortProcess(abortProperty)
                            .execute()
                            .join();
                    if (onFinish != null) {
                        onFinish.run();
                    }
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
    
    private void requestSaveFile() {
        if (saveFile.get() == null) {//If the programm has no path (hasnt been saved yet)
            //Get standard save location
            Path projectsDirectory = Resources.createAndGetDirectory("/Projects", false);
            if (projectsDirectory != null) {
                saveFile.set(projectsDirectory.resolve(nameProperty().get() + ".casey"));
                //Ask the user for a location (should nearly never happen)
            } else {
                FileChooser chooser = new FileChooser();
                chooser.setInitialDirectory(Resources.getPublicPath().resolve("Projects").toFile());
                chooser.setInitialFileName(nameProperty().get());
                chooser.setTitle(Language.get(Word.SAVE_DIALOG_TITLE));
                chooser.getExtensionFilters().add(
                        new FileChooser.ExtensionFilter(Language.get(Word.SAVE_DIALOG_EXTENSION_FILTER_CASEY_PROJECT), "*.casey")
                );
                Window ownerWindow = Stage.getWindows().size() > 0 ? Stage.getWindows().get(0) : null;
                File file = chooser.showSaveDialog(ownerWindow);
                if (file != null) {//User may closed the dialog
                    saveFile.set(file.toPath());
                }
            }
            if (saveFile.get() == null) {
                ExceptionDialog.show("Could not save File!", new Exception());
            }
        }
    }
    
    
}
