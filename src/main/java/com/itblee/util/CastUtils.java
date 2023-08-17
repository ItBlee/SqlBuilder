package com.itblee.util;

import com.itblee.repository.sqlbuilder.model.Code;
import com.itblee.repository.sqlbuilder.model.CodeList;
import com.itblee.repository.sqlbuilder.model.CodeSet;
import org.apache.commons.lang.math.NumberUtils;

import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class CastUtils {

    private static final String STRING_DELIMITER = ",";

    private CastUtils() {
        throw new AssertionError();
    }

    public static <T> Optional<T> cast(final Object o, Class<T> cls) {
        if (o == null)
            return Optional.empty();
        if (cls == null)
            throw new ClassCastException("Couldn't cast to null.");
        if (cls.isInstance(o))
            return Optional.of(cls.cast(o));

        String str = castToString(o);
        if (StringUtils.isBlank(str))
            return Optional.empty();

        Object obj;
        try {
            if (Collection.class.isAssignableFrom(cls)) {
                String[] arr = str.split(STRING_DELIMITER);
                Stream<?> stream = Arrays.stream(arr);
                if (CodeSet.class.isAssignableFrom(cls)) {
                    obj = stream.map(s -> new Code(s.toString()))
                            .collect(Collectors.toCollection(CodeSet::new));
                } else if (CodeList.class.isAssignableFrom(cls)) {
                    obj = stream.map(s -> new Code(s.toString()))
                            .collect(Collectors.toCollection(CodeList::new));
                } else if (Set.class.isAssignableFrom(cls)) {
                    obj = stream.map(s -> {
                        String temp = String.valueOf(s);
                        if (NumberUtils.isNumber(temp))
                            return Long.parseLong(temp);
                        return temp;
                    }).collect(Collectors.toSet());
                } else if (List.class.isAssignableFrom(cls)) {
                    obj = stream.map(s -> {
                        String temp = String.valueOf(s);
                        if (NumberUtils.isNumber(temp))
                            return Long.parseLong(temp);
                        return temp;
                    }).collect(Collectors.toList());
                } else throw new ClassCastException("Cast to " + cls.getSimpleName() + " not supported yet.");
            }
            else if (cls.isArray()) {
                String[] arr = str.split(STRING_DELIMITER);
                if (CharSequence[].class.isAssignableFrom(cls)) {
                    obj = arr;
                } else if (Integer[].class.isAssignableFrom(cls)) {
                    obj = Arrays.stream(arr).map(Integer::parseInt).toArray(Integer[]::new);
                } else if (Long[].class.isAssignableFrom(cls)) {
                    obj = Arrays.stream(arr).map(Long::parseLong).toArray(Long[]::new);
                } else {
                    throw new ClassCastException("Cast to " + cls.getSimpleName() + " not supported yet.");
                }
            } else {
                if (Code.class.isAssignableFrom(cls)) {
                    obj = Code.valueOf(str);
                } else if (CharSequence.class.isAssignableFrom(cls)) {
                    obj = str;
                } else if (Integer.class.isAssignableFrom(cls)) {
                    obj = Integer.valueOf(str);
                } else if (Long.class.isAssignableFrom(cls)) {
                    obj = Long.valueOf(str);
                } else if (Date.class.isAssignableFrom(cls)) {
                    obj = Date.valueOf(str);
                } else {
                    throw new ClassCastException("Cast to " + cls.getSimpleName() + " not supported yet.");
                }
            }

            return Optional.of(cls.cast(obj));
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid value type:" + e.getMessage());
        }
    }

    public static String castToString(Object o) {
        ValidateUtils.requireNonNull(o);
        String str;
        if (o instanceof CharSequence || o instanceof Number || o instanceof Date) {
            str = o.toString();
        } else if (o.getClass().isArray()) {
            Object[] arr = (Object[]) o;
            str = String.join(STRING_DELIMITER, Arrays.copyOf(arr, arr.length, String[].class));
        } else if (o instanceof Collection<?>) {
            Object[] arr = ((Collection<?>) o).toArray();
            str = String.join(STRING_DELIMITER, Arrays.copyOf(arr, arr.length, String[].class));
        } else
            throw new ClassCastException("Cast type " + o.getClass().getName() + " not supported yet.");
        return str;
    }

}
