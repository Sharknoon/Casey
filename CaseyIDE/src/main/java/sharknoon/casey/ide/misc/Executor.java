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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
    private static Optional<String> getJavaHomeDirectory() {
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
    
    /**
     * @param jarPath path to the .jar file
     * @param args
     * @return
     */
    private static CompletableFuture<Integer> runJar(@NotNull Path jarPath,
                                                     @Nullable Consumer<String> outputConsumer,
                                                     @Nullable Consumer<String> errorConsumer,
                                                     @Nullable StringExpression input,
                                                     @Nullable BooleanExpression abortProcess,
                                                     @Nullable List<Integer> expectedExitValues,
                                                     @Nullable Consumer<Map<String, Boolean>> onError,
                                                     @Nullable String... args) {
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
            if (args != null) {
                commands.addAll(List.of(args));
            }
            
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
    
            Runnable onErrorOccured = onError != null ? () -> onError.accept(output) : null;
    
            return run(jarPath.getParent().toFile(), commands, newOutputConsumer, newErrorConsumer, input, onErrorOccured, expectedExitValues, abortProcess);
        });
    }
    
    public static CompletableFuture<Integer> runClass(@NotNull Path workingDirectory,
                                                      @NotNull String mainClass,
                                                      @Nullable Consumer<String> outputConsumer,
                                                      @Nullable Consumer<String> errorConsumer,
                                                      @Nullable StringExpression input,
                                                      @Nullable BooleanExpression abortProcess,
                                                      @Nullable List<Integer> expectedExitValues,
                                                      @Nullable Consumer<Map<String, Boolean>> onError,
                                                      @Nullable String... args) {
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
            if (args != null) {
                commands.addAll(List.of(args));
            }
    
            Consumer<String> newOutputConsumer = outputConsumer == null ? null : s -> {
                outputConsumer.accept(s);
                output.put(s, false);
            };
    
            Consumer<String> newErrorConsumer = errorConsumer == null ? null : s -> {
                errorConsumer.accept(s);
                output.put(s, true);
            };
    
            return run(workingDirectory.toAbsolutePath().toFile(), commands, newOutputConsumer, newErrorConsumer, input, onError != null ? () -> onError.accept(output) : null, expectedExitValues, abortProcess);
        });
    }
    
    private static int run(@NotNull File workingDirectory,
                           @NotNull List<String> commands,
                           @Nullable Consumer<String> outputConsumer,
                           @Nullable Consumer<String> errorConsumer,
                           @Nullable StringExpression input,
                           @Nullable Runnable onError,
                           @Nullable List<Integer> expectedExitValues,
                           @Nullable BooleanExpression abortProcess) {
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
            int exitValue = start.exitValue();
            if ((onError != null) && (expectedExitValues != null) && !expectedExitValues.contains(exitValue)) {
                onError.run();
            }
            return exitValue;
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
        Dialogs.showCustomOutputDialog(
                Word.EXECUTOR_ERROR_TITLE,
                Word.EXECUTOR_ERROR_HEADER_TEXT,
                Word.EXECUTOR_ERROR_CONTENT_TEXT,
                Icon.ERROR,
                textFlow);
    }
    
    public abstract static class ExecutorBuilder<B extends ExecutorBuilder> {
        public static JarExecutorBuilder executeJar(Path jarPath) {
            return new JarExecutorBuilder(jarPath);
        }
        
        public static ClassExecutorBuilder executeClass(Path workingDirectory, String mainClass) {
            return new ClassExecutorBuilder(workingDirectory, mainClass);
        }
        
        String[] args;
        Consumer<String> outputConsumer;
        Consumer<String> errorConsumer;
        StringExpression input;
        BooleanExpression abortProcess;
        List<Integer> expectedExitValues;
        Consumer<Map<String, Boolean>> onError = m -> Platform.runLater(() -> showErrorWindow(m));
        
        public String[] getArgs() {
            return args;
        }
        
        @SuppressWarnings("unchecked")
        public B setArgs(List<String> args) {
            this.args = args.toArray(new String[0]);
            return (B) this;
        }
        
        @SuppressWarnings("unchecked")
        public B setArgs(String... args) {
            this.args = args;
            return (B) this;
        }
        
        public Consumer<String> getOutputConsumer() {
            return outputConsumer;
        }
        
        @SuppressWarnings("unchecked")
        public B setOutputConsumer(Consumer<String> outputConsumer) {
            this.outputConsumer = outputConsumer;
            return (B) this;
        }
        
        public Consumer<String> getErrorConsumer() {
            return errorConsumer;
        }
        
        @SuppressWarnings("unchecked")
        public B setErrorConsumer(Consumer<String> errorConsumer) {
            this.errorConsumer = errorConsumer;
            return (B) this;
        }
        
        public StringExpression getInput() {
            return input;
        }
        
        @SuppressWarnings("unchecked")
        public B setInput(StringExpression input) {
            this.input = input;
            return (B) this;
        }
        
        public BooleanExpression getAbortProcess() {
            return abortProcess;
        }
        
        @SuppressWarnings("unchecked")
        public B setAbortProcess(BooleanExpression abortProcess) {
            this.abortProcess = abortProcess;
            return (B) this;
        }
        
        public List<Integer> getExpectedExitValues() {
            return expectedExitValues;
        }
        
        @SuppressWarnings("unchecked")
        public B setExpectedExitValues(List<Integer> expectedExitValues) {
            this.expectedExitValues = expectedExitValues;
            return (B) this;
        }
        
        @SuppressWarnings("unchecked")
        public B setExpectedExitValues(Integer... expectedExitValues) {
            this.expectedExitValues = List.of(expectedExitValues);
            return (B) this;
        }
        
        public Consumer<Map<String, Boolean>> getOnError() {
            return onError;
        }
        
        @SuppressWarnings("unchecked")
        public B setOnError(Consumer<Map<String, Boolean>> onError) {
            this.onError = onError;
            return (B) this;
        }
        
        public abstract CompletableFuture<Integer> execute();
        
        public static class JarExecutorBuilder extends ExecutorBuilder<JarExecutorBuilder> {
            Path jarPath;
            
            public JarExecutorBuilder(Path jarPath) {
                this.jarPath = jarPath;
            }
            
            public Path getJarPath() {
                return jarPath;
            }
            
            public JarExecutorBuilder setJarPath(Path jarPath) {
                this.jarPath = jarPath;
                return this;
            }
            
            public CompletableFuture<Integer> execute() {
                return runJar(jarPath, outputConsumer, errorConsumer, input, abortProcess, expectedExitValues, onError, args);
            }
            
        }
        
        public static class ClassExecutorBuilder extends ExecutorBuilder<ClassExecutorBuilder> {
            Path workingDirectory;
            String mainClass;
            
            public ClassExecutorBuilder(Path workingDirectory, String mainClass) {
                this.workingDirectory = workingDirectory;
                this.mainClass = mainClass;
            }
            
            public Path getWorkingDirectory() {
                return workingDirectory;
            }
            
            public ClassExecutorBuilder setWorkingDirectory(Path workingDirectory) {
                this.workingDirectory = workingDirectory;
                return this;
            }
            
            public String getMainClass() {
                return mainClass;
            }
            
            public ClassExecutorBuilder setMainClass(String mainClass) {
                this.mainClass = mainClass;
                return this;
            }
            
            public CompletableFuture<Integer> execute() {
                return runClass(workingDirectory, mainClass, outputConsumer, errorConsumer, input, abortProcess, expectedExitValues, onError, args);
            }
        }
    }
}
