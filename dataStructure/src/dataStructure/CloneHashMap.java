package dataStructure;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class CloneHashMap<K, V> extends AbstractMap<K, V>
    implements Map<K, V>, Cloneable, Serializable {

    private static final long serialVersionUID = 362498820763181265L;

    static final int DEFAULT_INITIAL_CAPACITY = 1 << 4;

    static final int MAXIMUM_CAPACITY = 1 << 30;

    static final float DEFAULT_LOAD_FACTOR = 0.75f;

    static final int TREEIFY_THRESHOLD = 8;

    static final int UNTREEIFY_THRESHOLD = 6;

    static final int MIN_TREEIFY_CAPACITY = 64;

    static class CloneNode<K, V> implements Map.Entry<K, V> {
        final int hash;
        final K key;
        V value;
        CloneNode<K, V> next;

        CloneNode(int hash, K key, V value, CloneNode<K, V> next){
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }

        public final K getKey(){
            return key;
        }
        public final V getValue(){
            return value;
        }
        public final String toString(){
            return key + "=" + value;
        }

        public final int hashCode(){
            return Objects.hashCode(key) ^ Objects.hashCode(value);
        }

        public final V setValue(V newValue){
            V oldValue = value;
            value = newValue;
            return oldValue;
        }

        public final boolean equals(Object o){
            if(o == this)
                return true;

            if(o instanceof Map.Entry){
                Map.Entry<?, ?> e = (Map.Entry<?, ?>)o;
                if(Objects.equals(key, e.getKey()) &&
                   Objects.equals(value, e.getValue()))
                    return true;
            }

            return false;
        }

    }

    static final int hash(Object key){
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }

    static Class<?> comparableClassFor(Object x) {
        if (x instanceof Comparable){
            Class<?> c;
            Type[] ts, as;
            Type t;
            ParameterizedType p;

            if((c = x.getClass()) == String.class) //bypass checks
                return c;

            if((ts = c.getGenericInterfaces()) != null){
                for(int i=0; i<ts.length; ++i){
                    if(((t = ts[i]) instanceof ParameterizedType) &&
                        ((p = (ParameterizedType) t).getRawType() ==
                        Comparable.class) &&
                        (as = p.getActualTypeArguments()) != null &&
                        as.length == 1 && as[0] == c)
                        return c;
                }
            }
        }
        return null;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    static int compareComparables(Class<?> kc, Object k, Object x){
        return (x == null || x.getClass() != kc ? 0 :
                ((Comparable)k).compareTo(x));
    }

    static final int tableSizeFor(int cap){
        int n = cap - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
    }

    transient CloneNode<K, V>[] table;

    transient Set<Entry<K, V>> entrySet;

    transient int size;

    transient int modCount;

    int threshold;

    final float loadFactor;

    public CloneHashMap(int initialCapacity, float loadFactor){
        if(initialCapacity < 0)
            throw new IllegalArgumentException("Illegal initial capacity: " +
                                                initialCapacity);

        if(initialCapacity > MAXIMUM_CAPACITY)
            initialCapacity = MAXIMUM_CAPACITY;

        if(loadFactor <= 0 || Float.isNaN(loadFactor))
            throw new IllegalArgumentException("Illegal load factor: " +
                                                loadFactor);

        this.loadFactor = loadFactor;
        this.threshold = tableSizeFor(initialCapacity);
    }

}
