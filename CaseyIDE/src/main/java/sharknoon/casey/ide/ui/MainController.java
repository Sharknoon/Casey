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
package sharknoon.casey.ide.ui;

import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import sharknoon.casey.ide.logic.items.*;
import sharknoon.casey.ide.misc.Updater;
import sharknoon.casey.ide.ui.background.Background;
import sharknoon.casey.ide.ui.navigation.*;
import sharknoon.casey.ide.ui.tutorial.Tutorial;
import sharknoon.casey.ide.utils.settings.Keyboard;

import java.net.URL;
import java.util.ResourceBundle;

/**
 *
 * @author Josua Frank
 */
public class MainController {

    @FXML
    private TabPane tabPane;

    @FXML
    private TreeView<Item> treeView;

    @FXML
    private MenuBar menubar;

    @FXML
    private ToolBar toolbar;

    @FXML
    private ImageView imageView1;

    @FXML
    private ImageView imageView2;

    private static MainController controller;

    public MainController() {
        controller = this;
    }

    @FXML
    public void initialize() {
        Background.init(imageView1, imageView2);
        MenuBarInit.init(menubar);
        ToolBarInit.init(toolbar);
        ItemTreeView.init(treeView);
        ItemTabPane.init(tabPane);
        Keyboard.init();
        Updater.init();
        Tutorial.init();
        Welcome.getWelcome().getSite().select();
    }

    public static TabPane getTabPane() {
        return controller.tabPane;
    }

}
