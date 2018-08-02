package sharknoon.casey.updater.ui;/*
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

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.DoubleExpression;
import javafx.beans.binding.StringExpression;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.concurrent.CompletableFuture;

public class ProgressStage extends Application {
    
    private static DoubleExpression progress;
    private static StringExpression description;
    
    public static void show(DoubleExpression progress, StringExpression description) {
        CompletableFuture.runAsync(() -> {
            ProgressStage.progress = progress;
            ProgressStage.description = description;
            launch();
        });
    }
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Casey Updater");
        primaryStage.getIcons().clear();
        StackPane stackPaneRoot = new StackPane();
        stackPaneRoot.setPrefWidth(400);
        VBox vBoxContent = new VBox(10);
        vBoxContent.setPadding(new Insets(10));
        
        ProgressBar progressBar = new ProgressBar();
        progressBar.setMaxWidth(Double.MAX_VALUE);
        progressBar.setPrefHeight(30);
        progress.addListener((observable, oldValue, newValue) -> {
            Platform.runLater(() -> progressBar.setProgress(newValue.doubleValue()));
        });
        
        Label labelDescription = new Label();
        labelDescription.setFont(Font.font(16));
        description.addListener((observable, oldValue, newValue) -> {
            Platform.runLater(() -> labelDescription.setText(newValue));
        });
        
        vBoxContent.getChildren().addAll(progressBar, labelDescription);
        stackPaneRoot.getChildren().add(vBoxContent);
        Scene scene = new Scene(stackPaneRoot);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
