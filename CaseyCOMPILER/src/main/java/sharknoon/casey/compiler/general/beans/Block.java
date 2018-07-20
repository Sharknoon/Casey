package sharknoon.casey.compiler.general.beans;/*
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

import javafx.geometry.Side;

import java.util.Map;
import java.util.UUID;

public class Block {
    
    //The id of this block
    public UUID blockid;
    
    //The coordinates of this block
    public double blockX;
    public double blockY;
    
    //The type of this block
    public BlockType blocktype;
    
    //The connections from this to other blocks
    public Map<Side, Map<UUID, Side>> blockconnections;
    
    //The statements of this block
    public BlockContent blockcontent;
    
    @Override
    public String toString() {
        return "Block{" +
                "blockid=" + blockid +
                ", blockX=" + blockX +
                ", blockY=" + blockY +
                ", blocktype=" + blocktype +
                ", blockconnections= size=" + (blockconnections != null ? blockconnections.size() : 0) +
                ", blockcontent=" + blockcontent +
                '}';
    }
    
    public enum BlockType {
        START,
        END,
        DECISION,
        CALL,
        ASSIGNMENT,
        INPUT,
        OUTPUT
    }
    
    public static class BlockContent {
        
        //The statement of the block
        public Statement statement;
        
        //The variable the statement is assigned to or the input is being stored into
        public String variable;
        
        @Override
        public String toString() {
            return "BlockContent{" +
                    "statement=" + statement +
                    ", variable='" + variable + '\'' +
                    '}';
        }
    }
}
