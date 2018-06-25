package sharknoon.dualide.ui.bodies;

import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import org.controlsfx.control.BreadCrumbBar;
import org.controlsfx.control.SegmentedButton;
import sharknoon.dualide.logic.ValueReturnable;
import sharknoon.dualide.logic.items.Class;
import sharknoon.dualide.logic.items.Class.ObjectType;
import sharknoon.dualide.logic.items.*;
import sharknoon.dualide.logic.items.Package;
import sharknoon.dualide.logic.statements.Statement;
import sharknoon.dualide.logic.statements.calls.FunctionCall;
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
class ValueBrowser extends GridPane {
    
    private static HBox getEntries(Item<?, ?, ?> item, EventHandler<MouseEvent> onClick) {
        HBox hBoxChildren = new HBox(10);
        Node icon = Icons.get(item.getSite().getTabIcon());
        Label name = new Label(item.getName());
        hBoxChildren.setOnMouseClicked(onClick);
        hBoxChildren.getChildren().addAll(icon, name);
        hBoxChildren.setAlignment(Pos.CENTER_LEFT);
        return hBoxChildren;
    }
    
    private final VBox vBoxLeft = new VBox();
    private final VBox vBoxRight = new VBox();
    private final Collection<? extends Type> allowedTypes;
    private final boolean allowValueCreation;
    private final Statement parent;
    private final Consumer<Statement> statementConsumer;
    private GridPane gp = new GridPane();
    private int row = 0;
    private int size = -1;
    private Node previousContent = null;
    
    
    public ValueBrowser(Consumer<Statement> statementConsumer, Statement parent, Collection<? extends Type> allowedTypes, boolean allowValueCreation) {
        super();
        this.parent = parent;
        this.statementConsumer = statementConsumer;
        this.allowedTypes = allowedTypes;
        this.allowValueCreation = allowValueCreation;
        init();
        //null means no filter, empty means blocked all types
        if (allowedTypes != null && allowedTypes.isEmpty()) {
            addNoTypeLabel();
        } else {
            if (allowValueCreation) {
                addNewValueSelectors();
            }
            addExistingValueSelectors();
        }
    }
    
    private void init() {
        setHgap(10);
        setPadding(new Insets(25));
        setPrefWidth(1300);
        setMaxSize(1500, 500);
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHgrow(Priority.ALWAYS);
        if (allowValueCreation) {
            col1.setPercentWidth(60);
            ColumnConstraints col2 = new ColumnConstraints();
            col2.setPercentWidth(40);
            getColumnConstraints().addAll(col1, col2);
            add(vBoxLeft, 0, 0);
            vBoxLeft.setSpacing(10);
        } else {
            getColumnConstraints().add(col1);
        }
        add(vBoxRight, 1, 0);
        
        vBoxRight.setSpacing(10);
    }
    
