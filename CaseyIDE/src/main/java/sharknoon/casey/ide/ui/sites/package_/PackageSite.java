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
package sharknoon.casey.ide.ui.sites.package_;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import sharknoon.casey.ide.logic.items.ItemType;
import sharknoon.casey.ide.logic.items.Package;
import sharknoon.casey.ide.ui.misc.Icon;
import sharknoon.casey.ide.ui.sites.Site;
import sharknoon.casey.ide.ui.sites.SiteUtils;

import java.util.concurrent.CompletableFuture;

/**
 *
 * @author Josua Frank
 */
public class PackageSite extends Site<Package> {
    
    private Pane paneRoot;

    private static final ObjectProperty<Icon> icon = new SimpleObjectProperty<>(Icon.PACKAGE);

    public PackageSite(Package item) {
        super(item);
    }

    @Override
    public CompletableFuture<Node> getTabContentPane() {
        return CompletableFuture.supplyAsync(() -> {
            if (paneRoot == null) {
                paneRoot = SiteUtils.getItemContent(
                        getItem(),
                        ItemType.PACKAGE,
                        ItemType.CLASS,
                        ItemType.FUNCTION,
                        ItemType.VARIABLE
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
        return Icon.PLUSPACKAGE;
    }

}
