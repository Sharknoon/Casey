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

import sharknoon.dualide.ui.sites.function.blocks.block.Start;
import sharknoon.dualide.ui.sites.function.blocks.block.Assignment;
import sharknoon.dualide.ui.sites.function.blocks.block.End;
import sharknoon.dualide.ui.sites.function.blocks.block.Decision;
import java.util.stream.Stream;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;
import sharknoon.dualide.ui.sites.function.FunctionSite;
import sharknoon.dualide.ui.sites.function.UISettings;
import sharknoon.dualide.ui.sites.function.blocks.block.Call;
import sharknoon.dualide.ui.sites.function.blocks.block.Input;
import sharknoon.dualide.ui.sites.function.blocks.block.Output;

/**
 * This is the general class for all blocks, it has handy funtions to create new
 * blocks and manages the behaviour between the blocks
 *
 * @author Josua Frank
 */
public class Blocks {

    public static Block createStartBlock(FunctionSite functionSite) {
        return new Start(functionSite);
    }

    public static Block createEndBlock(FunctionSite functionSite) {
        return new End(functionSite);
    }

    public static Block createDecisionBlock(FunctionSite functionSite) {
        return new Decision(functionSite);
    }

    public static Block createAssignmentBlock(FunctionSite functionSite) {
        return new Assignment(functionSite);
    }
    
    public static Block createCallBlock(FunctionSite functionSite) {
        return new Call(functionSite);
    }
    
    public static Block createInputBlock(FunctionSite functionSite) {
        return new Input(functionSite);
    }
    
    public static Block createOutputBlock(FunctionSite functionSite) {
        return new Output(functionSite);
    }

    private static final ObservableMap<FunctionSite, ObservableSet<Block>> BLOCKS = FXCollections.observableHashMap();
    private static final ObservableMap<FunctionSite, BooleanProperty> MOUSE_OVER_BLOCK_PROPERTY = FXCollections.observableHashMap();
    private static final ObservableMap<FunctionSite, ObjectProperty<Block>> MOVING_BLOCK = FXCollections.observableHashMap();
    private static final ObservableSet<Block> EMPTY = FXCollections.observableSet();

    static void registerBlock(FunctionSite functionSite, Block block) {
        if (BLOCKS.containsKey(functionSite)) {
            BLOCKS.get(functionSite).add(block);
        } else {
            ObservableSet<Block> list = FXCollections.observableSet();
            list.add(block);
            BLOCKS.put(functionSite, list);
        }
    }

    static void unregisterBlock(FunctionSite functionSite, Block block) {
        if (BLOCKS.containsKey(functionSite)) {
            BLOCKS.get(functionSite).remove(block);
        }
    }

    public static void unselectAll(FunctionSite functionSite) {
        if (BLOCKS.containsKey(functionSite)) {
            BLOCKS.get(functionSite).forEach(Block::unselect);
        }
    }

    public static Block getMovingBlock(FunctionSite functionSite) {
        if (!MOVING_BLOCK.containsKey(functionSite)) {
            return null;
        }
        return MOVING_BLOCK.get(functionSite).get();
    }

    public static void setMovingBlock(FunctionSite functionSite, Block block) {
        if (MOVING_BLOCK.containsKey(functionSite)) {
            MOVING_BLOCK.get(functionSite).set(block);
        } else {
            MOVING_BLOCK.put(functionSite, new SimpleObjectProperty<>(block));
        }
    }

    public static ObjectProperty<Block> movingBlockBinding(FunctionSite functionSite) {
        return MOVING_BLOCK.get(functionSite);
    }

    public static Stream<Block> getAllBlocks(FunctionSite functionSite) {
        return allBlocksObsevable(functionSite).parallelStream();
    }

    public static Stream<Block> getSelectedBlocks(FunctionSite functionSite) {
        return getAllBlocks(functionSite).filter(Block::isSelected);
    }

    public static ObservableSet<Block> allBlocksObsevable(FunctionSite functionSite) {
        return BLOCKS.getOrDefault(functionSite, EMPTY);
    }

    public static boolean isSpaceFree(Block block, double x, double y) {
        boolean isSpaceFree = block.canMoveTo(x, y);
        return isSpaceFree && isInsideWorkspace(block, x, y);
    }

    public static boolean isInsideWorkspace(Block b, double x, double y) {
        return !(x < 0 + UISettings.WORKSPACE_PADDING
                || y < 0 + UISettings.WORKSPACE_PADDING
                || x + b.getWidth() > UISettings.WORKSPACE_MAX_X - UISettings.WORKSPACE_PADDING
                || y + b.getHeight() > UISettings.WORKSPACE_MAX_Y - UISettings.WORKSPACE_PADDING);
    }

    public static boolean isMouseOverBlock(FunctionSite functionSite) {
        return hoverOverBlockProperty(functionSite).get();
    }

    public static BooleanProperty hoverOverBlockProperty(FunctionSite functionSite) {
        if (!MOUSE_OVER_BLOCK_PROPERTY.containsKey(functionSite)) {
            MOUSE_OVER_BLOCK_PROPERTY.put(functionSite, new SimpleBooleanProperty());
        }
        return MOUSE_OVER_BLOCK_PROPERTY.get(functionSite);
    }

    public static boolean isDraggingBlock(FunctionSite aThis) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
