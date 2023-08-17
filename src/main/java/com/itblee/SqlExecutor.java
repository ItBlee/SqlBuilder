package com.itblee.repository.sqlbuilder;

import java.util.List;

public interface SqlExecutor {
    <T> List<T> executeQuery(String sql, Class<T> entityClass, Object... params);
    Object executeInsert(String sql, Object... params);
    void executeUpdate(String sql, Object... params);
}
