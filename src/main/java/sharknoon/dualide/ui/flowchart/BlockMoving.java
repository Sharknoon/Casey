package sharknoon.dualide.ui.flowchart;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.shape.Shape;
import sharknoon.dualide.ui.flowchart.blocks.Block;
import sharknoon.dualide.ui.flowchart.blocks.Blocks;

/**
 *
 * @author Josua Frank
 */
public class BlockMoving {

    private final Flowchart flowchart;
    private double startX;
    private double startY;
    private int startGridX;
    private int startGridY;
    Map<Block, Integer> lastGridX = new HashMap<>();
    Map<Block, Integer> lastGridY = new HashMap<>();
    boolean lastDragSwitch = false;
    boolean currentDragSwitch = true;

    public BlockMoving(Flowchart flowchart) {
        this.flowchart = flowchart;
    }

    public void init() {

    }

    public void onMousePressed(Point2D localMouse) {
        lastDragSwitch = !lastDragSwitch;
        Block block = Blocks.getCurrentBlock(flowchart);
        if (block == null) {
            return;
        }
        if (block.isSelected()) {
            Blocks.getSelectedBlocks(flowchart).forEach(b -> {
                b.startX = b.getMinX();
                b.startY = b.getMinY();
            });
        } else {
            block.startX = block.getMinX();
            block.startY = block.getMinY();
        }
        startX = localMouse.getX();
        startY = localMouse.getY();
        startGridX = (int) ((localMouse.getX() - UISettings.paddingInsideWorkSpace) / UISettings.gridSnappingX);
        startGridY = (int) ((localMouse.getY() - UISettings.paddingInsideWorkSpace) / UISettings.gridSnappingY);
    }

    public void onMouseDragged(Point2D localMouse) {
        Block block = Blocks.getCurrentBlock(flowchart);
        if (block == null) {
            return;
        }

        double currentX = localMouse.getX();
        double currentY = localMouse.getY();

        //dragging part
        double deltaX = currentX - startX;
        double deltaY = currentY - startY;

        if (block.isSelected()) {
            Blocks.getSelectedBlocksGroup(flowchart).getBlocks().forEach(b -> {
                b.setMinX(b.startX + deltaX);
                b.setMinY(b.startY + deltaY);
            });
        } else {
            block.setMinX(block.startX + deltaX);
            block.setMinY(block.startY + deltaY);
        }

        //shadow part
        double currentXWithoutPadding = currentX - UISettings.paddingInsideWorkSpace;
        double currentYWithoutPadding = currentY - UISettings.paddingInsideWorkSpace;

        int currentGridX = (int) (currentXWithoutPadding / UISettings.gridSnappingX);
        int currentGridY = (int) (currentYWithoutPadding / UISettings.gridSnappingY);

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
            Collection<Block> blocks = Blocks.getSelectedBlocks(flowchart);
            Map<Block, Double[]> futureShadows = new HashMap<>();
            boolean canMoveInX = true;
            boolean canMoveInY = true;
            for (Block b : blocks) {
                double newX = b.startX + ((currentGridX - startGridX) * UISettings.gridSnappingX);
                double newY = b.startY + ((currentGridY - startGridY) * UISettings.gridSnappingY);
                canMoveInX = canMoveInX ? isXInsideWorkspace(b, newX) : false;
                canMoveInY = canMoveInY ? isYInsideWorkspace(b, newY) : false;
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
            Shape shadow = block.getShadow();
            double newX = block.startX + ((currentGridX - startGridX) * UISettings.gridSnappingX);
            double newY = block.startY + ((currentGridY - startGridY) * UISettings.gridSnappingY);
            boolean isSpaceFree = block.canMoveTo(newX, newY);
            if (isSpaceFree && isXInsideWorkspace(block, newX)) {
                shadow.setTranslateX(newX);
            }
            if (isSpaceFree && isYInsideWorkspace(block, newY)) {
                shadow.setTranslateY(newY);
            }
        }
    }

    public void onMouseReleased() {
        Block block = Blocks.getCurrentBlock(flowchart);
        if (block == null) {
            return;
        }
        if (block.isSelected()) {
            Blocks.getSelectedBlocks(flowchart).forEach(b -> {
                b.setMinXAnimated(b.getShadow().getTranslateX());
                b.setMinYAnimated(b.getShadow().getTranslateY());
            });
        } else {
            block.setMinXAnimated(block.getShadow().getTranslateX());
            block.setMinYAnimated(block.getShadow().getTranslateY());
        }
    }

    private boolean isXInsideWorkspace(Block b, double x) {
        if (x < 0 + UISettings.paddingInsideWorkSpace) {
            return false;
        } else if (x + b.getWidth() > UISettings.maxWorkSpaceX - UISettings.paddingInsideWorkSpace) {
            return false;
        }
        return true;
    }

    private boolean isYInsideWorkspace(Block b, double y) {
        if (y < 0 + UISettings.paddingInsideWorkSpace) {
            return false;
        } else if (y + b.getHeight() > UISettings.maxWorkSpaceY - UISettings.paddingInsideWorkSpace) {
            return false;
        }
        return true;
    }

}
