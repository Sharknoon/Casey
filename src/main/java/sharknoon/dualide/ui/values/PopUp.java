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
package sharknoon.dualide.ui.values;

import sharknoon.dualide.logic.values.ValueType;
import java.util.Set;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import org.controlsfx.control.PopOver;

/**
 *
 * @author Josua Frank
 */
public class PopUp {

    public static void showValueSelectionPopUp(Node ownerNode, Set<ValueType> allowedValues) {
        PopOver popUp = new PopOver();
        //popUp.getRoot().getStylesheets().clear();
        popUp.getRoot().getStylesheets().add("sharknoon/dualide/ui/MainCSS.css");
        Pane selectionPane = ValueSelection.getValueSelectionPane(allowedValues);
        popUp.setContentNode(selectionPane);
        popUp.setArrowLocation(PopOver.ArrowLocation.BOTTOM_CENTER);
        popUp.show(ownerNode);
    }

}