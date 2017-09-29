/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sharknoon.dualide.utils.settings;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Properties;

/**
 *
 * @author frank
 */
public class Props {

    private static Properties PROPS;
    private static final String PATH = "props/props.properties";

    private static void init() {
        Optional<Path> propertiesFile = FileUtils.getFile(PATH, true);
        if (propertiesFile.isPresent()) {
            try {
                PROPS = new Properties();
                PROPS.load(Files.newInputStream(propertiesFile.get()));
            } catch (IllegalArgumentException | UnsupportedOperationException | IOException | SecurityException ex) {
                Logger.error("Could not load Properties file", ex);
                PROPS = new Properties();
            }
        } else {
            Logger.error("Could not find Properties file");
            PROPS = new Properties();
        }
    }

    public static Optional<String> get(String name) {
        if (PROPS == null) {
            init();
        }
        String prop = PROPS.getProperty(name);
        return Optional.ofNullable(prop);
    }

    public static void set(String name, String value) {
        if (PROPS == null) {
            init();
        }
        PROPS.setProperty(name, value);
        onChange();
    }

    private static void onChange() {
        if (PROPS == null) {
            init();
        }
        Path propertiesFile = FileUtils.createAndGetFile(PATH, true);
        try {
            PROPS.store(Files.newOutputStream(propertiesFile), null);
        } catch (IOException ex) {
            Logger.error("Could not save Properties", ex);
        }
    }
}
