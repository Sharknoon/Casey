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
package sharknoon.dualide.ui;

import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.JFXTreeView;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToolBar;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import sharknoon.dualide.logic.Item;
import sharknoon.dualide.ui.function.Function;
import sharknoon.dualide.ui.function.WorkspaceBackground;
import sharknoon.dualide.ui.welcome.WelcomeSite;
import sharknoon.dualide.utils.language.Language;
import sharknoon.dualide.utils.language.Word;

/**
 *
 * @author Josua Frank
 */
public class MainController implements Initializable {

    @FXML
    private TabPane tabPane;

    @FXML
    private TreeView treeView;

    @FXML
    private Menu menuOptions;

    @FXML
    private ImageView imageView1;

    @FXML
    private ImageView imageView2;

    private static Tab currentTab;
    private static final Map<Tab, TabPaneDisplayable> TABS = new HashMap<>();

    @FXML
    Button buttonAddFunction;

    private static MainController controller;

    public MainController() {
        controller = this;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //init of currentTab
        currentTab = tabPane.getSelectionModel().getSelectedItem();
        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            currentTab = newValue;
        });
        //init of handlers
        tabPane.setOnScroll((event) -> {
            getCurrentFunction().ifPresent(f -> f.onScroll(event));
        });
        tabPane.setOnZoom((event) -> {
            getCurrentFunction().ifPresent(f -> f.onZoom(event));
        });
        tabPane.setOnMousePressed((event) -> {
            //System.out.println("mouse pressed");
            getCurrentFunction().ifPresent(f -> f.onMousePressed(event));
        });
        tabPane.setOnMouseDragged((event) -> {
            getCurrentFunction().ifPresent(f -> f.onMouseDragged(event));
        });
        tabPane.setOnMouseReleased((event) -> {
            getCurrentFunction().ifPresent(f -> f.onMouseReleased(event));
        });
        tabPane.setOnMouseMoved((event) -> {
            getCurrentFunction().ifPresent(f -> f.onMouseMoved(event));
        });
        tabPane.setOnMouseClicked((event) -> {
            getCurrentFunction().ifPresent(f -> f.onMouseClicked(event));
        });
        tabPane.setOnContextMenuRequested((event) -> {
            getCurrentFunction().ifPresent(f -> f.onContextMenuRequested(event));
        });
        tabPane.setOnKeyReleased((event) -> {
            getCurrentFunction().ifPresent(f -> f.onKeyReleased(event));
        });
        buttonAddFunction.setOnAction((event) -> {
            createNewFunction("Hello World");
        });
        WorkspaceBackground.setBackground(imageView1, imageView2);
        initOptionsMenu();
        showWelcomeScreen();
    }

    public static Optional<Function> getCurrentFunction() {
        return Optional.empty();
    }

    public static void showWelcomeScreen() {
        addTab(new WelcomeSite());
    }

    public static void createNewFunction(String title) {
        Tab tab = new Tab(title);
        //controller.TABS.put(tab, new Function(tab));
        //controller.tabPane.getTabs().add(tab);
    }

    private static void addTab(TabPaneDisplayable displayable) {
        Tab newTab = new Tab(displayable.getName(), displayable.getPane());
        newTab.setGraphic(displayable.getIcon());
        TABS.put(newTab, displayable);
        controller.tabPane.getTabs().add(newTab);
    }

    public static TreeView getTreeView() {
        return controller.treeView;
    }

    private static void initOptionsMenu() {
        Menu menuOptions = controller.menuOptions;
        Language.setCustom(Word.MENUBAR_OPTIONS_TEXT, s -> menuOptions.setText(s));
        menuOptions.setGraphic(GlyphsDude.createIcon(FontAwesomeIcon.COG));

        Menu menuLanguage = new Menu();
        Language.setCustom(Word.MENUBAR_OPTIONS_LANGUAGE_TEXT, s -> menuLanguage.setText(s));
        menuLanguage.setGraphic(GlyphsDude.createIcon(FontAwesomeIcon.LANGUAGE));
        menuOptions.getItems().add(menuLanguage);

        Language.getAllLanguages().forEach((loc, lang) -> {
            MenuItem menuItemLanguage = new MenuItem();
            Language.setCustom(
                    e -> loc.getDisplayLanguage(Language.getLanguage().getLocale()),
                    s -> menuItemLanguage.setText(s));
            //menuItemLanguage.setGraphic(value);
            menuItemLanguage.setId(loc.getLanguage());
            menuItemLanguage.setOnAction((event) -> {
                Locale l = new Locale(menuItemLanguage.getId());
                Language.changeLanguage(Language.forLocale(l));
            });
            menuLanguage.getItems().add(menuItemLanguage);
        });

    }

}
