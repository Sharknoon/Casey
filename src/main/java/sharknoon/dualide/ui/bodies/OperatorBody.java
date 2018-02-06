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
package sharknoon.dualide.ui.bodies;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import sharknoon.dualide.logic.statements.Statement;
import sharknoon.dualide.logic.statements.operations.Operator;
import sharknoon.dualide.logic.statements.values.Value;
import sharknoon.dualide.logic.statements.values.ValueType;
import sharknoon.dualide.ui.misc.Icon;
import sharknoon.dualide.ui.misc.Icons;

/**
 *
 * @author Josua Frank
 */
public class OperatorBody extends Body<Operator> {

    public static OperatorBody createOperatorBody(Operator operator) {
        return new OperatorBody(operator);
    }

    public OperatorBody(Operator operator) {
        super(operator);
        Node content = createContentNode(operator);
        setContent(content);
    }

    private static final int DEFAULT_MARGIN = 5;
    private final Map<Integer, Body> PLACEHOLDERS = new HashMap<>();

    private Node createContentNode(Operator<Value, Value> operator) {
        HBox hBoxContent = new HBox();
        hBoxContent.setPrefSize(0, 0);
        hBoxContent.setAlignment(Pos.CENTER_LEFT);
        for (int i = 0; i < Math.max(operator.getMinimumParameterAmount(), operator.getParameters().size()); i++) {
            if (operator.getParameterIndexMap().containsKey(i)) {
                Statement<Value, Value, Value> parameter = operator.getParameterIndexMap().get(i);
                Body pb = parameter.getBody();
                hBoxContent.getChildren().add(pb);
                HBox.setMargin(pb, calculateMargin(operator.getReturnType(), parameter.getReturnType()));
            } else {
                final int index = i;
                Body plb = PlaceholderBody.createValuePlaceholderBody(
                        operator.getParameterTypes(),
                        operator,
                        v -> {
                            operator.addParameter(index, v);
                            int hboxIndex = hBoxContent.getChildren().indexOf(PLACEHOLDERS.get(index));
                            hBoxContent.getChildren().remove(hboxIndex);
                            hBoxContent.getChildren().add(hboxIndex, v.getBody());
                            HBox.setMargin(v.getBody(), calculateMargin(operator.getReturnType(), v.getReturnType()));
                        },
                        o -> {
                            operator.addParameter(index, o);
                            int hboxIndex = hBoxContent.getChildren().indexOf(PLACEHOLDERS.get(index));
                            hBoxContent.getChildren().remove(hboxIndex);
                            hBoxContent.getChildren().add(hboxIndex, o.getBody());
                            HBox.setMargin(o.getBody(), calculateMargin(operator.getReturnType(), o.getReturnType()));
                        });
                PLACEHOLDERS.put(index, plb);
                hBoxContent.getChildren().add(plb);
                HBox.setMargin(plb, calculateMargin(operator.getReturnType(), operator.getParameterTypes()));
            }
            Icon icon = operator.getOperatorType().getIcon();
            Node iconNode = Icons.get(icon, 50);
            hBoxContent.getChildren().add(iconNode);
        }
        if (hBoxContent.getChildren().size() > 2) {
            hBoxContent.getChildren().remove(hBoxContent.getChildren().size() - 1);
        }
        return hBoxContent;
    }

    private Insets calculateMargin(ValueType parent, ValueType... childs) {
        return new Insets(DEFAULT_MARGIN);//TODO
    }

    private Insets calculateMargin(ValueType parent, Set<ValueType> childs) {
        return new Insets(DEFAULT_MARGIN);//TODO
    }

}
