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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javafx.beans.property.MapProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Point2D;
import javafx.geometry.Side;
import sharknoon.dualide.logic.Returnable;
import sharknoon.dualide.logic.types.Type;
import sharknoon.dualide.ui.sites.function.FunctionSite;
import sharknoon.dualide.ui.sites.function.blocks.Block;
import sharknoon.dualide.ui.sites.function.blocks.BlockType;
import sharknoon.dualide.ui.sites.function.blocks.Blocks;
import sharknoon.dualide.ui.sites.function.lines.Lines;
import sharknoon.dualide.utils.math.Pairing;
import sharknoon.dualide.utils.settings.Logger;

/**
 *
 * @author Josua Frank
 */
public class Function extends Item<Function, Item<? extends Item, ? extends Item, Function>, Variable> implements Returnable {

    private final ObjectProperty<Type> returnTypeString = new SimpleObjectProperty<>();
    private static final String RETURNTYPE = "returntype";

    private final MapProperty<String, Type> parameters = new SimpleMapProperty<>(FXCollections.observableHashMap());
    private static final String PARAMETERS = "parameters";

    private static final String BLOCKS = "blocks";
    private static final String BLOCK_ID = "blockid";
    private static final String BLOCK_X = "blockX";
    private static final String BLOCK_Y = "blockY";
    private static final String BLOCK_TYPE = "blocktype";
    private static final String BLOCK_CONNECTIONS = "blockconnections";

    protected Function(Item<? extends Item, ? extends Item, Function> parent, String name) {
        super(parent, name);
    }

    @Override
    public Map<String, JsonNode> getAdditionalProperties() {
        Map<String, JsonNode> map = super.getAdditionalProperties();
        String typeString = returnTypeString.get() != null
                ? returnTypeString.get().getFullName().get()
                : "";
        map.put(RETURNTYPE, TextNode.valueOf(typeString));

        ObjectNode parametersNode = new ObjectNode(JsonNodeFactory.instance);
        parameters.forEach((s, t) -> {
            parametersNode.put(s, t.getFullName().get());
        });
        map.put(PARAMETERS, parametersNode);

          var blocksNode = new ArrayNode(JsonNodeFactory.instance);
        Blocks.getAllBlocks((FunctionSite) getSite()).forEach(b -> {
              var block = new ObjectNode(JsonNodeFactory.instance);
            block.put(BLOCK_ID, b.getId());
            block.put(BLOCK_X, b.getMinX());
            block.put(BLOCK_Y, b.getMinY());
            block.put(BLOCK_TYPE, b.getClass().getSimpleName().toUpperCase());

              var dots = new ObjectNode(JsonNodeFactory.instance);
            b.getOutputDots().forEach(d -> {
                  var lines = new ObjectNode(JsonNodeFactory.instance);
                d.getLines().forEach(l -> {
                      var inputDot = l.getInputDot();
                    if (inputDot != null) {//Is the case when during the creation of a line the window is being closed
                          var side = inputDot.getSide();
                          var endblock = inputDot.getBlock();
                        lines.put(endblock.getId(), side.name());
                    }
                });
                if (lines.size() > 0) {
                    dots.set(d.getSide().name(), lines);
                }
            });
            block.set(BLOCK_CONNECTIONS, dots);
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
                    Type.valueOf(value.asText()).ifPresent(returnTypeString::set);
                    break;
                case PARAMETERS:
                    ObjectNode pars = (ObjectNode) value;
                    for (Iterator<Map.Entry<String, JsonNode>> it = pars.fields(); it.hasNext();) {
                        Map.Entry<String, JsonNode> par = it.next();
                        Type.valueOf(par.getValue().asText()).ifPresent(v -> parameters.put(par.getKey(), v));
                    }
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

                          var fs = (FunctionSite) getSite();
                          var block = fs.getLogicSite().addBlock(
                                BlockType.forName(type),
                                new Point2D(x, y),
                                id
                        );
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
            originSides.forEach((originSide, lines) -> {
                lines.forEach((destinationBlockId, destinationSide) -> {
                    try {
                        blockIDs
                                .get(originBlockId)
                                .getOutputDot(originSide)
                                .ifPresent(originDot -> {
                                      var destinationBlock = blockIDs.get(destinationBlockId);
                                    destinationBlock.getInputDot(destinationSide).ifPresent(destinationDot -> {
                                          var line = Lines.createLine((FunctionSite) getSite(), originDot);
                                        line.setEndDot(destinationDot);
                                    });
                                });
                    } catch (Exception e) {
                        Logger.error("Could not load line", e);
                    }
                });
            });
        });
    }

    public ObjectProperty<Type> returnTypeProperty() {
        return returnTypeString;
    }

    @Override
    public Type getReturnType() {
        return returnTypeProperty().get();
    }

}
