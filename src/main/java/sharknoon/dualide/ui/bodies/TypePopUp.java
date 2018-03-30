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

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import org.controlsfx.control.BreadCrumbBar;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.SegmentedButton;
import sharknoon.dualide.logic.Returnable;
import sharknoon.dualide.logic.Statement;
import sharknoon.dualide.logic.items.Function;
import sharknoon.dualide.logic.items.Item;
import sharknoon.dualide.logic.items.Variable;
import sharknoon.dualide.logic.operators.OperatorType;
import sharknoon.dualide.logic.types.PrimitiveType;
import sharknoon.dualide.logic.types.PrimitiveType.BooleanType;
import sharknoon.dualide.logic.types.Type;
import sharknoon.dualide.logic.values.Value;
import sharknoon.dualide.ui.misc.Icon;
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
        TypePopUp popUp = new TypePopUp(ownerNode, typeConsumer);
    }

    private final GridPane gridPaneRoot = new GridPane();
    private final Consumer<Type> typeConsumer;

    private TypePopUp(Node ownerNode, Consumer<Type> typeConsumer) {
        this.typeConsumer = typeConsumer;
        init();
        addPrimitiveTypeSelectors();
        addObjectTypeSelectors();
        show(ownerNode);
    }

    private void init() {
        gridPaneRoot.setVgap(10);
        gridPaneRoot.setHgap(10);
        gridPaneRoot.setPadding(new Insets(25));
        gridPaneRoot.setPrefWidth(520);
        gridPaneRoot.setMaxSize(800, 800);
        gridPaneRoot.setAlignment(Pos.CENTER);
        getRoot().getStylesheets().add("sharknoon/dualide/ui/MainCSS.css");
        setContentNode(gridPaneRoot);
        setArrowLocation(ArrowLocation.BOTTOM_CENTER);
        setTitle(Language.get(Word.TYPE_SELECTION_POPUP_TITLE));
    }

    private void addPrimitiveTypeSelectors() {
        addPrimitiveTypeSeparator();
        addPrimitiveTypeSegmentedButtons();
    }

    private void addPrimitiveTypeSeparator() {
        String text = Language.get(Word.TYPE_SELECTION_POPUP_PRIMITIVE_TYPES);
        Node separator = getSeparator(text);
        gridPaneRoot.add(separator, 0, 0, 3, 1);
    }

    private void addPrimitiveTypeSegmentedButtons() {
        String textBoolean = PrimitiveType.BOOLEAN.getName().get();
        String textNumber = PrimitiveType.NUMBER.getName().get();
        String textText = PrimitiveType.TEXT.getName().get();

        Button buttonBoolean = new Button(textBoolean, Icons.get(Icon.BOOLEAN));
        Button buttonNumber = new Button(textNumber, Icons.get(Icon.NUMBER));
        Button buttonText = new Button(textText, Icons.get(Icon.TEXT));

        buttonBoolean.prefWidthProperty().bind(gridPaneRoot.widthProperty().divide(4));
        buttonNumber.prefWidthProperty().bind(gridPaneRoot.widthProperty().divide(4));
        buttonText.prefWidthProperty().bind(gridPaneRoot.widthProperty().divide(4));

        buttonBoolean.setOnAction((event) -> {
            if (typeConsumer != null) {
                typeConsumer.accept(PrimitiveType.BOOLEAN);
            }
            hide();
        });
        buttonNumber.setOnAction((event) -> {
            if (typeConsumer != null) {
                typeConsumer.accept(PrimitiveType.NUMBER);
            }
            hide();
        });
        buttonText.setOnAction((event) -> {
            if (typeConsumer != null) {
                typeConsumer.accept(PrimitiveType.TEXT);
            }
            hide();
        });

        gridPaneRoot.add(buttonBoolean, 0, 1);
        gridPaneRoot.add(buttonNumber, 1, 1);
        gridPaneRoot.add(buttonText, 2, 1);
    }

    private void addObjectTypeSelectors() {
        addObjectTypeSeparator();
        addTypeBrowser();
    }

    private void addObjectTypeSeparator() {
        String text = Language.get(Word.TYPE_SELECTION_POPUP_OBJECT_TYPES);
        Node separator = getSeparator(text);
        gridPaneRoot.add(separator, 0, 2, 3, 1);
    }

    private Node previousContent = null;

    private void addTypeBrowser() {
        GridPane gridPanePackagesAndClasses = new GridPane();
        gridPanePackagesAndClasses.setHgap(10);
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        gridPanePackagesAndClasses.getColumnConstraints().addAll(col1, col2);

        BreadCrumbBar<Item> breadCrumbBarNavigation = new BreadCrumbBar<>();
        ScrollPane scrollPaneBreadCrumbBar = new ScrollPane(breadCrumbBarNavigation);
        scrollPaneBreadCrumbBar.setMinHeight(45);
        gridPanePackagesAndClasses.add(scrollPaneBreadCrumbBar, 0, 0, 2, 1);

        ScrollPane scrollPanePackages = new ScrollPane();
        //scrollPanePackages.setFitToHeight(true);
        //scrollPanePackages.setFitToWidth(true);
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
        gridPaneRoot.getChildren().remove(previousContent);
        previousContent = gridPanePackagesAndClasses;
        gridPaneRoot.add(gridPanePackagesAndClasses, 0, 3, 3, 1);

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
