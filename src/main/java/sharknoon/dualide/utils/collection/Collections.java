/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sharknoon.dualide.utils.collection;

import static java.util.Collections.unmodifiableCollection;
import static java.util.Collections.unmodifiableSet;
import java.util.Comparator;
import java.util.ListIterator;
import java.util.function.UnaryOperator;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.StreamSupport;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
/**
 *
 * @author frank
 */
public class Collections {
    
    /**
     * Makes a List to a unmodifiable List, which prohibites any changes.
     * Otherwise the {@link Collections#unmodifiableList(List)} method, this
     * List will <b>not</b> throw any errors if you want to change something, it
     * just does nothing.
     *
     * @param <T> The Type of the List
     * @param list The list to be converted
     * @return The unmodifiable silent List
     */
    public static <T> List<T> silentUnmodifiableList(List<? extends T> list) {
        if (list == null) {
            return new SilentUnmodifiableList<>(new ArrayList<>());
        }
        return new SilentUnmodifiableList<>(list);
    }

    /**
     * Makes a Map to a unmodifiable Map, which prohibites any changes.
     * Otherwise the {@link Collections#unmodifiableMap(Map)} method, this Map
     * will <b>not</b> throw any errors if you want to change something, it just
     * does nothing.
     *
     * @param <K> The type of the Keys of the Map
     * @param <V> The type of the Values of the Map
     * @param map The Map to be converted
     * @return The unmodifiable silent Map
     */
    public static <K, V> Map<K, V> silentUnmodifiableMap(Map<? extends K, ? extends V> map) {
        if (map == null) {
            return new SilentUnmodifiableMap<>(new HashMap<>());
        }
        return new SilentUnmodifiableMap<>(map);
    }

    static class SilentUnmodifiableCollection<E> implements Collection<E>, Serializable {

        final Collection<? extends E> c;

        SilentUnmodifiableCollection(Collection<? extends E> c) {
            Objects.requireNonNull(c);
            this.c = c;
        }

        @Override
        public int size() {
            return c.size();
        }

        @Override
        public boolean isEmpty() {
            return c.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return c.contains(o);
        }

        @Override
        public Object[] toArray() {
            return c.toArray();
        }

        @Override
        public <T> T[] toArray(T[] a) {
            return c.toArray(a);
        }

        @Override
        public String toString() {
            return c.toString();
        }

        @Override
        public Iterator<E> iterator() {
            return new Iterator<E>() {
                private final Iterator<? extends E> i = c.iterator();

                @Override
                public boolean hasNext() {
                    return i.hasNext();
                }

                @Override
                public E next() {
                    return i.next();
                }

                @Override
                public void remove() {
                }

                @Override
                public void forEachRemaining(Consumer<? super E> action) {
                    // Use backing collection version
                    i.forEachRemaining(action);
                }
            };
        }

        @Override
        public boolean add(E e) {
            return false;
        }

        @Override
        public boolean remove(Object o) {
            return false;
        }

        @Override
        public boolean containsAll(Collection<?> coll) {
            return c.containsAll(coll);
        }

        @Override
        public boolean addAll(Collection<? extends E> coll) {
            return false;
        }

        @Override
        public boolean removeAll(Collection<?> coll) {
            return false;
        }

        @Override
        public boolean retainAll(Collection<?> coll) {
            return false;
        }

        @Override
        public void clear() {
        }

        // Override default methods in Collection
        @Override
        public void forEach(Consumer<? super E> action) {
            c.forEach(action);
        }

        @Override
        public boolean removeIf(Predicate<? super E> filter) {
            return false;
        }

        @SuppressWarnings("unchecked")
        @Override
        public Spliterator<E> spliterator() {
            return (Spliterator<E>) c.spliterator();
        }

        @SuppressWarnings("unchecked")
        @Override
        public Stream<E> stream() {
            return (Stream<E>) c.stream();
        }

        @SuppressWarnings("unchecked")
        @Override
        public Stream<E> parallelStream() {
            return (Stream<E>) c.parallelStream();
        }
    }

