/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sharknoon.dualide.utils.settings;

import java.util.Optional;

/**
 *
 * @author frank
 */
public class FirstRunInit {

    public static void init() {
        Optional<String> firstRun = Props.get("firstrun");
        if (firstRun.isPresent()) {
            if (firstRun.get().equals("true")) {
                init0();
                Props.set("firstrun", "false");
            }
        } else {
            Props.set("firstrun", "false");
        }
    }

    private static void init0() {
        String language = System.getProperty("user.language");
        String country = System.getProperty("user.country");
        if (language != null) {
            Props.set("language", language);
        }
        if (country != null) {
            Props.set("country", country);
        }
    }

}
