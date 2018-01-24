package sharknoon.dualide.ui.values;

import java.util.Set;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.TreeView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.controlsfx.control.BreadCrumbBar;
import org.controlsfx.control.SegmentedButton;
import sharknoon.dualide.logic.Item;
import sharknoon.dualide.ui.ItemTreeView;
import sharknoon.dualide.ui.MainController;

/**
 *
 * @author Josua Frank
 */
public class ValueSelection extends VBox {

    public static Pane getValueSelectionPane(Set<Value> allowedValues) {
        return new ValueSelection(allowedValues);
    }

    private Set<Value> allowedValues;

    private ValueSelection(Set<Value> allowedValues) {
        init();
        this.allowedValues = allowedValues;
        addNewValueSelectors();
        addExistingValueSelectors();
    }

    private void init() {
        setSpacing(20);
        setPadding(new Insets(25));
    }

    private void addNewValueSelectors() {
        addNewValueSparator();
        allowedValues.forEach(v -> {
            addValueButton(v);
        });
    }

    private void addNewValueSparator() {
        Node separator = getSeparator("New ValuesTODO");
        getChildren().add(separator);
    }

    private void addValueButton(Value value) {
        FlowPane flowPaneValueButtons = new FlowPane(20, 20);
        Label text = createLabel(value.name() + "values TODO");
        flowPaneValueButtons.getChildren().add(text);
        for (int i = 0; i < 5; i++) {//Demo
            flowPaneValueButtons.getChildren().add(new Button("Add TODO"));
        }
        getChildren().add(flowPaneValueButtons);
    }

    private void addExistingValueSelectors() {
        addExistingValueSeparator();
        addValueSourceSegmentedButtons();
        addContent();
    }

    private void addExistingValueSeparator() {
        Node separator = getSeparator("Existing ValuesTODO");
        getChildren().add(separator);
    }

    private void addValueSourceSegmentedButtons() {
        SegmentedButton segmentedButtonValueSource = new SegmentedButton();

        ToggleButton toggleButtonStatic = new ToggleButton("StaticTODO");
        ToggleButton toggleButtonThisClass = new ToggleButton("This ClassTODO");
        ToggleButton toggleButtonThisFunction = new ToggleButton("This functionTODO");

        toggleButtonStatic.setOnAction((event) -> {
            content.set(getStaticContent());
        });
        toggleButtonThisClass.setOnAction((event) -> {
            content.set(getThisClassContent());
        });
        toggleButtonThisFunction.setOnAction((event) -> {
            content.set(getThisFunctionContent());
        });

        segmentedButtonValueSource.getButtons().addAll(toggleButtonStatic, toggleButtonThisClass, toggleButtonThisFunction);
        getChildren().add(segmentedButtonValueSource);
    }

    private Node getStaticContent() {
        BreadCrumbBar<Item> breadCrumbBarNavigation = new BreadCrumbBar<>();
        TreeView<Item> tree = MainController.getTreeView();
        breadCrumbBarNavigation.setSelectedCrumb(tree.getSelectionModel().getSelectedItem());
        tree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            breadCrumbBarNavigation.setSelectedCrumb(newValue);
        });
        breadCrumbBarNavigation.selectedCrumbProperty().addListener((observable, oldValue, newValue) -> {
            tree.getSelectionModel().select(newValue);
        });
        return breadCrumbBarNavigation;
    }

    private Node getThisClassContent() {
        return new Label("This Class TODO");
    }

    private Node getThisFunctionContent() {
        return new Label("This Function TODO");
    }

    private final ObjectProperty<Node> content = new SimpleObjectProperty<>();

    private void addContent() {
        content.addListener((observable, oldValue, newValue) -> {
            if (oldValue != null) {
                getChildren().remove(oldValue);
            }
            getChildren().add(newValue);
        });
    }

    private Label createLabel(String stringText) {
        Label label = new Label(stringText);
        label.setFont(Font.font(20));
        return label;
    }

    private Node getSeparator(String name) {
        HBox hBowSeparator = new HBox();
        hBowSeparator.setAlignment(Pos.CENTER);
        hBowSeparator.setSpacing(15);

        Separator separatorLeft = new Separator();
        separatorLeft.setMaxWidth(100);

        Label labelText = new Label(name);

        Separator separatorRight = new Separator();

        hBowSeparator.getChildren().addAll(separatorLeft, labelText, separatorRight);
        HBox.setHgrow(separatorRight, Priority.ALWAYS);
        return hBowSeparator;
    }

}
