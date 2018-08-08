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

import afester.javafx.svg.SvgLoader;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Labeled;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.SVGPath;
import sharknoon.casey.ide.utils.settings.Logger;
import sharknoon.casey.ide.utils.settings.Resources;

import java.io.InputStream;
import java.util.*;

/**
 * @author Josua Frank
 */
public class Icons {
    
    private static final double DEFAULT_SIZE = 30;
    private static final Map<Icon, Image> IMAGE_CACHE = new HashMap<>();
    private static final Map<Icon, SVG> SVG_CACHE = new HashMap<>();
    private static final SvgLoader SVG_LOADER = new SvgLoader();
    
    public static Node get(Icon icon) {
        return get(icon, DEFAULT_SIZE);
    }
    
    public static Node get(Icon icon, double size) {
        return create(icon, size);
    }
    
    public static ObjectProperty<Node> iconToNodeProperty(ObjectProperty<Icon> icon) {
        return iconToNodeProperty(icon, DEFAULT_SIZE);
    }
    
    public static ObjectProperty<Node> iconToNodeProperty(ObjectProperty<Icon> icon, double size) {
        ObjectProperty<Node> result = new SimpleObjectProperty<>();
        ChangeListener<? super Icon> listener = (observable, oldValue, newValue) -> result.set(get(newValue, size));
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
        labeled.setGraphic(create(icon, DEFAULT_SIZE));
    }
    
    public static void set(Icon icon, Labeled labeled, double size) {
        labeled.setGraphic(create(icon, size));
    }
    
    public static void setCustom(Icon icon, ValueSetter<Node> valueSetter) {
        valueSetter.setValue(create(icon, DEFAULT_SIZE));
    }
    
    public static void setCustom(Icon icon, ValueSetter<Node> valueSetter, double size) {
        valueSetter.setValue(create(icon, size));
    }
    
    private static Node create(Icon icon, double desiredSize) {
        Optional<Group> svg = getSVG(icon);
        if (svg.isPresent()) {
            Group group = svg.get();
            double originalHeight = group.prefHeight(0.0);
            double originalWidth = group.prefWidth(0.0);
            double originalSideLength = originalHeight > originalWidth ? originalHeight : originalWidth;
            double scale = desiredSize / originalSideLength;
            group.setScaleX(scale);
            group.setScaleY(scale);
            StackPane result = new StackPane(new Group(group));
            result.setPrefSize(desiredSize, desiredSize);
            return result;
        }
        ImageView view = new ImageView();
        Optional<Image> imageOpt = getImage(icon);
        if (imageOpt.isPresent()) {
            view.setPreserveRatio(true);
            Image image = imageOpt.get();
            if (image.getWidth() > image.getHeight()) {
                view.setFitWidth(desiredSize);
            } else {
                view.setFitHeight(desiredSize);
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
                        var svgPaths = SVG_LOADER.loadSvg(stream.get());
                        var svg = new SVG(svgPaths);
                        SVG_CACHE.put(icon, svg);
                        return Optional.of(svgPaths);
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
            SVG svg = SVG_CACHE.get(icon);
            if (svg == null) {
                return Optional.empty();
            }
            Group result = svg.createSVG();
            return Optional.of(result);
        }
    }
    
    @FunctionalInterface
    public interface ValueSetter<T> {
        
        void setValue(T value);
    }
    
    private static class SVG {
        private final List<SVGShape> svgShapes;
        private final List<SVG> containers;
        private final int hashCode;
        
        public SVG(Group g) {
            svgShapes = new ArrayList<>();
            containers = new ArrayList<>();
            for (Node child : g.getChildren()) {
                if (child instanceof SVGPath) {
                    var path = (SVGPath) child;
                    SVGShape svgShape = new SVGShape();
                    svgShape.content = path.getContent();
                    svgShape.fillRule = path.getFillRule();
                    svgShape.fill = path.getFill();
                    svgShapes.add(svgShape);
                } else if (child instanceof Group) {
                    containers.add(new SVG((Group) child));
                } else {
                    Logger.error("Unknown SVG content: " + child);
                }
            }
            hashCode = svgShapes.hashCode() + containers.hashCode();
        }
        
        Group createSVG() {
            var result = new Group();
            var children = result.getChildren();
            for (SVGShape svgShape : svgShapes) {
                var svg = new SVGPath();
                svg.setContent(svgShape.content);
                svg.setFillRule(svgShape.fillRule);
                svg.setFill(svgShape.fill);
                children.add(svg);
            }
            for (SVG container : containers) {
                var svgc = container.createSVG();
                children.add(svgc);
            }
            return result;
        }
        
        @Override
        public int hashCode() {
            return hashCode;
        }
        
        private final static class SVGShape {
            //From SVGPath
            String content;
            FillRule fillRule;
            //From Shape
            Paint fill;
            
            int hashCode = -1;
            
            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                
                SVGShape svgShape = (SVGShape) o;
                
                return hashCode() == svgShape.hashCode();
            }
            
            @Override
            public int hashCode() {
                if (hashCode == -1) {
                    hashCode = Objects.hash(content, fillRule, fill);
                }
                return hashCode;
            }
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            
            SVG svg = (SVG) o;
            
            return hashCode() == svg.hashCode();
        }
        
        
    }
    
}
