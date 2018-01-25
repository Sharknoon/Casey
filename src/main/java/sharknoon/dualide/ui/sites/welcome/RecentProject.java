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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import org.dizitart.no2.objects.Id;
import sharknoon.dualide.logic.items.Project;
import sharknoon.dualide.utils.settings.Database;

/**
 * TODO instead of fullname move to path of the savefile
 *
 * @author Josua Frank
 */
public class RecentProject {

    @Id
    private String path;
    private String projectID;
    private String time;
    private static transient ObservableMap<String, RecentProject> PROJECTS_MAP = FXCollections.observableMap(new HashMap<>());

    static {
        Database.get(RecentProject.class).thenAccept((rps) -> {
            rps.forEach(rp -> PROJECTS_MAP.put(rp.path, rp));
        });
    }

    private RecentProject() {
    }

    public RecentProject(Project project) {
        this.projectID = project.getFullName();
        this.time = LocalDateTime.now().toString();
        this.path = project.getSaveFile().map(Path::toString).orElse("");
        Database.store(this);
    }

    public static Collection<RecentProject> getAllProjects() {
        return PROJECTS_MAP.values();
    }

    public static void updateProject(Project project) {
        CompletableFuture.runAsync(() -> {
            if (!project.getSaveFile().isPresent()) {
                //No save file present means no option to open it from a file -> no need to store it in recent projects
                return;
            }
            if (PROJECTS_MAP.containsKey(project.getSaveFile().get().toString())) {
                RecentProject rp = PROJECTS_MAP.get(project.getSaveFile().get().toString());
                rp.projectID = project.getFullName();
                rp.time = LocalDateTime.now().toString();
                Database.store(rp);
                LISTENERS.forEach(l -> l.run());
            } else {
                RecentProject rp = new RecentProject(project);
                PROJECTS_MAP.put(project.getSaveFile().get().toString(), rp);
                Database.store(rp);
            }
        });
    }

    public static void removeProject(RecentProject project) {
        if (PROJECTS_MAP.containsKey(project.path)) {
            PROJECTS_MAP.remove(project.path);
            Database.delete(project);
        }
    }

    public String getName() {
        int lastPointIndex = projectID.lastIndexOf(".");
        if (lastPointIndex >= 0) {
            return projectID.substring(projectID.lastIndexOf("."), projectID.length() - 1);
        } else {
            return projectID;
        }
    }

    public LocalDateTime getTime() {
        return LocalDateTime.parse(time);
    }
    private static final List<Runnable> LISTENERS = new ArrayList<>();

    public static void addListener(Runnable listener) {
        LISTENERS.add(listener);
        PROJECTS_MAP.addListener((MapChangeListener.Change<? extends String, ? extends RecentProject> change) -> {
            listener.run();
        });
    }

    /**
     * can be null!
     *
     * @return
     */
    public String getPath() {
        return path;
    }
}
