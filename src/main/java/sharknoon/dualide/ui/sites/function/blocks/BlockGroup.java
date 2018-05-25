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

import java.util.Collection;
import java.util.List;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleExpression;

/**
 * A Group of blocks which can be moved around in the workspace
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
    public DoubleExpression minXExpression() {
        DoubleExpression idendity = Bindings.createDoubleBinding(() -> 0.0);
        return blocks
                .stream()
                .map(Block::minXExpression)
                .reduce(idendity, (t, u) -> (DoubleExpression) Bindings.min(t, u));
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
    public DoubleExpression minYExpression() {
        DoubleExpression idendity = Bindings.createDoubleBinding(() -> 0.0);
        return blocks
                .stream()
                .map(Block::minYExpression)
                .reduce(idendity, (t, u) -> (DoubleExpression) Bindings.min(t, u));
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
    public DoubleExpression maxXExpression() {
        DoubleExpression idendity = Bindings.createDoubleBinding(() -> 0.0);
        return blocks
                .stream()
                .map(Block::maxXExpression)
                .reduce(idendity, (t, u) -> (DoubleExpression) Bindings.min(t, u));
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
    public DoubleExpression maxYExpression() {
        DoubleExpression idendity = Bindings.createDoubleBinding(() -> 0.0);
        return blocks
                .stream()
                .map(Block::maxYExpression)
                .reduce(idendity, (t, u) -> (DoubleExpression) Bindings.min(t, u));
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
