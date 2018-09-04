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
package sharknoon.casey.ide.ui.navigation;


import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import sharknoon.casey.ide.logic.items.*;
import sharknoon.casey.ide.misc.Updater;
import sharknoon.casey.ide.ui.about.About;
import sharknoon.casey.ide.ui.background.Background;
import sharknoon.casey.ide.ui.misc.*;
import sharknoon.casey.ide.ui.styles.Styles;
import sharknoon.casey.ide.ui.tutorial.Tutorial;
import sharknoon.casey.ide.utils.javafx.SnapSlider;
import sharknoon.casey.ide.utils.language.*;

import java.awt.*;
import java.util.*;
import java.util.function.Consumer;

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
                initOptionsLanguageMenu(),
                initOptionsBackgroundMenu(),
                initOptionsStylesMenu(),
                initOptionsUpdateMenu(),
                initOptionsHelpMenu(),
                initOptionsAboutMenu()
        );
        
        menubar.getMenus().add(menuOptions);
    }
    
    private static Menu initOptionsLanguageMenu() {
        Menu menuLanguage = new Menu();
        Language.setCustom(Word.MENUBAR_OPTIONS_LANGUAGE_TEXT, menuLanguage::setText);
        Icons.setCustom(Icon.LANGUAGE, menuLanguage::setGraphic);
        
        ToggleGroup toggleGroupLanguage = new ToggleGroup();
        Map<Language, RadioMenuItem> radioMenuItems = new HashMap<>();
        Language.getAllLanguages().forEach((locale, language) -> {
            RadioMenuItem menuItemLanguage = new RadioMenuItem();
            Language.setCustom(
                    () -> locale.getDisplayLanguage(locale),
                    menuItemLanguage::setText
            );
            Icon icon = Icon.forName(locale.getDisplayLanguage(Locale.ENGLISH));
            Icons.setCustom(icon, menuItemLanguage::setGraphic);
            menuItemLanguage.setToggleGroup(toggleGroupLanguage);
            //menuItemLanguage.setId(locale.getLanguage());
            menuItemLanguage.setOnAction((event) -> {
                //Locale l = new Locale(menuItemLanguage.getId());
                Language.changeLanguage(language);
            });
            radioMenuItems.put(language, menuItemLanguage);
            menuLanguage.getItems().add(menuItemLanguage);
        });
        Consumer<Language> set = l -> {
            if (radioMenuItems.containsKey(l)) {
                radioMenuItems.get(l).setSelected(true);
            }
        };
        Language.addLanguageChangeListener(set);
        set.accept(Language.getLanguage());
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
    
        //Reload image folder
        MenuItem menuItemReloadBackgroundImages = new MenuItem();
        Language.setCustom(Word.MENUBAR_OPTIONS_BACKGROUND_RELOAD_IMAGES, menuItemReloadBackgroundImages::setText);
        Icons.setCustom(Icon.RELOAD, menuItemReloadBackgroundImages::setGraphic);
        menuItemReloadBackgroundImages.setOnAction(event -> {
            Background.reloadImages();
        });
        menuBackgroundImages.getItems().add(menuItemReloadBackgroundImages);
        
        //Set image duration
        GridPane gridPaneMenuItemBackgroundDurationContent = new GridPane();
        gridPaneMenuItemBackgroundDurationContent.setVgap(10);
        gridPaneMenuItemBackgroundDurationContent.setHgap(10);
        gridPaneMenuItemBackgroundDurationContent.setAlignment(Pos.CENTER);
        
        Node nodeIcon = Icons.get(Icon.DURATION);
        gridPaneMenuItemBackgroundDurationContent.add(nodeIcon, 0, 0, 1, 2);
    
        Label textSetDurationText = new Label();
        textSetDurationText.getStyleClass().setAll("menu-item");
        Language.setCustom(Word.MENUBAR_OPTIONS_BACKGROUND_SET_DURATION_TEXT, textSetDurationText::setText);
        gridPaneMenuItemBackgroundDurationContent.add(textSetDurationText, 1, 0, 2, 1);
    
    
        SnapSlider sliderDuration = new SnapSlider(0, 60, -1);
        sliderDuration.setMinWidth(300);
        sliderDuration.setShowTickMarks(true);
        sliderDuration.setShowTickLabels(true);
        sliderDuration.setBlockIncrement(1);
        sliderDuration.setMinorTickCount(9);
        sliderDuration.setMajorTickUnit(10);
        Text textChangingValue = new Text();
        textChangingValue.getStyleClass().setAll("menu-item");
        textChangingValue.textProperty().bind(
                Bindings.createLongBinding(
                        () -> Math.round(sliderDuration.valueProperty().doubleValue()),
                        sliderDuration.valueProperty()
                )
                        .asString()
                        .concat(" " + Language.get(Word.MENUBAR_OPTIONS_BACKGROUND_SET_DURATION_SECONDS_TEXT))
        );
        Background.durationProperty().bind(sliderDuration.finalValueProperty());
    
        gridPaneMenuItemBackgroundDurationContent.add(sliderDuration, 1, 1, 1, 1);
        gridPaneMenuItemBackgroundDurationContent.add(textChangingValue, 2, 1, 1, 1);
        MenuItem menuItemSetBackgroundDuration = new CustomMenuItem(gridPaneMenuItemBackgroundDurationContent);
        menuBackgroundImages.getItems().add(menuItemSetBackgroundDuration);
        
        return menuBackgroundImages;
    }
    
    private static Menu initOptionsStylesMenu() {
        Menu menuStyles = new Menu();
        Language.setCustom(Word.MENUBAR_OPTIONS_STYLE_TEXT, menuStyles::setText);
        Icons.setCustom(Icon.STYLE, menuStyles::setGraphic);
        
        ToggleGroup toggleGroupStyle = new ToggleGroup();
        Map<Styles, RadioMenuItem> items = new HashMap<>();
        EnumSet.allOf(Styles.class).forEach(style -> {
            RadioMenuItem menuItemStyle = new RadioMenuItem();
            items.put(style, menuItemStyle);
            menuItemStyle.setToggleGroup(toggleGroupStyle);
            Language.setCustom(style.getName(), menuItemStyle::setText);
            Icons.setCustom(style.getIcon(), menuItemStyle::setGraphic);
            menuItemStyle.setOnAction(e -> Styles.setCurrentStyle(style));
            menuStyles.getItems().add(menuItemStyle);
        });
        Styles.getCurrentStyle().thenAccept(s -> {
            if (items.containsKey(s)) {
                items.get(s).setSelected(true);
            }
        });
        
        return menuStyles;
    }
    
    private static MenuItem initOptionsUpdateMenu() {
        MenuItem menuItemUpdate = new MenuItem();
        Language.setCustom(Word.MENUBAR_OPTIONS_UPDATE_TEXT, menuItemUpdate::setText);
        Icons.setCustom(Icon.UPDATE, menuItemUpdate::setGraphic);
        
        menuItemUpdate.setOnAction(e -> Updater.checkForUpdates());
        
        return menuItemUpdate;
    }
    
    private static MenuItem initOptionsHelpMenu() {
        MenuItem menuItemHelp = new MenuItem();
        Language.setCustom(Word.HELP, menuItemHelp::setText);
        Icons.setCustom(Icon.HELP, menuItemHelp::setGraphic);
        
        menuItemHelp.setOnAction(e -> Tutorial.showTutorial());
        
        return menuItemHelp;
    }
    
    private static MenuItem initOptionsAboutMenu() {
        MenuItem menuItemAbout = new MenuItem();
        Language.setCustom(Word.MENUBAR_OPTIONS_ABOUT_TEXT, menuItemAbout::setText);
        Icons.setCustom(Icon.INFO, menuItemAbout::setGraphic);
        
        menuItemAbout.setOnAction(e -> About.show());
        
        return menuItemAbout;
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
    
        menuProject.getItems().addAll(
                initProjectOpenFolderMenu(),
                initProjectCloseMenu()
        );
        
        menubar.getMenus().add(menuProject);
    }
    
    private static MenuItem initProjectOpenFolderMenu() {
        MenuItem menuOpenProjectFolder = new MenuItem();
        Language.setCustom(Word.MENUBAR_PROJECT_OPEN_FOLDER_TEXT, menuOpenProjectFolder::setText);
        Icons.setCustom(Icon.LOAD, menuOpenProjectFolder::setGraphic);
        menuOpenProjectFolder.setOnAction((event) -> {
            Project.getCurrentProject().ifPresent(p -> {
                try {
                    Desktop d = Desktop.getDesktop();
                    d.open(p.forceGetSaveFile().getParent().toFile());
                } catch (Exception ignored) {
                }
            });
        });
        return menuOpenProjectFolder;
    }
    
    private static MenuItem initProjectCloseMenu() {
        MenuItem menuCloseProject = new MenuItem();
        Language.setCustom(Word.MENUBAR_PROJECT_CLOSE_TEXT, menuCloseProject::setText);
        Icons.setCustom(Icon.CLOSE, menuCloseProject::setGraphic);
        menuCloseProject.setOnAction((event) -> {
            Project.getCurrentProject().ifPresent(Project::close);
            Welcome.getWelcome().getSite().select();
        });
        return menuCloseProject;
    }
}
