package com.funkyquest.app.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapOLists<K, V> {
    private Map<K, List<V>> map = new HashMap<K, List<V>>();

    public void remove(K key, V value) {
        List<V> values = map.get(key);
        if (values != null) {
            values.remove(value);
        }
    }

    public List<V> get(K key) {
        return map.get(key);
    }

    public void put(final K key, final V value) {
        List<V> values = map.get(key);
        if (values == null) {
            map.put(key, new ArrayList<V>() {{
                add(value);
            }});
        } else {
            values.add(value);
        }
    }

    public void clear() {
        map.clear();
    }
}