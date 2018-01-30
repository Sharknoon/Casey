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
package sharknoon.dualide.logic.operations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;
import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import sharknoon.dualide.logic.values.Value;

/**
 *
 * @author Josua Frank
 * @param <RV> Return value
 * @param <PV> Parameter value, if the type is unimportant, use the abstract
 * type value
 */
public abstract class Operator<RV extends Value, PV extends Value> {

    private final MapProperty<Integer, PV> parameters = new SimpleMapProperty<>(FXCollections.observableMap(new TreeMap<>()));

    /**
     * Overrides the parameter
     *
     * @param index starting at 0
     * @param parameter
     */
    public void addParameter(int index, PV parameter) {
        parameters.put(index, parameter);
    }

    public Optional<PV> getParameter(int index) {
        return Optional.ofNullable(parameters.get(index));
    }

    public List<PV> getParameters() {
        return new ArrayList<>(parameters.values());
    }

    public abstract RV calculateResult();

}
