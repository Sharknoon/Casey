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
package sharknoon.casey.ide.ui.fields;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import sharknoon.casey.ide.logic.types.Type;
import sharknoon.casey.ide.ui.browsers.TypePopUp;
import sharknoon.casey.ide.ui.misc.Icons;
import sharknoon.casey.ide.utils.javafx.BindUtils;
import sharknoon.casey.ide.utils.language.Language;
import sharknoon.casey.ide.utils.language.Word;

import java.util.Collection;
import java.util.Optional;

/**
 * @author frank
 */
public class TypeField extends Button implements Field {
    
    private ObjectProperty<Type> type = new SimpleObjectProperty<>();
    
    public TypeField() {
        this(null);
    }
    
    public TypeField(Collection<? extends Type> allowedTypes) {
        this(allowedTypes, false);
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
    
    public Optional<Type> getType() {
        return Optional.ofNullable(type.get());
    }
    
    public void setType(Type type) {
        this.type.set(type);
    }
    
    public ObjectProperty<Type> typeProperty() {
        return type;
    }
    
    @Override
    public ObservableList<Text> toText() {
        ObservableList<Text> result = FXCollections.observableArrayList();
        BindUtils.addListener(type, (observable, oldValue, newValue) -> {
            result.clear();
            Text text = new Text();
            if (newValue != null) {
                text.textProperty().bind(newValue.getLanguageDependentName());
            } else {
                text.setText("null");
            }
            result.add(text);
        });
        return result;
    }
}
