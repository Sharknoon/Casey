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
package sharknoon.dualide.ui.values;

import sharknoon.dualide.logic.values.ValueType;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SetProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleSetProperty;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.controlsfx.control.PopOver;

/**
 *
 * @author Josua Frank
 */
public class ValueField extends BorderPane {

    public SetProperty<ValueType> allowedValues = new SimpleSetProperty<>(FXCollections.observableSet(new LinkedHashSet<>()));

    public ValueField() {
        initGraphic();
        setAllowedValues((ValueType[]) ValueType.getAll().toArray());
    }

    public ValueField(ValueType... allowedValue) {
        setAllowedValues(allowedValue);
    }

    public SetProperty<ValueType> allowedValuesProperty() {
        return allowedValues;
    }

    public Set<ValueType> getAllowedValues() {
        return allowedValues.get();
    }

    public void setAllowedValues(ValueType... allowedValues) {
        this.allowedValues.get().clear();
        this.allowedValues.get().addAll(Arrays.asList(allowedValues));
    }

    private void initGraphic() {
        Rectangle rect = new Rectangle(200, 100, Color.GREEN);
        rect.setOnMouseClicked(e -> PopUp.showValueSelectionPopUp(rect, getAllowedValues()));
        setCenter(rect);
    }

}
