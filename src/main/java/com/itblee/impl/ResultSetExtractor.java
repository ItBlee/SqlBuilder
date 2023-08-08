package com.itblee.impl;

import com.itblee.SqlExtractor;
import com.itblee.entity.BaseEntity;
import com.itblee.model.MappedResult;
import com.itblee.util.CastUtils;
import com.itblee.util.MapUtils;
import com.itblee.util.ValidateUtils;
import com.itblee.util.function.ThrowableBiConsumer;
import com.itblee.util.function.ThrowableFunction;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import java.util.*;

public class ResultSetExtractor implements SqlExtractor {

    @Override
    public <T> List<T> extractData(ResultSet resultSet, Class<T> tClass) throws SQLException {
        ValidateUtils.requireNonNull(resultSet);
        Map<Long, T> groupById = new HashMap<>();
        Map<Long, MappedResult> results = new LinkedHashMap<>();
        for (Map<String, Object> row : extractRows(resultSet)) {
            Long id = MapUtils.getAndCast(row, "id", Long.class)
                    .orElseThrow(() -> new SQLSyntaxErrorException("Query without ID."));
            T instance = groupById.computeIfAbsent(id, (ThrowableFunction<Long, T>) inst -> tClass.newInstance());
            results.computeIfAbsent(id, result -> new MappedResult())
                    .add(mapRowToObject(instance, row));
        }
        results.forEach((id, result) -> {
            result.setTitle(tClass.getSimpleName() + " " + id);
            result.print();
        });
        return new ArrayList<>(groupById.values());
    }

    @Override
    public List<Map<String, Object>> extractRows(ResultSet resultSet) throws SQLException {
        List<Map<String, Object>> row = new ArrayList<>();
        ResultSetMetaData md = resultSet.getMetaData();
        while (resultSet.next()) {
            Map<String, Object> col = new LinkedHashMap<>();
            for (int i = 1; i <= md.getColumnCount(); i++) {
                if (resultSet.getObject(i) != null)
                    col.put(md.getColumnLabel(i), resultSet.getObject(i));
            }
            row.add(col);
        }
        return row;
    }

    private MappedResult mapRowToObject(Object instance, Map<String, ?> row) {
        ValidateUtils.requireNonNull(row);
        ValidateUtils.requireNonNull(instance);
        MappedResult results = new MappedResult();
        Map<Field, Object> childFields = new LinkedHashMap<>();
        row.forEach((ThrowableBiConsumer<String, Object>)
                (colName, colValue) -> results.add(
                        mapFieldIfMatchColumn(instance, colName, colValue, childFields, true)
                ));
        setFinalValueForChildFields(instance, childFields);
        return results;
    }

    private MappedResult mapFieldIfMatchColumn(final Object instance, String colName, Object colValue, Map<Field, Object> childFields, boolean isRoot) throws IllegalAccessException {
        MappedResult results = new MappedResult();
        StringTokenizer colNameTokens = new StringTokenizer(colName, ".");
        String fieldName = colNameTokens.nextToken();
        getSettersOf(instance).forEach((ThrowableBiConsumer<Field, Method>) (field, setter) -> {
            if (fieldName.equalsIgnoreCase(field.getName())) {
                Class<?> fieldType = field.getType();
                boolean isCollection = Collection.class.isAssignableFrom(fieldType);
                boolean isEntity = BaseEntity.class.isAssignableFrom(fieldType);
                boolean isChild = isCollection || isEntity;
                if (isChild) {
                    if (isCollection)
                        fieldType = (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                    Object childInstance = childFields.getOrDefault(field, fieldType.newInstance());
                    String childColName = colName.replaceFirst(fieldName + ".","");
                    results.add(
                            mapFieldIfMatchColumn(childInstance, childColName, colValue, childFields, false)
                    );
                    childFields.put(field, childInstance);
                } else if (!colNameTokens.hasMoreTokens()) {
                    try {
                        setter.invoke(instance, CastUtils.cast(colValue, fieldType).orElse(null));
                    } catch (Exception e) {
                        throw new RuntimeException("Value of column "
                                + instance.getClass().getSimpleName() + "." + colName
                                + "=" + colValue
                                + " not match " + instance.getClass().getSimpleName() + "." + field.getName()
                                +  " type " + fieldType.getSimpleName(), e.getCause());
                    }
                } else return;
                if (isRoot) {
                    results.newResult()
                            .setFieldType(fieldType.getSimpleName())
                            .setEntityField(instance.getClass().getSimpleName() + "." + field.getName())
                            .setColumnName(colName)
                            .setColumnValue(colValue)
                            .add();
                }
            }
        });
        return results;
    }

    private void setFinalValueForChildFields(final Object instance, final Map<Field, Object> childFields) {
        childFields.forEach((ThrowableBiConsumer<Field, Object>) (field, value) -> {
            boolean accessible = field.isAccessible();
            field.setAccessible(true);
            if (Collection.class.isAssignableFrom(field.getType())) {
                Object rootCollection = field.get(instance);
                Method addElement = rootCollection.getClass().getMethod("add", Object.class);
                addElement.invoke(rootCollection, value);
            } else field.set(instance, value);
            field.setAccessible(accessible);
        });
    }

    private List<Field> getFieldsOf(Object o) {
        List<Field> fields = new ArrayList<>();
        Class<?> fieldType = o.getClass().getSuperclass();
        while (fieldType != Object.class) {
            fields.addAll(Arrays.asList(fieldType.getDeclaredFields()));
            fieldType = fieldType.getSuperclass();
        }
        fields.addAll(Arrays.asList(o.getClass().getDeclaredFields()));
        return fields;
    }

    private Map<Field, Method> getSettersOf(Object o) {
        List<Field> fields = getFieldsOf(o);
        Map<Field, Method> setters = new LinkedHashMap<>();
        for (Field field : fields) {
            Arrays.stream(o.getClass().getMethods())
                    .filter(m -> m.getName().equalsIgnoreCase("set" + field.getName()))
                    .filter(m -> m.getParameterTypes().length ==  1)
                    .filter(m -> m.getParameterTypes()[0].equals(field.getType()))
                    .findFirst()
                    .ifPresent(method -> setters.put(field, method));
        }
        return setters;
    }

}
