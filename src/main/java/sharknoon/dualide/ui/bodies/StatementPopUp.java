package sharknoon.dualide.ui.bodies;

import java.util.Optional;
import sharknoon.dualide.logic.statements.values.ValueType;
import java.util.Set;
import java.util.function.Consumer;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import org.controlsfx.control.BreadCrumbBar;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.SegmentedButton;
import sharknoon.dualide.logic.statements.values.creations.CreationType;
import sharknoon.dualide.logic.items.Function;
import sharknoon.dualide.logic.items.Item;
import sharknoon.dualide.logic.statements.values.Value;
import sharknoon.dualide.ui.MainController;
import sharknoon.dualide.logic.items.Package;
import sharknoon.dualide.logic.items.Project;
import sharknoon.dualide.logic.items.Variable;
import sharknoon.dualide.logic.statements.Statement;
import sharknoon.dualide.ui.misc.Icons;
import sharknoon.dualide.utils.language.Language;
import sharknoon.dualide.utils.language.Word;

/**
 *
 * @author Josua Frank
 */
public class StatementPopUp extends PopOver {

    public static void showValueSelectionPopUp(Node ownerNode, Set<ValueType> allowedValues, Statement parent, Consumer<Statement> statementConsumer) {
        StatementPopUp popUp = new StatementPopUp(ownerNode, allowedValues, parent, statementConsumer);
    }

    private final VBox vBoxRoot = new VBox();
    private final Set<ValueType> allowedValues;
    private final Statement parent;
    private final Consumer<Statement> statementConsumer;

    private StatementPopUp(Node ownerNode, Set<ValueType> allowedValues, Statement parent, Consumer<Statement> statementConsumer) {
        this.allowedValues = allowedValues;
        this.parent = parent;
        this.statementConsumer = statementConsumer;
        init();
        addNewValueSelectors();
        addExistingValueSelectors();
        show(ownerNode);
    }

    private void init() {
        vBoxRoot.setSpacing(20);
        vBoxRoot.setPadding(new Insets(25));
        vBoxRoot.setMinWidth(800);
        getRoot().getStylesheets().add("sharknoon/dualide/ui/MainCSS.css");
        setContentNode(vBoxRoot);
        setArrowLocation(PopOver.ArrowLocation.BOTTOM_CENTER);
        setTitle(Language.get(Word.VALUE_SELECTION_POPUP_TITLE));
    }

    private void addNewValueSelectors() {
        addNewValueSparator();
        allowedValues.forEach(v -> {
            addNewValueButtons(v);
        });
    }

    private void addNewValueSparator() {
        String text = Language.get(Word.VALUE_SELECTION_POPUP_NEW_VALUES);
        Node separator = getSeparator(text);
        vBoxRoot.getChildren().add(separator);
    }

    private void addNewValueButtons(ValueType value) {
        FlowPane flowPaneValueButtons = new FlowPane(20, 20);
        String stringValues = Language.get(Word.VALUE_SELECTION_POPUP_VALUES_EXTENSION);
        Node icon = Icons.get(value.getIcon());
        Label text = createLabel(value.getName() + stringValues);
        flowPaneValueButtons.getChildren().addAll(icon, text);

        CreationType ct = value.getCreationType();
        Button buttonCreation = new Button(ct.getName(), Icons.get(ct.getIcon()));
        buttonCreation.setOnAction((event) -> {
            Optional<Value> createdValue = ct.create().create(parent);
            createdValue.ifPresent(cv -> statementConsumer.accept(cv));
            hide();
        });
        flowPaneValueButtons.getChildren().add(buttonCreation);

        value.getOperationTypes().forEach(ot -> {
            Button buttonOperation = new Button(ot.getName(), Icons.get(ot.getIcon()));
            buttonOperation.setOnAction((event) -> {
                statementConsumer.accept(ot.create(parent));
                hide();
            });
            flowPaneValueButtons.getChildren().add(buttonOperation);
        });

        vBoxRoot.getChildren().add(flowPaneValueButtons);
    }

    private void addExistingValueSelectors() {
        addExistingValueSeparator();
        addContentListener();
        addValueSourceSegmentedButtons();
    }

    private void addExistingValueSeparator() {
        String text = Language.get(Word.VALUE_SELECTION_POPUP_EXISTING_VALUES);
        Node separator = getSeparator(text);
        vBoxRoot.getChildren().add(separator);
    }

    private final ObjectProperty<Node> content = new SimpleObjectProperty<>();

    private void addContentListener() {
        content.addListener((observable, oldValue, newValue) -> {
            if (oldValue != null) {
                vBoxRoot.getChildren().remove(oldValue);
            }
            vBoxRoot.getChildren().add(newValue);
        });
    }

