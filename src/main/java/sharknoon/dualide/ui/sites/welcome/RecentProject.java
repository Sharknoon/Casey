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
package sharknoon.dualide.ui.sites.welcome;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import static org.dizitart.no2.objects.filters.ObjectFilters.*;
import sharknoon.dualide.logic.Project;
import sharknoon.dualide.utils.settings.Database;

/**
 *
 * @author Josua Frank
 */
public class RecentProject {

    private String name;
    private Path path;
    private String time;

    private static final transient ObservableSet<RecentProject> RECENT_PROJECTS = FXCollections.observableSet(new HashSet<>());

    static {
        RECENT_PROJECTS.addListener((SetChangeListener.Change<? extends RecentProject> change) -> {
            if (change.wasAdded()) {
                Database.store(change.getElementAdded());
            } else if (change.wasRemoved()) {
                RecentProject elem = change.getElementRemoved();
                Database.delete(RecentProject.class,
                        and(
                                eq("name", elem.name),
                                eq("path", elem.path)
                        )
                );
            }
        });
        Database.get(RecentProject.class).thenAccept((t) -> {
            RECENT_PROJECTS.addAll(t);
        });
    }

    private RecentProject() {
    }

    private RecentProject(String name, Path path, String time) {
        this.name = name;
        this.path = path;
        this.time = time;
        RECENT_PROJECTS.add(this);
    }

    public String getName() {
        return name == null ? "" : name;
    }

    public Path getPath() {
        return path;
    }

    public LocalDateTime getTime() {
        return LocalDateTime.parse(time);
    }

    public static void addProject(Project project) {
        RecentProject recentProject = new RecentProject(
                project.getName(),
                project.getSaveFile().orElse(null),
                LocalDateTime.now().toString()
        );
    }

    public static void addListener(SetChangeListener<? super RecentProject> listener) {
        RECENT_PROJECTS.addListener(listener);
    }

    public static Set<RecentProject> getRecentProjects() {
        return RECENT_PROJECTS;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 43 * hash + Objects.hashCode(this.name);
        hash = 43 * hash + Objects.hashCode(this.path);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RecentProject other = (RecentProject) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return Objects.equals(this.path, other.path);
    }

}
