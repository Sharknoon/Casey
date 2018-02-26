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
import java.time.LocalTime;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import org.dizitart.no2.Document;
import org.dizitart.no2.NitriteCollection;
import org.dizitart.no2.filters.Filters;
import sharknoon.dualide.logic.items.Project;
import sharknoon.dualide.utils.settings.Database;

/**
 * TODO instead of fullname move to path of the savefile
 *
 * @author Josua Frank
 */
public class RecentProject {

    private String id;
    private static final String ID = "id";
    private String name;
    private static final String NAME = "name";
    private String path;
    private static final String PATH = "path";
    private String time;
    private static final String TIME = "time";
    private static NitriteCollection COL;
    private static ListProperty<RecentProject> PROJECTS_LIST = new SimpleListProperty<>(FXCollections.observableArrayList());

    private RecentProject() {
    }

    private RecentProject(Project project) {
        this.id = project.getID();
        this.name = project.getFullName();
        this.path = project.getSaveFile().map(Path::toString).orElse("");
        this.time = LocalDateTime.now().toString();
    }

    private static CompletableFuture<Void> init() {
        if (COL != null) {
            return CompletableFuture.completedFuture(null);
        }
        return Database.getCollection("recentProjects").thenAccept((col) -> {
            COL = col;
            COL.find().forEach((d) -> {
                String idString = d.get(ID, String.class);
                String nameString = d.get(NAME, String.class);
                String pathString = d.get(PATH, String.class);
                String timeString = d.get(TIME, String.class);
                RecentProject rp = new RecentProject();
                rp.id = idString;
                rp.name = nameString;
                rp.path = pathString;
                rp.time = timeString;
                PROJECTS_LIST.add(rp);
            });
        });
    }

    public static CompletableFuture<Collection<RecentProject>> getAllProjects() {
        return init().thenApply(v -> PROJECTS_LIST);
    }

    public static void updateProject(Project project) {
        RecentProject rp = new RecentProject(project);
        PROJECTS_LIST.removeIf(rpl -> rpl.getID().equals(rp.getID()));
        PROJECTS_LIST.add(rp);
        Document doc = Document.createDocument(ID, project.getID());
        doc.put(NAME, project.getFullName());
        doc.put(PATH, project.getSaveFile().map(Path::toString).orElse(""));
        doc.put(TIME, LocalDateTime.now().toString());
        init().thenRun(() -> {
            if (COL.find(Filters.eq(ID, project.getID())).size() < 1) {
                COL.insert(doc);
            } else {
                COL.update(Filters.eq(ID, project.getID()), doc);
            }
        });
    }

    public static void removeProject(RecentProject project) {
        PROJECTS_LIST.remove(project);
        init().thenRun(() -> {
            COL.remove(Filters.eq(ID, project.id));
        });
    }

    public String getID() {
        return id;
    }

    public String getName() {
        int lastPointIndex = name.lastIndexOf(".");
        if (lastPointIndex >= 0) {
            return name.substring(name.lastIndexOf("."), name.length() - 1);
        } else {
            return name;
        }
    }

    public LocalDateTime getTime() {
        return LocalDateTime.parse(time);
    }

    /**
     * can be null!
     *
     * @return
     */
    public String getPath() {
        return path;
    }

    public static ListProperty recentProjectsProperty() {
        init().join();
        return PROJECTS_LIST;
    }
}
