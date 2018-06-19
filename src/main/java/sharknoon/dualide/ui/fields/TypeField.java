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
package sharknoon.dualide.ui.fields;

import java.util.Collection;
import java.util.Optional;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Button;
import sharknoon.dualide.logic.types.Type;
import sharknoon.dualide.ui.bodies.TypePopUp;
import sharknoon.dualide.ui.misc.Icons;
import sharknoon.dualide.utils.language.Language;
import sharknoon.dualide.utils.language.Word;

/**
 *
 * @author frank
 */
public class TypeField extends Button {

    private ObjectProperty<Type> type = new SimpleObjectProperty<>();

    public TypeField() {
        this(null);
    }

    public TypeField(Collection<? extends Type> allowedTypes) {
        this(allowedTypes,false);
    }

    public TypeField(Collection<? extends Type> allowedTypes, boolean withVoid) {
        Language.set(Word.TYPE_SELECTION_FIELD_SELECT_TYPE, this);

        setOnAction((event) -> {
            TypePopUp.showTypeSelectionPopUp(this, type::set, allowedTypes, withVoid);
        });

        typeProperty().addListener((o, old, t) -> {
            Language.unset(this);
            textProperty().bind(t.getLanguageDependentName());
            setGraphic(Icons.get(t.getIcon()));
        });
    }

    public void setType(Type type){
        this.type.set(type);
    }

    public Optional<Type> getType() {
        return Optional.ofNullable(type.get());
    }

    public ObjectProperty<Type> typeProperty() {
        return type;
    }

}
