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

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import sharknoon.dualide.ui.misc.Icon;

/**
 *
 * @author Josua Frank
 */
public class FunctionSiteVariables {

    public Pane getTabContentPane() {
        return new Pane(new Label("vars todo"));
    }

}
