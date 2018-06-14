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
package sharknoon.dualide.ui.menubar;

import java.util.Locale;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import sharknoon.dualide.logic.items.Project;
import sharknoon.dualide.logic.items.Welcome;
import sharknoon.dualide.ui.background.Background;
import sharknoon.dualide.ui.navigation.ItemTabPane;
import sharknoon.dualide.ui.navigation.ItemTreeView;
import sharknoon.dualide.ui.misc.Icon;
import sharknoon.dualide.ui.misc.Icons;
import sharknoon.dualide.utils.language.Language;
import sharknoon.dualide.utils.language.Word;
import sharknoon.dualide.utils.settings.Props;

/**
 *
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
        Language.setCustom(Word.MENUBAR_OPTIONS_TEXT, s -> menuOptions.setText(s));
        Icons.setCustom(Icon.COG, g -> menuOptions.setGraphic(g));
        menubar.getMenus().add(menuOptions);

        Menu menuLanguage = new Menu();
        Language.setCustom(Word.MENUBAR_OPTIONS_LANGUAGE_TEXT, s -> menuLanguage.setText(s));
        Icons.setCustom(Icon.LANGUAGE, g -> menuLanguage.setGraphic(g));
        menuOptions.getItems().add(menuLanguage);

        Language.getAllLanguages().forEach((loc, lang) -> {
            MenuItem menuItemLanguage = new MenuItem();
            Language.setCustom(
                    () -> loc.getDisplayLanguage(Language.getLanguage().getLocale()),
                    s -> menuItemLanguage.setText(s));
            Icon icon = Icon.forName(loc.getDisplayLanguage(Locale.ENGLISH));
            Icons.setCustom(icon, g -> menuItemLanguage.setGraphic(g));
            menuItemLanguage.setId(loc.getLanguage());
            menuItemLanguage.setOnAction((event) -> {
                Locale l = new Locale(menuItemLanguage.getId());
                Language.changeLanguage(Language.forLocale(l));
            });
            menuLanguage.getItems().add(menuItemLanguage);
        });

        Menu menuBackgroundImages = new Menu();
        Language.setCustom(Word.MENUBAR_OPTIONS_BACKGROUND_TEXT, s -> menuBackgroundImages.setText(s));
        Icons.setCustom(Icon.BACKGROUND, g -> menuBackgroundImages.setGraphic(g));
        menuOptions.getItems().add(menuBackgroundImages);

        MenuItem menuItemOpenBackgroundFolder = new MenuItem();
        Language.setCustom(Word.MENUBAR_OPTIONS_BACKGROUND_OPEN_FOLDER_TEXT, s -> menuItemOpenBackgroundFolder.setText(s));
        Icons.setCustom(Icon.LOAD, g -> menuItemOpenBackgroundFolder.setGraphic(g));
        menuItemOpenBackgroundFolder.setOnAction((event) -> {
            Background.openImagesFolder();
        });
        menuBackgroundImages.getItems().add(menuItemOpenBackgroundFolder);

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
        Label labelChangingValue = new Label();
        Slider sliderDuration = new Slider(0, 60, 1);
        sliderDuration.setMinWidth(300);
        sliderDuration.setShowTickMarks(true);
        sliderDuration.setShowTickLabels(true);
        sliderDuration.setBlockIncrement(1);
        sliderDuration.setMinorTickCount(1);
        sliderDuration.setMajorTickUnit(10);
        sliderDuration.valueProperty().addListener((observable, oldValue, newValue) -> {
            labelChangingValue.setText(newValue.intValue() + " " + Language.get(Word.MENUBAR_OPTIONS_BACKGROUND_SET_DURATION_MINUTES_TEXT));
        });
        sliderDuration.valueChangingProperty().addListener((observable, oldValue, isChangeing) -> {
            if (!isChangeing) {
                Background.setDuration((int) sliderDuration.getValue());
                Props.set(durationKey, (int) sliderDuration.getValue() + "");
            }
        });
        Props.get(durationKey).thenAccept(s -> {
            if (s.isPresent()) {
                sliderDuration.setValue(Double.valueOf(s.get()));
            }
        });
        gridPaneMenuItemBackgroundDurationContent.add(sliderDuration, 1, 1, 1, 1);
        gridPaneMenuItemBackgroundDurationContent.add(labelChangingValue, 2, 1, 1, 1);
        MenuItem menuItemSetBackgroundDuration = new CustomMenuItem(gridPaneMenuItemBackgroundDurationContent);
        menuBackgroundImages.getItems().add(menuItemSetBackgroundDuration);
    }

    private static void initProjectMenu() {
        Menu menuProject = new Menu();
        Language.setCustom(Word.MENUBAR_PROJECT_TEXT, s -> menuProject.setText(s));
        Icons.setCustom(Icon.PROJECT, g -> menuProject.setGraphic(g));
        menubar.getMenus().add(menuProject);

        MenuItem menuCloseProject = new MenuItem();
        Language.setCustom(Word.MENUBAR_PROJECT_CLOSE_TEXT, s -> menuCloseProject.setText(s));
        Icons.setCustom(Icon.CLOSE, g -> menuCloseProject.setGraphic(g));
        menuCloseProject.setOnAction((event) -> {
            Project.getCurrentProject().ifPresent(p -> {
                p.save();
            });
            Welcome.getWelcome().getSite().select();
        });
        menuProject.getItems().add(menuCloseProject);
    }
}
