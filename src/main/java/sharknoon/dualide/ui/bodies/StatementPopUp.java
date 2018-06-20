package sharknoon.dualide.ui.bodies;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import org.controlsfx.control.BreadCrumbBar;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.SegmentedButton;
import sharknoon.dualide.logic.Returnable;
import sharknoon.dualide.logic.items.Class;
import sharknoon.dualide.logic.items.Class.ObjectType;
import sharknoon.dualide.logic.items.*;
import sharknoon.dualide.logic.items.Package;
import sharknoon.dualide.logic.statements.Statement;
import sharknoon.dualide.logic.statements.operators.OperatorType;
import sharknoon.dualide.logic.statements.values.ObjectValue;
import sharknoon.dualide.logic.statements.values.Value;
import sharknoon.dualide.logic.types.PrimitiveType;
import sharknoon.dualide.logic.types.Type;
import sharknoon.dualide.ui.misc.Icons;
import sharknoon.dualide.ui.sites.Site;
import sharknoon.dualide.utils.language.Language;
import sharknoon.dualide.utils.language.Word;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author Josua Frank
 */
class StatementPopUp extends PopOver {

    private final GridPane gridPaneRoot = new GridPane();
    private final VBox vBoxLeft = new VBox();
    private final VBox vBoxRight = new VBox();
    private final Collection<? extends Type> allowedTypes;
    private final Statement parent;
    private final Consumer<Statement> statementConsumer;
    private GridPane gp = new GridPane();
    private int row = 0;
    private int size = -1;
    private Node previousContent = null;

    private StatementPopUp(Node ownerNode, Collection<? extends Type> allowedValues, Statement parent, Consumer<Statement> statementConsumer) {
        super();
        this.parent = parent;
        this.statementConsumer = statementConsumer;
        this.allowedTypes = allowedValues;
        init();
        //null means no filter, empty means blocked all types
        if (allowedValues != null && allowedValues.isEmpty()) {
            addNoTypeLabel();
        } else {
            addNewValueSelectors();
            addExistingValueSelectors();
        }
        show(ownerNode);
    }

    static void showValueSelectionPopUp(Node ownerNode, Collection<? extends Type> allowedValues, Statement parent, Consumer<Statement> statementConsumer) {
        StatementPopUp popUp = new StatementPopUp(ownerNode, allowedValues, parent, statementConsumer);
    }

    private void init() {
        gridPaneRoot.setHgap(10);
        vBoxLeft.setSpacing(10);
        vBoxRight.setSpacing(10);
        gridPaneRoot.setPadding(new Insets(25));
        gridPaneRoot.setPrefWidth(1300);
        gridPaneRoot.setMaxSize(1500, 500);
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(60);
        col1.setHgrow(Priority.ALWAYS);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(40);
        gridPaneRoot.getColumnConstraints().addAll(col1, col2);
        gridPaneRoot.add(vBoxLeft, 0, 0);
        gridPaneRoot.add(vBoxRight, 1, 0);
        getRoot().getStylesheets().add("sharknoon/dualide/ui/MainCSS.css");
        setContentNode(gridPaneRoot);
        setArrowLocation(ArrowLocation.BOTTOM_CENTER);
        setTitle(Language.get(Word.VALUE_SELECTION_POPUP_TITLE));
    }

    private void addNoTypeLabel() {
        var text = Language.get(Word.TYPE_SELECTION_POPUP_NO_TYPES);
        var labelNoType = new Label(text);
        labelNoType.setFont(Font.font(20));
        labelNoType.setMaxWidth(Double.MAX_VALUE);
        labelNoType.setAlignment(Pos.CENTER);
        gridPaneRoot.add(labelNoType, 0, 0, 2, 1);
    }

    private void addNewValueSelectors() {
        addNewValueSparator();
        //primitives
        for (Type type : allowedTypes == null ? PrimitiveType.getAll() : allowedTypes) {
            if (type != null && type.isPrimitive()) {
                addNewPrimitiveValueSelector(type.getPrimitiveType());
            }
        }
        //objects
        if (allowedTypes == null) {
            addNewObjectValueSelectors();
        } else {
            for (Type type : allowedTypes) {
                if (type != null && !type.isPrimitive()) {
                    addNewObjectValueSelectors();
                    break;
                }
            }
        }
    }

    private void addNewValueSparator() {
        String text = Language.get(Word.VALUE_SELECTION_POPUP_NEW_VALUES);
        Node separator = PopUpUtils.getSeparator(text, HPos.CENTER);
        vBoxLeft.getChildren().add(separator);
    }

