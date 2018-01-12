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
package sharknoon.dualide.ui.sites.function;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import sharknoon.dualide.ui.MainApplication;
import sharknoon.dualide.misc.Exitable;
import sharknoon.dualide.utils.settings.Logger;
import sharknoon.dualide.utils.settings.Ressources;

/**
 *
 * @author Josua Frank
 */
public class WorkspaceBackground implements Exitable {

    private final ImageView view1;
    private final ImageView view2;
    private final List<Path> images = new ArrayList<>();
    private int counter = 0;
    private final ScheduledExecutorService imageChangingScheduler = Executors.newScheduledThreadPool(1);
    private ImageView toBeResizedAsSoonAsAImageIsInIt = null;

    public static void setBackground(ImageView imageView1, ImageView imageView2) {
        WorkspaceBackground wb = new WorkspaceBackground(imageView1, imageView2);
    }

    private WorkspaceBackground(ImageView imageView1, ImageView imageView2) {
        view1 = imageView1;
        view2 = imageView2;
        reloadImages();
        imageChangingScheduler.scheduleAtFixedRate(() -> {
            try {
                if (images.size() - 1 > counter) {
                    counter++;
                } else {
                    counter = 0;
                }
                setImage(images.get(counter));
                if (toBeResizedAsSoonAsAImageIsInIt != null) {
                    resizeImage(toBeResizedAsSoonAsAImageIsInIt);
                    toBeResizedAsSoonAsAImageIsInIt = null;
                }
            } catch (Exception e) {
                Logger.error("Could not change the background image", e);
            }
        }, 0, (long) UISettings.workspaceBackgroundImageDuration.toMillis(), TimeUnit.MILLISECONDS);
        MainApplication.stage.widthProperty().addListener((observable, oldValue, newValue) -> {
            stageWidth = newValue.doubleValue();
            resizeImage(view1);
            resizeImage(view2);
        });
        MainApplication.stage.heightProperty().addListener((observable, oldValue, newValue) -> {
            stageHeight = newValue.doubleValue();
            resizeImage(view1);
            resizeImage(view2);
        });
        fadeToView1 = new Timeline(
                new KeyFrame(UISettings.workspaceBackgroundFadingDuration,
                        new KeyValue(view1.opacityProperty(), 1),
                        new KeyValue(view2.opacityProperty(), 0)
                )
        );
        fadeToView2 = new Timeline(
                new KeyFrame(UISettings.workspaceBackgroundFadingDuration,
                        new KeyValue(view1.opacityProperty(), 0),
                        new KeyValue(view2.opacityProperty(), 1)
                )
        );
        MainApplication.registerExitable(this);
    }

    public void reloadImages() {
        Ressources.getDirectory("backgroundimages", true).ifPresent((path) -> {
            try {
                images.clear();
                images.addAll(Files.list(path)
                        .filter(img -> {
                            String end = img.getFileName().toString().toLowerCase();
                            return end.endsWith("png")
                                    || end.endsWith("jpg")
                                    || end.endsWith("jpeg")
                                    || end.endsWith("gif")
                                    || end.endsWith("bmp");
                        })
                        .collect(Collectors.toList())
                );
            } catch (IOException | IllegalArgumentException ex) {
                Logger.error("Could not set background images", ex);
            }
        });
    }
    private double stageHeight = 0;
    private double stageWidth = 0;
    private boolean viewToggle = false;
    private final Timeline fadeToView1;
    private final Timeline fadeToView2;

    private void setImage(Path path) {
        Image image = new Image("file:///" + path.toString());
        if (viewToggle) {
            view1.setImage(image);
            fadeToView2.stop();
            fadeToView1.playFromStart();
        } else {
            view2.setImage(image);
            fadeToView1.stop();
            fadeToView2.playFromStart();
        }
        viewToggle = !viewToggle;
    }

    private void resizeImage(ImageView view) {
        Image image = view.getImage();
        if (image == null) {
            toBeResizedAsSoonAsAImageIsInIt = view;
            return;
        }
        double imageHeight = image.getHeight();
        double imageWidth = image.getWidth();
        double imageRatio = imageWidth / imageHeight;
        double stageRatio = stageWidth / stageHeight;
        if (stageRatio > imageRatio) {//Cut off some height
            view.setFitWidth(stageWidth);
            view.setFitHeight(9999999);
            view.setTranslateX(0);
            view.setTranslateY(-(((stageWidth / imageRatio) - stageHeight) / 2));
        } else {//Cut off some width
            view.setFitHeight(stageHeight);
            view.setFitWidth(9999999);
            view.setTranslateX(-(((stageHeight * imageRatio) - stageWidth) / 2));
            view.setTranslateY(0);
        }
    }

    @Override
    public void onExit() {
        if (!imageChangingScheduler.isShutdown()) {
            imageChangingScheduler.shutdown();
        }
    }

}
