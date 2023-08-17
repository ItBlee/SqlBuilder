package com.itblee.repository.sqlbuilder;

import com.itblee.repository.sqlbuilder.model.Range;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface SqlMap<K extends SqlKey> extends Map<SqlStatement, Object> {
    void addScope(K key);
    Object put(K key, Object value);
    Range put(K key, Object from, Object to);
    void putAll(Map<?, ?> params, Class<K> kClass);
    Object get(K key);
    Object getOrDefault(K key, Object defaultValue);
    boolean containsKey(K key);
    Object compute(K key, BiFunction<? super SqlStatement, ? super Object, ?> remappingFunction);
    Object computeIfPresent(K key, BiFunction<? super SqlStatement, ? super Object, ?> remappingFunction);
    Object computeIfAbsent(K key, Function<? super SqlStatement, ?> mappingFunction);
}
