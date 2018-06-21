/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sharknoon.dualide.utils.settings;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.NitriteCollection;
import org.dizitart.no2.objects.ObjectFilter;
import org.dizitart.no2.objects.ObjectRepository;
import sharknoon.dualide.ui.MainApplication;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 *
 * @author frank
 */
public class Database {
    
    private static Nitrite DB;
    
    static {
        try {
            DB = Nitrite
                    .builder()
                    .filePath(Resources.createAndGetFile("sharknoon/dualide/utils/settings/dualide.db", true).toFile())
                    .openOrCreate();
        } catch (Exception e) {
            MainApplication.stopApp(e.getMessage());
        }
        MainApplication.registerExitable(() -> {
            if (DB != null) {
                DB.close();
            }
        });
    }

    public static <T> void store(T... objects) {
        CompletableFuture.runAsync(() -> {
            if (objects.length > 0) {
                ObjectRepository<T> repository = DB.getRepository((Class<T>) objects.getClass().getComponentType());
                for (T object : objects) {
                    repository.update(object, true);
                }
            }
        });
    }

    public static <T> CompletableFuture<List<T>> get(Class<T> type) {
        return get(type, null);
    }

    public static <T> CompletableFuture<List<T>> get(Class<T> type, ObjectFilter filter) {
        return CompletableFuture.supplyAsync(() -> {
            if (type == null) {
                return Collections.emptyList();
            }
            ObjectRepository<T> repository = DB.getRepository(type);
            List<T> result;
            if (repository.size() < 1) {
                return Collections.emptyList();
            }
            if (filter == null) {
                result = repository.find().toList();
            } else {
                result = repository.find(filter).toList();
            }
            return result;
        });
    }

    public static <T> CompletableFuture<Integer> delete(Class<T> type) {
        return delete(type, null);
    }

    /**
     * Deletes a Class out of the database
     *
     * @param <T>
     * @param type
     * @param filter optional filter, WARNING, no filter removes ALL instances
     * of a class!!!
     * @return The amount of deleted classes
     */
    public static <T> CompletableFuture<Integer> delete(Class<T> type, ObjectFilter filter) {
        return CompletableFuture.supplyAsync(() -> {
            if (type == null) {
                return 0;
            }
            ObjectRepository<T> repository = DB.getRepository(type);
            if (filter == null) {
                long deleted = repository.size();
                repository.drop();
                return (int) deleted;
            }
            return repository.remove(filter).getAffectedCount();
        });
    }

    /**
     * Need to have an ID!!!!
     *
     * @param <T>
     * @param objects
     */
    public static <T> void delete(T... objects) {
        if (objects.length > 0) {
            ObjectRepository<T> repository = DB.getRepository((Class<T>) objects.getClass().getComponentType());
            for (T object : objects) {
                repository.remove(object);
            }
        }
    }

    public static CompletableFuture<NitriteCollection> getCollection(String name) {
        return CompletableFuture.supplyAsync(() -> {
            return DB.getCollection(name);
        });
    }

    /**
     * USE WITH CAUTION, RESETS THE COMPLETE DATABASE!!!!
     */
    public static void reset() {
        CompletableFuture.runAsync(() -> {
            DB.listCollectionNames()
                    .stream()
                    .map(DB::getCollection)
                    .forEach(NitriteCollection::drop);
            DB.listRepositories()
                    .stream()
                    .forEach(name -> {
                        try {
                            DB.getRepository(Class.forName(name)).drop();
                        } catch (ClassNotFoundException ex) {
                        }
                    });
        });
    }

}
