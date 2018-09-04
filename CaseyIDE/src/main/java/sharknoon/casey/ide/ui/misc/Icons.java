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
package sharknoon.casey.ide.ui.misc;

import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.scene.*;
import javafx.scene.control.Labeled;
import javafx.scene.image.*;
import javafx.scene.layout.BorderPane;
import org.apache.batik.anim.dom.SVGOMDocument;
import sharknoon.casey.ide.utils.javafx.svg.ChachedSvgLoader;
import sharknoon.casey.ide.utils.settings.*;

import java.io.InputStream;
import java.util.*;

/**
 * @author Josua Frank
 */
public class Icons {
    
    private static final double DEFAULT_SIZE = 30;
    private static final Map<Icon, Image> IMAGE_CACHE = new HashMap<>();
    private static final Map<Icon, SVGOMDocument> SVG_CACHE = new HashMap<>();
    private static final ChachedSvgLoader SVG_LOADER = new ChachedSvgLoader();
    
    public static Node get(Icon icon) {
        return get(icon, DEFAULT_SIZE);
    }
    
    public static Node get(Icon icon, double size) {
        return get(icon, size, -1);
    }
    
    public static Node get(Icon icon, double maxX, double maxY) {
        return create(icon, maxX, maxY);
    }
    
    
    public static ObjectProperty<Node> iconToNodeProperty(ObjectProperty<Icon> icon) {
        return iconToNodeProperty(icon, DEFAULT_SIZE);
    }
    
    public static ObjectProperty<Node> iconToNodeProperty(ObjectProperty<Icon> icon, double size) {
        return iconToNodeProperty(icon, size, -1);
    }
    
    public static ObjectProperty<Node> iconToNodeProperty(ObjectProperty<Icon> icon, double maxX, double maxY) {
        ObjectProperty<Node> result = new SimpleObjectProperty<>();
        ChangeListener<? super Icon> listener = (observable, oldValue, newValue) -> result.set(get(newValue, maxX, maxY));
        icon.addListener(listener);
        listener.changed(icon, null, icon.get());
        return result;
    }
    
    public static ObjectProperty<Image> iconToImageProperty(ObjectProperty<Icon> icon) {
        ObjectProperty<Image> result = new SimpleObjectProperty<>();
        ChangeListener<? super Icon> listener = (observable, oldValue, newValue) -> result.set(getImage(newValue).orElse(null));
        icon.addListener(listener);
        listener.changed(icon, null, icon.get());
        return result;
    }
    
    public static void set(Icon icon, Labeled labeled) {
        set(icon, labeled, DEFAULT_SIZE);
    }
    
    public static void set(Icon icon, Labeled labeled, double size) {
        set(icon, labeled, size, -1);
    }
    
    public static void set(Icon icon, Labeled labeled, double maxX, double maxY) {
        labeled.setGraphic(create(icon, maxX, maxY));
    }
    
    public static void setCustom(Icon icon, ValueSetter<Node> valueSetter) {
        setCustom(icon, valueSetter, DEFAULT_SIZE);
    }
    
    public static void setCustom(Icon icon, ValueSetter<Node> valueSetter, double size) {
        setCustom(icon, valueSetter, size, -1);
    }
    
    public static void setCustom(Icon icon, ValueSetter<Node> valueSetter, double maxX, double maxY) {
        valueSetter.setValue(create(icon, maxX, maxY));
    }
    
    private static Node create(Icon icon, double maxX, double maxY) {
        Optional<Group> svg = getSVG(icon);
        if (svg.isPresent()) {
            Group group = svg.get();
            double originalWidth = group.prefWidth(42);//42 is ignored
            double originalHeight = group.prefHeight(42);//42 is ignored
            double widthDifferenceMultiplier = maxX / originalWidth;
            double heightDifferenceMultiplier = maxY < 0 ? maxX / originalHeight : maxY / originalHeight;
            double differenceMultiplier = widthDifferenceMultiplier < heightDifferenceMultiplier ? widthDifferenceMultiplier : heightDifferenceMultiplier;
            group.setScaleX(differenceMultiplier);
            group.setScaleY(differenceMultiplier);
            BorderPane result = new BorderPane(new Group(group));
            if (maxY < 0) {
                if (originalWidth > originalHeight) {
                    result.setPrefSize(originalWidth * differenceMultiplier, originalWidth * differenceMultiplier);
                } else {
                    result.setPrefSize(originalHeight * differenceMultiplier, originalHeight * differenceMultiplier);
                }
            } else {
                result.setPrefSize(originalWidth * differenceMultiplier, originalHeight * differenceMultiplier);
            }
            return result;
        }
        ImageView view = new ImageView();
        Optional<Image> imageOpt = getImage(icon);
        if (imageOpt.isPresent()) {
            view.setPreserveRatio(true);
            Image image = imageOpt.get();
            view.setFitWidth(maxX);
            if (maxY < 0) {
                view.setFitHeight(maxX);
            } else {
                view.setFitHeight(maxY);
            }
            view.setImage(image);
        } else {
            Logger.warning("Icon " + icon.toString() + " not found!");
        }
        return view;
    }
    
    public static Optional<Image> getImage(Icon icon) {
        if (!IMAGE_CACHE.containsKey(icon)) {
            synchronized (Icons.class) {
                Optional<InputStream> stream = Resources.getFileAsStream(icon.getPath(false), true);
                if (stream.isPresent()) {
                    Image image = new Image(stream.get());
                    IMAGE_CACHE.put(icon, image);
                    return Optional.of(image);
                } else {
                    IMAGE_CACHE.put(icon, null);
                    return Optional.empty();
                }
            }
        } else {
            return Optional.ofNullable(IMAGE_CACHE.get(icon));
        }
    }
    
    public static Optional<Group> getSVG(Icon icon) {
        if (!SVG_CACHE.containsKey(icon)) {
            synchronized (Icons.class) {
                Optional<InputStream> stream = Resources.getFileAsStream(icon.getPath(true), true);
                if (stream.isPresent()) {
                    try {
                        //Takes 1.364 seconds on the first start :/
                        var svgDoc = SVG_LOADER.loadSvgDocument(stream.get());
                        var svg = SVG_LOADER.loadSvg(svgDoc);
                        SVG_CACHE.put(icon, svgDoc);
                        return Optional.of(svg);
                    } catch (Exception e) {
                        Logger.error("Could not load SVG-Icon " + icon.name(), e);
                        return Optional.empty();
                    }
                } else {
                    //To boost performance by saying, this icon is not available as svg
                    SVG_CACHE.put(icon, null);
                    return Optional.empty();
                }
            }
        } else {
            var svgDoc = SVG_CACHE.get(icon);
            if (svgDoc == null) {
                return Optional.empty();
            }
            Group result = SVG_LOADER.loadSvg(svgDoc);
            return Optional.of(result);
        }
    }
    
    @FunctionalInterface
    public interface ValueSetter<T> {
        
        void setValue(T value);
    }
    
}
