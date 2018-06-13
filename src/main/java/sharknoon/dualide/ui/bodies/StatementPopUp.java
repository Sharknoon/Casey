package sharknoon.dualide.ui.bodies;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import org.controlsfx.control.BreadCrumbBar;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.SegmentedButton;
import sharknoon.dualide.logic.Returnable;
import sharknoon.dualide.logic.items.Function;
import sharknoon.dualide.logic.items.Item;
import sharknoon.dualide.logic.values.Value;
import sharknoon.dualide.logic.items.Class;
import sharknoon.dualide.logic.items.Package;
import sharknoon.dualide.logic.items.Variable;
import sharknoon.dualide.logic.Statement;
import sharknoon.dualide.logic.items.Class.ObjectType;
import sharknoon.dualide.logic.operators.OperatorType;
import sharknoon.dualide.logic.types.PrimitiveType;
import sharknoon.dualide.logic.types.Type;
import sharknoon.dualide.logic.values.ObjectValue;
import sharknoon.dualide.ui.misc.Icons;
import sharknoon.dualide.ui.sites.Site;
import sharknoon.dualide.utils.javafx.FXUtils;
import sharknoon.dualide.utils.language.Language;
import sharknoon.dualide.utils.language.Word;

/**
 *
 * @author Josua Frank
 */
public class StatementPopUp extends PopOver {

    public static void showValueSelectionPopUp(Node ownerNode, Set<Type> allowedValues, Statement parent, Consumer<Statement> statementConsumer) {
        StatementPopUp popUp = new StatementPopUp(ownerNode, allowedValues, parent, statementConsumer);
    }

    private final GridPane gridPaneRoot = new GridPane();
    private final Set<Type> allowedValues;
    private final Statement parent;
    private final Consumer<Statement> statementConsumer;

    private StatementPopUp(Node ownerNode, Set<Type> allowedValues, Statement parent, Consumer<Statement> statementConsumer) {
        if (allowedValues == null || allowedValues.isEmpty()) {
            this.allowedValues = new LinkedHashSet<>(PrimitiveType.getAll());
            this.allowedValues.addAll(ObjectType.getAll());
            //this.allowedValues.add(Type.AllType);
        } else {
            this.allowedValues = allowedValues;
        }
        this.parent = parent;
        this.statementConsumer = statementConsumer;
        init();
        addNewValueSelectors();
        addExistingValueSelectors();
        show(ownerNode);
    }

    private void init() {
        gridPaneRoot.setVgap(10);
        gridPaneRoot.setHgap(10);
        gridPaneRoot.setPadding(new Insets(25));
        gridPaneRoot.setPrefWidth(1300);
        gridPaneRoot.setMaxSize(1500, 500);
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(60);
        col1.setHgrow(Priority.ALWAYS);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(40);
        gridPaneRoot.getColumnConstraints().addAll(col1, col2);
        getRoot().getStylesheets().add("sharknoon/dualide/ui/MainCSS.css");
        setContentNode(gridPaneRoot);
        setArrowLocation(ArrowLocation.BOTTOM_CENTER);
        setTitle(Language.get(Word.VALUE_SELECTION_POPUP_TITLE));
    }

    private void addNewValueSelectors() {
        addNewValueSparator();
        addNewPrimitiveValueSparator();
        addNewPrimitiveValueSelectors();
        addNewObjectValueSparator();
        addNewObjectValueSelectors();
    }

    private void addNewValueSparator() {
        String text = Language.get(Word.VALUE_SELECTION_POPUP_NEW_VALUES);
        Node separator = PopUpUtils.getSeparator(text, HPos.CENTER);
        gridPaneRoot.add(separator, 0, 0);
    }

    private void addNewPrimitiveValueSparator() {
        String text = Language.get(Word.VALUE_SELECTION_POPUP_NEW_PRIMITIVE_VALUES);
        Node separator = PopUpUtils.getSeparator(text, HPos.LEFT, 12);
        gridPaneRoot.add(separator, 0, 1);
    }

