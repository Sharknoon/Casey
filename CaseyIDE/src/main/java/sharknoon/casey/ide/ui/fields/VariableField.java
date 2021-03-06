package sharknoon.casey.ide.ui.fields;/*
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

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import sharknoon.casey.ide.logic.ValueHoldable;
import sharknoon.casey.ide.logic.types.Type;
import sharknoon.casey.ide.ui.browsers.VariablePopUp;
import sharknoon.casey.ide.ui.misc.Icons;
import sharknoon.casey.ide.utils.javafx.BindUtils;
import sharknoon.casey.ide.utils.language.Language;
import sharknoon.casey.ide.utils.language.Word;

import java.util.Optional;

public class VariableField extends Button implements Field {
    private ObjectProperty<ValueHoldable<?>> variable = new SimpleObjectProperty<>();
    
    public VariableField() {
        this(null);
    }
    
    public VariableField(boolean onlyPrmitives) {
        this(Type.UNDEFINED, null, onlyPrmitives);
    }
    
    public VariableField(ValueHoldable<?> variable) {
        this(Type.UNDEFINED, variable, false);
    }
    
    public VariableField(Type allowedTypes, ValueHoldable<?> variable, boolean onlyPrimitives) {
        Language.set(Word.VARIABLE_SELECTION_FIELD_SELECT_VARIABLE, this);
        
        setOnAction((event) -> {
            VariablePopUp.showVariableSelectionPopUp(this, this.variable::set, allowedTypes, onlyPrimitives);
        });
        
        variableProperty().addListener((o, old, v) -> {
            Language.unset(this);
            if (v != null) {
                textProperty().bind(v.toItem().nameProperty());
                graphicProperty().bind(Icons.iconToNodeProperty(v.toItem().getSite().tabIconProperty()));
                v.toItem().onDestroy(() -> this.variable.set(null));
            } else {
                textProperty().unbind();
                Language.set(Word.VARIABLE_SELECTION_FIELD_SELECT_VARIABLE, this);
                graphicProperty().unbind();
                setGraphic(null);
            }
        });
        
        if (variable != null) {
            this.variable.set(variable);
        }
    }
    
    public Optional<ValueHoldable<?>> getVariable() {
        return Optional.ofNullable(variableProperty().get());
    }
    
    public void setVariable(ValueHoldable<?> variable) {
        variableProperty().set(variable);
    }
    
    public ObjectProperty<ValueHoldable<?>> variableProperty() {
        return variable;
    }
    
    @Override
    public ObservableList<Text> toText() {
        ObservableList<Text> result = FXCollections.observableArrayList();
        BindUtils.addListener(variableProperty(), (observable, oldValue, newValue) -> {
            Text varName = new Text();
            if (newValue != null) {
                varName.textProperty().bind(newValue.toItem().nameProperty());
            } else {
                varName.textProperty().unbind();
                varName.setText("null");
            }
            result.setAll(varName);
        });
        return result;
    }
}

