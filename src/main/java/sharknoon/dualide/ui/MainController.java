package sharknoon.dualide.ui;

import com.jfoenix.controls.JFXTabPane;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToolBar;
import javafx.scene.image.ImageView;
import sharknoon.dualide.ui.flowchart.Flowchart;
import sharknoon.dualide.ui.flowchart.WorkspaceBackground;
import sharknoon.dualide.ui.toolbar.Radio;

/**
 *
 * @author Josua Frank
 */
public class MainController implements Initializable {

    @FXML
    private TabPane tabPane;
    
    @FXML
    private ImageView imageViewBackground;
    
    private Tab currentTab;
    private final Map<Tab, Flowchart> TABS = new HashMap<>();
    private static MainController controller;

    @FXML
    Button buttonAddFlowchart;

    @FXML
    ToolBar toolbarRadio;

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
            getCurrentFlowchart().ifPresent(f -> f.onScroll(event));
        });
        tabPane.setOnZoom((event) -> {
            getCurrentFlowchart().ifPresent(f -> f.onZoom(event));
        });
        tabPane.setOnMousePressed((event) -> {
            //System.out.println("mouse pressed");
            getCurrentFlowchart().ifPresent(f -> f.onMousePressed(event));
        });
        tabPane.setOnMouseDragged((event) -> {
            getCurrentFlowchart().ifPresent(f -> f.onMouseDragged(event));
        });
        tabPane.setOnMouseReleased((event) -> {
            getCurrentFlowchart().ifPresent(f -> f.onMouseReleased(event));
        });
        tabPane.setOnContextMenuRequested((event) -> {
            getCurrentFlowchart().ifPresent(f -> f.onContextMenuRequested(event));
        });
        tabPane.setOnKeyReleased((event) -> {
            getCurrentFlowchart().ifPresent(f -> f.onKeyReleased(event));
        });
        buttonAddFlowchart.setOnAction((event) -> {
            createNewFlowchart("Hello World");
        });
        WorkspaceBackground.setBackground(imageViewBackground);
        Radio.init(toolbarRadio);
    }
    
    public static Optional<Flowchart> getCurrentFlowchart() {
        return Optional.ofNullable(controller.TABS.get(controller.currentTab));
    }

    public static void createNewFlowchart(String title) {
        Tab tab = new Tab(title);
        controller.TABS.put(tab, new Flowchart(tab));
        controller.tabPane.getTabs().add(tab);
    }

}
