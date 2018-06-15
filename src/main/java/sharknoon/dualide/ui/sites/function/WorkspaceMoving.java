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
package sharknoon.dualide.ui.sites.function;

import javafx.scene.input.MouseEvent;
import sharknoon.dualide.ui.misc.MouseConsumable;

/**
 *
 * @author Josua Frank
 */
public class WorkspaceMoving implements MouseConsumable {

    private final FunctionSite functionSite;
    private double startMouseX = 0;
    private double startMouseY = 0;
    private double startTranslationX = 0;
    private double startTranslationY = 0;

    public WorkspaceMoving(FunctionSite functionSite) {
        this.functionSite = functionSite;
    }

    @Override
    public void onMousePressed(MouseEvent event) {
        startMouseX = event.getSceneX();
        startMouseY = event.getSceneY();
          var root = functionSite.getLogicSite().getRoot();
        startTranslationX = root.getTranslateX();
        startTranslationY = root.getTranslateY();
    }

    @Override
    public void onMouseDragged(MouseEvent event) {
          var deltaX = event.getSceneX() - startMouseX;
          var deltaY = event.getSceneY() - startMouseY;
          var root = functionSite.getLogicSite().getRoot();
        root.setTranslateX(startTranslationX + deltaX);
        root.setTranslateY(startTranslationY + deltaY);
    }

}
