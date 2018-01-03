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
package sharknoon.dualide.ui.flowchart;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import sharknoon.dualide.Main;
import sharknoon.dualide.misc.Exitable;
import sharknoon.dualide.utils.settings.Logger;
import sharknoon.dualide.utils.settings.Ressources;

/**
 *
 * @author Josua Frank
 */
public class WorkspaceBackground implements Exitable {

    final ImageView view;
    final List<Path> images = new ArrayList<>();
    int counter = 0;
    final ScheduledExecutorService imageChangingScheduler = Executors.newScheduledThreadPool(1);

    public static void setBackground(ImageView view){
        WorkspaceBackground wb = new WorkspaceBackground(view);
    }
    
    private WorkspaceBackground(ImageView view) {
        this.view = view;
        reloadImages();
        imageChangingScheduler.scheduleAtFixedRate(() -> {
            try {
                if (images.size() - 1 > counter) {
                    counter++;
                } else {
                    counter = 0;
                }
                setImage(images.get(counter));
            } catch (Exception e) {
                Logger.error("Could not change the background image", e);
            }
        }, 0, 5, TimeUnit.SECONDS);
        Main.stage.widthProperty().addListener((observable, oldValue, newValue) -> {
            stageWidth = newValue.doubleValue();
            resizeImage();
        });
        Main.stage.heightProperty().addListener((observable, oldValue, newValue) -> {
            stageHeight = newValue.doubleValue();
            resizeImage();
        });
        Main.registerExitable(this);
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
    double stageHeight = 0;
    double stageWidth = 0;

    private void setImage(Path path) {
        Image image = new Image("file:///" + path.toString());
        view.setImage(image);
    }

    private void resizeImage() {
        Image image = view.getImage();
        if (image == null) {
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
