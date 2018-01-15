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

import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import sharknoon.dualide.logic.Item;
import sharknoon.dualide.ui.sites.function.FunctionSite;
import sharknoon.dualide.ui.sites.function.WorkspaceBackground;
import sharknoon.dualide.ui.menubar.MenuBar;
import sharknoon.dualide.ui.sites.Site;
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
    private TreeView<Item> treeView;

    @FXML
    private Menu menuOptions;

    @FXML
    private ImageView imageView1;

    @FXML
    private ImageView imageView2;

    @FXML
    private Button buttonAddFunction;
    
    @FXML
    private Button buttonSave;

    private static MainController controller;

    public MainController() {
        controller = this;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
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
        GlyphsDude.setIcon(buttonSave, FontAwesomeIcon.SAVE);
        Language.set(Word.SAVE, buttonSave);
        buttonSave.setOnAction((event) -> {
            
        });
        WorkspaceBackground.setBackground(imageView1, imageView2);
        MenuBar.initOptionsMenu(menuOptions);
        ItemTreeView.init();
        ItemTabPane.init();
    }

    public static Optional<FunctionSite> getCurrentFunction() {
        return Optional.empty();
    }


    public static TreeView<Item> getTreeView() {
        return controller.treeView;
    }
    
    public static TabPane getTabPane(){
        return controller.tabPane;
    }

}
