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
package sharknoon.dualide.ui.flowchart;

import java.util.Optional;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import sharknoon.dualide.ui.flowchart.blocks.Block;
import sharknoon.dualide.ui.flowchart.blocks.Blocks;
import sharknoon.dualide.ui.flowchart.dots.Dot;
import sharknoon.dualide.ui.flowchart.dots.Dots;
import sharknoon.dualide.ui.flowchart.lines.Line;
import sharknoon.dualide.ui.flowchart.lines.Lines;

/**
 *
 * @author Josua Frank
 */
public class LineDrawing {

    private final Flowchart flowchart;

    public LineDrawing(Flowchart flowchart) {
        this.flowchart = flowchart;
    }

    private boolean vertical = true;

    public void onMouseMoved(Point2D local) {
        double x = local.getX();
        double y = local.getY();

        Line line = Lines.getDrawingLine();

        double lastCornerX = line.getLastCornerX();
        double lastCornerY = line.getLastCornerY();

        vertical = Math.abs(y - lastCornerY) > Math.abs(x - lastCornerX);

        double newCornerX, newCornerY;
        if (vertical) {
            newCornerY = y - (y % UISettings.lineGridSnappingY);
            if (y % UISettings.lineGridSnappingY > UISettings.lineGridSnappingY / 2) {
                newCornerY += UISettings.lineGridSnappingY;
            }
            if (line.canExtendTo(newCornerY, vertical)) {
                line.extend(newCornerY, vertical);
            }
        } else {
            newCornerX = x - (x % UISettings.lineGridSnappingX);
            if (x % UISettings.lineGridSnappingX > UISettings.lineGridSnappingX / 2) {
                newCornerX += UISettings.lineGridSnappingX;
            }
            if (line.canExtendTo(newCornerX, vertical)) {
                line.extend(newCornerX, vertical);
            }
        }

    }

}
