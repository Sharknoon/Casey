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
package sharknoon.dualide.ui.background;

import java.awt.Desktop;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import sharknoon.dualide.misc.Exitable;
import sharknoon.dualide.ui.MainApplication;
import sharknoon.dualide.ui.sites.function.UISettings;
import sharknoon.dualide.utils.settings.Logger;
import sharknoon.dualide.utils.settings.Resources;

/**
 *
 * @author Josua Frank
 */
public class Background implements Exitable {

    private static ImageView view1;
    private static ImageView view2;
    private static final List<Path> images = new ArrayList<>();
    private static int counter = 0;
    private static final ScheduledExecutorService imageChangingSchedulerService = Executors.newScheduledThreadPool(1);
    private static ScheduledFuture imageChangingScheduler;
    private static ImageView toBeResizedAsSoonAsAImageIsInIt = null;

    public static void init(ImageView imageView1, ImageView imageView2) {
        Background wb = new Background(imageView1, imageView2);
    }

    private Background(ImageView imageView1, ImageView imageView2) {
        view1 = imageView1;
        view2 = imageView2;
        reloadImages();
        setDuration(1);
        MainApplication.registerInitializable((scene) -> {
            scene.widthProperty().addListener((observable, oldValue, newValue) -> {
                stageWidth = newValue.doubleValue();
                resizeImage(view1);
                resizeImage(view2);
            });
        });
        MainApplication.registerInitializable((scene) -> {
            scene.heightProperty().addListener((observable, oldValue, newValue) -> {
                stageHeight = newValue.doubleValue();
                resizeImage(view1);
                resizeImage(view2);
            });
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
        Resources.getDirectory("Backgroundimages", false).ifPresent((path) -> {
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
    private static double stageHeight = 0;
    private static double stageWidth = 0;
    private static boolean viewToggle = false;
    private static Timeline fadeToView1;
    private static Timeline fadeToView2;

    private static void setImage(Path path) {
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

    private static void resizeImage(ImageView view) {
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

    public static void openImagesFolder() {
        Optional<Path> imagesPath = Resources.getDirectory("Backgroundimages", false);
        if (imagesPath.isPresent()) {
            try {
                Desktop.getDesktop().open(imagesPath.get().toFile());
            } catch (IOException ex) {
                Logger.error("Could not open the Backgroundimages folder", ex);
            }
        }
    }

    public static void setDuration(int durationInMinutes) {
        if (imageChangingScheduler != null) {
            imageChangingScheduler.cancel(false);
        }
        if (durationInMinutes > 0) {
            imageChangingScheduler = imageChangingSchedulerService.scheduleAtFixedRate(() -> {
                try {
                    if (images.size() - 1 > counter) {
                        counter++;
                    } else {
                        counter = 0;
                    }
                    if (images.size() > counter) {
                        setImage(images.get(counter));
                    }
                    if (toBeResizedAsSoonAsAImageIsInIt != null) {
                        resizeImage(toBeResizedAsSoonAsAImageIsInIt);
                        toBeResizedAsSoonAsAImageIsInIt = null;
                    }
                } catch (Exception e) {
                    Logger.error("Could not change the background image", e);
                }
            }, 0, (long) durationInMinutes, TimeUnit.MINUTES);
        }
    }

    @Override
    public void exit() {
        if (!imageChangingSchedulerService.isShutdown()) {
            imageChangingSchedulerService.shutdown();
        }
    }

}