    static class SilentUnmodifiableList<E> extends SilentUnmodifiableCollection<E>
            implements List<E> {

        final List<? extends E> list;

        SilentUnmodifiableList(List<? extends E> list) {
            super(list);
            this.list = list;
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
            final SilentUnmodifiableList<?> other = (SilentUnmodifiableList<?>) obj;
            return Objects.equals(this.list, other.list);
        }

        @Override
        public int hashCode() {
            return list.hashCode();
        }

        @Override
        public E get(int index) {
            return list.get(index);
        }

        @Override
        public E set(int index, E element) {
            return element;
        }

        @Override
        public void add(int index, E element) {
        }

        @Override
        public E remove(int index) {
            return get(index);
        }

        @Override
        public int indexOf(Object o) {
            return list.indexOf(o);
        }

        @Override
        public int lastIndexOf(Object o) {
            return list.lastIndexOf(o);
        }

        @Override
        public boolean addAll(int index, Collection<? extends E> c) {
            return false;
        }

        @Override
        public void replaceAll(UnaryOperator<E> operator) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void sort(Comparator<? super E> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ListIterator<E> listIterator() {
            return listIterator(0);
        }

        @Override
        public ListIterator<E> listIterator(final int index) {
            return new ListIterator<E>() {
                private final ListIterator<? extends E> i
                        = list.listIterator(index);

                @Override
                public boolean hasNext() {
                    return i.hasNext();
                }

                @Override
                public E next() {
                    return i.next();
                }

                @Override
                public boolean hasPrevious() {
                    return i.hasPrevious();
                }

                @Override
                public E previous() {
                    return i.previous();
                }

                @Override
                public int nextIndex() {
                    return i.nextIndex();
                }

                @Override
                public int previousIndex() {
                    return i.previousIndex();
                }

                @Override
                public void remove() {
                }

                @Override
                public void set(E e) {
                }

                @Override
                public void add(E e) {
                }

                @Override
                public void forEachRemaining(Consumer<? super E> action) {
                    i.forEachRemaining(action);
                }
            };
        }

        @Override
        public List<E> subList(int fromIndex, int toIndex) {
            return new SilentUnmodifiableList<>(list.subList(fromIndex, toIndex));
        }

    }

    /**
     * @serial include
     */
    static class SilentUnmodifiableSet<E> extends SilentUnmodifiableCollection<E>
            implements Set<E>, Serializable {

        private static final long serialVersionUID = -9215047833775013803L;

        SilentUnmodifiableSet(Set<? extends E> s) {
            super(s);
        }

        @Override
        public boolean equals(Object o) {
            return o == this || c.equals(o);
        }

        @Override
        public int hashCode() {
            return c.hashCode();
        }
    }

    /**
     * @serial include
     */
    private static class SilentUnmodifiableMap<K, V> implements Map<K, V>, Serializable {

        private static final long serialVersionUID = -1034234728574286014L;

        private final Map<? extends K, ? extends V> m;

        SilentUnmodifiableMap(Map<? extends K, ? extends V> m) {
            if (m == null) {
                throw new NullPointerException();
            }
            this.m = m;
        }

        @Override
        public int size() {
            return m.size();
        }

        @Override
        public boolean isEmpty() {
            return m.isEmpty();
        }

        @Override
        public boolean containsKey(Object key) {
            return m.containsKey(key);
        }

        @Override
        public boolean containsValue(Object val) {
            return m.containsValue(val);
        }

        @Override
        public V get(Object key) {
            return m.get(key);
        }

        @Override
        public V put(K key, V value) {
            return value;
        }

        @Override
        public V remove(Object key) {
            return null;
        }

        @Override
        public void putAll(Map<? extends K, ? extends V> m) {
        }

        @Override
        public void clear() {
        }

        private transient Set<K> keySet;
        private transient Set<Map.Entry<K, V>> entrySet;
        private transient Collection<V> values;

        @Override
        public Set<K> keySet() {
            if (keySet == null) {
                keySet = unmodifiableSet(m.keySet());
            }
            return keySet;
        }

        @Override
        public Set<Map.Entry<K, V>> entrySet() {
            if (entrySet == null) {
                entrySet = new SilentUnmodifiableEntrySet<>(m.entrySet());
            }
            return entrySet;
        }

        @Override
        public Collection<V> values() {
            if (values == null) {
                values = unmodifiableCollection(m.values());
            }
            return values;
        }

        @Override
        public boolean equals(Object o) {
            return o == this || m.equals(o);
        }

        @Override
        public int hashCode() {
            return m.hashCode();
        }

        @Override
        public String toString() {
            return m.toString();
        }

        // Override default methods in Map
        @Override
        @SuppressWarnings("unchecked")
        public V getOrDefault(Object k, V defaultValue) {
            // Safe cast as we don't change the value
            return ((Map<K, V>) m).getOrDefault(k, defaultValue);
        }

        @Override
        public void forEach(BiConsumer<? super K, ? super V> action) {
            m.forEach(action);
        }

        @Override
        public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        }

        @Override
        public V putIfAbsent(K key, V value) {
            return value;
        }

        @Override
        public boolean remove(Object key, Object value) {
            return false;
        }

        @Override
        public boolean replace(K key, V oldValue, V newValue) {
            return false;
        }

        @Override
        public V replace(K key, V value) {
            return value;
        }

        @Override
        public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
            return null;
        }

