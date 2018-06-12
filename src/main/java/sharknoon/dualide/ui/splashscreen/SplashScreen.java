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
package sharknoon.dualide.ui.splashscreen;

import java.util.function.Consumer;
import javafx.animation.FadeTransition;
import javafx.application.Preloader;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 *
 * @author Josua Frank
 */
public class SplashScreen extends Preloader {

    public static final String APPLICATION_ICON
            = "http://cdn1.iconfinder.com/data/icons/Copenhagen/PNG/32/people.png";
    public static final String SPLASH_IMAGE
            = "http://fxexperience.com/wp-content/uploads/2010/06/logo.png";

    private final ProgressBar loadProgress;
    private final Label progressText;
    private final Pane splashLayout;
    private Stage primaryStage;
    private static final int SPLASH_WIDTH = 676;
    private static final int SPLASH_HEIGHT = 227;

    public SplashScreen() {
          //var splash = new ImageView(new Image(SPLASH_IMAGE));

        loadProgress = new ProgressBar();
        loadProgress.setPrefWidth(SPLASH_WIDTH - 20);

        progressText = new Label("Will find friends for peanuts . . .");
        progressText.setAlignment(Pos.CENTER);

        splashLayout = new VBox();
        splashLayout.getChildren().addAll(/*splash, */loadProgress, progressText);
        splashLayout.setStyle(
                "-fx-padding: 5; "
                + "-fx-background-color: cornsilk; "
                + "-fx-border-width:5; "
                + "-fx-border-color: "
                + "linear-gradient("
                + "to bottom, "
                + "chocolate, "
                + "derive(chocolate, 50%)"
                + ");"
        );
        splashLayout.setEffect(new DropShadow());
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.primaryStage = stage;
        final   var splashScene = new Scene(splashLayout, Color.TRANSPARENT);
        final   var bounds = Screen.getPrimary().getBounds();
        primaryStage.setScene(splashScene);
        primaryStage.setX(bounds.getMinX() + bounds.getWidth() / 2 - SPLASH_WIDTH / 2);
        primaryStage.setY(bounds.getMinY() + bounds.getHeight() / 2 - SPLASH_HEIGHT / 2);
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setAlwaysOnTop(true);
        primaryStage.show();
    }

    @Override
    public boolean handleErrorNotification(ErrorNotification info) {
        progressText.setText(info.getDetails());
        return false;//show additional errors
    }

    @Override
    public void handleApplicationNotification(PreloaderNotification info) {
        progressText.setText(info.toString());
    }

    @Override
    public void handleStateChangeNotification(StateChangeNotification info) {
        switch (info.getType()) {
            case BEFORE_INIT:
                break;
            case BEFORE_LOAD:
                break;
            case BEFORE_START:
                break;
        }
    }

    @Override
    public void handleProgressNotification(ProgressNotification info) {
        loadProgress.setProgress(info.getProgress());
        if (info.getProgress() >= 1.0) {
            primaryStage.close();
        }
    }

}
