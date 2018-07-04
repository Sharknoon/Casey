package sharknoon.dualide.ui.styles;/*
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
import sharknoon.dualide.ui.misc.Icon;
import sharknoon.dualide.utils.language.Word;

public enum Styles {
    dark("DarkCSS.css", Icon.DARK, Word.MENUBAR_OPTIONS_STYLE_DARK_TEXT),
    light("LightCSS.css", Icon.LIGHT, Word.MENUBAR_OPTIONS_STYLE_LIGHT_TEXT);
    
    public static final ObjectProperty<Styles> currentStyle = new SimpleObjectProperty<>(dark);
    
    public static Styles getCurrentStyle() {
        return currentStyleProperty().get();
    }
    
    public static void setCurrentStyle(Styles style) {
        currentStyleProperty().set(style);
    }
    
    public static ObjectProperty<Styles> currentStyleProperty() {
        return currentStyle;
    }
    
    public static void bindStyleSheets(ObservableList<String> stylesheets) {
        //TODO set currentstyle style from db here
        stylesheets.addAll(
                "sharknoon/dualide/ui/styles/GeneralCSS.css",
                getCurrentStyle().getFullStyleSheetName()
        );
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
