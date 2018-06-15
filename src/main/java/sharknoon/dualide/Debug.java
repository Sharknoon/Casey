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
package sharknoon.dualide;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class Debug extends Application {

    Rectangle rectBack;
    Rectangle rectFront;

    @Override
    public void start(Stage primaryStage) {
        rectBack = new Rectangle(100, 100);
        rectBack.setFill(Color.GREEN);
        rectFront = new Rectangle(50, 50);
        rectFront.setFill(Color.BLUE);

        rectBack.setOnMouseDragged(event -> {
            System.out.println("mouse dragged back node");
        });
        rectFront.setOnMouseDragged(event -> {
            System.out.println("mouse dragged front node");
        });
        StackPane root = new StackPane();
        root.getChildren().addAll(rectBack, rectFront);
        root.setOnMouseDragged(event -> {
            System.out.println("mouse dragged root node");
        });
        root.setStyle("-fx-background-color: yellow");
        Scene scene = new Scene(root, 300, 300);

        primaryStage.setTitle("MouseClicked!");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
