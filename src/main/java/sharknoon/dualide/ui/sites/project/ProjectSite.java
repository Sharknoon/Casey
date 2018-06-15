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
package sharknoon.dualide.ui.sites.project;

import java.util.concurrent.CompletableFuture;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import sharknoon.dualide.logic.items.Project;
import sharknoon.dualide.ui.sites.Site;
import sharknoon.dualide.logic.items.ItemType;
import sharknoon.dualide.ui.misc.Icon;
import sharknoon.dualide.ui.sites.SiteUtils;

/**
 *
 * @author Josua Frank
 */
public class ProjectSite extends Site<Project> {

    private Pane paneRoot;

    public ProjectSite(Project item) {
        super(item);
    }

    @Override
    public CompletableFuture<Node> getTabContentPane() {
        return CompletableFuture.supplyAsync(() -> {
            if (paneRoot == null) {
                paneRoot = SiteUtils.getItemContent(
                        getItem(),
                        ItemType.PACKAGE
                );
            }
            return paneRoot;
        });
    }

    @Override
    public Icon getTabIcon() {
        return Icon.PROJECT;
    }

    @Override
    public Icon getAddIcon() {
        return Icon.PLUSPROJECT;
    }

}
