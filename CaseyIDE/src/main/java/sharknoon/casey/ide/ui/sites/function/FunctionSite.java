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
package sharknoon.casey.ide.ui.sites.function;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import sharknoon.casey.ide.logic.items.Function;
import sharknoon.casey.ide.logic.types.PrimitiveType;
import sharknoon.casey.ide.logic.types.Type;
import sharknoon.casey.ide.ui.misc.Icon;
import sharknoon.casey.ide.ui.misc.Icons;
import sharknoon.casey.ide.ui.sites.Site;
import sharknoon.casey.ide.utils.language.Language;
import sharknoon.casey.ide.utils.language.Word;

import java.util.concurrent.CompletableFuture;

/**
 * @author Josua Frank
 */
public class FunctionSite extends Site<Function> {

    private final FunctionSiteVariablesParameters variableAndParameterSite;
    private final FunctionSiteLogic logicSite;
    private final FunctionSiteReturnType returnSite;
    private ObjectProperty<Icon> icon;


    private TabPane root;

    public FunctionSite(Function item) {
        super(item);
        variableAndParameterSite = new FunctionSiteVariablesParameters(this);
        logicSite = new FunctionSiteLogic(this);
        returnSite = new FunctionSiteReturnType(this);
    }

    public FunctionSiteVariablesParameters getVariableAndParameterSite() {
        return variableAndParameterSite;
    }

    public FunctionSiteLogic getLogicSite() {
        return logicSite;
    }

    public FunctionSiteReturnType getReturnSite() {
        return returnSite;
    }

    public TabPane getRoot() {
        return root;
    }
    
    @Override
    public void afterInit() {
        ChangeListener<? super Type> listener = (observable, oldValue, newValue) -> icon.set(getIcon(newValue));
        getItem().returnTypeProperty().addListener(listener);
        listener.changed(getItem().returnTypeProperty(), null, getItem().returnTypeProperty().get());
    }

    private void init() {
        var tabLogic = new Tab();
        Language.setCustom(Word.FUNCTION_SITE_FUNCTION_LOGIC, tabLogic.textProperty()::set);
        Icons.setCustom(Icon.FUNCTIONFLOWCHART, tabLogic.graphicProperty()::set);
        tabLogic.setContent(logicSite.getTabContentPane());

        var tabVariablesAndParameters = new Tab();
        Language.setCustom(Word.FUNCTION_SITE_FUNCTION_VARIABLES_AND_PARAMETER, tabVariablesAndParameters.textProperty()::set);
        Icons.setCustom(Icon.FUNCTIONVARIABLEPARAMETER, tabVariablesAndParameters.graphicProperty()::set);
        tabVariablesAndParameters.setContent(variableAndParameterSite.getTabContentPane());

        var tabReturnType = new Tab();
        Language.setCustom(Word.FUNCTION_SITE_FUNCTION_RETURNTYPE, tabReturnType.textProperty()::set);
        Icons.setCustom(Icon.FUNCTIONRETURN, tabReturnType.graphicProperty()::set);
        tabReturnType.setContent(returnSite.getTabContentPane());

        root = new TabPane(tabLogic, tabVariablesAndParameters, tabReturnType);
        root.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
    }

    @Override
    public CompletableFuture<Node> getTabContentPane() {
        return CompletableFuture.supplyAsync(() -> {
            if (root == null) {
                init();
            }
            return root;
        });
    }

    @Override
    public ObjectProperty<Icon> tabIconProperty() {
        if (icon == null) {
            icon = new SimpleObjectProperty<>(Icon.FUNCTION);
        }
        return icon;
    }

    @Override
    public Icon getAddIcon() {
        return Icon.PLUSFUNCTION;
    }
    
    private Icon getIcon(Type type) {
        if (type == null) {
            return Icon.FUNCTION;
        } else if (!type.isPrimitive()) {
            return Icon.FUNCTIONCLASS;
        } else if (type == PrimitiveType.BOOLEAN) {
            return Icon.FUNCTIONBOOLEAN;
        } else if (type == PrimitiveType.NUMBER) {
            return Icon.FUNCTIONNUMBER;
        } else if (type == PrimitiveType.TEXT) {
            return Icon.FUNCTIONTEXT;
        } else if (type == PrimitiveType.VOID) {
            return Icon.FUNCTIONVOID;
        } else {
            return Icon.FUNCTION;
        }
    }

}
