package sharknoon.dualide.ui.fields;/*
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
import javafx.scene.control.Button;
import sharknoon.dualide.logic.ValueHoldable;
import sharknoon.dualide.logic.types.Type;
import sharknoon.dualide.ui.browsers.VariablePopUp;
import sharknoon.dualide.ui.misc.Icons;
import sharknoon.dualide.utils.language.Language;
import sharknoon.dualide.utils.language.Word;

import java.util.Optional;

public class VariableField extends Button {
    private ObjectProperty<ValueHoldable> variable = new SimpleObjectProperty<>();
    
    public VariableField() {
        this(Type.UNDEFINED);
    }
    
    public VariableField(Type allowedTypes) {
        Language.set(Word.VARIABLE_SELECTION_FIELD_SELECT_VARIABLE, this);
        
        setOnAction((event) -> {
            VariablePopUp.showVariableSelectionPopUp(this, variable::set, allowedTypes);
        });
        
        variableProperty().addListener((o, old, v) -> {
            Language.unset(this);
            textProperty().bind(v.toItem().nameProperty());
            graphicProperty().bind(Icons.iconToNodeProperty(v.toItem().getSite().tabIconProperty()));
        });
    }
    
    public Optional<ValueHoldable> getVariable() {
        return Optional.ofNullable(variableProperty().get());
    }
    
    public void setVariable(ValueHoldable variable) {
        variableProperty().set(variable);
    }
    
    public ObjectProperty<ValueHoldable> variableProperty() {
        return variable;
    }
    
}

