package sharknoon.dualide.ui;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import sharknoon.dualide.radio.stations.DUFM;
import sharknoon.dualide.ui.flowchart.Flowchart;
import sharknoon.dualide.utils.settings.FileUtils;

/**
 *
 * @author Josua Frank
 */
public class MainController implements Initializable {

    @FXML
    private TabPane tabpane;
    private Tab currentTab;
    private final Map<Tab, Flowchart> TABS = new HashMap<>();
    private static MainController controller;

    @FXML
    Menu menuIDE;

    @FXML
    Menu menuDUFM;

    @FXML
    Button buttonAddFlowchart;

    public MainController() {
        controller = this;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //init of currentTab
        currentTab = tabpane.getSelectionModel().getSelectedItem();
        tabpane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            currentTab = newValue;
        });
        //init of handlers
        tabpane.setOnScroll((event) -> {
            getCurrentFlowchart().ifPresent(f -> f.onScroll(event));
        });
        tabpane.setOnZoom((event) -> {
            getCurrentFlowchart().ifPresent(f -> f.onZoom(event));
        });
        tabpane.setOnMousePressed((event) -> {
            getCurrentFlowchart().ifPresent(f -> f.onMousePressed(event));
        });
        tabpane.setOnMouseDragged((event) -> {
            getCurrentFlowchart().ifPresent(f -> f.onMouseDragged(event));
        });
        tabpane.setOnMouseReleased((event) -> {
            getCurrentFlowchart().ifPresent(f -> f.onMouseReleased(event));
        });
        tabpane.setOnContextMenuRequested((event) -> {
            getCurrentFlowchart().ifPresent(f -> f.onContextMenuRequested(event));
        });
        tabpane.setOnKeyReleased((event) -> {
            getCurrentFlowchart().ifPresent(f -> f.onKeyReleased(event));
        });
        buttonAddFlowchart.setOnAction((event) -> {
            createNewFlowchart("Hello World");
        });
        //setBackgroundImage();
        menuDUFM.getItems().get(0).setOnAction((event) -> {
            DUFM.start();
        });
        menuDUFM.getItems().get(1).setOnAction((event) -> {
            DUFM.stop();
        });
    }

    public static Optional<Flowchart> getCurrentFlowchart() {
        return Optional.ofNullable(controller.TABS.get(controller.currentTab));
    }

    public static void createNewFlowchart(String title) {
        Tab tab = new Tab(title);
        controller.TABS.put(tab, new Flowchart(tab));
        controller.tabpane.getTabs().add(tab);
    }

    private static void setBackgroundImage() {
        InputStream stream = FileUtils.getFileAsStream("images/landscape.jpg", true).orElse(null);
        Image image = new Image(stream);
        controller.tabpane.setBackground(new Background(new BackgroundImage(image, BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));
    }

}
