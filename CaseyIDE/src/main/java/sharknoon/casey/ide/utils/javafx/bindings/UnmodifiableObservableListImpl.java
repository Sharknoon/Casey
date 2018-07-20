package sharknoon.casey.ide.utils.javafx.bindings;/*
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
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import javafx.collections.WeakListChangeListener;

import java.util.Collection;

class UnmodifiableObservableListImpl<T> extends ObservableListBase<T> implements ObservableList<T> {
    
    private final ObservableList<T> backingList;
    private final ListChangeListener<T> listener;
    
    public UnmodifiableObservableListImpl(ObservableList<T> backingList) {
        this.backingList = backingList;
        listener = c -> {
            fireChange(new SourceAdapterChange<>(UnmodifiableObservableListImpl.this, c));
        };
        this.backingList.addListener(new WeakListChangeListener<T>(listener));
    }
    
    ObservableList<T> getBackingList() {
        return backingList;
    }
    
    @Override
    public T get(int index) {
        return backingList.get(index);
    }
    
    @Override
    public int size() {
        return backingList.size();
    }
    
    @Override
    public boolean addAll(T... elements) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean setAll(T... elements) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean setAll(Collection<? extends T> col) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean removeAll(T... elements) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean retainAll(T... elements) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void remove(int from, int to) {
        throw new UnsupportedOperationException();
    }
    
}