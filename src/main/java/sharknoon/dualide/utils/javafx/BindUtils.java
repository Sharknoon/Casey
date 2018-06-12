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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.function.Function;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import java.util.List;
import java.util.function.Consumer;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.value.ObservableBooleanValue;
import org.fxmisc.easybind.EasyBind;

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

    /**
     * Creates a {@link BooleanBinding} that calculates the conditional-AND
     * operation on the value of two instance of
     * {@link javafx.beans.value.ObservableBooleanValue}.
     *
     * @param op1 first {@code ObservableBooleanValue}
     * @param op2 second {@code ObservableBooleanValue}
     * @return the new {@code BooleanBinding}
     * @throws NullPointerException if one of the operands is {@code null}
     */
    public static BooleanBinding and(final ObservableBooleanValue op1, final ObservableBooleanValue op2, final ObservableBooleanValue... ops) {
        if ((op1 == null) || (op2 == null) || (ops == null)) {
            throw new NullPointerException("Operands cannot be null.");
        }

        return new BooleanAndBinding(op1, op2, ops);
    }

    private static class BooleanAndBinding extends BooleanBinding {

        private final ObservableBooleanValue op1;
        private final ObservableBooleanValue op2;
        private final ObservableBooleanValue[] ops;

        public BooleanAndBinding(final ObservableBooleanValue op1, final ObservableBooleanValue op2, final ObservableBooleanValue... ops) {
            this.op1 = op1;
            this.op2 = op2;
            this.ops = ops;
        }

        @Override
        protected boolean computeValue() {
              var result = op1.get() && op2.get();
            if (!result) {
                return result;
            }
            for (ObservableBooleanValue op : ops) {
                result = result == op.get();
                if (!result) {
                    return result;
                }
            }
            return result;
        }

        @Override
        public ObservableList<?> getDependencies() {
            ObservableList deps = FXCollections.observableArrayList();
            deps.add(op1);
            deps.add(op2);
            for (ObservableBooleanValue op : ops) {
                deps.add(op);
            }
            return FXCollections.unmodifiableObservableList(deps);
        }
    }

}
