/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sharknoon.dualide.utils.settings;

import java.util.ArrayList;
import java.util.List;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.NitriteCollection;
import org.dizitart.no2.objects.ObjectFilter;
import org.dizitart.no2.objects.ObjectRepository;

/**
 *
 * @author frank
 */
public class Database {

    private static final Nitrite DB = Nitrite
            .builder()
            .filePath(Ressources.createAndGetFile("alioth.db", true).toFile())
            .openOrCreate();

    public static <T> void store(T... objects) {
        if (objects.length > 0) {
            ObjectRepository<T> repository = DB.getRepository((Class) objects.getClass().getComponentType());
            repository.insert(objects);
        }
    }

    public static <T> List<T> get(Class<T> type, ObjectFilter filter) {
        ObjectRepository<T> repository = DB.getRepository(type);
        List<T> result = new ArrayList<>();
        if (repository.size() < 1) {
            return result;
        }
        if (filter == null) {
            repository.find().forEach(result::add);
        } else {
            repository.find(filter).forEach(result::add);
        }
        return result;
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
    public static <T> int delete(Class<T> type, ObjectFilter filter) {
        ObjectRepository<T> repository = DB.getRepository(type);
        if (filter == null) {
            long deleted = repository.size();
            repository.drop();
            return (int) deleted;
        }
        return repository.remove(filter).getAffectedCount();
    }

    /**
     * TODO not yet working, Objects needs to have an @ID Annotation
     *
     * @param <T>
     * @param objects
     */
    private static <T> void delete(T... objects) {
        if (objects.length > 0) {
            ObjectRepository<T> repository = DB.getRepository((Class<T>) objects.getClass().getComponentType());
            for (T object : objects) {
                //TODO not yet working, Objects needs to have an @ID Annotation
                repository.remove(object);
            }
        }
    }

    public static NitriteCollection getCollection(String name){
        return DB.getCollection(name);
    }
    
    
    /**
     * USE WITH CAUTION, RESETS THE COMPLETE DATABASE!!!!
     */
    public static void reset() {
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
    }
}
