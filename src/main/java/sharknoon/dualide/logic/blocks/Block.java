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
package sharknoon.dualide.logic.blocks;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.geometry.Side;
import sharknoon.dualide.logic.ValueHoldable;
import sharknoon.dualide.logic.items.Function;
import sharknoon.dualide.logic.statements.Statement;
import sharknoon.dualide.logic.types.Type;
import sharknoon.dualide.ui.frames.Frame;
import sharknoon.dualide.ui.frames.Frames;

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
    //The UI Component of this block
    private final Frame<?> frame;
    //The connections to other blocks
    private final Map<Side, Map<Block, Side>> connections = new HashMap<>();
    //The optional Statement of this block
    private ObjectProperty<Statement<Type, Type, Type>> statement = new SimpleObjectProperty<>();
    //The optional ValueHoldable (variable or parameter) of this block
    private ObjectProperty<ValueHoldable<Type>> variable = new SimpleObjectProperty<>();
    
    
    public Block(Function function, BlockType type) {
        this(function, type, UUID.randomUUID().toString(), new Point2D(-1, -1));
    }
    
    public Block(Function function, BlockType type, String id, Point2D origin) {
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
        frame.remove();
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
        if (!connections.containsKey(originSide)) {
            connections.put(originSide, new HashMap<>());
        }
        connections.get(originSide).put(destinationBlock, destinationSide);
    }
    
    public void removeConnection(Side originSide, Block destinationBlock) {
        if (!connections.containsKey(originSide)) {
            return;
        }
        connections.get(originSide).remove(destinationBlock);
    }
    
    public Optional<Statement<Type, Type, Type>> getStatement() {
        return Optional.ofNullable(statement.get());
    }
    
    public void setStatement(Statement<Type, Type, Type> statement) {
        this.statement.set(statement);
    }
    
    public ObjectProperty<Statement<Type, Type, Type>> statementProperty() {
        return statement;
    }
    
    public Optional<ValueHoldable<Type>> getVariable() {
        return Optional.ofNullable(variable.get());
    }
    
    public void setVariable(ValueHoldable<Type> variable) {
        this.variable.set(variable);
    }
    
    public ObjectProperty<ValueHoldable<Type>> variableProperty() {
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
