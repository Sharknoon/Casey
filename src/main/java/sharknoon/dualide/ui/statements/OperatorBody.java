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
package sharknoon.dualide.ui.statements;

import java.util.List;
import java.util.stream.Collectors;
import javafx.geometry.Insets;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Shape;
import sharknoon.dualide.logic.statements.operations.Operator;
import javafx.scene.layout.StackPane;
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
        Shape shape = createOuterShape(operator.getReturnType());
        Control content = createContentNode(operator);
        getChildren().addAll(shape, content);
    }

    private Control createContentNode(Operator<Value, Value> operator) {
        HBox hBoxContent = new HBox(20);
        hBoxContent.setPrefSize(0, 0);
        operator.getParameters()
                .stream()
                .map(v -> v.getBody())
                .forEach(b -> {
                    hBoxContent.getChildren().add(b);
                    HBox.setMargin(b, new Insets(0, 5, 0, 5));

                    Icon icon = operator.getOperatorType().getIcon();
                    ImageView iconView = Icons.get(icon, 50);
                    hBoxContent.getChildren().add(iconView);
                    HBox.setMargin(iconView, new Insets(5, 0, 0, 0));
                });
        if (getChildren().size() > 2) {
            getChildren().remove(getChildren().size() - 1);
        }
        if (getChildren().size() < 3) {
            if (getChildren().size() < 1) {
                //placeholeder + 
            }
            //placeholder
        }
        widthProperty.bind(hBoxContent.widthProperty());//TODO evtl. cyclic
        return new Label("TODO");
    }

}