    private void addNewPrimitiveValueSelectors() {
        GridPane gp = new GridPane();
        gp.setHgap(10);
        gp.setVgap(10);
        gp.setAlignment(Pos.TOP_LEFT);
        int row = 0;
        for (Type type : allowedValues) {
            if (type.isPrimitive()) {
                String stringValues = Language.get(Word.VALUE_SELECTION_POPUP_VALUES_EXTENSION);

                Node icon = Icons.get(type.getIcon());
                Label text = PopUpUtils.createLabel(type.getName().get() + stringValues);
                gp.add(new HBox(10, icon, text), 0, row);

                FlowPane flowPaneButtons = new FlowPane();
                flowPaneButtons.setHgap(10);
                flowPaneButtons.setVgap(10);
                Button buttonCreation = new Button(type.getCreationText().get(), Icons.get(type.getCreationIcon()));
                flowPaneButtons.getChildren().add(buttonCreation);

                buttonCreation.setOnAction((event) -> {
                    Optional<Value> createdValue = type.createValue(parent);
                    createdValue.ifPresent(cv -> {
                        if (statementConsumer != null) {
                            statementConsumer.accept(cv);
                        }
                    });
                    hide();
                });
                Set<OperatorType> ots = OperatorType.forType(type);
                ots.forEach(ot -> {
                    Button buttonOperation = new Button(ot.getName(), Icons.get(ot.getIcon()));
                    buttonOperation.setOnAction((event) -> {
                        if (statementConsumer != null) {
                            statementConsumer.accept(ot.create(parent));
                        }
                        hide();
                    });
                    flowPaneButtons.getChildren().add(buttonOperation);
                });

                gp.add(flowPaneButtons, 1, row);

                if ((row / 2) + 1 < allowedValues.size()) {
                    Separator separator = new Separator();
                    gp.add(separator, 1, row + 1);
                }
                row += 2;
            }
        }
        ColumnConstraints col0 = new ColumnConstraints();
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHgrow(Priority.ALWAYS);
        gp.getColumnConstraints().addAll(col0, col1);
        gridPaneRoot.add(gp, 0, 2, 1, 2);
    }

    private void addNewObjectValueSparator() {
        String text = Language.get(Word.VALUE_SELECTION_POPUP_NEW_OBJECT_VALUES);
        Node separator = PopUpUtils.getSeparator(text, HPos.LEFT, 12);
        gridPaneRoot.add(separator, 0, 4);
    }

    private void addNewObjectValueSelectors() {
        gridPaneRoot.add(new Label("TODO"), 0, 5);
    }

    private void addExistingValueSelectors() {
        addExistingValueSeparator();
        addValueSourceSegmentedButtons();
    }

    private void addExistingValueSeparator() {
        String text = Language.get(Word.VALUE_SELECTION_POPUP_EXISTING_VALUES);
        Node separator = PopUpUtils.getSeparator(text, HPos.CENTER);
        gridPaneRoot.add(separator, 1, 0);
    }

    private void addValueSourceSegmentedButtons() {
        SegmentedButton segmentedButtonValueSource = new SegmentedButton();

        String textStatic = Language.get(Word.VALUE_SELECTION_POPUP_STATIC_VALUES);
        String textThisClass = Language.get(Word.VALUE_SELECTION_POPUP_CLASS_VALUES);
        String textThisFunction = Language.get(Word.VALUE_SELECTION_POPUP_FUNCTION_VALUES);

        ToggleButton toggleButtonStatic = new ToggleButton(textStatic);
        ToggleButton toggleButtonThisClass = new ToggleButton(textThisClass);
        ToggleButton toggleButtonThisFunction = new ToggleButton(textThisFunction);

        toggleButtonStatic.prefWidthProperty().bind(gridPaneRoot.widthProperty().multiply(0.4).divide(3));
        toggleButtonThisClass.prefWidthProperty().bind(gridPaneRoot.widthProperty().multiply(0.4).divide(3));
        toggleButtonThisFunction.prefWidthProperty().bind(gridPaneRoot.widthProperty().multiply(0.4).divide(3));

        toggleButtonStatic.setOnAction((event) -> {
            setStaticContent();
        });
        toggleButtonThisClass.setOnAction((event) -> {
            setThisClassContent();
        });
        toggleButtonThisFunction.setOnAction((event) -> {
            setThisFunctionContent();
        });

        segmentedButtonValueSource.getButtons().addAll(toggleButtonStatic, toggleButtonThisClass, toggleButtonThisFunction);
        gridPaneRoot.add(segmentedButtonValueSource, 1, 1);
        GridPane.setHgrow(segmentedButtonValueSource, Priority.ALWAYS);
        //GridPane.setFillWidth(segmentedButtonValueSource, true);
        toggleButtonStatic.fire();
    }

