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
package sharknoon.casey.ide.utils.javafx;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.fxmisc.easybind.EasyBind;
import sharknoon.casey.ide.utils.javafx.bindings.AggregatedObservableList;
import sharknoon.casey.ide.utils.javafx.bindings.SingleFireListChange;

import java.util.function.Function;

/**
 * @author Josua Frank
 */
public class BindUtils {
    
    public static <T, U> ObservableValue<U> map(ObservableValue<T> value, Function<T, U> function) {
        return Bindings.createObjectBinding(() -> function.apply(value.getValue()), value);
    }
    
    public static <T, U> ObservableList<U> map(ObservableList<T> value, Function<T, U> function) {
        return EasyBind.map(value, function);
    }
    
    public static <T> ObservableList<T> concatFromList(ObservableList<ObservableList<T>> lists) {
        AggregatedObservableList<T> result = new AggregatedObservableList<>();
        lists.addListener((ListChangeListener<ObservableList<T>>) c -> {
            while (c.next()) {
                if (c.wasPermutated()) {
                    for (int i = c.getFrom(); i < c.getTo(); ++i) {
                        int newIndex = c.getPermutation(i);
                        //newIndex > 1 because otherwise the lists would be swapped twice
                        if (newIndex != i && newIndex > i) {
                            result.swapLists(i, newIndex);
                        }
                    }
                } else if (c.wasUpdated()) {
                    //do nothing
                } else {
                    for (ObservableList<T> remList : c.getRemoved()) {
                        result.removeList(remList);
                    }
                    for (int i = 0; i < c.getAddedSubList().size(); i++) {
                        ObservableList<T> addList = c.getAddedSubList().get(i);
                        result.addList(addList, c.getFrom() + i);
                    }
                }
            }
        });
        for (ObservableList<T> list : lists) {
            result.appendList(list);
        }
        return result;
    }
    
    /**
     * Merges multiple lists into one unmodifiable list, which listener listens for changes for each sublist
     *
     * @param lists
     * @param <T>
     * @return
     */
    public static <T> ObservableList<T> concatAll(ObservableList<? extends T>... lists) {
        AggregatedObservableList<T> result = new AggregatedObservableList<>();
        for (ObservableList<? extends T> list : lists) {
            result.appendList((ObservableList<T>) list);
        }
        return result;
    }
    
    public static <T> void addListener(ObservableValue<T> observable, ChangeListener<? super T> changeListener) {
        changeListener.changed(observable, null, observable.getValue());
        observable.addListener(changeListener);
    }
    
    public static <T> void addListener(ObservableList<T> observableList, ListChangeListener<? super T> listChangeListener) {
        listChangeListener.onChanged(new SingleFireListChange<>(observableList));
        observableList.addListener(listChangeListener);
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
    
    public static <E> ObjectBinding<E> getLast(ObservableList<E> list) {
        IntegerBinding lastIndex = Bindings.size(list).subtract(1);
        return Bindings.valueAt(list, lastIndex);
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
        public ObservableList<?> getDependencies() {
            ObservableList deps = FXCollections.observableArrayList();
            deps.add(op1);
            deps.add(op2);
            for (ObservableBooleanValue op : ops) {
                deps.add(op);
            }
            return FXCollections.unmodifiableObservableList(deps);
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
    }
    
}
