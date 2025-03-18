package org.factory;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;

public class Factory<K, D, T> {

    public final Map<K, Function<D, T>> creators = new HashMap<>();

    public void add(K key, Function<D, T> method) {
        creators.put(key, method);
    }

    public T create(K key, D param) {
        Function<D, T> func = creators.get(key);
        if (null == func) {
            throw new NoSuchElementException("no method is registered for key " + key);
        }
        return func.apply(param);
    }

}
