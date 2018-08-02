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
package sharknoon.casey.ide.utils.settings;

import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import org.dizitart.no2.Cursor;
import org.dizitart.no2.Document;
import org.dizitart.no2.NitriteCollection;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author Josua Frank
 */
public class Props {
    
    private static final NitriteCollection COLLECTION;
    private static final ObservableMap<String, Object> OBSERVABLE_DOCUMENT;
    
    static {
        COLLECTION = Database.getCollection("ideProps").join();
        final Document tmpDoc;
        Cursor cursor = COLLECTION.find();
        if (cursor.firstOrDefault() == null) {
            tmpDoc = new Document();
            tmpDoc.put("_id", 0L);
            COLLECTION.insert(tmpDoc);
        } else {
            tmpDoc = cursor.firstOrDefault();
        }
        OBSERVABLE_DOCUMENT = FXCollections.observableMap(tmpDoc);
        OBSERVABLE_DOCUMENT.addListener((MapChangeListener.Change<? extends String, ? extends Object> change) -> {
            COLLECTION.update(tmpDoc);
        });
    }
    
    public static void set(String key, String value) {
        CompletableFuture.runAsync(() -> {
            OBSERVABLE_DOCUMENT.put(key, value);
        });
    }
    
    public static CompletableFuture<Optional<String>> get(String key) {
        return CompletableFuture.supplyAsync(() -> {
            Object result = OBSERVABLE_DOCUMENT.get(key);
            return result != null ? Optional.of(result.toString()) : Optional.empty();
        });
    }
    
    public static CompletableFuture<String> getOrDefault(String key, String defaultValue) {
        return CompletableFuture.supplyAsync(() -> {
            Object result = OBSERVABLE_DOCUMENT.getOrDefault(key, defaultValue);
            return Objects.toString(result);
        });
    }
    
    public static CompletableFuture<String> remove(String key) {
        return CompletableFuture.supplyAsync(() -> {
            return OBSERVABLE_DOCUMENT.remove(key).toString();
        });
    }
    
    public static CompletableFuture<Set<String>> getAll(Predicate<String> keyfilter) {
        return CompletableFuture.supplyAsync(() -> {
            return OBSERVABLE_DOCUMENT
                    .entrySet()
                    .stream()
                    .filter(e -> keyfilter.test(e.getKey()))
                    .map(e -> e.getValue().toString())
                    .collect(Collectors.toSet());
        });
    }
    
    public static void addListener(InvalidationListener listener) {
        OBSERVABLE_DOCUMENT.addListener(listener);
    }
    
    public static void addListener(MapChangeListener<? super String, ? super String> listener) {
        OBSERVABLE_DOCUMENT.addListener((MapChangeListener<? super String, ? super Object>) listener);
    }
}
