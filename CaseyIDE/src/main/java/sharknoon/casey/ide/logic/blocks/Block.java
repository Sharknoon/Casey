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
package sharknoon.casey.ide.logic.blocks;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.geometry.Side;
import sharknoon.casey.ide.logic.ValueHoldable;
import sharknoon.casey.ide.logic.items.Function;
import sharknoon.casey.ide.logic.statements.Statement;
import sharknoon.casey.ide.ui.frames.Frame;
import sharknoon.casey.ide.ui.frames.Frames;
import sharknoon.casey.ide.ui.lines.Line;
import sharknoon.casey.ide.ui.lines.Lines;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * A Body is a part of a function and contains statements
 *
 * @author Josua Frank
 */
public abstract class Block {
    
    //The parent function of this block
    private final Function function;
    //An id for the Frame to uniquely identify them, needed for derialisation
    private final String id;
    //The type of this block
    private final BlockType type;
    //The connections to other blocks
    private final Map<Side, Map<Block, Side>> connections = new HashMap<>();
    //The UI Component of this block
    private Frame<?> frame;
    //The optional Statement of this block
    private ObjectProperty<Statement<?, ?, ?>> statement = new SimpleObjectProperty<>();
    //The optional ValueHoldable (variable or parameter) of this block
    private ObjectProperty<ValueHoldable<?>> variable = new SimpleObjectProperty<>();
    
    Block(Function function, BlockType type) {
        this(function, type, UUID.randomUUID().toString(), new Point2D(-1, -1));
    }
    
    
    Block(Function function, BlockType type, String id, Point2D origin) {
        this.function = function;
        this.id = id == null ? UUID.randomUUID().toString() : id;
        this.type = type;
        this.frame = Frames.createFrame(this, origin);
    }
    
    /**
     * Gets the sides of this blocktype, where there are output dots
     *
     * @return The sides of this blocktype, where there are output dots
     */
    public abstract Side[] initDotOutputSides();
    
    /**
     * Gets the sides of this blocktype, where there are input dots
     *
     * @return The sides of this blocktype, where there are input dots
     */
    public abstract Side[] initDotInputSides();
    
    /**
     * Returns true if this block contains a statement, e.g. a variable, a operator or a method call
     *
     * @return
     */
    public abstract boolean hasStatement();
    
    /**
     * Returns true if this block contains a variable assignment, e.g. the input or the assignment block
     *
     * @return
     */
    public abstract boolean hasVariable();
    
    /**
     * Destroyes this block completely
     */
    public void remove() {
        getFrame().remove();
        function.blocksProperty().remove(this);
    }
    
    public Function getFunction() {
        return function;
    }
    
    public String getId() {
        return id;
    }
    
    public BlockType getType() {
        return type;
    }
    
    public Frame<?> getFrame() {
        return frame;
    }
    
    public Map<Side, Map<Block, Side>> getConnections() {
        return connections;
    }
    
    public void addConnection(Side originSide, Block destinationBlock, Side destinationSide) {
        addConnection(originSide, destinationBlock, destinationSide, true);
    }
    
    public void addConnection(Side originSide, Block destinationBlock, Side destinationSide, boolean addToUI) {
        if (!connections.containsKey(originSide)) {
            connections.put(originSide, new HashMap<>());
        }
        connections.get(originSide).put(destinationBlock, destinationSide);
        if (addToUI) {//In the case where the line comes from the serialisation and not from the ui
            Line line = Lines.createLine(getFrame().getFunctionSite(), getFrame().getOutputDot(originSide).orElse(null));
            line.setEndDot(destinationBlock.getFrame().getInputDot(destinationSide).orElse(null));
        }
    }
    
    public void removeConnection(Side originSide, Block destinationBlock) {
        if (!connections.containsKey(originSide)) {
            return;
        }
        connections.get(originSide).remove(destinationBlock);
    }
    
    public Optional<Statement<?, ?, ?>> getStatement() {
        return Optional.ofNullable(statement.get());
    }
    
    public void setStatement(Statement<?, ?, ?> statement) {
        this.statement.set(statement);
    }
    
    public ObjectProperty<Statement<?, ?, ?>> statementProperty() {
        return statement;
    }
    
    public Optional<ValueHoldable<?>> getVariable() {
        return Optional.ofNullable(variable.get());
    }
    
    public void setVariable(ValueHoldable<?> variable) {
        this.variable.set(variable);
    }
    
    public ObjectProperty<ValueHoldable<?>> variableProperty() {
        return variable;
    }
    
    @Override
    public int hashCode() {
        return id.hashCode();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
    
        Block block = (Block) o;
        
        return id.equals(block.id);
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" + "id=" + id + ", function=" + function + '}';
    }
    
}
