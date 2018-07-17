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
package sharknoon.dualide.ui.lines;

import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import sharknoon.dualide.ui.misc.MouseConsumable;
import sharknoon.dualide.ui.sites.function.FunctionSite;

/**
 *
 * @author Josua Frank
 */
public class LineDrawing implements MouseConsumable {

    private final FunctionSite functionSite;

    public LineDrawing(FunctionSite functionSite) {
        this.functionSite = functionSite;
    }

    @Override
    public void onMouseMoved(MouseEvent event) {
          var line = Lines.getDrawingLine(functionSite);
        Point2D local = new Point2D(event.getX(), event.getY());
        line.onMouseMoved(local);

    }

}
