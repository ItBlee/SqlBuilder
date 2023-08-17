package com.itblee.util;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public final class MapUtils {

    private MapUtils() {
        throw new AssertionError();
    }

    public static Object getOrNull(Map<?, ?> map, Object key) {
        return map.getOrDefault(key, null);
    }

    public static <T> Optional<T> getAndCast(Map<?, ?> map, Object key, Class<T> cast) {
        return CastUtils.cast(getOrNull(map, key), cast);
    }

    public static <T extends Enum<T>> Map<String, String> of(T[] enums) {
        return Arrays.stream(enums).collect(Collectors.toMap(T::name, T::toString, (s, s2) -> s, LinkedHashMap::new));
    }

    public static Map<?, ?> from(Object o, boolean ignoreBlank) {
        Map<String, Object> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for (Field field : o.getClass().getDeclaredFields()) {
            boolean accessible = field.isAccessible();
            field.setAccessible(true);
            try {
                Object value = field.get(o);
                if (!(ignoreBlank && StringUtils.isBlank(value.toString())))
                    map.put(field.getName(), value);
            } catch (Exception ignored) { }
            field.setAccessible(accessible);
        }
        return map;
    }

}
