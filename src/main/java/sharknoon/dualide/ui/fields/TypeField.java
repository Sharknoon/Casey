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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
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
        Language.set(Word.TYPE_SELECTION_FIELD_SELECT_TYPE, this);

        setOnAction((event) -> {
            TypePopUp.showTypeSelectionPopUp(this, (t) -> {
                type.set(t);
            });
        });

        typeProperty().addListener((o, old, t) -> {
            Language.unset(this);
            textProperty().bind(t.getSimpleName());
            setGraphic(Icons.get(t.getIcon()));
        });
    }

    public Optional<Type> getType() {
        return Optional.ofNullable(type.get());
    }

    public ObjectProperty<Type> typeProperty() {
        return type;
    }

}
