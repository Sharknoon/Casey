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
package sharknoon.dualide;

import javafx.application.Application;
import sharknoon.dualide.ui.MainApplication;
import sharknoon.dualide.utils.settings.Logger;
import sharknoon.dualide.utils.settings.Ressources;

/**
 *
 * @author Josua Frank
 */
public class Main {

    public static void main(String[] args) {
        System.setProperty("javafx.animation.framerate", "144");
        Logger.setLogLevel(Logger.LogLevel.DEBUG);
        //Ressources.resetRessources(true);
        Application.launch(MainApplication.class);
    }
}