    private void addNewPrimitiveValueSelector(PrimitiveType type) {
        if (!vBoxLeft.getChildren().contains(gp)) {
            gp.setHgap(10);
            gp.setVgap(10);
            gp.setAlignment(Pos.TOP_LEFT);
            ColumnConstraints col0 = new ColumnConstraints();
            ColumnConstraints col1 = new ColumnConstraints();
            col1.setHgrow(Priority.ALWAYS);
            gp.getColumnConstraints().addAll(col0, col1);
            vBoxLeft.getChildren().add(gp);
        }
        if (size < 0) {
            size = allowedTypes == null ? PrimitiveType.getAll().size() : allowedTypes.size();
        }

        String stringValues = Language.get(Word.VALUE_SELECTION_POPUP_VALUES_EXTENSION);

        Node icon = Icons.get(type.getIcon());
        Label text = PopUpUtils.createLabel(type.getLanguageDependentName().get() + stringValues);
        gp.add(new HBox(10, icon, text), 0, row);

        FlowPane flowPaneButtons = new FlowPane();
        flowPaneButtons.setHgap(10);
        flowPaneButtons.setVgap(10);
        Button buttonCreation = new Button(type.creationTextProperty().get(), Icons.get(type.getCreationIcon()));
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

        if ((row / 2) + 1 < size) {
            Separator separator = new Separator();
            gp.add(separator, 1, row + 1);
        }
        row += 2;
    }

    private void addNewObjectValueSelectors() {
        if (row > 0) {
            Separator separator = new Separator();
            gp.add(separator, 1, row - 1);
        }

        String stringValues = Language.get(Word.VALUE_SELECTION_POPUP_VALUES_EXTENSION);
        Node icon = Icons.get(ObjectType.GENERAL.getIcon());
        Label text = PopUpUtils.createLabel(ObjectType.GENERAL.getLanguageDependentName().get() + stringValues);
        gp.add(new HBox(10, icon, text), 0, row);

        Button buttonCreation = new Button(ObjectType.GENERAL.creationTextProperty().get(), Icons.get(ObjectType.GENERAL.getCreationIcon()));
        buttonCreation.setOnAction((event) -> {
            Optional<ObjectValue> t = ObjectType.GENERAL.createValue(parent);
            if (statementConsumer != null && t.isPresent()) {
                statementConsumer.accept(t.get());
            }
            hide();
        });
        gp.add(buttonCreation, 1, row);
    }

    private void addExistingValueSelectors() {
        addExistingValueSeparator();
        addValueSourceSegmentedButtons();
    }

    private void addExistingValueSeparator() {
        String text = Language.get(Word.VALUE_SELECTION_POPUP_EXISTING_VALUES);
        Node separator = PopUpUtils.getSeparator(text, HPos.CENTER);
        vBoxRight.getChildren().add(separator);
    }

    private void addValueSourceSegmentedButtons() {
        SegmentedButton segmentedButtonValueSource = new SegmentedButton();

        String textStatic = Language.get(Word.VALUE_SELECTION_POPUP_STATIC_VALUES);
        String textThisClass = Language.get(Word.VALUE_SELECTION_POPUP_CLASS_VALUES);
        String textThisFunction = Language.get(Word.VALUE_SELECTION_POPUP_FUNCTION_VALUES);

        ToggleButton toggleButtonStatic = new ToggleButton(textStatic);
        ToggleButton toggleButtonThisClass = new ToggleButton(textThisClass);
        ToggleButton toggleButtonThisFunction = new ToggleButton(textThisFunction);

        toggleButtonStatic.prefWidthProperty().bind(vBoxRight.widthProperty().divide(3));
        toggleButtonThisClass.prefWidthProperty().bind(vBoxRight.widthProperty().divide(3));
        toggleButtonThisFunction.prefWidthProperty().bind(vBoxRight.widthProperty().divide(3));

        toggleButtonStatic.setOnAction((event) -> setStaticContent());
        toggleButtonThisClass.setOnAction((event) -> setThisClassContent());
        toggleButtonThisFunction.setOnAction((event) -> setThisFunctionContent());

        segmentedButtonValueSource.getButtons().addAll(toggleButtonStatic, toggleButtonThisClass, toggleButtonThisFunction);
        vBoxRight.getChildren().add(segmentedButtonValueSource);
        GridPane.setHgrow(segmentedButtonValueSource, Priority.ALWAYS);
        //GridPane.setFillWidth(segmentedButtonValueSource, true);
        toggleButtonStatic.fire();
    }

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
                        .map(TreeItem::getValue)
                        .filter(i -> i instanceof Returnable || i instanceof Class)
                        .filter(i -> (i instanceof Class || allowedTypes == null)
                                || allowedTypes.contains(((Returnable) i).getReturnType())
                        )
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
        vBoxRight.getChildren().remove(previousContent);
        previousContent = gridPanePackagesAndVariables;
        vBoxRight.getChildren().add(gridPanePackagesAndVariables);

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
        vBoxRight.getChildren().remove(previousContent);
        previousContent = label;
        vBoxRight.getChildren().add(label);
    }

    private void setThisFunctionContent() {
        Label label = new Label("This Function TODO");
        vBoxRight.getChildren().remove(previousContent);
        previousContent = label;
        vBoxRight.getChildren().add(label);
    }

}
