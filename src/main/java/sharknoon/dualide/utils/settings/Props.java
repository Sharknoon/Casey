/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sharknoon.dualide.utils.settings;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Properties;

/**
 *
 * @author frank
 */
public class Props {

    private static final Properties PROPS = new Properties();
    private static final String PATH = "props/properties.props";
    private static boolean initialized = false;

    static {
        //Optional<InputStream> props = FileUtils.getFile(PATH);
        /*if (props.isPresent()) {
            try {
                PROPS.load(Files.newInputStream(props.get()));
                initialized = true;
            } catch (IOException ex) {
                Logger.error("Could not load properties file", ex);
            }
        } else {
            Logger.error("Could not find properties file");
        }*/
    }

    public static Optional<String> get(String name) {
        String prop = PROPS.getProperty(name);
        return Optional.ofNullable(prop);
    }

    public static void set(String name, String value) {
        PROPS.setProperty(name, value);
        onChange();
    }

    private static void onChange() {
        if (initialized) {
            /*Optional<Path> path = FileUtils.createAndGetFile(PATH);
            if (path.isPresent()) {
                try {
                    PROPS.store(Files.newOutputStream(path.get()), null);
                } catch (IOException ex) {
                    Logger.error("Could not save Properties", ex);
                }
            } else {
                Logger.error("Could not save Properties file, file not found");
            }*/
        }
    }
}
