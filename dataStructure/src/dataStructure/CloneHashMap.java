package dataStructure;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

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

    public CloneHashMap(int initialCapacity){
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    public CloneHashMap(){
        this.loadFactor = DEFAULT_LOAD_FACTOR;
    }

    public CloneHashMap(Map<? extends K, ? extends V> m){
        this.loadFactor = DEFAULT_LOAD_FACTOR;
        putMapEntries(m, false);
    }

    final void putMapEntries(Map<? extends K, ? extends V> m, boolean evict){
        int s = m.size();
        if (s > 0){
            if(table == null){
                float ft = ((float)s / loadFactor) + 1.0F;
                int t = ((ft < (float)MAXIMUM_CAPACITY) ?
                        (int)ft : MAXIMUM_CAPACITY);
                if(t > threshold)
                    threshold = tableSizeFor(t);
            }
            else if(s > threshold){
                resize();
            }

            for(Map.Entry<? extends K, ? extends V> e : m.entrySet()){
                K key = e.getKey();
                V value = e.getValue();
                putVal(hash(key), key, value, false, evict);
            }
        }
    }

    public int size(){
        return size;
    }

    public boolean isEmpty(){
        return size == 0;
    }

    public V get(Object key){
        CloneNode<K, V> e;
        return (e = getNode(hash(key), key)) == null ? null : e.value;
    }

    final CloneNode<K, V> getNode(int hash, Object key){
        CloneNode<K, V>[] tab;
        CloneNode<K, V> first, e;
        int n;
        K k;
        if((tab = table) != null && (n = tab.length) > 0 &&
            (first = tab[(n - 1) & hash]) != null){
            if (first.hash == hash &&
                ((k = first.key) == key || (key != null && key.equals(k))))
                return first;

            if ((e = first.next) != null){
                if (first instanceof CloneTreeNode)
                    return ((CloneTreeNode<K, V>)first).getTreeNode(hash, key);
                do{
                    if(e.hash == hash &&
                        (k = e.key) == key || (key != null && key.equals(k)))
                        return e;
                }while ((e = e.next) != null);
            }
        }
        return null;
    }

    public boolean containsKey(Object key){
        return getNode(hash(key), key) != null;
    }

    public V put(K key, V value){
        return putVal(hash(key), key, value, false, true);
    }

    final V putVal(int hash, K key, V value, boolean onlyIfAbsent,
                   boolean evict){
        CloneNode<K, V>[] tab;
        CloneNode<K, V> p;
        int n, i;

        if((tab = table) == null || (n = tab.length) == 0)
            n = (tab = resize()).length;

        if((p = tab[1 = (n - 1) & hash]) == null)
            tab[i] = newNode(hash, key, value, null);
        else{
            CloneNode<K, V> e;
            K k;
            if(p.hash == hash &&
                ((k = p.key) == key || (key != null && key.equals(k))))
                e = p;
            else if(p instanceof CloneTreeNode)
                e = ((CloneTreeNode<K, V>)p).putTreeVal(this, tab, hash, key, value);
            else{
                for(int binCount = 0; ; binCount++){
                    if((e = p.next) == null){
                        p.next = newNode(hash, key, value, null);
                        if(binCount >= TREEIFY_THRESHOLD - 1)
                            treeifyBin(tab, hash);
                        break;
                    }
                    if (e.hash == hash &&
                        ((k = e.key) == key || (key != null && key.equals(k))))
                        break;
                }
            }
            if(e != null){
                V oldValue = e.value;
                if(!onlyIfAbsent || oldValue == null)
                    e.value = value;

                afterNodeAccess(e);
                return oldValue;
            }
        }
        ++modCount;
        if(++size > threshold)
            resize();
        afterNodeInsertion(evict);
        return null;
    }

    final CloneNode<K, V>[] resize(){
        CloneNode<K, V>[] oldTab = table;
        int oldCap = (oldTab == null) ? 0 : oldTab.length;
        int oldThr = threshold;
        int newCap, newThr = 0;
        if(oldCap > 0){
            if(oldCap >= MAXIMUM_CAPACITY){
                threshold = Integer.MAX_VALUE;
                return oldTab;
            }
            else if((newCap = oldCap << 1) < MAXIMUM_CAPACITY &&
                    oldCap >= DEFAULT_INITIAL_CAPACITY)
                newThr = oldThr << 1;
        }
        else if(oldThr > 0)
            newCap = oldThr;
        else{
            newCap = DEFAULT_INITIAL_CAPACITY;
            newThr = (int)(DEFAULT_LOAD_FACTOR * DEFAULT_INITIAL_CAPACITY);
        }
        if(newThr == 0){
            float ft = (float)newCap * loadFactor;
            newThr = (newCap < MAXIMUM_CAPACITY && ft < (float)MAXIMUM_CAPACITY ?
                    (int)ft : Integer.MAX_VALUE);
        }
        threshold = newThr;
        @SuppressWarnings({"rawtypes","unchecked"})
        CloneNode<K, V>[] newTab = (CloneNode<K,V>[])new CloneNode[newCap];
        table = newTab;
        if(oldTab != null){
            for(int j=0; j<oldCap; j++){
                CloneNode<K,V> e;
                if((e = oldTab[j]) != null){
                    oldTab[j] = null;
                    if(e.next == null)
                        newTab[e.hash & (newCap - 1)] = e;
                    else if (e instanceof CloneTreeNode)
                        ((CloneTreeNode<K, V>e).split(this, newTab, j, oldCap));
                    else {
                        CloneNode<K, V> loHead = null, loTail = null;
                        CloneNode<K, V> hiHead = null, hiTail = null;
                        CloneNode<K, V> next;
                        do{
                            next = e.next;
                            if((e.hash & oldCap) == 0){
                                if(loTail == null)
                                    loHead = e;
                                else
                                    loTail.next = e;
                                loTail = e;
                            }
                            else {
                                if (hiTail == null)
                                    hiHead = e;
                                else
                                    hiTail.next = e;
                                hiTail = e;
                            }
                        }while((e = next) != null);
                        if(loTail != null){
                            loTail.next = null;
                            newTab[j] = loHead;
                        }
                        if(hiTail != null){
                            hiTail.next = null;
                            newTab[j + oldCap] = hiHead;
                        }
                    }

                }
            }
        }
        return newTab;
    }

}
