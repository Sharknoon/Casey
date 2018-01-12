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
package sharknoon.dualide.ui.menubar;

import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import java.util.Locale;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import sharknoon.dualide.utils.language.Language;
import sharknoon.dualide.utils.language.Word;

/**
 *
 * @author Josua Frank
 */
public class MenuBar {

    public static void initOptionsMenu(Menu menuOptions) {
        Language.setCustom(Word.MENUBAR_OPTIONS_TEXT, s -> menuOptions.setText(s));
        menuOptions.setGraphic(GlyphsDude.createIcon(FontAwesomeIcon.COG));

        Menu menuLanguage = new Menu();
        Language.setCustom(Word.MENUBAR_OPTIONS_LANGUAGE_TEXT, s -> menuLanguage.setText(s));
        menuLanguage.setGraphic(GlyphsDude.createIcon(FontAwesomeIcon.LANGUAGE));
        menuOptions.getItems().add(menuLanguage);

        Language.getAllLanguages().forEach((loc, lang) -> {
            MenuItem menuItemLanguage = new MenuItem();
            Language.setCustom(
                    e -> loc.getDisplayLanguage(Language.getLanguage().getLocale()),
                    s -> menuItemLanguage.setText(s));
            //menuItemLanguage.setGraphic(value);
            menuItemLanguage.setId(loc.getLanguage());
            menuItemLanguage.setOnAction((event) -> {
                Locale l = new Locale(menuItemLanguage.getId());
                Language.changeLanguage(Language.forLocale(l));
            });
            menuLanguage.getItems().add(menuItemLanguage);
        });

    }
}
