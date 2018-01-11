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
package sharknoon.dualide.ui;

import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import sharknoon.dualide.logic.Item;

/**
 * This interface is being used to signal, that this can be displayed in the tabpane
 * @author Josua Frank
 */
public interface TabPaneDisplayable {

    /**
     * The Pane of the Tab in the Tabpane
     * @return 
     */
    public Pane getPane();
    
    /**
     * The Name of the Tab in the Tabpane
     * @return 
     */
    public String getName();
    
    /**
     * The Icon of the Tab in the Tabpane
     * @return 
     */
    public Node getIcon();
}
