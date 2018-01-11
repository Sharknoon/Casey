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
package sharknoon.dualide.ui.function;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import sharknoon.dualide.ui.function.blocks.Blocks;
import sharknoon.dualide.ui.function.lines.Lines;

/**
 *
 * @author Josua Frank
 */
public class Selection {

    private final Function flowchart;
    private final Rectangle selectionRectangle = new Rectangle();
    private double startX = 0;
    private double startY = 0;

    public Selection(Function controller) {
        this.flowchart = controller;
    }

    public void init() {
        createSelectionRectangle();
    }

    private void createSelectionRectangle() {
        selectionRectangle.setFill(Color.LIGHTBLUE);
        selectionRectangle.setOpacity(0.4);
        selectionRectangle.setStrokeWidth(1);
        selectionRectangle.setStroke(Color.BLUE);
        selectionRectangle.setVisible(false);
        flowchart.add(selectionRectangle);
    }

    public void onMousePressed(Point2D localCoordinates) {
        if (localCoordinates.getX() < 0 || localCoordinates.getX() > UISettings.maxWorkSpaceX
                || localCoordinates.getY() < 0 || localCoordinates.getY() > UISettings.maxWorkSpaceY) {
            return;
        }
        selectionRectangle.setVisible(true);
        startX = localCoordinates.getX();
        startY = localCoordinates.getY();

        selectionRectangle.setTranslateX(startX);
        selectionRectangle.setTranslateY(startY);
    }

    public void onMouseDragged(Point2D localCoordinates) {
        double currentX = localCoordinates.getX();
        double currentY = localCoordinates.getY();
        double width = currentX - startX;
        double hight = currentY - startY;
        if (startX + width > UISettings.maxWorkSpaceX) {
            width = UISettings.maxWorkSpaceX - startX;
        } else if (startX + width < 0) {
            width = -startX;
        }
        if (startY + hight > UISettings.maxWorkSpaceY) {
            hight = UISettings.maxWorkSpaceY - startY;
        } else if (startY + hight < 0) {
            hight = -startY;
        }

        double translateX;
        double translateY;
        if (width > 0) {
            translateX = startX;
        } else {
            if (currentX < 0) {
                currentX = 0;
            }
            translateX = currentX;
            width = -width;
        }
        if (hight > 0) {
            translateY = startY;
        } else {
            if (currentY < 0) {
                currentY = 0;
            }
            translateY = currentY;
            hight = -hight;
        }

        selectionRectangle.setTranslateX(translateX);
        selectionRectangle.setTranslateY(translateY);
        selectionRectangle.setWidth(width);
        selectionRectangle.setHeight(hight);

        final double finalWidth = width;
        final double finalHight = hight;

        Blocks.getAllBlocks(flowchart).stream().forEach(b -> {
            if (b.getMinX() > translateX
                    && b.getMinY() > translateY
                    && b.getMinX() + b.getWidth() < translateX + finalWidth
                    && b.getMinY() + b.getHeight() < translateY + finalHight) {
                b.select();
            } else {
                b.unselect();
            }
        });

        Lines.getAllLines(flowchart).stream().forEach(l -> {
            if (l.getMinX() > translateX
                    && l.getMinY() > translateY
                    && l.getMinX() + l.getWidth() < translateX + finalWidth
                    && l.getMinY() + l.getHeight() < translateY + finalHight) {
                l.select();
            } else {
                l.unselect();
            }
        });
    }

    public void onMouseReleased(Point2D localCoordinates) {
        selectionRectangle.setVisible(false);
        selectionRectangle.setWidth(0);
        selectionRectangle.setHeight(0);
        if (!Blocks.isMouseOverBlock() && startX == localCoordinates.getX() && startY == localCoordinates.getY()) {
            Blocks.unselectAll(flowchart);
        }
    }

}
