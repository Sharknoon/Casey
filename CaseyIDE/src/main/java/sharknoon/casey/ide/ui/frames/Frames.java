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
package sharknoon.casey.ide.ui.frames;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.geometry.Point2D;
import sharknoon.casey.ide.logic.blocks.Block;
import sharknoon.casey.ide.ui.UISettings;
import sharknoon.casey.ide.ui.frames.frame.*;
import sharknoon.casey.ide.ui.sites.function.FunctionSite;
import sharknoon.casey.ide.utils.settings.Logger;

import java.util.stream.Stream;

/**
 * This is the general class for all frames, it has handy funtions to create new
 * frames and manages the behaviour between the frames
 *
 * @author Josua Frank
 */
public class Frames {
    
    private static final ObservableMap<FunctionSite, ObservableSet<Frame<?>>> FRAMES = FXCollections.observableHashMap();
    private static final ObservableMap<FunctionSite, ObjectProperty<Frame<?>>> MOVING_FRAME = FXCollections.observableHashMap();
    private static final ObservableSet<Frame<?>> EMPTY = FXCollections.emptyObservableSet();
    
    
    /**
     * Creates a new Frame
     *
     * @param block The corresponding block
     * @return The newly created Frame
     */
    public static Frame<?> createFrame(Block block) {
        return createFrame(block, new Point2D(-1, -1));
    }
    
    /**
     * Creates a new Frame
     *
     * @param block  The corresponding block
     * @param origin The origin coordinates of this block in the functionsite
     * @return The newly created Frame
     */
    public static Frame<?> createFrame(Block block, Point2D origin) {
        switch (block.getType()) {
            case ASSIGNMENT:
                return new AssignmentFrame(block, origin);
            case START:
                return new StartFrame(block, origin);
            case CALL:
                return new CallFrame(block, origin);
            case DECISION:
                return new DecisionFrame(block, origin);
            case END:
                return new EndFrame(block, origin);
            case INPUT:
                return new InputFrame(block, origin);
            case OUTPUT:
                return new OutputFrame(block, origin);
            default:
                Logger.error("Could not make Frame to Body of Type " + block.getType());
                return new CallFrame(block, origin);
        }
    }
    
    static void registerFrame(FunctionSite functionSite, Frame<?> frame) {
        if (FRAMES.containsKey(functionSite)) {
            FRAMES.get(functionSite).add(frame);
        } else {
            ObservableSet<Frame<?>> list = FXCollections.observableSet();
            list.addListener((SetChangeListener.Change<? extends Frame<?>> change) -> {
                if (change.wasAdded()) {
                    Logger.debug("Added Frame " + change.getElementAdded().toString());
                } else if (change.wasRemoved()) {
                    Logger.debug("Removed Frame " + change.getElementRemoved().toString());
                }
            });
            list.add(frame);
            FRAMES.put(functionSite, list);
        }
    }
    
    static void unregisterFrame(FunctionSite functionSite, Frame<?> frame) {
        if (FRAMES.containsKey(functionSite)) {
            FRAMES.get(functionSite).remove(frame);
        }
    }
    
    public static void unselectAll(FunctionSite functionSite) {
        if (FRAMES.containsKey(functionSite)) {
            FRAMES.get(functionSite).forEach(Frame::unselect);
        }
    }
    
    static Frame<?> getMovingFrame(FunctionSite functionSite) {
        if (!MOVING_FRAME.containsKey(functionSite)) {
            return null;
        }
        return MOVING_FRAME.get(functionSite).get();
    }
    
    static void setMovingFrame(FunctionSite functionSite, Frame<?> frame) {
        if (MOVING_FRAME.containsKey(functionSite)) {
            MOVING_FRAME.get(functionSite).set(frame);
        } else {
            MOVING_FRAME.put(functionSite, new SimpleObjectProperty<>(frame));
        }
    }
    
    public static ObjectProperty<Frame<?>> movingBlockBinding(FunctionSite functionSite) {
        return MOVING_FRAME.get(functionSite);
    }
    
    public static Stream<Frame<?>> getAllFrames(FunctionSite functionSite) {
        return allFramesObsevable(functionSite).stream();
    }
    
    static Stream<Frame<?>> getSelectedFrames(FunctionSite functionSite) {
        return getAllFrames(functionSite).filter(Frame::isSelected);
    }
    
    private static ObservableSet<Frame<?>> allFramesObsevable(FunctionSite functionSite) {
        return FRAMES.getOrDefault(functionSite, EMPTY);
    }
    
    public static boolean isSpaceFree(Frame<?> frame, double x, double y) {
        boolean isSpaceFree = frame.canMoveTo(x, y);
        return isSpaceFree && isInsideWorkspace(frame, x, y);
    }
    
    private static boolean isInsideWorkspace(Frame<?> b, double x, double y) {
        return !(x < 0 + UISettings.WORKSPACE_PADDING
                || y < 0 + UISettings.WORKSPACE_PADDING
                || x + b.getWidth() > UISettings.WORKSPACE_MAX_X - UISettings.WORKSPACE_PADDING
                || y + b.getHeight() > UISettings.WORKSPACE_MAX_Y - UISettings.WORKSPACE_PADDING);
    }
    
}
