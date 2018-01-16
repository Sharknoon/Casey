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
package sharknoon.dualide.ui.misc;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javafx.scene.Node;
import javafx.scene.control.Labeled;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import sharknoon.dualide.logic.Item;
import sharknoon.dualide.utils.settings.Logger;
import sharknoon.dualide.utils.settings.Ressources;

/**
 *
 * @author Josua Frank
 */
public class Icons {

    private static final double DEFAULT_HEIGHT = 30;

    public static ImageView get(Icon icon) {
        return get(icon, DEFAULT_HEIGHT);
    }

    public static ImageView get(Icon icon, double height) {
        return create(icon, height);
    }

    public static void set(Labeled labeled, Icon icon) {
        labeled.setGraphic(create(icon, DEFAULT_HEIGHT));
    }

    public static void set(Labeled labeled, Icon icon, double height) {
        labeled.setGraphic(create(icon, height));
    }

    public static void setCustom(ValueSetter<Node> valueSetter, Icon icon){
        valueSetter.setValue(create(icon, DEFAULT_HEIGHT));
    }

    public static void setCustom(ValueSetter<Node> valueSetter, Icon icon, double height){
        valueSetter.setValue(create(icon, height));
    }

    private static final Map<Icon, Image> IMAGE_CACHE = new HashMap<>();

    private static ImageView create(Icon icon, double height) {
        ImageView view = new ImageView();
        Optional<Image> image = getImage(icon);
        if (image.isPresent()) {
            view.setPreserveRatio(true);
            view.setFitHeight(height);
            view.setImage(image.get());
        }
        return view;
    }

    private static Optional<Image> getImage(Icon icon) {
        if (!IMAGE_CACHE.containsKey(icon)) {
            Optional<InputStream> stream = Ressources.getFileAsStream(icon.getPath(), true);
            if (stream.isPresent()) {
                Image image = new Image(stream.get());
                IMAGE_CACHE.put(icon, image);
                return Optional.of(image);
            } else {
                IMAGE_CACHE.put(icon, null);
                Logger.warning("Icon " + icon.toString() + " not found!");
                return Optional.empty();
            }
        } else {
            return Optional.ofNullable(IMAGE_CACHE.get(icon));
        }
    }

    @FunctionalInterface
    public interface ValueSetter<T> {

        public void setValue(T value);
    }

}
