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

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.scene.shape.Shape;
import sharknoon.dualide.ui.sites.function.FunctionSite;
import sharknoon.dualide.ui.sites.function.UISettings;
import sharknoon.dualide.ui.sites.function.blocks.block.*;
import sharknoon.dualide.utils.settings.Logger;

import java.util.Set;
import java.util.stream.Stream;

/**
 * This is the general class for all blocks, it has handy funtions to create new
 * blocks and manages the behaviour between the blocks
 *
 * @author Josua Frank
 */
public class Blocks {

    private static final ObservableMap<FunctionSite, ObservableSet<Block<?>>> BLOCKS = FXCollections.observableHashMap();
    private static final ObservableMap<FunctionSite, BooleanProperty> MOUSE_OVER_BLOCK_PROPERTY = FXCollections.observableHashMap();
    private static final ObservableMap<FunctionSite, ObjectProperty<Block<?>>> MOVING_BLOCK = FXCollections.observableHashMap();
    private static final ObservableSet<Block<?>> EMPTY = FXCollections.observableSet(Set.of());

    /**
     * Creates a new Block
     *
     * @param functionSite The general site of the function as reference
     * @param type         The type of the block
     * @return The newly created Block
     */
    public static Block<?> createBlock(FunctionSite functionSite, BlockType type) {
        return createBlock(functionSite, type, null);
    }

    /**
     * Creates a new Block
     *
     * @param functionSite The general site of the function as reference
     * @param type         The type of the block
     * @param id           OPTIONAL Id (just needed for parsing) can be null
     * @return The newly created Block
     */
    public static Block<?> createBlock(FunctionSite functionSite, BlockType type, String id) {
        switch (type) {
            case START:
                return new Start(functionSite, id);
            case END:
                return new End(functionSite, id);
            case DECISION:
                return new Decision(functionSite, id);
            case ASSIGNMENT:
                return new Assignment(functionSite, id);
            case CALL:
                return new Call(functionSite, id);
            case INPUT:
                return new Input(functionSite, id);
            case OUTPUT:
                return new Output(functionSite, id);
            default:
                return new Call(functionSite, id);
        }
    }

    static void registerBlock(FunctionSite functionSite, Block<? extends Shape> block) {
        if (BLOCKS.containsKey(functionSite)) {
            BLOCKS.get(functionSite).add(block);
        } else {
            ObservableSet<Block<?>> list = FXCollections.observableSet();
            list.addListener((SetChangeListener.Change<? extends Block> change) -> {
                if (change.wasAdded()) {
                    Logger.debug("Added Block " + change.getElementAdded().toString());
                } else if (change.wasRemoved()) {
                    Logger.debug("Removed Block " + change.getElementRemoved().toString());
                }
            });
            list.add(block);
            BLOCKS.put(functionSite, list);
        }
    }

    static void unregisterBlock(FunctionSite functionSite, Block<? extends Shape> block) {
        if (BLOCKS.containsKey(functionSite)) {
            BLOCKS.get(functionSite).remove(block);
        }
    }

    public static void unselectAll(FunctionSite functionSite) {
        if (BLOCKS.containsKey(functionSite)) {
            BLOCKS.get(functionSite).forEach(Block::unselect);
        }
    }

    static Block<?> getMovingBlock(FunctionSite functionSite) {
        if (!MOVING_BLOCK.containsKey(functionSite)) {
            return null;
        }
        return MOVING_BLOCK.get(functionSite).get();
    }

    static void setMovingBlock(FunctionSite functionSite, Block<? extends Shape> block) {
        if (MOVING_BLOCK.containsKey(functionSite)) {
            MOVING_BLOCK.get(functionSite).set(block);
        } else {
            MOVING_BLOCK.put(functionSite, new SimpleObjectProperty<>(block));
        }
    }

    public static ObjectProperty<Block<?>> movingBlockBinding(FunctionSite functionSite) {
        return MOVING_BLOCK.get(functionSite);
    }

    public static Stream<Block<?>> getAllBlocks(FunctionSite functionSite) {
        return allBlocksObsevable(functionSite).stream();
    }

    static Stream<Block<?>> getSelectedBlocks(FunctionSite functionSite) {
        return getAllBlocks(functionSite).filter(Block::isSelected);
    }

    private static ObservableSet<Block<?>> allBlocksObsevable(FunctionSite functionSite) {
        return BLOCKS.getOrDefault(functionSite, EMPTY);
    }

    public static boolean isSpaceFree(Block<? extends javafx.scene.shape.Shape> block, double x, double y) {
        boolean isSpaceFree = block.canMoveTo(x, y);
        return isSpaceFree && isInsideWorkspace(block, x, y);
    }

    private static boolean isInsideWorkspace(Block<? extends javafx.scene.shape.Shape> b, double x, double y) {
        return !(x < 0 + UISettings.WORKSPACE_PADDING
                || y < 0 + UISettings.WORKSPACE_PADDING
                || x + b.getWidth() > UISettings.WORKSPACE_MAX_X - UISettings.WORKSPACE_PADDING
                || y + b.getHeight() > UISettings.WORKSPACE_MAX_Y - UISettings.WORKSPACE_PADDING);
    }

    public static boolean isMouseOverBlock(FunctionSite functionSite) {
        return hoverOverBlockProperty(functionSite).get();
    }

    static BooleanProperty hoverOverBlockProperty(FunctionSite functionSite) {
        if (!MOUSE_OVER_BLOCK_PROPERTY.containsKey(functionSite)) {
            MOUSE_OVER_BLOCK_PROPERTY.put(functionSite, new SimpleBooleanProperty());
        }
        return MOUSE_OVER_BLOCK_PROPERTY.get(functionSite);
    }

}
