/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sharknoon.dualide.radio.stations;

import net.sourceforge.jaad.Radio;

/**
 *
 * @author frank
 */
public class DUFM {

    private static final String DOMAIN = "http://ns511142.ip-198-27-66.net:8048/stream";
    private static Radio radio;

    public static void start() {
        if (radio == null) {
            radio = Radio.start(DOMAIN);
        } else {
            radio.start();
        }
    }

    public static void stop() {
        if (radio != null) {
            radio.stop();
        }
    }

}
