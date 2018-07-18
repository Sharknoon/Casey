package sharknoon.dualide.utils.javafx.bindings;/*
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

import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;

import java.util.List;

public class SourceAdapterChange<E> extends ListChangeListener.Change<E> {
    private final Change<? extends E> change;
    private int[] perm;
    
    public SourceAdapterChange(ObservableList<E> list, Change<? extends E> change) {
        super(list);
        this.change = change;
    }
    
    @Override
    public boolean next() {
        perm = null;
        return change.next();
    }
    
    @Override
    public void reset() {
        change.reset();
    }
    
    @Override
    public int getFrom() {
        return change.getFrom();
    }
    
    @Override
    public int getTo() {
        return change.getTo();
    }
    
    @Override
    public List<E> getRemoved() {
        return (List<E>) change.getRemoved();
    }
    
    @Override
    public boolean wasUpdated() {
        return change.wasUpdated();
    }
    
    @Override
    protected int[] getPermutation() {
        if (perm == null) {
            if (change.wasPermutated()) {
                final int from = change.getFrom();
                final int n = change.getTo() - from;
                perm = new int[n];
                for (int i = 0; i < n; i++) {
                    perm[i] = change.getPermutation(from + i);
                }
            } else {
                perm = new int[0];
            }
        }
        return perm;
    }
    
    @Override
    public String toString() {
        return change.toString();
    }
    
}