    private void addValueSourceSegmentedButtons() {
        SegmentedButton segmentedButtonValueSource = new SegmentedButton();

        String textStatic = Language.get(Word.VALUE_SELECTION_POPUP_STATIC_VALUES);
        String textThisClass = Language.get(Word.VALUE_SELECTION_POPUP_CLASS_VALUES);
        String textThisFunction = Language.get(Word.VALUE_SELECTION_POPUP_FUNCTION_VALUES);

        ToggleButton toggleButtonStatic = new ToggleButton(textStatic);
        ToggleButton toggleButtonThisClass = new ToggleButton(textThisClass);
        ToggleButton toggleButtonThisFunction = new ToggleButton(textThisFunction);

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
        vBoxRoot.getChildren().add(segmentedButtonValueSource);
        toggleButtonStatic.fire();
    }

    private Node getStaticContent() {
        GridPane gridPaneStaticContent = new GridPane();
        gridPaneStaticContent.setAlignment(Pos.CENTER_LEFT);
        gridPaneStaticContent.setMaxWidth(Double.MAX_VALUE);
        gridPaneStaticContent.setVgap(20);
        gridPaneStaticContent.setHgap(20);

        BreadCrumbBar<Item> breadCrumbBarNavigation = new BreadCrumbBar<>();
        gridPaneStaticContent.add(breadCrumbBarNavigation, 0, 0, 2, 1);

        ScrollPane scrollPanePackages = new ScrollPane();
        scrollPanePackages.setFitToHeight(true);
        scrollPanePackages.setFitToWidth(true);
        VBox vBoxSubPackages = new VBox(10);
        breadCrumbBarNavigation.selectedCrumbProperty().addListener((observable, oldValue, newValue) -> {
            vBoxSubPackages.getChildren().clear();
            if (newValue != null) {
                newValue
                        .getChildren()
                        .stream()
                        .filter(ti -> ti.getValue() instanceof Package)
                        .forEach((ti) -> {
                            HBox hBoxPackage = new HBox(20);
                            hBoxPackage.setAlignment(Pos.CENTER);
                            Item item = ti.getValue();
                            Node icon = Icons.get(item.getSite().getTabIcon());
                            Label name = new Label(item.getName());
                            hBoxPackage.setOnMouseClicked((event) -> {
                                breadCrumbBarNavigation.setSelectedCrumb(ti);
                                breadCrumbBarNavigation.requestFocus();
                            });
                            hBoxPackage.getChildren().addAll(icon, name);
                            vBoxSubPackages.getChildren().add(hBoxPackage);
                        });
            }
        });
        scrollPanePackages.setContent(vBoxSubPackages);
        gridPaneStaticContent.add(scrollPanePackages, 0, 1);

        ScrollPane scrollPaneFunctionsAndVariables = new ScrollPane();
        scrollPaneFunctionsAndVariables.setFitToHeight(true);
        scrollPaneFunctionsAndVariables.setFitToWidth(true);
        VBox vBoxFunctionsAndVariables = new VBox(10);
        breadCrumbBarNavigation.selectedCrumbProperty().addListener((observable, oldValue, newValue) -> {
            vBoxFunctionsAndVariables.getChildren().clear();
            if (newValue != null) {
                newValue
                        .getChildren()
                        .stream()
                        .map(ti -> ti.getValue())
                        .filter(i -> i instanceof Function || i instanceof Variable)
                        .forEach((t) -> {
                            HBox hBoxFunctionOrVariable = new HBox(10);
                            hBoxFunctionOrVariable.setAlignment(Pos.CENTER);
                            Node icon = Icons.get(t.getSite().getTabIcon());
                            Label name = new Label(t.getName());
                            hBoxFunctionOrVariable.setOnMouseClicked((event) -> {
                                //TODO
                            });
                            hBoxFunctionOrVariable.getChildren().addAll(icon, name);
                            vBoxFunctionsAndVariables.getChildren().add(hBoxFunctionOrVariable);
                        });
            }
        });
        scrollPaneFunctionsAndVariables.setContent(vBoxFunctionsAndVariables);
        gridPaneStaticContent.add(scrollPaneFunctionsAndVariables, 1, 1);

        TreeView<Item> tree = MainController.getTreeView();
        TreeItem<Item> selectedItem = tree.getSelectionModel().getSelectedItem();
        while (!(selectedItem.getValue() instanceof Package) && !(selectedItem.getValue() instanceof Project)) {
            selectedItem = selectedItem.getParent();
        }
        breadCrumbBarNavigation.setSelectedCrumb(selectedItem);
        return gridPaneStaticContent;
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
