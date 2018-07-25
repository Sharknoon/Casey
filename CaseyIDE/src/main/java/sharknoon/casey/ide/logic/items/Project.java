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
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.apache.commons.exec.*;
import sharknoon.casey.ide.serial.Serialisation;
import sharknoon.casey.ide.ui.MainApplication;
import sharknoon.casey.ide.ui.sites.Site;
import sharknoon.casey.ide.ui.sites.welcome.RecentProject;
import sharknoon.casey.ide.ui.styles.Styles;
import sharknoon.casey.ide.utils.javafx.BindUtils;
import sharknoon.casey.ide.utils.language.Language;
import sharknoon.casey.ide.utils.language.Word;
import sharknoon.casey.ide.utils.settings.Logger;
import sharknoon.casey.ide.utils.settings.Resources;

import java.io.File;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

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
    
    public static boolean compile(StringProperty statusProperty, String... commands) {
        Optional<String> optionalJavaCommand = getJavaHomeDirectory();
        if (!optionalJavaCommand.isPresent()) {
            return false;
        }
        String javaCommand = optionalJavaCommand.get();
        Optional<Path> optionalCaseyCompiler = Resources.getFile("sharknoon/casey/ide/CaseyCOMPILER.jar", true);
        if (!optionalCaseyCompiler.isPresent()) {
            return false;
        }
        String caseyCompiler = optionalCaseyCompiler.get().toAbsolutePath().toString();
        
        try {
            CommandLine commandLine = new CommandLine(javaCommand);
            commandLine.addArgument("-jar");
            commandLine.addArgument(caseyCompiler);
            commandLine.addArguments(commands);
            
            LogOutputStream logStream = new LogOutputStream() {
                @Override
                protected void processLine(String line, int logLevel) {
                    Platform.runLater(() -> statusProperty.set(line));
                    System.out.println(line);
                }
            };
            
            
            DefaultExecutor executor = new DefaultExecutor();
            executor.setStreamHandler(new PumpStreamHandler(logStream));
            
            int result = executor.execute(commandLine);
            return result == 0;
        } catch (ExecuteException e) {
            Logger.error("Could not execute Compiler, Errorcode: " + e.getExitValue() + "\n(1 = Argument parsing, 2 = Casey parsing, 3 = Code generation, 4 = Code compilation)", e);
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
    public static CompletableFuture<Boolean> execute(Path basePath, Item mainItem) {
        Optional<String> optionalJavaCommand = getJavaHomeDirectory();
        if (!optionalJavaCommand.isPresent()) {
            return CompletableFuture.completedFuture(false);
        }
        String javaCommand = optionalJavaCommand.get();
        String classpath = basePath.toAbsolutePath().toString();
        String mainClassFile = mainItem.getFullName();
        
        try {
            CommandLine commandLine = new CommandLine(javaCommand);
            commandLine.addArgument("-cp");
            commandLine.addArgument(classpath);
            commandLine.addArgument(mainClassFile);
            
            DefaultExecutor executor = new DefaultExecutor();
            
            ObjectProperty<Text> outAndErr = new SimpleObjectProperty<>();
            StringProperty in = new SimpleStringProperty();
            showInputOutputWindow(in, outAndErr);
            
            LogOutputStream outStream = new LogOutputStream() {
                @Override
                protected void processLine(String line, int logLevel) {
                    Text text = new Text(line);
                    outAndErr.set(text);
                    System.out.println(line);
                }
            };
            LogOutputStream errStream = new LogOutputStream() {
                @Override
                protected void processLine(String line, int logLevel) {
                    Text text = new Text(line);
                    text.setFill(Color.RED);
                    outAndErr.set(text);
                    System.err.println(line);
                }
            };
            PipedInputStream input = new PipedInputStream();
            PipedOutputStream output = new PipedOutputStream(input);
            BindUtils.addListener(in, (observable, oldValue, newValue) -> {
                if (newValue != null) {
                    try {
                        output.write(newValue.getBytes(StandardCharsets.UTF_8));
                    } catch (IOException e) {
                        Logger.error("Could not redirect Input to process", e);
                    }
                }
            });
            
            executor.setStreamHandler(new PumpStreamHandler(outStream, errStream, input));
            
            return CompletableFuture.supplyAsync(() -> {
                try {
                    int result = executor.execute(commandLine);
                    return result == 0;
                } catch (Exception e) {
                    System.err.println("Error running the Java Program: " + e);
                    return false;
                }
            });
        } catch (Exception e) {
            System.err.println("Error running the Java Program: " + e);
            return CompletableFuture.completedFuture(false);
        }
    }
    
    public static void showInputOutputWindow(StringProperty inputLine, ObjectExpression<Text> errorAndOutputLine) {
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
            inputLine.set(inputs.getText());
            inputs.clear();
        });
        root.setBottom(inputs);
        
        Scene newScene = new Scene(root);
        Styles.bindStyleSheets(newScene.getStylesheets());
        Stage newWindow = new Stage();
        newWindow.setScene(newScene);
        newWindow.setOnCloseRequest();
        newWindow.show();
    }
    
    public static Optional<String> getJavaHomeDirectory() {
        String javaHome = System.getProperty("java.home", "");
        
        if (!javaHome.isEmpty()) {
            try {
                Path javaPath = Paths.get(javaHome).resolve("bin").resolve("java");
                String javaPathString = javaPath.toString();
                return Optional.of(javaPathString);
            } catch (Exception e) {
                System.err.println("Could not find Java-Home");
                return Optional.empty();
            }
        }
        return Optional.empty();
    }
    
    private final ObjectProperty<Path> saveFile = new SimpleObjectProperty<>();
    private String id;
    
    protected Project(Welcome parent, String name) {
        superInit(parent, name);
        init();
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
    
    public void run(StringProperty statusProperty) {
        requestSaveFile();
        Item<?, ?, ?> currentItem = Site.currentSelectedProperty().get();
        if (saveFile.get() != null && currentItem != null) {
            //TODO parameter check
            Path caseyFile = saveFile.get();
            Path basePath = saveFile.get().getParent();
            boolean success = compile(statusProperty, "-l", "java", "-p", caseyFile.toString(), "-f", currentItem.getFullName());
            if (success) {
                execute(basePath, currentItem);
            }
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
