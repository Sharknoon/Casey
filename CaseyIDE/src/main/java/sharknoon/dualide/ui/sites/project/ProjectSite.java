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

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import sharknoon.dualide.logic.items.ItemType;
import sharknoon.dualide.logic.items.Project;
import sharknoon.dualide.ui.misc.Icon;
import sharknoon.dualide.ui.sites.Site;
import sharknoon.dualide.ui.sites.SiteUtils;

import java.util.concurrent.CompletableFuture;

/**
 *
 * @author Josua Frank
 */
public class ProjectSite extends Site<Project> {

    private Pane paneRoot;

    private static final ObjectProperty<Icon> icon = new SimpleObjectProperty<>(Icon.PROJECT);

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
    public ObjectProperty<Icon> tabIconProperty() {
        return icon;
    }

    @Override
    public Icon getAddIcon() {
        return Icon.PLUSPROJECT;
    }

}
