package sharknoon.casey.ide.ui.styles;/*
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

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import sharknoon.casey.ide.ui.misc.Icon;
import sharknoon.casey.ide.utils.language.Word;
import sharknoon.casey.ide.utils.settings.Logger;
import sharknoon.casey.ide.utils.settings.Props;

import java.util.EnumSet;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public enum Styles {
    dark("DarkCSS.css", Icon.DARK, Word.MENUBAR_OPTIONS_STYLE_DARK_TEXT),
    light("LightCSS.css", Icon.LIGHT, Word.MENUBAR_OPTIONS_STYLE_LIGHT_TEXT);
    
    public static final Styles defaultStyle = dark;
    public static final ObjectProperty<Styles> currentStyle = new SimpleObjectProperty<>();
    private static final String styleKey = "style";
    
    public static CompletableFuture<Styles> getCurrentStyle() {
        if (currentStyle.get() == null) {
            return initCurrentStyle().thenApply((v) -> currentStyle.get());
        } else {
            return CompletableFuture.completedFuture(currentStyleProperty().get());
        }
    }
    
    public static void setCurrentStyle(Styles style) {
        if (currentStyle.get() == null) {
            initCurrentStyle().thenRun(() -> currentStyle.set(style));
        } else {
            currentStyleProperty().set(style);
        }
    }
    
    public static ObjectProperty<Styles> currentStyleProperty() {
        if (currentStyle.get() == null) {
            initCurrentStyle().join();
        }
        return currentStyle;
    }
    
    public static CompletableFuture<Void> initCurrentStyle() {
        return Props.get(styleKey).thenAccept(s -> {
            if (s.isPresent()) {
                String styleName = s.get();
                try {
                    currentStyle.set(valueOf(styleName));
                } catch (Exception ex) {
                    Logger.error(
                            "Invalid style name in database: " + styleName + " of " +
                                    EnumSet.allOf(Styles.class)
                                            .stream()
                                            .map(Enum::name)
                                            .collect(Collectors.joining(", ")),
                            ex
                    );
                }
            } else {
                Props.set(styleKey, defaultStyle.name());
                currentStyle.set(defaultStyle);
            }
            currentStyle.addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    Props.set(styleKey, newValue.name());
                } else {
                    Logger.error("New Style is null, using the default dark theme");
                    Props.set(styleKey, defaultStyle.name());
                    currentStyle.set(defaultStyle);
                }
            });
        });
    }
    
    public static void bindStyleSheets(ObservableList<String> stylesheets) {
        getCurrentStyle().thenAccept(s -> stylesheets.addAll(
                "sharknoon/casey/ide/ui/styles/GeneralCSS.css",
                s.getFullStyleSheetName()
        ));
        currentStyleProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != null) {
                stylesheets.remove(oldValue.getFullStyleSheetName());
            }
            if (newValue != null) {
                stylesheets.add(newValue.getFullStyleSheetName());
            }
        });
    }
    
    private final Icon icon;
    private final Word name;
    private final String styleSheetName;
    private final String fullStyleSheetName;
    
    Styles(String styleSheetName, Icon icon, Word name) {
        this.icon = icon;
        this.name = name;
        this.styleSheetName = styleSheetName;
        this.fullStyleSheetName = Styles
                .class
                .getPackageName()
                .replace('.', '/')
                + '/'
                + getStyleSheetName();
    }
    
    public Icon getIcon() {
        return icon;
    }
    
    public Word getName() {
        return name;
    }
    
    public String getStyleSheetName() {
        return styleSheetName;
    }
    
    public String getFullStyleSheetName() {
        return fullStyleSheetName;
    }
}
