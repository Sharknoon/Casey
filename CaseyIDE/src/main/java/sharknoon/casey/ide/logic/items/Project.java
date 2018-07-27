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
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.apache.commons.exec.*;
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
import sharknoon.casey.ide.utils.settings.Logger;
import sharknoon.casey.ide.utils.settings.Resources;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author Josua Frank
 */
public class Project extends Item<Project, Item, Package> {
    
    private static final String ID = "id";
    private static ObjectProperty<Project> currentProject = new SimpleObjectProperty<>();
    private static Stage s = null;
    
    public static Optional<Project> getCurrentProject() {
        return Optional.ofNullable(currentProject.get());
    }
    
    public static ObjectProperty<Project> currentProjectProperty() {
        return currentProject;
    }
    
    public static void showErrorWindow(Map<String, Boolean> lines) {
        TextFlow textFlow = new TextFlow();
        lines.forEach((line, error) -> {
            Text text = new Text(line + "\n");
            if (error) {
                text.setFill(Color.RED);
            }
            textFlow.getChildren().add(text);
        });
        Dialogs.showCustionOutputDialog(
                Word.COMPILER_ERROR_TITLE,
                Word.COMPILER_ERROR_HEADER_TEXT,
                Word.COMPILER_ERROR_CONTENT_TEXT,
                Icon.ERROR,
                textFlow);
    }
    
    public static Optional<String> getJavaHomeDirectory() {
        String javaHome = System.getProperty("java.home", "");
        
        if (!javaHome.isEmpty()) {
            try {
                Path javaPath = Paths.get(javaHome).resolve("bin").resolve("java");
                String javaPathString = javaPath.toString();
                return Optional.of(javaPathString);
            } catch (Exception e) {
                Logger.error("Could not find Java-Home");
                return Optional.empty();
            }
        }
        Logger.error("Could not find Java-Home");
        return Optional.empty();
    }
    
    private final ObjectProperty<Path> saveFile = new SimpleObjectProperty<>();
    private Process process;
    private String id;
    
    protected Project(Welcome parent, String name) {
        superInit(parent, name);
        init();
    }
    
    public boolean compile(Consumer<String> consumer, List<String> commands) {
        Optional<String> optionalJavaCommand = getJavaHomeDirectory();
        if (!optionalJavaCommand.isPresent()) {
            return false;
        }
        String javaCommand = optionalJavaCommand.get();
        Optional<Path> optionalCaseyCompiler = Resources.getFile("sharknoon/casey/ide/CaseyCOMPILER.jar", true);
        if (!optionalCaseyCompiler.isPresent()) {
            Logger.error("Casey-Compiler not found");
            return false;
        }
        String caseyCompiler = optionalCaseyCompiler.get().toAbsolutePath().toString();
        
        Map<String, Boolean> output = new LinkedHashMap<>();
        
        try {
            CommandLine commandLine = new CommandLine(javaCommand);
            commandLine.addArgument("-jar");
            commandLine.addArgument(caseyCompiler);
            commandLine.addArguments(commands.toArray(new String[0]));
            
            LogOutputStream outStream = new LogOutputStream() {
                @Override
                protected void processLine(String line, int logLevel) {
                    consumer.accept(line);
                    Logger.debug(line);
                    output.put(line, false);
                }
            };
            LogOutputStream errStream = new LogOutputStream() {
                @Override
                protected void processLine(String line, int logLevel) {
                    consumer.accept(line);
                    Logger.debug(line);
                    output.put(line, true);
                }
            };
            
            
            DefaultExecutor executor = new DefaultExecutor();
            executor.setStreamHandler(new PumpStreamHandler(outStream, errStream));
            Logger.debug("Executing \"" + String.join(" ", commandLine.toStrings()) + "\" in Working Directory \"" + executor.getWorkingDirectory() + "\"");
            
            int result = executor.execute(commandLine);
            return result == 0;
        } catch (ExecuteException e) {
            Logger.error("Could not execute Compiler, Errorcode: " + e.getExitValue() + "\n(1 = Argument parsing, 2 = Casey parsing, 3 = Code generation, 4 = Code compilation)");
            Platform.runLater(() -> showErrorWindow(output));
            return false;
        } catch (Exception e) {
            Logger.error("Could not execute Compiler", e);
            return false;
        }
    }
    
    /**
     * Executes the compiled version of this item
     *
     * @param basePath The base path where the .casey file is in
     * @param mainItem The item which should be executed
     * @return
     */
    public Boolean execute(Path basePath, Item mainItem) {
        String title = mainItem.getName();
        Icon icon = mainItem.getSite().getTabIcon();
        
        Optional<String> optionalJavaCommand = getJavaHomeDirectory();
        if (!optionalJavaCommand.isPresent()) {
            return false;
        }
        String javaCommand = optionalJavaCommand.get();
        String classpath = basePath.toAbsolutePath().toString();
        String mainClassFile = mainItem.getFullName();
        
        try {
            ProcessBuilder pb = new ProcessBuilder(javaCommand, "-cp", classpath, mainClassFile);
            Logger.debug("Executing \"" + String.join(" ", pb.command()) + "\" in Working Directory \"" + pb.directory() + "\"");
            
            ObjectProperty<Text> outAndErr = new SimpleObjectProperty<>();
    
            process = pb.start();
            InputStream outputFromProcess = process.getInputStream();
            InputStream errorFromProcess = process.getErrorStream();
            OutputStream inputForProcess = process.getOutputStream();
            
            LogOutputStream out = new LogOutputStream() {
                @Override
                protected void processLine(String line, int logLevel) {
                    Text text = new Text(line + "\n");
                    outAndErr.set(text);
                    System.out.println(line);
                }
            };
            new Thread(() -> {
                try {
                    outputFromProcess.transferTo(out);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }, "ProcessOutputListenerThread").start();
            
            LogOutputStream err = new LogOutputStream() {
                @Override
                protected void processLine(String line, int logLevel) {
                    Text text = new Text(line + "\n");
                    text.setFill(Color.RED);
                    outAndErr.set(text);
                    System.out.println(line);
                }
            };
            new Thread(() -> {
                try {
                    errorFromProcess.transferTo(err);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }, "ProcessErrorListenerThread").start();
    
            ExecutorService pool = Executors.newCachedThreadPool();
            Consumer<String> in = s -> {
                pool.submit(() -> {
                    try {
                        inputForProcess.write((s + "\n").getBytes(StandardCharsets.UTF_8));
                        inputForProcess.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            };
    
    
            Platform.runLater(() -> s = showInputOutputWindow(title, icon, in, outAndErr));
    
            process.waitFor();
            return process.exitValue() == 0;
        } catch (Exception e) {
            System.err.println("Error running the Java Program: " + e);
            e.printStackTrace();
            return false;
        } finally {
            if (s != null) {
                Platform.runLater(() -> s.close());
            }
        }
    }
    
    public Stage showInputOutputWindow(String title, Icon icon, Consumer<String> inputLine, ObjectExpression<Text> errorAndOutputLine) {
        BorderPane root = new BorderPane();
        
        TextFlow outputs = new TextFlow();
        errorAndOutputLine.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                Platform.runLater(() -> outputs.getChildren().add(newValue));
            }
        });
        root.setCenter(outputs);
        
        TextField inputs = new TextField();
        inputs.setOnAction(ae -> {
            inputLine.accept(inputs.getText());
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
            if (process != null) {
                process.destroy();
            }
        });
        newWindow.show();
        return newWindow;
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
                boolean success = compile(statusProperty, commands);
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
