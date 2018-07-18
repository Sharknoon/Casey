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
package sharknoon.dualide.ui.frames;

import javafx.application.Platform;
import javafx.scene.input.MouseEvent;
import sharknoon.dualide.ui.UISettings;
import sharknoon.dualide.ui.lines.Lines;
import sharknoon.dualide.ui.misc.MouseConsumable;
import sharknoon.dualide.ui.sites.function.FunctionSite;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Josua Frank
 */
public class FrameMoving implements MouseConsumable {

    private final FunctionSite functionSite;
    private double startX;
    private double startY;
    private int startGridX;
    private int startGridY;
    Map<Frame, Integer> lastGridX = new HashMap<>();
    Map<Frame, Integer> lastGridY = new HashMap<>();
    boolean lastDragSwitch = false;
    boolean currentDragSwitch = true;
    public static final Map<FunctionSite, Boolean> IS_DRAGGING = new HashMap<>();

    public static boolean isDragging(FunctionSite fs) {
        return IS_DRAGGING.getOrDefault(fs, false);
    }
    
    public FrameMoving(FunctionSite functionSite) {
        this.functionSite = functionSite;
    }

    @Override
    public void onMousePressed(MouseEvent event) {
        var localMouseX = event.getX();
        var localMouseY = event.getY();
        IS_DRAGGING.put(functionSite, true);
        lastDragSwitch = !lastDragSwitch;
        var frame = Frames.getMovingFrame(functionSite);
        if (frame == null) {
            return;
        }
        if (frame.isSelected()) {
            Frames.getSelectedFrames(functionSite).forEach(b -> {
                b.startX = b.getMinX();
                b.startY = b.getMinY();
            });
        } else {
            frame.startX = frame.getMinX();
            frame.startY = frame.getMinY();
        }
        startX = localMouseX;
        startY = localMouseY;
        startGridX = (int) ((localMouseX - UISettings.WORKSPACE_PADDING) / UISettings.BLOCK_GRID_SNAPPING_X);
        startGridY = (int) ((localMouseY - UISettings.WORKSPACE_PADDING) / UISettings.BLOCK_GRID_SNAPPING_Y);
    }

    @Override
    public void onMouseDragged(MouseEvent event) {
        if (!Platform.isFxApplicationThread()) {
            throw new IllegalArgumentException("Wrong thread!: " + Thread.currentThread().getName());
        }
        if (!IS_DRAGGING.getOrDefault(functionSite, false)) {
            return;
        }
        Frame frame = Frames.getMovingFrame(functionSite);
        if (frame == null || Lines.isLineDrawing(functionSite)) {
            return;
        }

        double currentX = event.getX();
        double currentY = event.getY();

        //dragging part
        double deltaX = currentX - startX;
        double deltaY = currentY - startY;
    
        if (frame.isSelected()) {
            Frames.getSelectedFrames(functionSite).collect(Collectors.toList()).stream().forEach(b -> {
                b.setMinX(b.startX + deltaX);
                b.setMinY(b.startY + deltaY);
            });
        } else {
            frame.setMinX(frame.startX + deltaX);
            frame.setMinY(frame.startY + deltaY);
        }

        //shadow part
        double currentXWithoutPadding = currentX - UISettings.WORKSPACE_PADDING;
        double currentYWithoutPadding = currentY - UISettings.WORKSPACE_PADDING;

        int currentGridX = (int) (currentXWithoutPadding / UISettings.BLOCK_GRID_SNAPPING_X);
        int currentGridY = (int) (currentYWithoutPadding / UISettings.BLOCK_GRID_SNAPPING_Y);
    
        if (currentDragSwitch != lastDragSwitch || !lastGridX.containsKey(frame)) {
            lastGridX.put(frame, currentGridX);
            lastDragSwitch = currentDragSwitch;
        }
        if (currentDragSwitch != lastDragSwitch || !lastGridY.containsKey(frame)) {
            lastGridY.put(frame, currentGridY);
            lastDragSwitch = currentDragSwitch;
        }
    
        int lastMouseGridX = lastGridX.get(frame);
        int lastMouseGridY = lastGridY.get(frame);

        if (currentGridX == lastMouseGridX && currentGridY == lastMouseGridY) {
            return;
        }
        lastGridX.put(frame, currentGridX);
        lastGridY.put(frame, currentGridY);
    
        if (frame.isSelected()) {
            Stream<Frame<?>> frames = Frames.getSelectedFrames(functionSite);
            Map<Frame<?>, Double[]> futureShadows = new HashMap<>();
            boolean canMoveInX = true;
            boolean canMoveInY = true;
            for (Iterator<Frame<?>> it = frames.iterator(); it.hasNext(); ) {
                Frame<?> f = it.next();
                double newX = f.startX + ((currentGridX - startGridX) * UISettings.BLOCK_GRID_SNAPPING_X);
                double newY = f.startY + ((currentGridY - startGridY) * UISettings.BLOCK_GRID_SNAPPING_Y);
                canMoveInX = canMoveInX && isXInsideWorkspace(f, newX);
                canMoveInY = canMoveInY && isYInsideWorkspace(f, newY);
                if (!f.canMoveTo(newX, newY, false)) {
                    return;
                }
                futureShadows.put(f, new Double[]{newX, newY});
            }
            boolean finalMoveX = canMoveInX;
            boolean finalMoveY = canMoveInY;
            futureShadows.forEach((b, c) -> {
                if (finalMoveX) {
                    b.getShadow().setTranslateX(c[0]);
                }
                if (finalMoveY) {
                    b.getShadow().setTranslateY(c[1]);
                }
            });
        } else {
            var shadow = frame.getShadow();
            var newX = frame.startX + ((currentGridX - startGridX) * UISettings.BLOCK_GRID_SNAPPING_X);
            var newY = frame.startY + ((currentGridY - startGridY) * UISettings.BLOCK_GRID_SNAPPING_Y);
            var isSpaceFree = frame.canMoveTo(newX, newY);
            if (isSpaceFree && isXInsideWorkspace(frame, newX)) {
                shadow.setTranslateX(newX);
            }
            if (isSpaceFree && isYInsideWorkspace(frame, newY)) {
                shadow.setTranslateY(newY);
            }
        }
    }

    @Override
    public void onMouseReleased(MouseEvent event) {
        IS_DRAGGING.put(functionSite, false);
        var frame = Frames.getMovingFrame(functionSite);
        if (frame == null) {
            return;
        }
        if (frame.isSelected()) {
            Frames.getSelectedFrames(functionSite).forEach(b -> {
                b.setMinXAnimated(b.getShadow().getTranslateX());
                b.setMinYAnimated(b.getShadow().getTranslateY());
            });
        } else {
            frame.setMinXAnimated(frame.getShadow().getTranslateX());
            frame.setMinYAnimated(frame.getShadow().getTranslateY());
        }
    }
    
    
    private boolean isXInsideWorkspace(Frame b, double x) {
        if (x < 0 + UISettings.WORKSPACE_PADDING) {
            return false;
        } else return !(x + b.getWidth() > UISettings.WORKSPACE_MAX_X - UISettings.WORKSPACE_PADDING);
    }
    
    private boolean isYInsideWorkspace(Frame b, double y) {
        if (y < 0 + UISettings.WORKSPACE_PADDING) {
            return false;
        } else return !(y + b.getHeight() > UISettings.WORKSPACE_MAX_Y - UISettings.WORKSPACE_PADDING);
    }

}
