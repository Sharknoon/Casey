package sharknoon.casey.ide.logic.items;/*
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

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Optional;

public class Items {
    
    private static ObservableList<Item<?, ?, ?>> ITEMS = FXCollections.observableArrayList();
    
    public static void registerItem(Item<?, ?, ?> item) {
        ITEMS.add(item);
    }
    
    public static Optional<Item<?, ?, ?>> forName(String fullname) {
        return ITEMS.stream()
                .filter(i -> i.getFullName().equals(fullname))
                .findAny();
    }
    
}
