package sharknoon.dualide.ui.browsers;/*
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

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.TreeItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import org.controlsfx.control.BreadCrumbBar;
import org.controlsfx.control.SegmentedButton;
import sharknoon.dualide.logic.ValueReturnable;
import sharknoon.dualide.logic.items.Class;
import sharknoon.dualide.logic.items.*;
import sharknoon.dualide.logic.items.Package;
import sharknoon.dualide.logic.statements.Statement;
import sharknoon.dualide.logic.statements.calls.Call;
import sharknoon.dualide.logic.types.Type;
import sharknoon.dualide.ui.misc.Icons;
import sharknoon.dualide.ui.sites.Site;
import sharknoon.dualide.utils.language.Language;
import sharknoon.dualide.utils.language.Word;

import java.util.Collection;
import java.util.function.Consumer;

public class BrowserUtils {
    
    public static HBox getEntries(Item<?, ?, ?> item, EventHandler<MouseEvent> onClick) {
        HBox hBoxChildren = new HBox(10);
        Node icon = Icons.get(item.getSite().getTabIcon());
        Label name = new Label(item.getName());
        hBoxChildren.setOnMouseClicked(onClick);
        hBoxChildren.getChildren().addAll(icon, name);
        hBoxChildren.setAlignment(Pos.CENTER_LEFT);
        return hBoxChildren;
    }
    
    public static Label getNoTypeLabel() {
        var text = Language.get(Word.TYPE_SELECTION_POPUP_NO_TYPES);
        var labelNoType = new Label(text);
        labelNoType.setFont(Font.font(20));
        labelNoType.setMaxWidth(Double.MAX_VALUE);
        labelNoType.setAlignment(Pos.CENTER);
        return labelNoType;
    }
    
    /**
     * @param allowedType       the allowed returnTypes of the functions, parameters or variables
     * @param statementConsumer
     * @param parent
     * @param allowedItems      null means all items are allowed, empty means no items are allowed
     * @return
     */
    public static VBox getStatementSelector(Type allowedType, Consumer<Statement> statementConsumer, Statement parent, Collection<ItemType> allowedItems) {
        Consumer<Item> itemConsumer = i -> statementConsumer.accept(new Call(parent, i, allowedType));
        return getItemSelector(allowedType, itemConsumer, allowedItems);
    }
    
    public static VBox getItemSelector(Type allowedType, Consumer<Item> itemConsumer, Collection<ItemType> allowedItems) {
        VBox root = new VBox();
        root.setSpacing(10);
        
        SegmentedButton segmentedButtonValueSource = new SegmentedButton();
        
        Item<?, ?, ?> i = Site.currentSelectedProperty().get();
        boolean inClass = i != null && (i.isIn(ItemType.CLASS) || i.getType() == ItemType.CLASS);
        boolean inFunction = i != null && (i.isIn(ItemType.FUNCTION) || i.getType() == ItemType.FUNCTION);
        int amountButtons = 1 + (inClass ? 1 : 0) + (inFunction ? 1 : 0);
        
        ObjectProperty<Node> previousContent = new SimpleObjectProperty<>();
        
        String textStatic = Language.get(Word.VALUE_SELECTION_POPUP_STATIC_VALUES);
        ToggleButton toggleButtonStatic = new ToggleButton(textStatic);
        toggleButtonStatic.prefWidthProperty().bind(root.widthProperty().divide(amountButtons));
        toggleButtonStatic.setOnAction((event) -> setStaticContent(root, itemConsumer, allowedType, previousContent, allowedItems));
        segmentedButtonValueSource.getButtons().add(toggleButtonStatic);
        
        if (inClass) {
            String textThisClass = Language.get(Word.VALUE_SELECTION_POPUP_CLASS_VALUES);
            ToggleButton toggleButtonThisClass = new ToggleButton(textThisClass);
            toggleButtonThisClass.prefWidthProperty().bind(root.widthProperty().divide(amountButtons));
            toggleButtonThisClass.setOnAction((event) -> setThisClassContent(root, itemConsumer, allowedType, previousContent, allowedItems));
            segmentedButtonValueSource.getButtons().add(toggleButtonThisClass);
        }
        
        if (inFunction) {
            String textThisFunction = Language.get(Word.VALUE_SELECTION_POPUP_FUNCTION_VALUES);
            ToggleButton toggleButtonThisFunction = new ToggleButton(textThisFunction);
            toggleButtonThisFunction.prefWidthProperty().bind(root.widthProperty().divide(amountButtons));
            toggleButtonThisFunction.setOnAction((event) -> setThisFunctionContent(root, itemConsumer, allowedType, previousContent, allowedItems));
            segmentedButtonValueSource.getButtons().add(toggleButtonThisFunction);
        }
        
        root.getChildren().add(segmentedButtonValueSource);
        GridPane.setHgrow(segmentedButtonValueSource, Priority.ALWAYS);
        //GridPane.setFillWidth(segmentedButtonValueSource, true);
        toggleButtonStatic.fire();
        
        return root;
    }
    
    
    private static void setStaticContent(VBox vBoxRoot, Consumer<Item> itemConsumer, Type allowedType, ObjectProperty<Node> previousContent, Collection<ItemType> allowedItems) {
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
                        .filter(i -> allowedItems == null || allowedItems.contains(i.getType()))
                        .filter(i -> allowedType == Type.UNDEFINED
                                || allowedType == ((ValueReturnable) i).getReturnType()
                                || ((ValueReturnable) i).getReturnType().isObject()
                        )
                        .forEach((i) -> vBoxFunctionsAndVariables.getChildren().add(getEntries(i, event -> {
                            itemConsumer.accept(i);
                        })));
            }
        });
        scrollPaneFunctionsAndVariables.setContent(vBoxFunctionsAndVariables);
        gridPanePackagesFunctionsAndVariables.add(scrollPaneFunctionsAndVariables, 1, 1);
        vBoxRoot.getChildren().remove(previousContent.get());
        previousContent.set(gridPanePackagesFunctionsAndVariables);
        vBoxRoot.getChildren().add(gridPanePackagesFunctionsAndVariables);
        
        TreeItem<Item> selectedTreeItem = Site.currentSelectedProperty().get().getSite().getTreeItem();
        if (selectedTreeItem != null) {
            Item item = selectedTreeItem.getValue();
            while ((item instanceof Function)
                    || (item instanceof Variable)
                    || (item instanceof Parameter)
                    || (item instanceof Class)
                    || (item.isIn(ItemType.CLASS))
                    || (item.isIn(ItemType.FUNCTION))) {
                selectedTreeItem = selectedTreeItem.getParent();
                item = selectedTreeItem.getValue();
            }
            breadCrumbBarNavigation.setSelectedCrumb(selectedTreeItem);
        }
    }
    
    private static void setThisClassContent(VBox vBoxRoot, Consumer<Item> itemConsumer, Type allowedType, ObjectProperty<Node> previousContent, Collection<ItemType> allowedItems) {
        ScrollPane scrollPaneFunctionsAndVariables = new ScrollPane();
        scrollPaneFunctionsAndVariables.setFitToHeight(true);
        scrollPaneFunctionsAndVariables.setFitToWidth(true);
        VBox vBoxFunctionsAndVariables = new VBox(10);
        
        Item<?, ?, ?> currentItem = Site.currentSelectedProperty().get();
        sharknoon.dualide.logic.items.Class currentClass = null;
        while (currentClass == null) {
            if (currentItem == null) {
                return;
            }
            if (currentItem.getType() == ItemType.CLASS) {
                currentClass = (sharknoon.dualide.logic.items.Class) currentItem;
            } else {
                currentItem = currentItem.getParent().orElse(null);
            }
        }
        currentClass
                .getChildren()
                .stream()
                .filter(i -> allowedItems == null || allowedItems.contains(i.getType()))
                .filter(i -> allowedType == Type.UNDEFINED
                        || allowedType == ((ValueReturnable) i).getReturnType()
                        || ((ValueReturnable) i).getReturnType().isObject()
                )
                .forEach((Item<? extends Item, sharknoon.dualide.logic.items.Class, ? extends Item> i) -> vBoxFunctionsAndVariables.getChildren().add(getEntries(i, event -> {
                    itemConsumer.accept(i);
                })));
        scrollPaneFunctionsAndVariables.setContent(vBoxFunctionsAndVariables);
        
        vBoxRoot.getChildren().remove(previousContent.get());
        previousContent.set(scrollPaneFunctionsAndVariables);
        vBoxRoot.getChildren().add(scrollPaneFunctionsAndVariables);
    }
    
    private static void setThisFunctionContent(VBox vBoxRoot, Consumer<Item> itemConsumer, Type allowedType, ObjectProperty<Node> previousContent, Collection<ItemType> allowedItems) {
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
                .filter(i -> allowedItems == null || allowedItems.contains(i.getType()))
                .filter(i -> allowedType == Type.UNDEFINED
                        || allowedType == ((ValueReturnable) i).getReturnType()
                        || ((ValueReturnable) i).getReturnType().isObject()
                )
                .forEach((i) -> vBoxVariables.getChildren().add(getEntries(i, event -> {
                    itemConsumer.accept(i);
                })));
        scrollPaneVariables.setContent(vBoxVariables);
        
        vBoxRoot.getChildren().remove(previousContent.get());
        previousContent.set(scrollPaneVariables);
        vBoxRoot.getChildren().add(scrollPaneVariables);
    }
    
}
