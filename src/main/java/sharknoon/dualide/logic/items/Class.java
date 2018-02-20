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
package sharknoon.dualide.logic.items;

import java.util.Optional;
import java.util.stream.Collectors;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

/**
 *
 * @author Josua Frank
 */
public class Class extends Item<Class, Package, Item<? extends Item, Class, ? extends Item>> {

    private static final transient ListProperty<Class> CLASSES = new SimpleListProperty<>(FXCollections.observableArrayList());

    protected Class(Package parent, String name) {
        super(parent, name);
        CLASSES.add(this);
    }

    public static ListProperty<Class> classesProperty() {
        return CLASSES;
    }

    /**
     * case sensitive!
     *
     * @param fullName
     * @return
     */
    public static Optional<Class> forName(String fullName) {
        return CLASSES.stream()
                .filter(c -> c.getFullName().equals(fullName))
                .findFirst();
    }

}
