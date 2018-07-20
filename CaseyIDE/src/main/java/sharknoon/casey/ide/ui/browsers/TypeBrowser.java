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
package sharknoon.casey.ide.ui.browsers;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.controlsfx.control.BreadCrumbBar;
import sharknoon.casey.ide.logic.items.Class.ObjectType;
import sharknoon.casey.ide.logic.items.Function;
import sharknoon.casey.ide.logic.items.Item;
import sharknoon.casey.ide.logic.items.Variable;
import sharknoon.casey.ide.logic.types.PrimitiveType;
import sharknoon.casey.ide.logic.types.Type;
import sharknoon.casey.ide.ui.bodies.PopUpUtils;
import sharknoon.casey.ide.ui.misc.Icons;
import sharknoon.casey.ide.ui.sites.Site;
import sharknoon.casey.ide.utils.language.Language;
import sharknoon.casey.ide.utils.language.Word;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

/**
 *
 * @author Josua Frank
 */
public class TypeBrowser extends VBox {

    private final Consumer<Type> typeConsumer;
    private final Collection<? extends Type> allowedTypes;

    public static TypeBrowser createTypeBrowser(Consumer<Type> typeConsumer, Collection<? extends Type> allowedTypes){
        return new TypeBrowser(typeConsumer, allowedTypes, false, false);
    }
    
    /**
     * Dismisses the primitive types
     *
     * @param typeConsumer
     * @param allowedTypes
     * @return
     */
    public static TypeBrowser createOnlyObjectTypebrowser(Consumer<Type> typeConsumer, Collection<? extends Type> allowedTypes){
        return new TypeBrowser(typeConsumer, allowedTypes, true, false);
    }
    
    /**
     * Allows the primitive type void, e.g. for method return type
     * @param typeConsumer
     * @param allowedTypes
     * @return
     */
    public static TypeBrowser createTypeBrowserWithVoid(Consumer<Type> typeConsumer, Collection<? extends Type> allowedTypes){
        return new TypeBrowser(typeConsumer, allowedTypes, false, true);
    }

    private TypeBrowser(Consumer<Type> typeConsumer, Collection<? extends Type> allowedTypes, boolean onlyObjects, boolean withVoid) {
        this.typeConsumer = typeConsumer;
        this.allowedTypes = allowedTypes;
        init();
        if (allowedTypes != null && allowedTypes.isEmpty()) {
            addNoTypeLabel();
        } else {
            if (!onlyObjects) {
                addPrimitiveTypeSelectors(withVoid);
            }
            addObjectTypeSelectors();
        }
    }

    private void init() {
        setSpacing(10);
        setPadding(new Insets(25));
        setPrefWidth(520);
        setMaxSize(800, 400);
        setAlignment(Pos.CENTER);
    }

    private void addNoTypeLabel() {
          var text = Language.get(Word.TYPE_SELECTION_POPUP_NO_TYPES);
          var labelNoType = new Label(text);
        labelNoType.setFont(Font.font(20));
        labelNoType.setMaxWidth(Double.MAX_VALUE);
        labelNoType.setAlignment(Pos.CENTER);
        getChildren().add(labelNoType);
    }

    private void addPrimitiveTypeSelectors(boolean withVoid) {
        Collection<? extends Type> types
                = allowedTypes == null
                        ? PrimitiveType.getAll()
                        : allowedTypes;
        boolean hasPrimitiveType = false;
        for (Type type : types) {
            if (type != null && type.isPrimitive()) {
                if (!hasPrimitiveType) {
                    addPrimitiveTypeSeparator();
                    hasPrimitiveType = true;
                }
                addPrimitiveTypeSegmentedButtons(type.getPrimitiveType());
            }
        }
        if (withVoid && allowedTypes == null){
            if (!hasPrimitiveType) {
                addPrimitiveTypeSeparator();
            }
            addPrimitiveTypeSegmentedButtons(PrimitiveType.VOID);
        }
    }

    private void addPrimitiveTypeSeparator() {
        String text = Language.get(Word.TYPE_SELECTION_POPUP_PRIMITIVE_TYPES);
        Node separator = PopUpUtils.getSeparator(text);
        getChildren().add(separator);
    }

    private HBox hBoxSegmentedButtons = new HBox(10);

    private void addPrimitiveTypeSegmentedButtons(PrimitiveType type) {
        String text = type.getLanguageDependentName().get();

        Button button = new Button(text, Icons.get(type.getIcon()));
        button.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(button, Priority.ALWAYS);

        button.setOnAction((event) -> {
            if (typeConsumer != null) {
                typeConsumer.accept(type);
            }
        });

        hBoxSegmentedButtons.getChildren().add(button);
        if (!getChildren().contains(hBoxSegmentedButtons)) {
            getChildren().add(hBoxSegmentedButtons);
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
            if (type != null && !type.isPrimitive()) {
                if (!hasObjectType) {
                    addObjectTypeSeparator();
                    hasObjectType = true;
                }
                objectTypes.add(type.getObjectType());
            }
        }
        if (!objectTypes.isEmpty()) {
            addTypeBrowser(objectTypes);
        }
    }

    private void addObjectTypeSeparator() {
        String text = Language.get(Word.TYPE_SELECTION_POPUP_OBJECT_TYPES);
        Node separator = PopUpUtils.getSeparator(text);
        getChildren().add(separator);
    }

    private Node previousContent = null;
    private HBox previousSelectedType = null;

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
                        .filter(ti -> ti.getValue() instanceof sharknoon.casey.ide.logic.items.Package)
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
                        .filter(i -> i instanceof sharknoon.casey.ide.logic.items.Class)
                        .filter(i -> types == null || types.contains(((sharknoon.casey.ide.logic.items.Class) i).toType())
                        )
                        .forEach((t) -> {
                            HBox hBoxclass = new HBox(10);
                            Node icon = Icons.get(t.getSite().getTabIcon());
                            Label name = new Label(t.getName());
                            hBoxclass.setOnMouseClicked((event) -> {
                                if (previousSelectedType != null) {
                                    previousSelectedType.setBackground(Background.EMPTY);
                                }
                                hBoxclass.setBackground(new Background(new BackgroundFill(Color.GRAY, CornerRadii.EMPTY, Insets.EMPTY)));
                                previousSelectedType = hBoxclass;
                                if (typeConsumer != null) {
                                    typeConsumer.accept(((sharknoon.casey.ide.logic.items.Class) t).toType());
                                }
                            });
                            hBoxclass.getChildren().addAll(icon, name);
                            hBoxclass.setAlignment(Pos.CENTER_LEFT);
                            vBoxClasses.getChildren().add(hBoxclass);
                        });
            }
        });
        scrollPaneClasses.setContent(vBoxClasses);
        gridPanePackagesAndClasses.add(scrollPaneClasses, 1, 1);
        getChildren().remove(previousContent);
        previousContent = gridPanePackagesAndClasses;
        getChildren().add(gridPanePackagesAndClasses);

        TreeItem selectedItem = Site.currentSelectedProperty().get().getSite().getTreeItem();
        if (selectedItem != null && selectedItem.getValue() != null) {
            while ((selectedItem.getValue() instanceof Function)
                    || (selectedItem.getValue() instanceof Variable)
                    || (selectedItem.getValue() instanceof sharknoon.casey.ide.logic.items.Class)) {
                selectedItem = selectedItem.getParent();
            }
            breadCrumbBarNavigation.setSelectedCrumb(selectedItem);
        }
    }
}
