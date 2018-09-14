package sharknoon.casey.ide.ui.tutorial;/*
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

import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.*;
import javafx.util.Duration;
import sharknoon.casey.ide.ui.misc.*;
import sharknoon.casey.ide.ui.styles.Styles;
import sharknoon.casey.ide.utils.settings.Props;

import java.util.*;

public class Tutorial {
    
    private static final String FIRST_START_KEY = "firstStart";
    
    private static boolean isShowing = false;
    
    public static void init() {
        Props.get(FIRST_START_KEY).thenAccept(s -> {
            if (!s.isPresent() || s.get().equals("true")) {
                showTutorial();
            }
            Props.set(FIRST_START_KEY, "false");
        });
    }
    
    public static void showTutorial() {
        if (isShowing) {
            return;
        }
        isShowing = true;
        
        Platform.runLater(() -> {
            
            int margin = 50;
            int height = 500;
            int width = 750;
            int arrowsize = 75;
            int imagewidth = width - (2 * margin);
            int imageheight = height - (2 * margin);
            Stage stage = new Stage();
            
            Set<Node> images = new LinkedHashSet<>();
            images.add(Icons.get(Icon.TUTORIAL1, imagewidth, imageheight));
            images.add(Icons.get(Icon.TUTORIAL2, imagewidth, imageheight));
            images.add(Icons.get(Icon.TUTORIAL3, imagewidth, imageheight));
            images.add(Icons.get(Icon.TUTORIAL4, imagewidth, imageheight));
            
            AnchorPane scrollPaneMaximisier = new AnchorPane();
            
            Rectangle background = new Rectangle();
            background.heightProperty().bind(scrollPaneMaximisier.heightProperty());
            background.widthProperty().bind(scrollPaneMaximisier.widthProperty());
            background.setArcHeight(75);
            background.setArcWidth(75);
            background.setStyle("-fx-fill: -fx-background");
            scrollPaneMaximisier.getChildren().add(background);
            
            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setFitToHeight(true);
            scrollPane.setFitToWidth(true);
            scrollPane.setVbarPolicy(ScrollBarPolicy.NEVER);
            scrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
            AnchorPane.setTopAnchor(scrollPane, 0.0);
            AnchorPane.setRightAnchor(scrollPane, 0.0);
            AnchorPane.setBottomAnchor(scrollPane, 0.0);
            AnchorPane.setLeftAnchor(scrollPane, 0.0);
            scrollPaneMaximisier.getChildren().add(scrollPane);
            
            HBox hBoxImages = new HBox();
            hBoxImages.setLayoutX(arrowsize);
            hBoxImages.setMaxWidth(width);
            scrollPane.setContent(hBoxImages);
            for (Node image : images) {
                StackPane imageCenterizerAndPlaceholder = new StackPane(image);
                HBox.setMargin(imageCenterizerAndPlaceholder, new Insets(margin));
                imageCenterizerAndPlaceholder.setMinHeight(imageheight);
                imageCenterizerAndPlaceholder.setMinWidth(imagewidth);
                hBoxImages.getChildren().add(imageCenterizerAndPlaceholder);
            }
            
            Timeline changeImageTimeline = new Timeline();
            
            IntegerProperty currentImageIndex = new SimpleIntegerProperty(0);
            currentImageIndex.addListener((observable, oldValue, newValue) -> {
                if (newValue != null && (newValue.intValue() < images.size() && newValue.intValue() >= 0)) {
                    KeyValue valStart = new KeyValue(scrollPane.hvalueProperty(), scrollPane.getHvalue());
                    KeyValue valEnd = new KeyValue(scrollPane.hvalueProperty(), (1.0 / (images.size() - 1.0)) * newValue.doubleValue());
                    
                    changeImageTimeline.getKeyFrames().setAll(
                            new KeyFrame(Duration.ZERO, valStart),
                            new KeyFrame(Duration.millis(500), valEnd)
                    );
                    Platform.runLater(() -> {
                        changeImageTimeline.stop();
                        changeImageTimeline.play();
                    });
                }
            });
    
            Node imageLeft = Icons.get(Icon.ARROWLEFTROUND, arrowsize - 16);//The padding around the arrow
            Button leftButton = new Button();
            BorderPane.setAlignment(leftButton, Pos.CENTER);
            leftButton.setGraphic(imageLeft);
            leftButton.disableProperty().bind(currentImageIndex.lessThanOrEqualTo(0));
            leftButton.setOnMouseClicked(e -> currentImageIndex.set(currentImageIndex.get() - 1));
            leftButton.setStyle("-fx-background-color: transparent");
            
            Button rightButton = new Button();
            BorderPane.setAlignment(rightButton, Pos.CENTER);
            rightButton.graphicProperty().bind(
                    Bindings.when(currentImageIndex.greaterThanOrEqualTo(images.size() - 1))
                            .then(Icons.get(Icon.CLOSEROUND, arrowsize - 16))
                            .otherwise(Icons.get(Icon.ARROWRIGHTROUND, arrowsize - 16))
            );
            rightButton.setOnMouseClicked(e -> {
                if (currentImageIndex.get() >= images.size() - 1) {
                    stage.close();
                    return;
                }
                currentImageIndex.set(currentImageIndex.get() + 1);
            });
            rightButton.setStyle("-fx-background-color: transparent");
            
            BorderPane root = new BorderPane(scrollPaneMaximisier, null, rightButton, null, leftButton);
            root.setStyle("-fx-background-color: transparent");
            root.setPrefSize(width + (2 * arrowsize), height);
            root.setBackground(Background.EMPTY);
            Scene scene = new Scene(root);
            Styles.bindStyleSheets(scene.getStylesheets());
            scene.setFill(Color.TRANSPARENT);
            scene.setOnKeyPressed(e -> {
                if (e.getCode() == KeyCode.ESCAPE) {
                    stage.close();
                }
            });
            
            stage.setScene(scene);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.show();
            Platform.runLater(stage::toFront);
        });
        
        isShowing = false;
    }
    
}
