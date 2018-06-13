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
package sharknoon.dualide.ui.bodies;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.controlsfx.control.BreadCrumbBar;
import org.controlsfx.control.PopOver;
import sharknoon.dualide.logic.items.Class.ObjectType;
import sharknoon.dualide.logic.items.Function;
import sharknoon.dualide.logic.items.Item;
import sharknoon.dualide.logic.items.Variable;
import sharknoon.dualide.logic.types.PrimitiveType;
import sharknoon.dualide.logic.types.Type;
import sharknoon.dualide.ui.misc.Icons;
import sharknoon.dualide.ui.sites.Site;
import sharknoon.dualide.utils.language.Language;
import sharknoon.dualide.utils.language.Word;

/**
 *
 * @author frank
 */
public class TypePopUp extends PopOver {

    public static void showTypeSelectionPopUp(Node ownerNode, Consumer<Type> typeConsumer) {
        showTypeSelectionPopUp(ownerNode, typeConsumer, null);
    }

    public static void showTypeSelectionPopUp(Node ownerNode, Consumer<Type> typeConsumer, Collection<? extends Type> allowedTypes) {
        TypePopUp popUp = new TypePopUp(ownerNode, typeConsumer, allowedTypes);
    }

    private final VBox VBoxRoot = new VBox();
    private final Consumer<Type> typeConsumer;
    private final Collection<? extends Type> allowedTypes;

    private TypePopUp(Node ownerNode, Consumer<Type> typeConsumer, Collection<? extends Type> allowedTypes) {
        this.typeConsumer = typeConsumer;
        this.allowedTypes = allowedTypes;
        init();
        addPrimitiveTypeSelectors();
        addObjectTypeSelectors();
        show(ownerNode);
    }

    private void init() {
        VBoxRoot.setSpacing(10);
        VBoxRoot.setPadding(new Insets(25));
        VBoxRoot.setPrefWidth(520);
        VBoxRoot.setMaxSize(800, 800);
        VBoxRoot.setAlignment(Pos.CENTER);
        getRoot().getStylesheets().add("sharknoon/dualide/ui/MainCSS.css");
        setContentNode(VBoxRoot);
        setArrowLocation(ArrowLocation.BOTTOM_CENTER);
        setTitle(Language.get(Word.TYPE_SELECTION_POPUP_TITLE));
    }

    private void addPrimitiveTypeSelectors() {
        Collection<? extends Type> types
                = allowedTypes == null
                        ? PrimitiveType.getAll()
                        : allowedTypes;
        boolean hasPrimitiveType = false;
        for (Type type : types) {
            if (type.isPrimitive()) {
                if (!hasPrimitiveType) {
                    addPrimitiveTypeSeparator();
                    hasPrimitiveType = true;
                }
                addPrimitiveTypeSegmentedButtons(type.getPrimitiveType());
            }
        }
    }

    private void addPrimitiveTypeSeparator() {
        String text = Language.get(Word.TYPE_SELECTION_POPUP_PRIMITIVE_TYPES);
        Node separator = PopUpUtils.getSeparator(text);
        VBoxRoot.getChildren().add(separator);
    }

    private HBox hBoxSegmentedButtons = new HBox(10);

    private void addPrimitiveTypeSegmentedButtons(PrimitiveType type) {
        String text = type.getName().get();

        Button button = new Button(text, Icons.get(type.getIcon()));
        button.setMinWidth(Double.MAX_VALUE);
        HBox.setHgrow(button, Priority.ALWAYS);

        button.setOnAction((event) -> {
            if (typeConsumer != null) {
                typeConsumer.accept(type);
            }
            hide();
        });

        hBoxSegmentedButtons.getChildren().add(button);
        if (!VBoxRoot.getChildren().contains(hBoxSegmentedButtons)) {
            VBoxRoot.getChildren().add(hBoxSegmentedButtons);
        }
    }

