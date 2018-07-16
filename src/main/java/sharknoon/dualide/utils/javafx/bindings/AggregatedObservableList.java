package sharknoon.dualide.utils.javafx.bindings;


import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * This class aggregates several other Observed Lists (sublists), observes changes on those sublists and applies those same changes to the
 * aggregated list.
 * This class is <b>immutable</b>!
 * Inspired by:
 * - http://stackoverflow.com/questions/25705847/listchangelistener-waspermutated-block
 * - http://stackoverflow.com/questions/37524662/how-to-concatenate-observable-lists-in-javafx
 * - https://github.com/lestard/advanced-bindings/blob/master/src/main/java/eu/lestard/advanced_bindings/api/CollectionBindings.java
 * Posted result on: http://stackoverflow.com/questions/37524662/how-to-concatenate-observable-lists-in-javafx
 *
 * @param <T>
 */
public class AggregatedObservableList<T> extends UnmodifiableObservableListImpl<T> {
    
    //final ReadOnlyListWrapper<T> aggregatedList;
    private final List<ObservableList<T>> lists = new ArrayList<>();
    private final List<Integer> sizes = new ArrayList<>();
    private final List<InternalListModificationListener> listeners = new ArrayList<>();
    
    /**
     * Creates a new AggregatedObservableList based on a ArrayList
     */
    public AggregatedObservableList() {
        this(new ArrayList<>());
    }
    
    /**
     * Creates a new AggregatedList based on a List
     *
     * @param backingList The backing List for this AggregatedList
     */
    public AggregatedObservableList(@NotNull List<T> backingList) {
        super(FXCollections.observableList(backingList));
    }
    
    /**
     * Adds a list to the specified index of this aggregated list
     *
     * @param list
     * @param index
     */
    public void addList(@NotNull ObservableList<T> list, int index) {
        assert !lists.contains(list) : "List is already contained: " + list;
        assert index <= lists.size() && index >= 0 : "Index out of range: " + index;
        lists.add(index, list);
        final InternalListModificationListener listener = new InternalListModificationListener(list);
        list.addListener(listener);
        //System.out.println("list = " + list + " puttingInMap=" + list.hashCode());
        sizes.add(index, list.size());
        int startIndex = getInsertionIndex(index);
        getBackingList().addAll(startIndex, list);
        listeners.add(index, listener);
        assert lists.size() == sizes.size() && lists.size() == listeners.size() :
                "lists.size=" + lists.size() + " not equal to sizes.size=" + sizes.size() + " or not equal to listeners.size=" + listeners.size();
    }
    
    /**
     * Adds a list to the end of this aggregated list
     *
     * @param list
     */
    public void appendList(@NotNull ObservableList<T> list) {
        addList(list, lists.size());
    }
    
    /**
     * Adds a list to the start of this aggregated list
     *
     * @param list
     */
    public void prependList(@NotNull ObservableList<T> list) {
        addList(list, 0);
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
        getBackingList().remove(startIndex, endIndex + 1); // end + 1 because end is exclusive
        assert lists.size() == sizes.size() && lists.size() == listeners.size() :
                "lists.size=" + lists.size() + " not equal to sizes.size=" + sizes.size() + " or not equal to listeners.size=" + listeners.size();
    }
    
    /**
     * Swaps two lists in this aggregated list
     *
     * @param firstIndex
     * @param lastIndex
     */
    public void swapLists(int firstIndex, int lastIndex) {
        assert lists.size() == sizes.size() && lists.size() == listeners.size() :
                "lists.size=" + lists.size() + " not equal to sizes.size=" + sizes.size() + " or not equal to listeners.size=" + listeners.size();
        assert firstIndex <= lists.size() && firstIndex >= 0 : "First index out of range: " + firstIndex;
        assert lastIndex <= lists.size() && lastIndex >= 0 : "Last index out of range: " + lastIndex;
        ObservableList<T> firstList = lists.get(firstIndex);
        ObservableList<T> lastList = lists.get(lastIndex);
        removeList(firstList);
        removeList(lastList);
        //The order is important, the lower index needs to be swapped first
        if (firstIndex < lastIndex) {
            addList(lastList, firstIndex);
            addList(firstList, lastIndex);
        } else {
            addList(firstList, lastIndex);
            addList(lastList, firstIndex);
        }
    }
    
    /**
     * Gets the insertion index in the aggregated list.
     * Basically converts a index of the lists of lists to the first index of the aggregated list.
     * This is a private function.
     *
     * @param listIndex the index of the newly to be inserted list in between the lists
     * @return the first index of the newly to be inserted list in the aggregated list
     */
    private int getInsertionIndex(int listIndex) {
        assert listIndex <= lists.size() && listIndex >= 0 : "List index out of range: " + listIndex;
        if (listIndex == 0) {
            return 0;
        }
        ObservableList<T> previousList = lists.get(listIndex - 1);
        int startIndex = getStartIndex(previousList);
        int endIndex = getEndIndex(previousList, startIndex);
        return endIndex + 1;
    }
    
    /**
     * Get the start index of this list inside the aggregated List.
     * This is a private function. we can safely assume, that the list is in the map.
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
     * This is a private function. we can safely assume, that the list is in the map.
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
        getBackingList().forEach(el -> sb.append(function.apply(el)).append(","));
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
                    final ArrayList<T> copy = new ArrayList<>(getBackingList().subList(startIndex + from, startIndex + to));
                    //System.out.println("  permutating sublist=" + copy);
                    for (int oldIndex = from; oldIndex < to; oldIndex++) {
                        int newIndex = change.getPermutation(oldIndex);
                        copy.set(newIndex - from, getBackingList().get(startIndex + oldIndex));
                    }
                    //System.out.println("  permutating done sublist=" + copy);
                    getBackingList().subList(startIndex + from, startIndex + to).clear();
                    getBackingList().addAll(startIndex + from, copy);
                } else if (change.wasUpdated()) {
                    // do nothing
                } else {
                    if (change.wasRemoved()) {
                        List<? extends T> removed = change.getRemoved();
                        //System.out.println("  removed= " + removed);
                        // IMPORTANT! FROM == TO when removing items.
                        getBackingList().remove(startIndex + from, startIndex + from + removed.size());
                    }
                    if (change.wasAdded()) {
                        List<? extends T> added = change.getAddedSubList();
                        //System.out.println("  added= " + added);
                        //add those elements to your data
                        getBackingList().addAll(startIndex + from, added);
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