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

import javafx.geometry.Point2D;
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
    private boolean negative = false;
    private double lastPointX = -1;
    private double lastPointY = -1;
    private boolean allowed = true;

    public void onMouseMoved(Point2D local) {
        double x = local.getX();
        double y = local.getY();

        Line line = Lines.getDrawingLine();

        double oldCornerX = line.getLastCornerX();
        double oldCornerY = line.getLastCornerY();

        vertical = Math.abs(y - oldCornerY) > Math.abs(x - oldCornerX);

        double lineGridSnapping = vertical
                ? UISettings.lineGridSnappingY
                : UISettings.lineGridSnappingX;

        double oldCorner = vertical ? oldCornerY : oldCornerX;
        double newPoint;
        if (vertical) {
            newPoint = y - (y % lineGridSnapping);
            if (y % lineGridSnapping > lineGridSnapping / 2) {
                newPoint += lineGridSnapping;
            }
        } else {
            newPoint = x - (x % lineGridSnapping);
            if (x % lineGridSnapping > lineGridSnapping / 2) {
                newPoint += lineGridSnapping;
            }
        }
        if (lastPointX < 0) {
            lastPointX = oldCornerX;
        }
        if (lastPointY < 0) {
            lastPointY = oldCornerY;
        }
        if (newPoint == (vertical ? lastPointY : lastPointX)) {
            return;
        }
        //System.out.println("corner: " + oldCorner + ", lastpoint: " + (vertical ? lastPointY : lastPointX) + ", newpoint: " + newPoint);
        negative = newPoint < oldCorner;

        double nextPoint = oldCorner;
        line.removePointsSinceLastCorner();
        if (nextPoint != newPoint) {
            while (true) {
                if (negative) {
                    nextPoint -= lineGridSnapping;
                    if (nextPoint < newPoint) {
                        return;
                    }
                } else {
                    nextPoint += lineGridSnapping;
                    if (nextPoint > newPoint) {
                        return;
                    }
                }
                if (vertical) {
                    lastPointY = nextPoint;
                } else {
                    lastPointX = nextPoint;
                }
                if (line.canExtendTo(vertical ? oldCornerX : nextPoint, vertical ? nextPoint : oldCornerY)) {
                    allowed = true;
                    line.extend(vertical ? oldCornerX : nextPoint, vertical ? nextPoint : oldCornerY);
                } else {
                    allowed = false;
                    if (vertical) {
                        lastPointY = newPoint;
                    } else {
                        lastPointX = newPoint;
                    }
                    return;
                }
            }
        } else {
            if (vertical) {
                lastPointY = nextPoint;
            } else {
                lastPointX = nextPoint;
            }
            if (line.canExtendTo(vertical ? oldCornerX : nextPoint, vertical ? nextPoint : oldCornerY)) {
                allowed = true;
                line.extend(vertical ? oldCornerX : nextPoint, vertical ? nextPoint : oldCornerY);
            } else {
                allowed = false;
            }
        }

    }

    public void onMouseClicked(Point2D local) {
        Line line = Lines.getDrawingLine();
        if (allowed) {
            line.addCorner();
        } else {
            line.destroy();
        }
    }

}
