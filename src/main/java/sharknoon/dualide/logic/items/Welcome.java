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
package sharknoon.dualide.logic.items;

import sharknoon.dualide.ui.sites.Site;
import sharknoon.dualide.ui.sites.welcome.WelcomeSite;
import sharknoon.dualide.utils.language.Language;
import sharknoon.dualide.utils.language.Word;

/**
 *
 * @author Josua Frank
 */
public class Welcome extends Item<Welcome, Item<? extends Item, ? extends Item, Welcome>, Project> {

    private Welcome() {
        super();
    }

    protected Welcome(Item<? extends Item, ? extends Item, Welcome> parent, String name) {
        super(parent, name);
    }

    private static Welcome welcome;

    public static Welcome getWelcome() {
        if (welcome == null) {
            welcome = Item.createItem(ItemType.WELCOME, null, "");
            Language.setCustom(Word.WELCOME_SITE_TAB_TITLE, n -> welcome.setName(n));
        }
        return welcome;
    }
 

}
