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
package sharknoon.dualide.ui.sites.function.blocks;

import javafx.application.Platform;
import javafx.scene.input.MouseEvent;
import sharknoon.dualide.ui.misc.MouseConsumable;
import sharknoon.dualide.ui.sites.function.FunctionSite;
import sharknoon.dualide.ui.sites.function.UISettings;
import sharknoon.dualide.ui.sites.function.lines.Lines;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Josua Frank
 */
public class BlockMoving implements MouseConsumable {

    private final FunctionSite functionSite;
    private double startX;
    private double startY;
    private int startGridX;
    private int startGridY;
    Map<Block, Integer> lastGridX = new HashMap<>();
    Map<Block, Integer> lastGridY = new HashMap<>();
    boolean lastDragSwitch = false;
    boolean currentDragSwitch = true;
    public static final Map<FunctionSite, Boolean> IS_DRAGGING = new HashMap<>();

    public static boolean isDragging(FunctionSite fs) {
        return IS_DRAGGING.getOrDefault(fs, false);
    }

    public BlockMoving(FunctionSite functionSite) {
        this.functionSite = functionSite;
    }

    @Override
    public void onMousePressed(MouseEvent event) {
        var localMouseX = event.getX();
        var localMouseY = event.getY();
        IS_DRAGGING.put(functionSite, true);
        lastDragSwitch = !lastDragSwitch;
        var block = Blocks.getMovingBlock(functionSite);
        if (block == null) {
            return;
        }
        if (block.isSelected()) {
            Blocks.getSelectedBlocks(functionSite).forEach(b -> {
                b.startX = b.getMinX();
                b.startY = b.getMinY();
            });
        } else {
            block.startX = block.getMinX();
            block.startY = block.getMinY();
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
        Block block = Blocks.getMovingBlock(functionSite);
        if (block == null || Lines.isLineDrawing(functionSite)) {
            return;
        }

        double currentX = event.getX();
        double currentY = event.getY();

        //dragging part
        double deltaX = currentX - startX;
        double deltaY = currentY - startY;

        if (block.isSelected()) {
            Blocks.getSelectedBlocks(functionSite).collect(Collectors.toList()).stream().forEach(b -> {
                b.setMinX(b.startX + deltaX);
                b.setMinY(b.startY + deltaY);
            });
        } else {
            block.setMinX(block.startX + deltaX);
            block.setMinY(block.startY + deltaY);
        }

        //shadow part
        double currentXWithoutPadding = currentX - UISettings.WORKSPACE_PADDING;
        double currentYWithoutPadding = currentY - UISettings.WORKSPACE_PADDING;

        int currentGridX = (int) (currentXWithoutPadding / UISettings.BLOCK_GRID_SNAPPING_X);
        int currentGridY = (int) (currentYWithoutPadding / UISettings.BLOCK_GRID_SNAPPING_Y);

        if (currentDragSwitch != lastDragSwitch || !lastGridX.containsKey(block)) {
            lastGridX.put(block, currentGridX);
            lastDragSwitch = currentDragSwitch;
        }
        if (currentDragSwitch != lastDragSwitch || !lastGridY.containsKey(block)) {
            lastGridY.put(block, currentGridY);
            lastDragSwitch = currentDragSwitch;
        }

        int lastMouseGridX = lastGridX.get(block);
        int lastMouseGridY = lastGridY.get(block);

        if (currentGridX == lastMouseGridX && currentGridY == lastMouseGridY) {
            return;
        }
        lastGridX.put(block, currentGridX);
        lastGridY.put(block, currentGridY);

        if (block.isSelected()) {
            Stream<Block> blocks = Blocks.getSelectedBlocks(functionSite);
            Map<Block, Double[]> futureShadows = new HashMap<>();
            boolean canMoveInX = true;
            boolean canMoveInY = true;
            for (Iterator<Block> it = blocks.iterator(); it.hasNext(); ) {
                Block b = it.next();
                double newX = b.startX + ((currentGridX - startGridX) * UISettings.BLOCK_GRID_SNAPPING_X);
                double newY = b.startY + ((currentGridY - startGridY) * UISettings.BLOCK_GRID_SNAPPING_Y);
                canMoveInX = canMoveInX && isXInsideWorkspace(b, newX);
                canMoveInY = canMoveInY && isYInsideWorkspace(b, newY);
                if (!b.canMoveTo(newX, newY, false)) {
                    return;
                }
                futureShadows.put(b, new Double[]{newX, newY});
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
            var shadow = block.getShadow();
            var newX = block.startX + ((currentGridX - startGridX) * UISettings.BLOCK_GRID_SNAPPING_X);
            var newY = block.startY + ((currentGridY - startGridY) * UISettings.BLOCK_GRID_SNAPPING_Y);
            var isSpaceFree = block.canMoveTo(newX, newY);
            if (isSpaceFree && isXInsideWorkspace(block, newX)) {
                shadow.setTranslateX(newX);
            }
            if (isSpaceFree && isYInsideWorkspace(block, newY)) {
                shadow.setTranslateY(newY);
            }
        }
    }

    @Override
    public void onMouseReleased(MouseEvent event) {
        IS_DRAGGING.put(functionSite, false);
        var block = Blocks.getMovingBlock(functionSite);
        if (block == null) {
            return;
        }
        if (block.isSelected()) {
            Blocks.getSelectedBlocks(functionSite).forEach(b -> {
                b.setMinXAnimated(b.getShadow().getTranslateX());
                b.setMinYAnimated(b.getShadow().getTranslateY());
            });
        } else {
            block.setMinXAnimated(block.getShadow().getTranslateX());
            block.setMinYAnimated(block.getShadow().getTranslateY());
        }
    }


    private boolean isXInsideWorkspace(Block b, double x) {
        if (x < 0 + UISettings.WORKSPACE_PADDING) {
            return false;
        } else return !(x + b.getWidth() > UISettings.WORKSPACE_MAX_X - UISettings.WORKSPACE_PADDING);
    }

    private boolean isYInsideWorkspace(Block b, double y) {
        if (y < 0 + UISettings.WORKSPACE_PADDING) {
            return false;
        } else return !(y + b.getHeight() > UISettings.WORKSPACE_MAX_Y - UISettings.WORKSPACE_PADDING);
    }

}
