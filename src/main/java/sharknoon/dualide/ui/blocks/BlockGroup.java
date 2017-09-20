package sharknoon.dualide.ui.blocks;

import java.util.Collection;
import java.util.List;

/**
 *
 * @author Josua Frank
 */
public final class BlockGroup implements Moveable {

    private final List<Block> blocks;

    public BlockGroup(List<Block> blocks) {
        this.blocks = blocks;
    }

    @Override
    public double getMinX() {
        return blocks
                .stream()
                .mapToDouble(Block::getMinX)
                .min()
                .orElse(0);
    }

    @Override
    public double getMinY() {
        return blocks
                .stream()
                .mapToDouble(Block::getMinY)
                .min()
                .orElse(0);
    }

    @Override
    public double getMaxX() {
        return blocks
                .stream()
                .mapToDouble(Block::getMaxX)
                .max()
                .orElse(0);
    }

    @Override
    public double getMaxY() {
        return blocks
                .stream()
                .mapToDouble(Block::getMaxY)
                .max()
                .orElse(0);
    }

    @Override
    public void setMinX(double x) {
        blocks.stream().forEach(b -> b.setMinX(x));
    }

    @Override
    public void setMinY(double y) {
        blocks.stream().forEach(b -> b.setMinY(y));
    }

    /**
     *
     * @param x delta values!
     * @param y delta values!
     * @return
     */
    @Override
    public boolean canMoveTo(double x, double y) {
        return blocks
                .stream()
                .allMatch(b -> b.canMoveTo(b.getMinX() + x, b.getMinY() + y, false));
    }

    public Collection<Block> getBlocks() {
        return blocks;
    }

}