    private Node previousContent = null;

    private void setStaticContent() {
        GridPane gridPanePackagesAndVariables = new GridPane();
        gridPanePackagesAndVariables.setHgap(10);
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        gridPanePackagesAndVariables.getColumnConstraints().addAll(col1, col2);

        BreadCrumbBar<Item> breadCrumbBarNavigation = new BreadCrumbBar<>();
        breadCrumbBarNavigation.setCache(false);
        ScrollPane scrollPaneBreadCrumbBar = new ScrollPane(breadCrumbBarNavigation);
        scrollPaneBreadCrumbBar.setMinHeight(50);
        gridPanePackagesAndVariables.add(scrollPaneBreadCrumbBar, 0, 0, 2, 1);

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
                            HBox hBoxPackage = new HBox(10);
                            Item item = ti.getValue();
                            Node icon = Icons.get(item.getSite().getTabIcon());
                            Label name = new Label(item.getName());
                            hBoxPackage.setOnMouseClicked((event) -> {
                                breadCrumbBarNavigation.setSelectedCrumb(ti);
                                breadCrumbBarNavigation.requestFocus();
                            });
                            hBoxPackage.getChildren().addAll(icon, name);
                            hBoxPackage.setAlignment(Pos.CENTER_LEFT);
                            vBoxSubPackages.getChildren().add(hBoxPackage);
                        });
            }
        });
        scrollPanePackages.setContent(vBoxSubPackages);
        gridPanePackagesAndVariables.add(scrollPanePackages, 0, 1);

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
                        .filter(i -> i instanceof Returnable)
                        .forEach((t) -> {
                            HBox hBoxFunctionOrVariable = new HBox(10);
                            Node icon = Icons.get(t.getSite().getTabIcon());
                            Label name = new Label(t.getName());
                            hBoxFunctionOrVariable.setOnMouseClicked((event) -> {
                                //TODO
                            });
                            hBoxFunctionOrVariable.getChildren().addAll(icon, name);
                            hBoxFunctionOrVariable.setAlignment(Pos.CENTER_LEFT);
                            vBoxFunctionsAndVariables.getChildren().add(hBoxFunctionOrVariable);
                        });
            }
        });
        scrollPaneFunctionsAndVariables.setContent(vBoxFunctionsAndVariables);
        gridPanePackagesAndVariables.add(scrollPaneFunctionsAndVariables, 1, 1);
        gridPaneRoot.getChildren().remove(previousContent);
        previousContent = gridPanePackagesAndVariables;
        gridPaneRoot.add(gridPanePackagesAndVariables, 1, 2);

        TreeItem selectedItem = Site.currentSelectedProperty().get().getSite().getTreeItem();
        if (selectedItem != null) {
            while ((selectedItem.getValue() instanceof Function)
                    || (selectedItem.getValue() instanceof Variable)
                    || (selectedItem.getValue() instanceof Class)) {
                selectedItem = selectedItem.getParent();
            }
            breadCrumbBarNavigation.setSelectedCrumb(selectedItem);
        }
    }

    private void setThisClassContent() {
        Label label = new Label("This Class TODO");
        gridPaneRoot.getChildren().remove(previousContent);
        previousContent = label;
        gridPaneRoot.add(label, 1, 2);
    }

    private void setThisFunctionContent() {
        Label label = new Label("This Function TODO");
        gridPaneRoot.getChildren().remove(previousContent);
        previousContent = label;
        gridPaneRoot.add(label, 1, 2);
    }

}
