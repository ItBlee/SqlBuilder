package com.itblee.impl;

import com.itblee.SqlKey;
import com.itblee.SqlMap;
import com.itblee.SqlStatement;
import com.itblee.exception.BadRequestException;
import com.itblee.model.ForwardingMap;
import com.itblee.model.Range;
import com.itblee.util.CastUtils;
import com.itblee.util.MapUtils;
import com.itblee.util.StringUtils;
import com.itblee.util.ValidateUtils;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.itblee.model.Range.RANGE_FROM;
import static com.itblee.model.Range.RANGE_TO;

public class LinkedSqlMap<K extends SqlKey> extends ForwardingMap<SqlStatement, Object> implements SqlMap<K> {

	private static final long serialVersionUID = -6418551830238036585L;

    public LinkedSqlMap() {
        super(new LinkedHashMap<>());
    }

    @Override
    public void addScope(K key) {
        ValidateUtils.requireNonNull(key);
        ValidateUtils.requireNonNull(key.getStatement());
        if (!key.isScope())
            throw new IllegalArgumentException("Required Scope not key.");
        super.put(key.getStatement(), null);
    }

    @Override
    public Object put(SqlStatement statement, Object value) {
        ValidateUtils.requireNonNull(statement);
        return super.put(statement, value);
    }

    @Override
    public Object put(K key, Object value) {
        ValidateUtils.requireNonNull(key);
        ValidateUtils.requireNonNull(value);
        try {
            if (key.isScope())
                throw new IllegalArgumentException("Unsupported param.");
            Object cast = CastUtils.cast(value, key.getType())
                    .orElseThrow(() -> new IllegalArgumentException("Unsupported type."));
            return put(key.getStatement(), cast);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(key.getParamName() + ": " + e.getMessage());
        }
    }

    @Override
    public Range put(K key, Object from, Object to) {
        ValidateUtils.requireNonNull(key);
        try {
            if (key.isScope())
                throw new IllegalStateException("Unsupported param.");
            Number fromNum = CastUtils.cast(from, Integer.class).orElse(null);
            Number toNum = CastUtils.cast(to, Integer.class).orElse(null);
            return (Range) super.put(key.getStatement(), Range.valueOf(fromNum, toNum));
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new BadRequestException(key.getParamName() + " invalid: " + e.getMessage());
        }
    }

    @Override
    public void putAll(Map<? extends SqlStatement, ?> m) {
        m.forEach(this::put);
    }

    @Override
    public void putAll(Map<?, ?> params, Class<K> kClass) {
        ValidateUtils.requireNonNull(params);
        ValidateUtils.requireNonNull(kClass);
        params.forEach((param, val) -> {
            String keyName = StringUtils.removeIfLast(param.toString(), RANGE_FROM, RANGE_TO);
            Map<String, SqlKey> keys = Arrays.stream(kClass.getEnumConstants())
                    .filter(key -> !key.isScope())
                    .collect(Collectors.toMap(SqlKey::getParamName, Function.identity(),
                            (o1, o2) -> o1, () -> new TreeMap<>(String.CASE_INSENSITIVE_ORDER)));
            K key = MapUtils.getAndCast(keys, keyName, kClass).orElse(null);
            if (key == null)
                throw new BadRequestException(param + ": Unsupported.");
            if (!key.getType().equals(Range.class))
                put(key, val);
            else put(key, params.getOrDefault(key.getParamName() + RANGE_FROM, null),
                    params.getOrDefault(key.getParamName() + RANGE_TO, null));
        });
    }

    @Override
    public Object get(K key) {
        return super.get(key.getStatement());
    }

    @Override
    public Object getOrDefault(K key, Object defaultValue) {
        return super.getOrDefault(key.getStatement(), defaultValue);
    }

    @Override
    public boolean containsKey(K key) {
        return super.containsKey(key.getStatement());
    }

    @Override
    public Object compute(K key, BiFunction<? super SqlStatement, ? super Object, ?> remappingFunction) {
        return super.compute(key.getStatement(), remappingFunction);
    }

    @Override
    public Object computeIfPresent(K key, BiFunction<? super SqlStatement, ? super Object, ?> remappingFunction) {
        return super.computeIfPresent(key.getStatement(), remappingFunction);
    }

    @Override
    public Object computeIfAbsent(K key, Function<? super SqlStatement, ?> mappingFunction) {
        return super.computeIfAbsent(key.getStatement(), mappingFunction);
    }

    @Override
    public Object putIfAbsent(SqlStatement statement, Object value) {
        Object stmt = getOrDefault(statement, null);
        if (stmt == null)
            stmt = this.put(statement, value);
        return stmt;
    }

}
