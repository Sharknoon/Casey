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
package sharknoon.dualide.logic;

import sharknoon.dualide.ui.sites.Site;
import sharknoon.dualide.ui.sites.package_.PackageSite;

/**
 *
 * @author Josua Frank
 */
public class Package extends Item<Package, Item<? extends Item, ? extends Item, Package>, Item<? extends Item, Package, ? extends Item>> {

    public Package(Item<? extends Item, ? extends Item, Package> parent, String name) {
        super(parent, name);
    }

    @Override
    protected Site<Package> createSite() {
        return new PackageSite(this);
    }

}
