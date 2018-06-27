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
import org.jetbrains.annotations.NotNull;
import sharknoon.dualide.utils.javafx.bindings.SingleFireListChange;

import java.util.ArrayList;
import java.util.List;
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
        AggregatedObservableArrayList<T> result = new AggregatedObservableArrayList<>();
        //TODO listen for List changes
        for (ObservableList<T> list : lists) {
            result.appendList(list);
        }
        return result.getAggregatedList();
    }
    
    /**
     * Merges multiple lists into one unmodifiable list, which listener listens for changes for each sublist
     *
     * @param lists
     * @param <T>
     * @return
     */
    public static <T> ObservableList<T> concatAll(ObservableList<? extends T>... lists) {
        AggregatedObservableArrayList<T> result = new AggregatedObservableArrayList<>();
        for (ObservableList<? extends T> list : lists) {
            result.appendList((ObservableList<T>) list);
        }
        return result.getAggregatedList();
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
    
    /**
     * This class aggregates several other Observed Lists (sublists), observes changes on those sublists and applies those same changes to the
     * aggregated list.
     * Inspired by:
     * - http://stackoverflow.com/questions/25705847/listchangelistener-waspermutated-block
     * - http://stackoverflow.com/questions/37524662/how-to-concatenate-observable-lists-in-javafx
     * - https://github.com/lestard/advanced-bindings/blob/master/src/main/java/eu/lestard/advanced_bindings/api/CollectionBindings.java
     * Posted result on: http://stackoverflow.com/questions/37524662/how-to-concatenate-observable-lists-in-javafx
     */
    public static class AggregatedObservableArrayList<T> {
        
        protected final List<ObservableList<T>> lists = new ArrayList<>();
        final protected ObservableList<T> aggregatedList = FXCollections.observableArrayList();
        final private List<Integer> sizes = new ArrayList<>();
        final private List<InternalListModificationListener> listeners = new ArrayList<>();
        
        public AggregatedObservableArrayList() {
        
        }
        
        /**
         * The Aggregated Observable List. This list is unmodifiable, because sorting this list would mess up the entire bookkeeping we do here.
         *
         * @return an unmodifiable view of the aggregatedList
         */
        public ObservableList<T> getAggregatedList() {
            return FXCollections.unmodifiableObservableList(aggregatedList);
        }
        
        /**
         * Adds a list to the end of this aggregated list
         *
         * @param list
         */
        public void appendList(@NotNull ObservableList<T> list) {
            assert !lists.contains(list) : "List is already contained: " + list;
            lists.add(list);
            final InternalListModificationListener listener = new InternalListModificationListener(list);
            list.addListener(listener);
            //System.out.println("list = " + list + " puttingInMap=" + list.hashCode());
            sizes.add(list.size());
            aggregatedList.addAll(list);
            listeners.add(listener);
            assert lists.size() == sizes.size() && lists.size() == listeners.size() :
                    "lists.size=" + lists.size() + " not equal to sizes.size=" + sizes.size() + " or not equal to listeners.size=" + listeners.size();
        }
        
        /**
         * Adds a list to the start of this aggregated list
         *
         * @param list
         */
        public void prependList(@NotNull ObservableList<T> list) {
            assert !lists.contains(list) : "List is already contained: " + list;
            lists.add(0, list);
            final InternalListModificationListener listener = new InternalListModificationListener(list);
            list.addListener(listener);
            //System.out.println("list = " + list + " puttingInMap=" + list.hashCode());
            sizes.add(0, list.size());
            aggregatedList.addAll(0, list);
            listeners.add(0, listener);
            assert lists.size() == sizes.size() && lists.size() == listeners.size() :
                    "lists.size=" + lists.size() + " not equal to sizes.size=" + sizes.size() + " or not equal to listeners.size=" + listeners.size();
        }
        
        /**
         * Removes a list from this aggregated list
         *
         * @param list
         */
        public void removeList(@NotNull ObservableList<T> list) {
            assert lists.size() == sizes.size() && lists.size() == listeners.size() :
                    "lists.size=" + lists.size() + " not equal to sizes.size=" + sizes.size() + " or not equal to listeners.size=" + listeners.size();
            final int index = lists.indexOf(list);
            if (index < 0) {
                throw new IllegalArgumentException("Cannot remove a list that is not contained: " + list + " lists=" + lists);
            }
            final int startIndex = getStartIndex(list);
            final int endIndex = getEndIndex(list, startIndex);
            // we want to find the start index of this list inside the aggregated List. End index will be start + size - 1.
            lists.remove(list);
            sizes.remove(index);
            final InternalListModificationListener listener = listeners.remove(index);
            list.removeListener(listener);
            aggregatedList.remove(startIndex, endIndex + 1); // end + 1 because end is exclusive
            assert lists.size() == sizes.size() && lists.size() == listeners.size() :
                    "lists.size=" + lists.size() + " not equal to sizes.size=" + sizes.size() + " or not equal to listeners.size=" + listeners.size();
        }
        
        /**
         * Get the start index of this list inside the aggregated List.
         * This is a private function. we can safely asume, that the list is in the map.
         *
         * @param list the list in question
         * @return the start index of this list in the aggregated List
         */
        private int getStartIndex(@NotNull ObservableList<T> list) {
            int startIndex = 0;
            //System.out.println("=== searching startIndex of " + list);
            assert lists.size() == sizes.size() : "lists.size=" + lists.size() + " not equal to sizes.size=" + sizes.size();
            final int listIndex = lists.indexOf(list);
            for (int i = 0; i < listIndex; i++) {
                final Integer size = sizes.get(i);
                startIndex += size;
                //System.out.println(" startIndex = " + startIndex + " added=" + size);
            }
            //System.out.println("startIndex = " + startIndex);
            return startIndex;
        }
        
        /**
         * Get the end index of this list inside the aggregated List.
         * This is a private function. we can safely asume, that the list is in the map.
         *
         * @param list       the list in question
         * @param startIndex the start of the list (retrieve with {@link #getStartIndex(ObservableList)}
         * @return the end index of this list in the aggregated List
         */
        private int getEndIndex(@NotNull ObservableList<T> list, int startIndex) {
            assert lists.size() == sizes.size() : "lists.size=" + lists.size() + " not equal to sizes.size=" + sizes.size();
            final int index = lists.indexOf(list);
            return startIndex + sizes.get(index) - 1;
        }
        
        public String dump(Function<T, Object> function) {
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            aggregatedList.forEach(el -> sb.append(function.apply(el)).append(","));
            final int length = sb.length();
            sb.replace(length - 1, length, "");
            sb.append("]");
            return sb.toString();
        }
        
        private class InternalListModificationListener implements ListChangeListener<T> {
            
            @NotNull
            private final ObservableList<T> list;
            
            public InternalListModificationListener(@NotNull ObservableList<T> list) {
                this.list = list;
            }
            
            /**
             * Called after a change has been made to an ObservableList.
             *
             * @param change an object representing the change that was done
             * @see Change
             */
            @Override
            public void onChanged(Change<? extends T> change) {
                final ObservableList<? extends T> changedList = change.getList();
                final int startIndex = getStartIndex(list);
                final int index = lists.indexOf(list);
                final int newSize = changedList.size();
                //System.out.println("onChanged for list=" + list + " aggregate=" + aggregatedList);
                while (change.next()) {
                    final int from = change.getFrom();
                    final int to = change.getTo();
                    //System.out.println(" startIndex=" + startIndex + " from=" + from + " to=" + to);
                    if (change.wasPermutated()) {
                        final ArrayList<T> copy = new ArrayList<>(aggregatedList.subList(startIndex + from, startIndex + to));
                        //System.out.println("  permutating sublist=" + copy);
                        for (int oldIndex = from; oldIndex < to; oldIndex++) {
                            int newIndex = change.getPermutation(oldIndex);
                            copy.set(newIndex - from, aggregatedList.get(startIndex + oldIndex));
                        }
                        //System.out.println("  permutating done sublist=" + copy);
                        aggregatedList.subList(startIndex + from, startIndex + to).clear();
                        aggregatedList.addAll(startIndex + from, copy);
                    } else if (change.wasUpdated()) {
                        // do nothing
                    } else {
                        if (change.wasRemoved()) {
                            List<? extends T> removed = change.getRemoved();
                            //System.out.println("  removed= " + removed);
                            // IMPORTANT! FROM == TO when removing items.
                            aggregatedList.remove(startIndex + from, startIndex + from + removed.size());
                        }
                        if (change.wasAdded()) {
                            List<? extends T> added = change.getAddedSubList();
                            //System.out.println("  added= " + added);
                            //add those elements to your data
                            aggregatedList.addAll(startIndex + from, added);
                        }
                    }
                }
                // update the size of the list in the map
                //System.out.println("list = " + list + " puttingInMap=" + list.hashCode());
                sizes.set(index, newSize);
                //System.out.println("listSizesMap = " + sizes);
            }
            
        }
        
    }
    
}
