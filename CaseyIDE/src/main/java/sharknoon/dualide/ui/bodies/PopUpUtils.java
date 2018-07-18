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

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;

/**
 *
 * @author Josua Frank
 */
public class PopUpUtils {

    public static Label createLabel(String stringText) {
        Label label = new Label(stringText);
        label.setFont(Font.font(20));
        return label;
    }

    public static Node getSeparator(String name) {
        return getSeparator(name, HPos.LEFT, 20);
    }

    public static Node getSeparator(String name, HPos pos) {
        return getSeparator(name, pos, 20);
    }

    public static Node getSeparator(String name, HPos pos, double textSize) {
        HBox hBowSeparator = new HBox();
        hBowSeparator.setAlignment(Pos.CENTER);
        hBowSeparator.setSpacing(15);

        Separator separatorLeft = new Separator();
        if (pos == HPos.LEFT) {
            separatorLeft.setMaxWidth(100);
        } else {
            HBox.setHgrow(separatorLeft, Priority.ALWAYS);
        }

        Label labelText = new Label(name);
        labelText.setFont(Font.font(textSize));

        Separator separatorRight = new Separator();
        if (pos == HPos.RIGHT) {
            separatorLeft.setMaxWidth(100);
        } else {
            HBox.setHgrow(separatorRight, Priority.ALWAYS);
        }

        hBowSeparator.getChildren().addAll(separatorLeft, labelText, separatorRight);
        return hBowSeparator;
    }
}
