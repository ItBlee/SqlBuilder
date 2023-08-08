package com.itblee.impl;

import com.itblee.SqlBuilder;
import com.itblee.SqlMap;
import com.itblee.model.SqlQuery;
import com.itblee.util.ValidateUtils;

import java.util.LinkedHashMap;
import java.util.Map;

public class SqlBuilderFactory {

    private final SqlMap<?> statements;
    private SqlBuilder builder;

    private SqlBuilderFactory(SqlMap<?> statements) {
        this.statements = statements;
    }

    public static SqlBuilderFactory newInstance(String action, SqlMap<?> statements) {
        ValidateUtils.requireNonNull(statements);
        SqlBuilderFactory factory = new SqlBuilderFactory(statements);
        switch (action) {
            case "query":
                factory.builder = factory.newQueryBuilder();
                break;
            case "insert":
                factory.builder = factory.newInsertBuilder();
                break;
            case "update":
                factory.builder = factory.newUpdateBuilder();
                break;
            case "delete":
                factory.builder = factory.newDeleteBuilder();
                break;
            default:
                throw new UnsupportedOperationException();
        }
        return factory;
    }

    public SqlBuilder getBuilder() {
        return this.builder;
    }

    private SqlBuilder newQueryBuilder() {
        Map<SqlQuery, Object> queries = new LinkedHashMap<>();
        statements.entrySet().stream()
                .filter(stmt -> stmt.getKey() instanceof SqlQuery)
                .forEach(stmt -> queries.put((SqlQuery) stmt.getKey(), stmt.getValue()));
        return new SqlQueryBuilder(queries);
    }

    private SqlBuilder newInsertBuilder() {
        throw new UnsupportedOperationException();
    }

    private SqlBuilder newUpdateBuilder() {
        throw new UnsupportedOperationException();
    }

    private SqlBuilder newDeleteBuilder() {
        throw new UnsupportedOperationException();
    }

}
