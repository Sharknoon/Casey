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

import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;

import java.util.List;

public class SingleFireListChange<E> extends Change<E> {
    boolean firstChange = true;
    
    /**
     * Constructs a new Change instance on the given list.
     *
     * @param list The list that was changed
     */
    public SingleFireListChange(ObservableList list) {
        super(list);
    }
    
    @Override
    public boolean next() {
        if (firstChange) {
            firstChange = false;
            return true;
        }
        return false;
    }
    
    @Override
    public void reset() {
    
    }
    
    @Override
    public int getFrom() {
        return 0;
    }
    
    @Override
    public int getTo() {
        return 0;
    }
    
    @Override
    public List<E> getRemoved() {
        return List.of();
    }
    
    @Override
    protected int[] getPermutation() {
        return new int[0];
    }
}
