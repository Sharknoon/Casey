package sharknoon.dualide.ui.values;

import sharknoon.dualide.logic.values.ValueType;
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
import sharknoon.dualide.logic.items.Item;
import sharknoon.dualide.ui.ItemTreeView;
import sharknoon.dualide.ui.MainController;

/**
 *
 * @author Josua Frank
 */
public class ValueSelection extends VBox {

    public static Pane getValueSelectionPane(Set<ValueType> allowedValues) {
        return new ValueSelection(allowedValues);
    }

    private final Set<ValueType> allowedValues;

    private ValueSelection(Set<ValueType> allowedValues) {
        init();
        this.allowedValues = allowedValues;
        addNewValueSelectors();
        addExistingValueSelectors();
    }

    private void init() {
        setSpacing(20);
        setPadding(new Insets(25));
        setMinWidth(800);
    }

    private void addNewValueSelectors() {
        addNewValueSparator();
        allowedValues.forEach(v -> {
            addNewValueButtons(v);
        });
    }

    private void addNewValueSparator() {
        Node separator = getSeparator("New ValuesTODO");
        getChildren().add(separator);
    }

    private void addNewValueButtons(ValueType value) {
        FlowPane flowPaneValueButtons = new FlowPane(20, 20);
        Label text = createLabel(value.name() + "values TODO");
        flowPaneValueButtons.getChildren().add(text);

        value.getOperationTypes().forEach(ot -> {
            
        });

        getChildren().add(flowPaneValueButtons);
    }

    private void addExistingValueSelectors() {
        addExistingValueSeparator();
        addContentListener();
        addValueSourceSegmentedButtons();
    }

    private void addExistingValueSeparator() {
        Node separator = getSeparator("Existing ValuesTODO");
        getChildren().add(separator);
    }

    private final ObjectProperty<Node> content = new SimpleObjectProperty<>();

    private void addContentListener() {
        content.addListener((observable, oldValue, newValue) -> {
            if (oldValue != null) {
                getChildren().remove(oldValue);
            }
            getChildren().add(newValue);
        });
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
        toggleButtonStatic.fire();
    }

    private Node getStaticContent() {
        BreadCrumbBar<Item> breadCrumbBarNavigation = new BreadCrumbBar<>();
        TreeView<Item> tree = MainController.getTreeView();
        breadCrumbBarNavigation.setSelectedCrumb(tree.getSelectionModel().getSelectedItem());
        return breadCrumbBarNavigation;
    }

    private Node getThisClassContent() {
        return new Label("This Class TODO");
    }

    private Node getThisFunctionContent() {
        return new Label("This Function TODO");
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
        labelText.setFont(Font.font(20));

        Separator separatorRight = new Separator();

        hBowSeparator.getChildren().addAll(separatorLeft, labelText, separatorRight);
        HBox.setHgrow(separatorRight, Priority.ALWAYS);
        return hBowSeparator;
    }

}
