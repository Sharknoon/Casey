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
package sharknoon.dualide.ui.sites.parameter;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import sharknoon.dualide.logic.items.Parameter;
import sharknoon.dualide.logic.types.PrimitiveType;
import sharknoon.dualide.logic.types.Type;
import sharknoon.dualide.ui.fields.TypeField;
import sharknoon.dualide.ui.misc.Icon;
import sharknoon.dualide.ui.sites.Site;
import sharknoon.dualide.ui.sites.SiteUtils;
import sharknoon.dualide.utils.language.Language;
import sharknoon.dualide.utils.language.Word;

import java.util.concurrent.CompletableFuture;

/**
 * @author Josua Frank
 */
public class ParameterSite extends Site<Parameter> {
    
    private ObjectProperty<Icon> icon;
    private BorderPane borderPaneRoot;
    
    public ParameterSite(Parameter item) {
        super(item);
    }
    
    @Override
    public void afterInit() {
        ChangeListener<? super Type> listener = (observable, oldValue, newValue) -> icon.set(getIcon(newValue));
        getItem().typeProperty().addListener(listener);
        listener.changed(getItem().typeProperty(), null, getItem().typeProperty().get());
    }
    
    @Override
    public CompletableFuture<Node> getTabContentPane() {
        return CompletableFuture.supplyAsync(() -> {
            if (borderPaneRoot == null) {
                init();
            }
            return borderPaneRoot;
        });
    }
    
    @Override
    public ObjectProperty<Icon> tabIconProperty() {
        if (icon == null) {
            icon = new SimpleObjectProperty<>(Icon.PARAMETER);
        }
        return icon;
    }
    
    @Override
    public Icon getAddIcon() {
        return Icon.PLUSPARAMETER;
    }
    
    private void init() {
        borderPaneRoot = new BorderPane();
        borderPaneRoot.setCenter(getContent());
        borderPaneRoot.setBottom(SiteUtils.getFooter(getItem()));
    }
    
    private Icon getIcon(Type type) {
        if (type == null) {
            return Icon.PARAMETER;
        } else if (!type.isPrimitive()) {
            return Icon.PARAMETERCLASS;
        } else if (type == PrimitiveType.BOOLEAN) {
            return Icon.PARAMETERBOOLEAN;
        } else if (type == PrimitiveType.NUMBER) {
            return Icon.PARAMETERNUMBER;
        } else if (type == PrimitiveType.TEXT) {
            return Icon.PARAMETERTEXT;
        } else if (type == PrimitiveType.VOID) {
            return Icon.VOID;
        } else {
            return Icon.PARAMETER;
        }
    }
    
    private ScrollPane getContent() {
        var gridPaneContent = new GridPane();
        gridPaneContent.setVgap(20);
        gridPaneContent.setHgap(20);
        gridPaneContent.setAlignment(Pos.TOP_LEFT);
        gridPaneContent.setPadding(new Insets(50));
        
        Label labelClass = new Label();
        Language.set(Word.PARAMETER_SITE_CLASS_LABEL_TEXT, labelClass);
        
        gridPaneContent.addRow(0, labelClass);
        
        TypeField typeField = new TypeField();
        typeField.typeProperty().bindBidirectional(getItem().typeProperty());
        
        CheckBox checkBoxFinal = new CheckBox();
        checkBoxFinal.selectedProperty().bindBidirectional(getItem().modifiableProperty());
        Language.set(Word.PARAMETER_SITE_FINAL_COMBOBOX_TEXT, checkBoxFinal);
        
        gridPaneContent.addRow(1, typeField, checkBoxFinal);
        
        var scrollPaneChildren = new ScrollPane(gridPaneContent);
        scrollPaneChildren.setFitToHeight(true);
        scrollPaneChildren.setFitToWidth(true);
        return scrollPaneChildren;
    }
    
}
