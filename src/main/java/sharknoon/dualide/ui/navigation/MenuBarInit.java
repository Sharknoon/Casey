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
package sharknoon.dualide.ui.navigation;


import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import sharknoon.dualide.logic.items.Project;
import sharknoon.dualide.logic.items.Welcome;
import sharknoon.dualide.ui.background.Background;
import sharknoon.dualide.ui.misc.Icon;
import sharknoon.dualide.ui.misc.Icons;
import sharknoon.dualide.ui.styles.Styles;
import sharknoon.dualide.utils.javafx.SnapSlider;
import sharknoon.dualide.utils.language.Language;
import sharknoon.dualide.utils.language.Word;
import sharknoon.dualide.utils.settings.Props;

import java.util.EnumSet;
import java.util.Locale;

/**
 * @author Josua Frank
 */
public class MenuBarInit {
    
    private static javafx.scene.control.MenuBar menubar;
    
    public static void init(javafx.scene.control.MenuBar menu) {
        menubar = menu;
        initProjectMenu();
        initOptionsMenu();
    }
    
    private static void initOptionsMenu() {
        Menu menuOptions = new Menu();
        Language.setCustom(Word.MENUBAR_OPTIONS_TEXT, menuOptions::setText);
        Icons.setCustom(Icon.COG, menuOptions::setGraphic);
        
        menuOptions.getItems().addAll(
                initOptionsLanugageMenu(),
                initOptionsBackgroundMenu(),
                initOptionsStylesMenu()
        );
        
        menubar.getMenus().add(menuOptions);
    }
    
    private static Menu initOptionsLanugageMenu() {
        Menu menuLanguage = new Menu();
        Language.setCustom(Word.MENUBAR_OPTIONS_LANGUAGE_TEXT, menuLanguage::setText);
        Icons.setCustom(Icon.LANGUAGE, menuLanguage::setGraphic);
        
        
        Language.getAllLanguages().forEach((locale, language) -> {
            MenuItem menuItemLanguage = new MenuItem();
            Language.setCustom(
                    () -> locale.getDisplayLanguage(locale),
                    menuItemLanguage::setText);
            Icon icon = Icon.forName(locale.getDisplayLanguage(Locale.ENGLISH));
            Icons.setCustom(icon, menuItemLanguage::setGraphic);
            menuItemLanguage.setId(locale.getLanguage());
            menuItemLanguage.setOnAction((event) -> {
                Locale l = new Locale(menuItemLanguage.getId());
                Language.changeLanguage(Language.forLocale(l));
            });
            menuLanguage.getItems().add(menuItemLanguage);
        });
        return menuLanguage;
    }
    