        @Override
        public V computeIfPresent(K key,
                BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
            return null;
        }

        @Override
        public V compute(K key,
                BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
            return null;
        }

        @Override
        public V merge(K key, V value,
                BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
            return null;
        }

        /**
         * We need this class in addition to UnmodifiableSet as Map.Entries
         * themselves permit modification of the backing Map via their setValue
         * operation. This class is subtle: there are many possible attacks that
         * must be thwarted.
         *
         * @serial include
         */
        static class SilentUnmodifiableEntrySet<K, V>
                extends Collections.SilentUnmodifiableSet<Map.Entry<K, V>> {

            private static final long serialVersionUID = 7854390611657943733L;

            @SuppressWarnings({"unchecked", "rawtypes"})
            SilentUnmodifiableEntrySet(Set<? extends Map.Entry<? extends K, ? extends V>> s) {
                // Need to cast to raw in order to work around a limitation in the type system
                super((Set) s);
            }

            static <K, V> Consumer<Map.Entry<K, V>> entryConsumer(Consumer<? super Map.Entry<K, V>> action) {
                return e -> action.accept(new SilentUnmodifiableEntry<>(e));
            }

            @Override
            public void forEach(Consumer<? super Map.Entry<K, V>> action) {
                if (action != null) {
                    c.forEach(entryConsumer(action));
                }
            }

            static final class SilentUnmodifiableEntrySetSpliterator<K, V>
                    implements Spliterator<Map.Entry<K, V>> {

                final Spliterator<Map.Entry<K, V>> s;

                SilentUnmodifiableEntrySetSpliterator(Spliterator<Map.Entry<K, V>> s) {
                    this.s = s;
                }

                @Override
                public boolean tryAdvance(Consumer<? super Map.Entry<K, V>> action) {
                    return action == null ? false : s.tryAdvance(entryConsumer(action));
                }

                @Override
                public void forEachRemaining(Consumer<? super Map.Entry<K, V>> action) {
                    if (action != null) {
                        s.forEachRemaining(entryConsumer(action));
                    }
                }

                @Override
                public Spliterator<Map.Entry<K, V>> trySplit() {
                    Spliterator<Map.Entry<K, V>> split = s.trySplit();
                    return split == null
                            ? null
                            : new SilentUnmodifiableEntrySetSpliterator<>(split);
                }

                @Override
                public long estimateSize() {
                    return s.estimateSize();
                }

                @Override
                public long getExactSizeIfKnown() {
                    return s.getExactSizeIfKnown();
                }

                @Override
                public int characteristics() {
                    return s.characteristics();
                }

                @Override
                public boolean hasCharacteristics(int characteristics) {
                    return s.hasCharacteristics(characteristics);
                }

                @Override
                public Comparator<? super Map.Entry<K, V>> getComparator() {
                    return s.getComparator();
                }
            }

            @SuppressWarnings("unchecked")
            @Override
            public Spliterator<Map.Entry<K, V>> spliterator() {
                return new SilentUnmodifiableEntrySetSpliterator<>(
                        (Spliterator<Map.Entry<K, V>>) c.spliterator());
            }

            @Override
            public Stream<Map.Entry<K, V>> stream() {
                return StreamSupport.stream(spliterator(), false);
            }

            @Override
            public Stream<Map.Entry<K, V>> parallelStream() {
                return StreamSupport.stream(spliterator(), true);
            }

            @Override
            public Iterator<Map.Entry<K, V>> iterator() {
                return new Iterator<Map.Entry<K, V>>() {
                    private final Iterator<? extends Map.Entry<? extends K, ? extends V>> i = c.iterator();

                    @Override
                    public boolean hasNext() {
                        return i.hasNext();
                    }

                    @Override
                    public Map.Entry<K, V> next() {
                        return new SilentUnmodifiableEntry<>(i.next());
                    }

                    @Override
                    public void remove() {
                    }
                };
            }

            @SuppressWarnings("unchecked")
            @Override
            public Object[] toArray() {
                Object[] a = c.toArray();
                for (int i = 0; i < a.length; i++) {
                    a[i] = new SilentUnmodifiableEntry<>((Map.Entry<? extends K, ? extends V>) a[i]);
                }
                return a;
            }

            @SuppressWarnings("unchecked")
            @Override
            public <T> T[] toArray(T[] a) {
                // We don't pass a to c.toArray, to avoid window of
                // vulnerability wherein an unscrupulous multithreaded client
                // could get his hands on raw (unwrapped) Entries from c.
                Object[] arr = c.toArray(a.length == 0 ? a : Arrays.copyOf(a, 0));

                for (int i = 0; i < arr.length; i++) {
                    arr[i] = new SilentUnmodifiableEntry<>((Map.Entry<? extends K, ? extends V>) arr[i]);
                }

                if (arr.length > a.length) {
                    return (T[]) arr;
                }

                System.arraycopy(arr, 0, a, 0, arr.length);
                if (a.length > arr.length) {
                    a[arr.length] = null;
                }
                return a;
            }

            /**
             * This method is overridden to protect the backing set against an
             * object with a nefarious equals function that senses that the
             * equality-candidate is Map.Entry and calls its setValue method.
             */
            @Override
            public boolean contains(Object o) {
                if (!(o instanceof Map.Entry)) {
                    return false;
                }
                return c.contains(
                        new SilentUnmodifiableEntry<>((Map.Entry<?, ?>) o));
            }

            /**
             * The next two methods are overridden to protect against an
             * unscrupulous List whose contains(Object o) method senses when o
             * is a Map.Entry, and calls o.setValue.
             */
            @Override
            public boolean containsAll(Collection<?> coll) {
                for (Object e : coll) {
                    if (!contains(e)) // Invokes safe contains() above
                    {
                        return false;
                    }
                }
                return true;
            }

            @Override
            public boolean equals(Object o) {
                if (o == this) {
                    return true;
                }

                if (!(o instanceof Set)) {
                    return false;
                }
                Set<?> s = (Set<?>) o;
                if (s.size() != c.size()) {
                    return false;
                }
                return containsAll(s); // Invokes safe containsAll() above
            }

            /**
             * This "wrapper class" serves two purposes: it prevents the client
             * from modifying the backing Map, by short-circuiting the setValue
             * method, and it protects the backing Map against an ill-behaved
             * Map.Entry that attempts to modify another Map Entry when asked to
             * perform an equality check.
             */
            private static class SilentUnmodifiableEntry<K, V> implements Map.Entry<K, V> {

                private Map.Entry<? extends K, ? extends V> e;

                SilentUnmodifiableEntry(Map.Entry<? extends K, ? extends V> e) {
                    this.e = Objects.requireNonNull(e);
                }

                @Override
                public K getKey() {
                    return e.getKey();
                }

                @Override
                public V getValue() {
                    return e.getValue();
                }

                @Override
                public V setValue(V value) {
                    return value;
                }

                @Override
                public int hashCode() {
                    return e.hashCode();
                }

                @Override
                public boolean equals(Object o) {
                    if (this == o) {
                        return true;
                    }
                    if (!(o instanceof Map.Entry)) {
                        return false;
                    }
                    Map.Entry<?, ?> t = (Map.Entry<?, ?>) o;
                    return eq(e.getKey(), t.getKey())
                            && eq(e.getValue(), t.getValue());
                }

                @Override
                public String toString() {
                    return e.toString();
                }
            }
        }
    }

    static boolean eq(Object o1, Object o2) {
        return o1 == null ? o2 == null : o1.equals(o2);
}
}
