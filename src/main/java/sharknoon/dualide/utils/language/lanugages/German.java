/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sharknoon.dualide.utils.language.lanugages;

import java.util.Locale;
import sharknoon.dualide.utils.language.Language;
import sharknoon.dualide.utils.language.Word;

/**
 *
 * @author frank
 */
public class German extends Language {

    public German(Locale locale) {
        super(locale);
    }

    {
        add(Word.RUN, "Los!");
    }
}
