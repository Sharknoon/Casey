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
package sharknoon.casey.ide;

import javafx.application.Application;
import sharknoon.casey.ide.ui.MainApplication;
import sharknoon.casey.ide.utils.settings.Logger;

/**
 *
 * @author Josua Frank
 */
public class Main {

    public static void main(String[] args) {
        System.setProperty("javafx.animation.framerate", "144");
        //System.setProperty("javafx.preloader", SplashScreen.class.getLanguageDependentName());
        Logger.setLogLevel(Logger.LogLevel.DEBUG);
        //Resources.resetResources(true);
        Application.launch(MainApplication.class);
    }
}
