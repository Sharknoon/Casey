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
package sharknoon.dualide.ui.sites.function;

import java.util.concurrent.CompletableFuture;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;
import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;
import sharknoon.dualide.logic.items.Function;
import sharknoon.dualide.ui.misc.Icon;
import sharknoon.dualide.ui.misc.Icons;
import sharknoon.dualide.ui.sites.Site;
import sharknoon.dualide.utils.language.Language;
import sharknoon.dualide.utils.language.Word;

/**
 *
 * @author Josua Frank
 */
public class FunctionSite extends Site<Function> {

    private final FunctionSiteVariables variableSite;
    private final FunctionSiteLogic logicSite;

    private TabPane root;

    public FunctionSite(Function item) {
        super(item);
        variableSite = new FunctionSiteVariables(this);
        logicSite = new FunctionSiteLogic(this);
    }

    public FunctionSiteVariables getVariableSite() {
        return variableSite;
    }

    public FunctionSiteLogic getLogicSite() {
        return logicSite;
    }

    public TabPane getRoot() {
        return root;
    }

    private void init() {
          var tabLogic = new Tab();
        Language.setCustom(Word.FUNCTION_SITE_FUNCTION_LOGIC, tabLogic.textProperty()::set);
        Icons.setCustom(Icon.FUNCTIONFLOWCHART, tabLogic.graphicProperty()::set);
        tabLogic.setContent(logicSite.getTabContentPane());

          var tabVariables = new Tab();
        Language.setCustom(Word.FUNCTION_SITE_FUNCTION_VARIABLES, tabVariables.textProperty()::set);
        Icons.setCustom(Icon.FUNCTIONVARIABLE, tabVariables.graphicProperty()::set);
        tabVariables.setContent(variableSite.getTabContentPane());

        root = new TabPane(tabLogic, tabVariables);
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
    public Icon getTabIcon() {
        return Icon.FUNCTION;
    }

    @Override
    public Icon getAddIcon() {
        return Icon.PLUSFUNCTION;
    }

}