    private static Menu initOptionsBackgroundMenu() {
        Menu menuBackgroundImages = new Menu();
        Language.setCustom(Word.MENUBAR_OPTIONS_BACKGROUND_TEXT, menuBackgroundImages::setText);
        Icons.setCustom(Icon.BACKGROUND, menuBackgroundImages::setGraphic);
        
        //Open image folder
        MenuItem menuItemOpenBackgroundFolder = new MenuItem();
        Language.setCustom(Word.MENUBAR_OPTIONS_BACKGROUND_OPEN_FOLDER_TEXT, menuItemOpenBackgroundFolder::setText);
        Icons.setCustom(Icon.LOAD, menuItemOpenBackgroundFolder::setGraphic);
        menuItemOpenBackgroundFolder.setOnAction((event) -> {
            Background.openImagesFolder();
        });
        menuBackgroundImages.getItems().add(menuItemOpenBackgroundFolder);
        
        //Set image duration
        GridPane gridPaneMenuItemBackgroundDurationContent = new GridPane();
        gridPaneMenuItemBackgroundDurationContent.setVgap(10);
        gridPaneMenuItemBackgroundDurationContent.setHgap(10);
        gridPaneMenuItemBackgroundDurationContent.setAlignment(Pos.CENTER);
        
        Node nodeIcon = Icons.get(Icon.DURATION);
        gridPaneMenuItemBackgroundDurationContent.add(nodeIcon, 0, 0, 1, 2);
        
        Label labelSetDurationText = new Label();
        Language.set(Word.MENUBAR_OPTIONS_BACKGROUND_SET_DURATION_TEXT, labelSetDurationText);
        gridPaneMenuItemBackgroundDurationContent.add(labelSetDurationText, 1, 0, 2, 1);
        
        final String durationKey = "backgroundChangeingDuration";
        SnapSlider sliderDuration = new SnapSlider(0, 60, -1);
        sliderDuration.setMinWidth(300);
        sliderDuration.setShowTickMarks(true);
        sliderDuration.setShowTickLabels(true);
        sliderDuration.setBlockIncrement(1);
        sliderDuration.setMinorTickCount(9);
        sliderDuration.setMajorTickUnit(10);
        Label labelChangingValue = new Label();
        labelChangingValue.textProperty().bind(
                Bindings.createLongBinding(
                        () -> Math.round(sliderDuration.valueProperty().doubleValue()),
                        sliderDuration.valueProperty()
                )
                        .asString()
                        .concat(" " + Language.get(Word.MENUBAR_OPTIONS_BACKGROUND_SET_DURATION_SECONDS_TEXT))
        );
        Background.durationProperty().bind(sliderDuration.finalValueProperty());
        sliderDuration.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (sliderDuration.isValueChanging())
                Props.set(durationKey, (int) sliderDuration.getValue() + "");
        });
        Props.get(durationKey).thenAccept(os -> os.map(Double::valueOf).ifPresent(sliderDuration::setValue));
        gridPaneMenuItemBackgroundDurationContent.add(sliderDuration, 1, 1, 1, 1);
        gridPaneMenuItemBackgroundDurationContent.add(labelChangingValue, 2, 1, 1, 1);
        MenuItem menuItemSetBackgroundDuration = new CustomMenuItem(gridPaneMenuItemBackgroundDurationContent);
        menuBackgroundImages.getItems().add(menuItemSetBackgroundDuration);
        
        return menuBackgroundImages;
    }
    
    private static Menu initOptionsStylesMenu() {
        Menu menuStyles = new Menu();
        Language.setCustom(Word.MENUBAR_OPTIONS_STYLE_TEXT, menuStyles::setText);
        Icons.setCustom(Icon.STYLE, menuStyles::setGraphic);
        
        ToggleGroup toggleGroupStyle = new ToggleGroup();
        Styles currentStyle = Styles.getCurrentStyle();
        EnumSet.allOf(Styles.class).forEach(style -> {
            RadioMenuItem menuItemStyle = new RadioMenuItem();
            menuItemStyle.setToggleGroup(toggleGroupStyle);
            Language.setCustom(style.getName(), menuItemStyle::setText);
            Icons.setCustom(style.getIcon(), menuItemStyle::setGraphic);
            if (style == currentStyle) {
                menuItemStyle.setSelected(true);
            }
            menuItemStyle.setOnAction(e -> Styles.setCurrentStyle(style));
            menuStyles.getItems().add(menuItemStyle);
        });
        
        return menuStyles;
    }
    
    private static void initProjectMenu() {
        Menu menuProject = new Menu();
        menuProject.setDisable(true);
        Language.setCustom(Word.MENUBAR_PROJECT_TEXT, menuProject::setText);
        Icons.setCustom(Icon.PROJECT, menuProject::setGraphic);
        Project.currentProjectProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                menuProject.setDisable(true);
            } else {
                menuProject.setDisable(false);
            }
        });
        menubar.getMenus().add(menuProject);
        
        MenuItem menuCloseProject = new MenuItem();
        Language.setCustom(Word.MENUBAR_PROJECT_CLOSE_TEXT, menuCloseProject::setText);
        Icons.setCustom(Icon.CLOSE, menuCloseProject::setGraphic);
        menuCloseProject.setOnAction((event) -> {
            Project.getCurrentProject().ifPresent(Project::close);
            Welcome.getWelcome().getSite().select();
        });
        menuProject.getItems().add(menuCloseProject);
    }
}
