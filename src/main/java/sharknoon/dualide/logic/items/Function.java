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
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.MapProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import sharknoon.dualide.logic.Returnable;
import sharknoon.dualide.logic.types.PrimitiveType;
import sharknoon.dualide.logic.types.Type;

/**
 *
 * @author Josua Frank
 */
public class Function extends Item<Function, Item<? extends Item, ? extends Item, Function>, Variable> implements Returnable {

    private final ObjectProperty<Type> returnType = new SimpleObjectProperty<>();
    private static final String RETURNTYPE = "returntype";

    private final MapProperty<String, Type> parameters = new SimpleMapProperty<>(FXCollections.observableHashMap());
    private static final String PARAMETERS = "parameters";

    protected Function(Item<? extends Item, ? extends Item, Function> parent, String name) {
        super(parent, name);
    }

    @Override
    public Map<String, JsonNode> getAdditionalProperties() {
        Map<String, JsonNode> map = super.getAdditionalProperties();
        String typeString = returnType.get() != null
                ? returnType.get().getFullName().get()
                : "";
        map.put(RETURNTYPE, TextNode.valueOf(typeString));

        ObjectNode object = new ObjectNode(JsonNodeFactory.instance);
        parameters.forEach((s, t) -> {
            object.put(s, t.getFullName().get());
        });
        map.put(PARAMETERS, object);
        return map;
    }

    @Override
    public void setAdditionalProperties(Map<String, JsonNode> properties) {
        properties.forEach((key, value) -> {
            switch (key) {
                case RETURNTYPE:
                    Type.valueOf(value.asText()).ifPresent(returnType::set);
                    break;
                case PARAMETERS:
                    ObjectNode pars = (ObjectNode) value;
                    for (Iterator<Map.Entry<String, JsonNode>> it = pars.fields(); it.hasNext();) {
                        Map.Entry<String, JsonNode> par = it.next();
                        Type.valueOf(par.getValue().asText()).ifPresent(v -> parameters.put(par.getKey(), v));
                    }
                    break;
            }
        });
    }

    public ObjectProperty<Type> returnTypeProperty() {
        return returnType;
    }

    @Override
    public Type getReturnType() {
        return returnTypeProperty().get();
    }

}
