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
package sharknoon.casey.ide.ui.sites.function;

import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import sharknoon.casey.ide.ui.UISettings;
import sharknoon.casey.ide.ui.frames.Frames;
import sharknoon.casey.ide.ui.lines.Lines;
import sharknoon.casey.ide.ui.misc.MouseConsumable;
import sharknoon.casey.ide.ui.styles.StyleClasses;

/**
 * @author Josua Frank
 */
public class WorkspaceSelection implements MouseConsumable {
    
    private static Rectangle createSelectionRectangle() {
        var selectionRectangle = new Rectangle();
        selectionRectangle.getStyleClass().add(StyleClasses.rectangleSelection.name());
        selectionRectangle.setVisible(false);
        return selectionRectangle;
    }
    
    private final FunctionSite functionSite;
    private final Rectangle selectionRectangle;
    private double startX;
    private double startY;
    private boolean firstRun = true;
    private boolean isSelecting = false;
    
    public WorkspaceSelection(FunctionSite functionSite) {
        this.functionSite = functionSite;
        this.selectionRectangle = createSelectionRectangle();
    }
    
    @Override
    public void onMousePressed(MouseEvent event) {
        if (firstRun) {
            this.functionSite.getLogicSite().addInFront(selectionRectangle);
            firstRun = false;
        }
        if (event.getX() < 0 || event.getX() > UISettings.WORKSPACE_MAX_X
                || event.getY() < 0 || event.getY() > UISettings.WORKSPACE_MAX_Y) {
            return;
        }
        selectionRectangle.setVisible(true);
        startX = event.getX();
        startY = event.getY();
        
        selectionRectangle.setTranslateX(startX);
        selectionRectangle.setTranslateY(startY);
        selectionRectangle.toFront();
        isSelecting = true;
    }
    
    @Override
    public void onMouseDragged(MouseEvent event) {
        var currentX = event.getX();
        var currentY = event.getY();
        var width = currentX - startX;
        var hight = currentY - startY;
        if (startX + width > UISettings.WORKSPACE_MAX_X) {
            width = UISettings.WORKSPACE_MAX_X - startX;
        } else if (startX + width < 0) {
            width = -startX;
        }
        if (startY + hight > UISettings.WORKSPACE_MAX_Y) {
            hight = UISettings.WORKSPACE_MAX_Y - startY;
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
        
        final var finalWidth = width;
        final var finalHight = hight;
    
        Frames.getAllFrames(functionSite).forEach(b -> {
            if (b.getMinX() > translateX
                    && b.getMinY() > translateY
                    && b.getMinX() + b.getWidth() < translateX + finalWidth
                    && b.getMinY() + b.getHeight() < translateY + finalHight) {
                b.select();
            } else {
                b.unselect();
            }
        });
        
        Lines.getAllLines(functionSite).forEach(l -> {
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
    
    @Override
    public void onMouseReleased(MouseEvent event) {
        selectionRectangle.setVisible(false);
        selectionRectangle.setWidth(0);
        selectionRectangle.setHeight(0);
        isSelecting = false;
    }
    
    public boolean isSelecting() {
        return isSelecting;
    }
}
