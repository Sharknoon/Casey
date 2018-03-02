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
package sharknoon.dualide.utils.javafx;

import java.util.ArrayList;
import java.util.function.Function;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import java.util.List;
import java.util.function.Consumer;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.value.ChangeListener;
import org.fxmisc.easybind.EasyBind;
import sharknoon.dualide.logic.items.Class;
import sharknoon.dualide.logic.types.PrimitiveType;

/**
 *
 * @author Josua Frank
 */
public class BindUtils {

    public static <T, U> ObservableValue<U> map(ObservableValue<T> value, Function<T, U> function) {
        return Bindings.createObjectBinding(() -> function.apply(value.getValue()), value);
    }

    public static <T, U> ObservableList<U> map(ObservableList<T> value, Function<T, U> function) {
        return EasyBind.map(value, function);
    }

    public static <T> ObservableList<T> concat(ObservableList<? extends T>... lists) {
        ObservableList<T> result = FXCollections.observableArrayList();
        List<IntegerBinding> lengths = new ArrayList<>();
        for (int i = 0; i < lists.length; i++) {
            ObservableList<? extends T> list = lists[i];

            lengths.add(Bindings.size(list));

            int j = i;
            list.addListener((ListChangeListener.Change<? extends T> c) -> {
                while (c.next()) {
                    int listStartIndex = 0;
                    if (c.wasAdded() || c.wasRemoved()) {
                        listStartIndex = lengths.stream().limit(j).mapToInt(ib -> ib.get()).sum();
                    }
                    if (c.wasAdded()) {
                        List<? extends T> addedSubList = c.getAddedSubList();
                        result.addAll(listStartIndex + c.getFrom(), addedSubList);
                    }
                    if (c.wasRemoved()) {
                        result.remove(listStartIndex + c.getFrom(), listStartIndex + c.getTo() + 1);
                    }
                }
            });
            result.addAll(list);
        }
        return result;
    }

    public static <T> void listen(ObservableValue<T> observable, Consumer<? super T> subscriber) {
        subscriber.accept(observable.getValue());
        observable.addListener((obs, oldValue, newValue) -> subscriber.accept(newValue));
    }

}
