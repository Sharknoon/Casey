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
import sharknoon.dualide.logic.Function;
import sharknoon.dualide.logic.Item;
import sharknoon.dualide.ui.buttonbar.ToolBarInit;
import sharknoon.dualide.ui.menubar.MenuBarInit;
import sharknoon.dualide.ui.sites.function.FunctionSite;

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
    private static Optional<FunctionSite> currentFunction = Optional.empty();

    public MainController() {
        controller = this;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //init of handlers
        ItemTabPane.selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue instanceof Function) {
                currentFunction = Optional.ofNullable((FunctionSite) newValue.getSite());
            } else {
                currentFunction = Optional.empty();
            }
        });
        tabPane.setOnScroll((event) -> {
            currentFunction.ifPresent(f -> f.onScroll(event));
        });
        tabPane.setOnZoom((event) -> {
            currentFunction.ifPresent(f -> f.onZoom(event));
        });
        tabPane.setOnMousePressed((event) -> {
            //System.out.println("mouse pressed");
            currentFunction.ifPresent(f -> f.onMousePressed(event));
        });
        tabPane.setOnMouseDragged((event) -> {
            currentFunction.ifPresent(f -> f.onMouseDragged(event));
        });
        tabPane.setOnMouseReleased((event) -> {
            currentFunction.ifPresent(f -> f.onMouseReleased(event));
        });
        tabPane.setOnMouseMoved((event) -> {
            currentFunction.ifPresent(f -> f.onMouseMoved(event));
        });
        tabPane.setOnMouseClicked((event) -> {
            currentFunction.ifPresent(f -> f.onMouseClicked(event));
        });
        tabPane.setOnContextMenuRequested((event) -> {
            currentFunction.ifPresent(f -> f.onContextMenuRequested(event));
        });
        tabPane.setOnKeyReleased((event) -> {
            currentFunction.ifPresent(f -> f.onKeyReleased(event));
        });
        Background.setBackground(imageView1, imageView2);
        MenuBarInit.init(menubar);
        ToolBarInit.init(toolbar);
        ItemTreeView.init();
        ItemTabPane.init();
    }

    public static TreeView<Item> getTreeView() {
        return controller.treeView;
    }

    public static TabPane getTabPane() {
        return controller.tabPane;
    }

}
