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
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.geometry.Point2D;
import javafx.geometry.Side;
import sharknoon.dualide.logic.Returnable;
import sharknoon.dualide.logic.types.Type;
import sharknoon.dualide.ui.sites.function.FunctionSite;
import sharknoon.dualide.ui.sites.function.blocks.Block;
import sharknoon.dualide.ui.sites.function.blocks.BlockType;
import sharknoon.dualide.ui.sites.function.blocks.Blocks;
import sharknoon.dualide.ui.sites.function.lines.Lines;
import sharknoon.dualide.utils.settings.Logger;

import java.util.*;

/**
 * @author Josua Frank
 */
public class Function extends Item<Function, Item<? extends Item, ? extends Item, Function>, Variable> implements Returnable {

    private static final String RETURNTYPE = "returntype";
    private static final String PARAMETERS = "parameters";
    //Needed for the usages check in the class
    private static final ObservableMap<Type, List<Function>> ALL_RETURN_TYPES = FXCollections.observableHashMap();
    private static final ObservableSet<Parameter> ALL_PARAMETERS = FXCollections.observableSet();
    private static final String PARAMETERNAME = "name";
    private static final String PARAMETERTYPE = "type";
    private static final String PARAMETERCOMMENTS = "comments";
    private static final String BLOCKS = "blocks";
    private static final String BLOCK_ID = "blockid";
    private static final String BLOCK_X = "blockX";
    private static final String BLOCK_Y = "blockY";
    private static final String BLOCK_TYPE = "blocktype";
    private static final String BLOCK_CONNECTIONS = "blockconnections";
    private final ObjectProperty<Type> returnType = new SimpleObjectProperty<>();
    private final ListProperty<Parameter> parameters = new SimpleListProperty<>(FXCollections.observableArrayList());

    protected Function(Item<? extends Item, ? extends Item, Function> parent, String name) {
        super(parent, name);
        parameters.addListener((ListChangeListener<Parameter>) c -> {
            while (c.next()) {
                ALL_PARAMETERS.removeAll(c.getRemoved());
                ALL_PARAMETERS.addAll(c.getAddedSubList());
            }
        });
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

    static ObservableMap<Type, List<Function>> getAllReturnTypes() {
        return ALL_RETURN_TYPES;
    }

    static ObservableSet<Parameter> getAllParameters() {
        return ALL_PARAMETERS;
    }

    @Override
    public Map<String, JsonNode> getAdditionalProperties() {
        Map<String, JsonNode> map = super.getAdditionalProperties();
        String typeString = returnType.get() != null
                ? returnType.get().fullNameProperty().get()
                : "";
        map.put(RETURNTYPE, TextNode.valueOf(typeString));

        ArrayNode parametersNode = new ArrayNode(JsonNodeFactory.instance);
        parameters.forEach(p -> {
            ObjectNode parameterNode = new ObjectNode(JsonNodeFactory.instance);
            parameterNode.put(PARAMETERNAME, p.getName());
            parameterNode.put(PARAMETERTYPE, p.getType().fullNameProperty().get());
            parameterNode.put(PARAMETERCOMMENTS, p.getComments());
            parametersNode.add(parameterNode);
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
        final Map<String, Block<?>> blockIDs = new HashMap<>();
        //<blockid<side<id,side>>
        final Map<String, Map<Side, Map<String, Side>>> blockConections = new HashMap<>();
        properties.forEach((key, value) -> {
            switch (key) {
                case RETURNTYPE:
                    Type.valueOf(value.asText()).ifPresent(returnType::set);
                    break;
                case PARAMETERS:
                    ArrayNode pars = (ArrayNode) value;
                    for (JsonNode par : pars) {
                        ObjectNode parNode = (ObjectNode) par;
                        String name = parNode.get(PARAMETERNAME).asText("");
                        Optional<Type> type = Type.valueOf(parNode.get(PARAMETERTYPE).asText(""));
                        String comments = parNode.get(PARAMETERCOMMENTS).asText("");
                        if (!type.isPresent()) {
                            Logger.error("Could not determine function parameter type: " + par.get(PARAMETERTYPE));
                            break;
                        }
                        Parameter parameter = new Parameter(name, type.get(), comments, this);
                        parameters.add(parameter);
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
            originSides.forEach((originSide, lines) -> lines.forEach((destinationBlockId, destinationSide) -> {
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
            }));
        });
    }

    public ObjectProperty<Type> returnTypeProperty() {
        return returnType;
    }

    @Override
    public Type getReturnType() {
        return returnTypeProperty().get();
    }

    public ListProperty<Parameter> parametersProperty() {
        return parameters;
    }

    public ObservableList<Parameter> getParameters() {
        return parameters.get();
    }

    public static class Parameter {
        private final StringProperty nameProperty = new SimpleStringProperty();
        private final ObjectProperty<Type> typeProperty = new SimpleObjectProperty<>();
        private final StringProperty commentsProperty = new SimpleStringProperty();

        private final ObjectProperty<Function> functionProperty = new SimpleObjectProperty<>();

        public Parameter(String name, Type type, String comments, Function function) {
            nameProperty.set(name);
            typeProperty.set(type);
            commentsProperty.set(comments);
            functionProperty.set(function);
        }

        public Type getType() {
            return typeProperty.get();
        }

        public ObjectProperty<Type> typeProperty() {
            return typeProperty;
        }

        public String getName() {
            return nameProperty.get();
        }

        public StringProperty nameProperty() {
            return nameProperty;
        }

        public String getComments() {
            return commentsProperty.get();
        }

        public StringProperty commentsProperty() {
            return commentsProperty;
        }

        public Function getFunction() {
            return functionProperty.get();
        }

        public ObjectProperty<Function> functionProperty() {
            return functionProperty;
        }
    }

}
