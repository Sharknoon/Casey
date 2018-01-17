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
package sharknoon.dualide.ui.sites.clazz;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import sharknoon.dualide.ui.sites.Site;
import sharknoon.dualide.logic.Class;
import sharknoon.dualide.ui.misc.Icon;
import sharknoon.dualide.ui.misc.Icons;

/**
 *
 * @author Josua Frank
 */
public class ClassSite extends Site<Class> {

    public ClassSite(Class item) {
        super(item);
    }

    @Override
    public Pane getTabContentPane() {
        return new Pane();
    }

    @Override
    public Icon getTabIcon() {
        return Icon.CLASS;
    }

}