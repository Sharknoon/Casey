package sharknoon.casey.ide.misc;/*
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

import javafx.application.Platform;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.binding.StringExpression;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.LogOutputStream;
import sharknoon.casey.ide.ui.dialogs.Dialogs;
import sharknoon.casey.ide.ui.misc.Icon;
import sharknoon.casey.ide.utils.language.Word;
import sharknoon.casey.ide.utils.settings.Logger;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class Executor {
    
    /**
     * Returns the path to the bin folder of the java home
     *
     * @return
     */
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
    
    public static CompletableFuture<Integer> runJar(Path jarPath, String... args) {
        return runJar(jarPath, null, null, null, null, args);
    }
    
    /**
     * @param jarPath path to the .jar file
     * @param args
     * @return
     */
    public static CompletableFuture<Integer> runJar(Path jarPath, Consumer<String> outputConsumer, Consumer<String> errorConsumer, StringExpression input, BooleanExpression abortProcess, String... args) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<String> optionalJavaCommand = getJavaHomeDirectory();
            if (!optionalJavaCommand.isPresent()) {
                return -1;
            }
            String javaCommand = optionalJavaCommand.get();
            
            String jarFile = jarPath.toAbsolutePath().toString();
            
            Map<String, Boolean> output = new LinkedHashMap<>();
            
            List<String> commands = new ArrayList<>();
            commands.add(javaCommand);
            commands.add("-jar");
            commands.add(jarFile);
            commands.addAll(Arrays.asList(args));
            
            Runnable onError = () -> Platform.runLater(() -> showErrorWindow(output));
            
            Consumer<String> newOutputConsumer = s -> {
                if (outputConsumer != null) {
                    outputConsumer.accept(s);
                }
                output.put(s, false);
            };
            
            Consumer<String> newErrorConsumer = s -> {
                if (errorConsumer != null) {
                    errorConsumer.accept(s);
                }
                output.put(s, true);
            };
            
            return run(jarPath.getParent().toFile(), commands, newOutputConsumer, newErrorConsumer, input, onError, abortProcess);
        });
    }
    
    public static CompletableFuture<Integer> runClass(Path workingDirectory, String mainClass, Consumer<String> outputConsumer, Consumer<String> errorConsumer, StringExpression input, BooleanExpression abortProcess, String... args) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<String> optionalJavaCommand = getJavaHomeDirectory();
            if (!optionalJavaCommand.isPresent()) {
                return -1;
            }
            String javaCommand = optionalJavaCommand.get();
            
            Map<String, Boolean> output = new LinkedHashMap<>();
            
            List<String> commands = new ArrayList<>();
            commands.add(javaCommand);
            commands.add(mainClass);
            commands.addAll(Arrays.asList(args));
            
            Runnable onError = () -> Platform.runLater(() -> showErrorWindow(output));
            
            Consumer<String> newOutputConsumer = s -> {
                if (outputConsumer != null) {
                    outputConsumer.accept(s);
                }
                output.put(s, false);
            };
            
            Consumer<String> newErrorConsumer = s -> {
                if (errorConsumer != null) {
                    errorConsumer.accept(s);
                }
                output.put(s, true);
            };
            
            return run(workingDirectory.toAbsolutePath().toFile(), commands, newOutputConsumer, newErrorConsumer, input, onError, abortProcess);
        });
    }
    
    private static int run(File workingDirectory, List<String> commands, Consumer<String> outputConsumer, Consumer<String> errorConsumer, StringExpression input, Runnable onError, BooleanExpression abortProcess) {
        try {
            ProcessBuilder builder = new ProcessBuilder();
            
            builder.command(commands);
            builder.directory(workingDirectory);
            
            Logger.debug("Executing \"" + String.join(" ", commands) + "\" in Working Directory \"" + builder.directory() + "\"");
            
            Process start = builder.start();
            if (outputConsumer != null) {
                LogOutputStream outStream = new LogOutputStream() {
                    @Override
                    protected void processLine(String line, int logLevel) {
                        outputConsumer.accept(line);
                        Logger.debug(line);
                    }
                };
                new Thread(() -> {
                    try {
                        start.getInputStream().transferTo(outStream);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }, "ProcessOutputListenerThread").start();
            }
            if (errorConsumer != null) {
                LogOutputStream errStream = new LogOutputStream() {
                    @Override
                    protected void processLine(String line, int logLevel) {
                        errorConsumer.accept(line);
                        Logger.debug(line);
                    }
                };
                new Thread(() -> {
                    try {
                        start.getErrorStream().transferTo(errStream);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }, "ProcessErrorListenerThread").start();
            }
            if (input != null) {
                ExecutorService executorService = Executors.newCachedThreadPool();
                input.addListener((observable, oldValue, s) -> {
                    if (s == null) {
                        return;
                    }
                    executorService.submit(() -> {
                        try {
                            OutputStream inputForProcess = start.getOutputStream();
                            inputForProcess.write((s + "\n").getBytes(StandardCharsets.UTF_8));
                            inputForProcess.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                });
            }
            if (abortProcess != null) {
                abortProcess.addListener((observable, oldValue, newValue) -> {
                    if (newValue) {
                        start.destroy();
                    }
                });
            }
            start.waitFor();
            return start.exitValue();
        } catch (ExecuteException e) {
            Logger.error("Could not execute Java-File, Errorcode: " + e.getExitValue());
            if (onError != null) {
                onError.run();
            }
            return -1;
        } catch (Exception e) {
            Logger.error("Could not execute Java-File", e);
            return -1;
        }
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
                Word.EXECUTOR_ERROR_TITLE,
                Word.EXECUTOR_ERROR_HEADER_TEXT,
                Word.EXECUTOR_ERROR_CONTENT_TEXT,
                Icon.ERROR,
                textFlow);
    }
}