    private void addObjectTypeSelectors() {
        if (allowedTypes == null) {
            addObjectTypeSeparator();
            addTypeBrowser(null);
            return;
        }
        boolean hasObjectType = false;
        List<ObjectType> objectTypes = new ArrayList<>();
        for (Type type : allowedTypes) {
            if (!type.isPrimitive()) {
                if (!hasObjectType) {
                    addObjectTypeSeparator();
                    hasObjectType = true;
                }
                objectTypes.add(type.getClassType());
            }
        }
        if (!objectTypes.isEmpty()) {
            addTypeBrowser(objectTypes);
        }
    }

    private void addObjectTypeSeparator() {
        String text = Language.get(Word.TYPE_SELECTION_POPUP_OBJECT_TYPES);
        Node separator = PopUpUtils.getSeparator(text);
        VBoxRoot.getChildren().add(separator);
    }

    private Node previousContent = null;

    private void addTypeBrowser(Collection<ObjectType> types) {
        GridPane gridPanePackagesAndClasses = new GridPane();
        gridPanePackagesAndClasses.setHgap(10);
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        gridPanePackagesAndClasses.getColumnConstraints().addAll(col1, col2);

        BreadCrumbBar<Item> breadCrumbBarNavigation = new BreadCrumbBar<>();
        ScrollPane scrollPaneBreadCrumbBar = new ScrollPane(breadCrumbBarNavigation);
        scrollPaneBreadCrumbBar.setMinHeight(50);
        scrollPaneBreadCrumbBar.maxWidthProperty().bind(gridPanePackagesAndClasses.widthProperty().subtract(gridPanePackagesAndClasses.hgapProperty()));
        gridPanePackagesAndClasses.add(scrollPaneBreadCrumbBar, 0, 0, 2, 1);

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
                        .filter(ti -> ti.getValue() instanceof sharknoon.dualide.logic.items.Package)
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
        gridPanePackagesAndClasses.add(scrollPanePackages, 0, 1);

        ScrollPane scrollPaneClasses = new ScrollPane();
        scrollPaneClasses.setFitToHeight(true);
        scrollPaneClasses.setFitToWidth(true);
        VBox vBoxClasses = new VBox(10);
        breadCrumbBarNavigation.selectedCrumbProperty().addListener((observable, oldValue, newValue) -> {
            vBoxClasses.getChildren().clear();
            if (newValue != null) {
                newValue
                        .getChildren()
                        .stream()
                        .map(ti -> ti.getValue())
                        .filter(i -> i instanceof sharknoon.dualide.logic.items.Class)
                        .filter(i -> {
                            return types == null
                                    ? true
                                    : types.contains(((sharknoon.dualide.logic.items.Class) i).toType());
                        })
                        .forEach((t) -> {
                            HBox hBoxclass = new HBox(10);
                            Node icon = Icons.get(t.getSite().getTabIcon());
                            Label name = new Label(t.getName());
                            hBoxclass.setOnMouseClicked((event) -> {
                                if (typeConsumer != null) {
                                    typeConsumer.accept(((sharknoon.dualide.logic.items.Class) t).toType());
                                }
                                hide();
                            });
                            hBoxclass.getChildren().addAll(icon, name);
                            hBoxclass.setAlignment(Pos.CENTER_LEFT);
                            vBoxClasses.getChildren().add(hBoxclass);
                        });
            }
        });
        scrollPaneClasses.setContent(vBoxClasses);
        gridPanePackagesAndClasses.add(scrollPaneClasses, 1, 1);
        VBoxRoot.getChildren().remove(previousContent);
        previousContent = gridPanePackagesAndClasses;
        VBoxRoot.getChildren().add(gridPanePackagesAndClasses);

        TreeItem selectedItem = Site.currentSelectedProperty().get().getSite().getTreeItem();
        if (selectedItem != null) {
            while ((selectedItem.getValue() instanceof Function)
                    || (selectedItem.getValue() instanceof Variable)
                    || (selectedItem.getValue() instanceof sharknoon.dualide.logic.items.Class)) {
                selectedItem = selectedItem.getParent();
            }
            breadCrumbBarNavigation.setSelectedCrumb(selectedItem);
        }
    }

}
