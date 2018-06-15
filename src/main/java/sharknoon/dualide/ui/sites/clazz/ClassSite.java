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

import java.util.concurrent.CompletableFuture;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import sharknoon.dualide.ui.sites.Site;
import sharknoon.dualide.logic.items.Class;
import sharknoon.dualide.logic.items.ItemType;
import sharknoon.dualide.ui.misc.Icon;
import sharknoon.dualide.ui.sites.SiteUtils;

/**
 *
 * @author Josua Frank
 */
public class ClassSite extends Site<Class> {

    private Pane paneRoot;

    public ClassSite(Class item) {
        super(item);
    }

    @Override
    public CompletableFuture<Node> getTabContentPane() {
        return CompletableFuture.supplyAsync(() -> {
            if (paneRoot == null) {
                paneRoot = SiteUtils.getItemContent(
                        getItem(),
                        ItemType.FUNCTION,
                        ItemType.VARIABLE
                );
            }
            return paneRoot;
        });
    }

    @Override
    public Icon getTabIcon() {
        return Icon.CLASS;
    }

    @Override
    public Icon getAddIcon() {
        return Icon.PLUSCLASS;
    }

}
