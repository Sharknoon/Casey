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
package sharknoon.dualide.ui.fields;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import javafx.scene.layout.Pane;
import sharknoon.dualide.logic.Statement;
import sharknoon.dualide.logic.types.Type;
import sharknoon.dualide.ui.bodies.StatementPlaceholderBody;

/**
 *
 * @author Josua Frank
 */
public class ValueField extends Pane {

    public static ValueField DISABLED = new ValueField(true);

    private Statement statement;
    private Consumer<Statement> onStatementSet;
    private Runnable onStatementDestroyed;

    public ValueField() {
        this(null);
    }

    public ValueField(Set<Type> allowedTypes) {
        StatementPlaceholderBody body = StatementPlaceholderBody.createValuePlaceholderBody(allowedTypes, null);

        body.setStatementConsumer(s -> {
            getChildren().set(0, s.getBody());
            s.addChangeListener(() -> {
                statementChangeListeners.forEach(l -> l.accept(s));
            });
            s.getBody().setOnBodyDestroyed(() -> {
                getChildren().set(0, body);
                if (onStatementDestroyed != null) {
                    onStatementDestroyed.run();
                }
            });
            statement = s;
            if (onStatementSet != null) {
                onStatementSet.accept(s);
            }
        });

        getChildren().add(body);
    }

    private ValueField(boolean disabled) {
        getChildren().add(StatementPlaceholderBody.DISABLED);
    }

    public Optional<Statement> getStatement() {
        return Optional.ofNullable(statement);
    }

    public void setOnStatementSet(Consumer<Statement> consumer) {
        onStatementSet = consumer;
    }

    public void setOnStatementDestroyed(Runnable runnable) {
        onStatementDestroyed = runnable;
    }

    private final List<Consumer<Statement>> statementChangeListeners = new ArrayList<>();

    public void addStatementChangeListener(Consumer<Statement> consumer) {
        statementChangeListeners.add(consumer);
    }

}
