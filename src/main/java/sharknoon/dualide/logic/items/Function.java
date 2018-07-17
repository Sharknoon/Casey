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
package sharknoon.dualide.logic.items;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.geometry.Point2D;
import javafx.geometry.Side;
import sharknoon.dualide.logic.ValueReturnable;
import sharknoon.dualide.logic.blocks.Block;
import sharknoon.dualide.logic.blocks.BlockType;
import sharknoon.dualide.logic.blocks.Blocks;
import sharknoon.dualide.logic.types.PrimitiveType;
import sharknoon.dualide.logic.types.Type;
import sharknoon.dualide.ui.lines.Lines;
import sharknoon.dualide.ui.sites.function.FunctionSite;
import sharknoon.dualide.utils.settings.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Josua Frank
 */
public class Function extends Item<Function, Item<? extends Item, ? extends Item, Function>, Item<? extends Item, Function, ? extends Item>> implements ValueReturnable<Type> {
    
    private static final String RETURNTYPE = "returntype";
    //Needed for the usages check in the class
    private static final ObservableMap<Type, List<Function>> ALL_RETURN_TYPES = FXCollections.observableHashMap();
    private static final String BLOCKS = "blocks";
    private static final String BLOCK_ID = "blockid";
    private static final String BLOCK_X = "blockX";
    private static final String BLOCK_Y = "blockY";
    private static final String BLOCK_TYPE = "blocktype";
    private static final String BLOCK_CONNECTIONS = "blockconnections";
    private static final String BLOCK_CONTENT = "blockcontent";
    private static final String BLOCK_STATEMENT = "statement";
    private static final String BLOCK_VARIABLE = "variable";
    
    static ObservableMap<Type, List<Function>> getAllReturnTypes() {
        return ALL_RETURN_TYPES;
    }
    
    private final ObjectProperty<Type> returnType = new SimpleObjectProperty<>(PrimitiveType.VOID);
    private final ObservableList<Block> blocks = FXCollections.observableArrayList();
    
    protected Function(Item<? extends Item, ? extends Item, Function> parent, String name) {
        superInit(parent, name);
        returnType.addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !ALL_RETURN_TYPES.containsKey(newValue)) {
                ALL_RETURN_TYPES.put(newValue, new ArrayList<>());
            }
            ALL_RETURN_TYPES.get(newValue).add(this);
            if (oldValue != null && ALL_RETURN_TYPES.containsKey(oldValue)) {
                ALL_RETURN_TYPES.get(oldValue).remove(this);
            }
        });
    }
    
    @Override
    public Map<String, JsonNode> getAdditionalProperties() {
        Map<String, JsonNode> map = super.getAdditionalProperties();
        String typeString = returnType.get() != null
                ? returnType.get().fullNameProperty().get()
                : "";
        map.put(RETURNTYPE, TextNode.valueOf(typeString));
        
        var blocksNode = new ArrayNode(JsonNodeFactory.instance);
        blocks.forEach(b -> {
            var block = new ObjectNode(JsonNodeFactory.instance);
            //General block stuff
            block.put(BLOCK_ID, b.getId());
            block.put(BLOCK_X, b.getFrame().getMinX());
            block.put(BLOCK_Y, b.getFrame().getMinY());
            block.put(BLOCK_TYPE, b.getClass().getSimpleName().toUpperCase());
        
            //Connections to other blocks
            var blockConnections = new ObjectNode(JsonNodeFactory.instance);
            b.getConnections().forEach((originSide, destinations) -> {
                var destinationConnections = new ObjectNode(JsonNodeFactory.instance);
                destinations.forEach((d, destSide) -> {
                    destinationConnections.put(d.getId(), destSide.name());
                });
                blockConnections.set(originSide.name(), destinationConnections);
            });
            block.set(BLOCK_CONNECTIONS, blockConnections);
        
            //Blockcontent
            var content = new ObjectNode(JsonNodeFactory.instance);
            b.getVariable().ifPresent(vh -> {
                content.put(BLOCK_VARIABLE, vh.toItem().getFullName());
            });
            b.getStatement().ifPresent(s -> {
                var statement = new ObjectNode(JsonNodeFactory.instance);
                statement.setAll(s.getAdditionalProperties());
                content.set(BLOCK_STATEMENT, statement);
            });
            block.set(BLOCK_CONTENT, content);
            
            blocksNode.add(block);
        });
        map.put(BLOCKS, blocksNode);
        
        return map;
    }
    
    @Override
    public void setAdditionalProperties(Map<String, JsonNode> properties) {
        final Map<String, Block> blockIDs = new HashMap<>();
        //<blockid<side<id,side>>
        final Map<String, Map<Side, Map<String, Side>>> blockConections = new HashMap<>();
        properties.forEach((key, value) -> {
            switch (key) {
                case RETURNTYPE:
                    Type.valueOf(value.asText()).ifPresentOrElse(returnType::set, () -> returnType.set(PrimitiveType.VOID));
                    break;
                case BLOCKS:
                    ArrayNode blocks = (ArrayNode) value;
                    for (JsonNode b : blocks) {
                        var blockNode = (ObjectNode) b;
    
                        var id = blockNode.get(BLOCK_ID).asText("");
                        var x = blockNode.get(BLOCK_X).asInt(0);
                        var y = blockNode.get(BLOCK_Y).asInt(0);
                        var type = blockNode.get(BLOCK_TYPE).asText("");
                        var connections = (ObjectNode) blockNode.get(BLOCK_CONNECTIONS);
    
                        var block = Blocks.createBlock(BlockType.forName(type), this, id, new Point2D(x, y));
                        blockIDs.put(id, block);
    
                        connections.fields().forEachRemaining(e -> {
                            var side = e.getKey();
                            var lines = (ObjectNode) e.getValue();
                            if (!blockConections.containsKey(id)) {
                                blockConections.put(id, new HashMap<>());
                            }
                            var sidesMap = blockConections.get(id);
                            var s = Side.valueOf(side);
                            if (!sidesMap.containsKey(s)) {
                                sidesMap.put(s, new HashMap<>());
                            }
                            var linesMap = sidesMap.get(s);
                            lines.fields().forEachRemaining(e2 -> {
                                try {
                                    linesMap.put(e2.getKey(), Side.valueOf(e2.getValue().asText()));
                                } catch (Exception ex) {
                                    Logger.error("Could not load line: " + ex);
                                }
                            });
                        });
                    }
                    break;
            }
        });
        blockConections.forEach((originBlockId, originSides) -> {
            originSides.forEach((originSide, lines) -> lines.forEach((destinationBlockId, destinationSide) -> {
                try {
                    blockIDs
                            .get(originBlockId)
                            .getFrame()
                            .getOutputDot(originSide)
                            .ifPresent(originDot -> {
                                var destinationBlock = blockIDs.get(destinationBlockId);
                                destinationBlock.getFrame().getInputDot(destinationSide).ifPresent(destinationDot -> {
                                    var line = Lines.createLine((FunctionSite) getSite(), originDot);
                                    line.setEndDot(destinationDot);
                                });
                            });
                } catch (Exception e) {
                    Logger.error("Could not load line", e);
                }
            }));
        });
    }
    
    @Override
    public Type getReturnType() {
        return returnTypeProperty().get();
    }
    
    public ObjectProperty<Type> returnTypeProperty() {
        return returnType;
    }
    
    public ObservableList<Block> blocksProperty() {
        return blocks;
    }
    
    public boolean isInClass() {
        return isIn(ItemType.CLASS);
    }
    
    public boolean isInPackage() {
        return isIn(ItemType.PACKAGE);
    }
    
}
