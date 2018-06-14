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

import sharknoon.dualide.ui.background.Background;
import sharknoon.dualide.ui.navigation.ItemTreeView;
import sharknoon.dualide.ui.navigation.ItemTabPane;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToolBar;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import sharknoon.dualide.logic.items.Function;
import sharknoon.dualide.logic.items.Item;
import sharknoon.dualide.logic.items.Welcome;
import sharknoon.dualide.ui.toolbar.ToolBarInit;
import sharknoon.dualide.ui.menubar.MenuBarInit;
import sharknoon.dualide.ui.sites.Site;
import sharknoon.dualide.ui.sites.function.FunctionSite;
import sharknoon.dualide.ui.sites.function.FunctionSiteLogic;
import sharknoon.dualide.utils.settings.Keyboard;

/**
 *
 * @author Josua Frank
 */
public class MainController implements Initializable {

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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Background.init(imageView1, imageView2);
        MenuBarInit.init(menubar);
        ToolBarInit.init(toolbar);
        ItemTreeView.init(treeView);
        ItemTabPane.init(tabPane);
        Keyboard.init();
        Welcome.getWelcome().getSite().select();
    }

    public static TabPane getTabPane() {
        return controller.tabPane;
    }

}