    private void addNoTypeLabel() {
        var text = Language.get(Word.TYPE_SELECTION_POPUP_NO_TYPES);
        var labelNoType = new Label(text);
        labelNoType.setFont(Font.font(20));
        labelNoType.setMaxWidth(Double.MAX_VALUE);
        labelNoType.setAlignment(Pos.CENTER);
        add(labelNoType, 0, 0, 2, 1);
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
            createdValue.ifPresent(statementConsumer::accept);
        });
        Set<OperatorType> ots = OperatorType.forType(type);
        ots.forEach(ot -> {
            Button buttonOperation = new Button(ot.getName(), Icons.get(ot.getIcon()));
            buttonOperation.setOnAction((event) -> statementConsumer.accept(ot.create(parent)));
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
            t.ifPresent(statementConsumer::accept);
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
        
        Item<?, ?, ?> i = Site.currentSelectedProperty().get();
        boolean inClass = i != null && (i.isIn(ItemType.CLASS) || i.getType() == ItemType.CLASS);
        boolean inFunction = i != null && (i.isIn(ItemType.FUNCTION) || i.getType() == ItemType.FUNCTION);
        int amountButtons = 1 + (inClass ? 1 : 0) + (inFunction ? 1 : 0);
        
        String textStatic = Language.get(Word.VALUE_SELECTION_POPUP_STATIC_VALUES);
        ToggleButton toggleButtonStatic = new ToggleButton(textStatic);
        toggleButtonStatic.prefWidthProperty().bind(vBoxRight.widthProperty().divide(amountButtons));
        toggleButtonStatic.setOnAction((event) -> setStaticContent());
        segmentedButtonValueSource.getButtons().add(toggleButtonStatic);
        
        if (inClass) {
            String textThisClass = Language.get(Word.VALUE_SELECTION_POPUP_CLASS_VALUES);
            ToggleButton toggleButtonThisClass = new ToggleButton(textThisClass);
            toggleButtonThisClass.prefWidthProperty().bind(vBoxRight.widthProperty().divide(amountButtons));
            toggleButtonThisClass.setOnAction((event) -> setThisClassContent());
            segmentedButtonValueSource.getButtons().add(toggleButtonThisClass);
        }
        
        if (inFunction) {
            String textThisFunction = Language.get(Word.VALUE_SELECTION_POPUP_FUNCTION_VALUES);
            ToggleButton toggleButtonThisFunction = new ToggleButton(textThisFunction);
            toggleButtonThisFunction.prefWidthProperty().bind(vBoxRight.widthProperty().divide(amountButtons));
            toggleButtonThisFunction.setOnAction((event) -> setThisFunctionContent());
            segmentedButtonValueSource.getButtons().add(toggleButtonThisFunction);
        }
        
        vBoxRight.getChildren().add(segmentedButtonValueSource);
        GridPane.setHgrow(segmentedButtonValueSource, Priority.ALWAYS);
        //GridPane.setFillWidth(segmentedButtonValueSource, true);
        toggleButtonStatic.fire();
    }
    
    private void setStaticContent() {
        GridPane gridPanePackagesFunctionsAndVariables = new GridPane();
        gridPanePackagesFunctionsAndVariables.setHgap(10);
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        gridPanePackagesFunctionsAndVariables.getColumnConstraints().addAll(col1, col2);
        
        BreadCrumbBar<Item> breadCrumbBarNavigation = new BreadCrumbBar<>();
        breadCrumbBarNavigation.setCache(false);
        ScrollPane scrollPaneBreadCrumbBar = new ScrollPane(breadCrumbBarNavigation);
        scrollPaneBreadCrumbBar.setMinHeight(50);
        gridPanePackagesFunctionsAndVariables.add(scrollPaneBreadCrumbBar, 0, 0, 2, 1);
        
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
                        .filter(i -> i.getValue() instanceof Package)
                        .forEach((ti) -> vBoxSubPackages.getChildren().add(getEntries(ti.getValue(), event -> {
                            breadCrumbBarNavigation.setSelectedCrumb(ti);
                            breadCrumbBarNavigation.requestFocus();
                        })));
            }
        });
        scrollPanePackages.setContent(vBoxSubPackages);
        gridPanePackagesFunctionsAndVariables.add(scrollPanePackages, 0, 1);
        
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
                        .filter(i -> i instanceof ValueReturnable)
                        .filter(i -> allowedTypes == null
                                || allowedTypes.contains(((ValueReturnable) i).getReturnType())
                        )
                        .forEach((i) -> vBoxFunctionsAndVariables.getChildren().add(getEntries(i, event -> {
                            if (i.getType() == ItemType.FUNCTION) {
                                statementConsumer.accept(new FunctionCall(parent, (Function) i));
                            } else if (i.getType() == ItemType.VARIABLE) {
                                //statementConsumer.accept(new VariableCall());
                            }
                            //TODO
                        })));
            }
        });
        scrollPaneFunctionsAndVariables.setContent(vBoxFunctionsAndVariables);
        gridPanePackagesFunctionsAndVariables.add(scrollPaneFunctionsAndVariables, 1, 1);
        vBoxRight.getChildren().remove(previousContent);
        previousContent = gridPanePackagesFunctionsAndVariables;
        vBoxRight.getChildren().add(gridPanePackagesFunctionsAndVariables);
        
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
        ScrollPane scrollPaneFunctionsAndVariables = new ScrollPane();
        scrollPaneFunctionsAndVariables.setFitToHeight(true);
        scrollPaneFunctionsAndVariables.setFitToWidth(true);
        VBox vBoxFunctionsAndVariables = new VBox(10);
        
        Item<?, ?, ?> currentItem = Site.currentSelectedProperty().get();
        Class currentClass = null;
        while (currentClass == null) {
            if (currentItem == null) {
                return;
            }
            if (currentItem.getType() == ItemType.CLASS) {
                currentClass = (Class) currentItem;
            } else {
                currentItem = currentItem.getParent().orElse(null);
            }
        }
        currentClass
                .getChildren()
                .stream()
                .filter(i -> i instanceof ValueReturnable)
                .filter(i -> allowedTypes == null
                        || allowedTypes.contains(((ValueReturnable) i).getReturnType())
                )
                .forEach((item) -> vBoxFunctionsAndVariables.getChildren().add(getEntries(item, event -> {
                    //TODO
                })));
        scrollPaneFunctionsAndVariables.setContent(vBoxFunctionsAndVariables);
        
        vBoxRight.getChildren().remove(previousContent);
        previousContent = scrollPaneFunctionsAndVariables;
        vBoxRight.getChildren().add(scrollPaneFunctionsAndVariables);
    }
    
    private void setThisFunctionContent() {
        ScrollPane scrollPaneVariables = new ScrollPane();
        scrollPaneVariables.setFitToHeight(true);
        scrollPaneVariables.setFitToWidth(true);
        VBox vBoxVariables = new VBox(10);
        
        Item<?, ?, ?> currentItem = Site.currentSelectedProperty().get();
        Function currentFunction = null;
        while (currentFunction == null) {
            if (currentItem == null) {
                return;
            }
            if (currentItem.getType() == ItemType.FUNCTION) {
                currentFunction = (Function) currentItem;
            } else {
                currentItem = currentItem.getParent().orElse(null);
            }
        }
        currentFunction
                .getChildren()
                .stream()
                .filter(i -> i instanceof ValueReturnable)
                .filter(i -> allowedTypes == null
                        || allowedTypes.contains(((ValueReturnable) i).getReturnType())
                )
                .forEach((item) -> vBoxVariables.getChildren().add(getEntries(item, event -> {
                    //TODO
                })));
        scrollPaneVariables.setContent(vBoxVariables);
        
        vBoxRight.getChildren().remove(previousContent);
        previousContent = scrollPaneVariables;
        vBoxRight.getChildren().add(scrollPaneVariables);
    }
    
}
