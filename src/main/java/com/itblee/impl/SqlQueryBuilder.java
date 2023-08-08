package com.itblee.impl;

import com.itblee.QueryBuilder;
import com.itblee.SqlBuilder;
import com.itblee.SqlFormatter;
import com.itblee.model.SqlQuery;

import java.io.Serializable;
import java.sql.SQLSyntaxErrorException;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

class SqlQueryBuilder implements Serializable, SqlBuilder.Query {

    private static final long serialVersionUID = 6764670140121398972L;

    private final Map<SqlQuery, ?> statements;
    private int limit = -1;
    private int offset = -1;

    public SqlQueryBuilder(Map<SqlQuery, ?> statements) {
        this.statements = Collections.unmodifiableMap(statements);
    }

    @Override
    public String build() throws SQLSyntaxErrorException {
        return SqlBuilder.limitAndOffset(buildQuery(), limit, offset);
    }

    @Override
    public String buildQuery() throws SQLSyntaxErrorException {
        StringBuilder sql = new StringBuilder();
        sql.append(buildSelectClause());
        sql.append(buildFromClause());
        sql.append(buildJoinClause());
        sql.append(buildWhereClause());
        sql.append(buildGroupByClause());
        sql.append(buildHavingClause());
        sql.append(buildOrderByClause());
        System.out.println("\n" + new SqlFormatter().format(sql.toString()));
        return sql.toString();
    }

    @Override
    public StringBuilder buildSelectClause() throws SQLSyntaxErrorException {
        return QueryBuilder.buildSelectClause(statements.keySet());
    }

    @Override
    public StringBuilder buildFromClause() throws SQLSyntaxErrorException {
        return QueryBuilder.buildFromClause(statements.keySet());
    }

    @Override
    public StringBuilder buildJoinClause() throws SQLSyntaxErrorException {
        return QueryBuilder.buildJoinClause(statements.keySet());
    }

    @Override
    public StringBuilder buildWhereClause()  throws SQLSyntaxErrorException {
        return QueryBuilder.buildWhereClause(statements);
    }

    @Override
    public StringBuilder buildGroupByClause() throws SQLSyntaxErrorException {
        return QueryBuilder.buildGroupByClause(statements.keySet());
    }

    @Override
    public StringBuilder buildHavingClause() throws SQLSyntaxErrorException {
        return QueryBuilder.buildHavingClause(statements.keySet());
    }

    @Override
    public StringBuilder buildOrderByClause() throws SQLSyntaxErrorException {
        return QueryBuilder.buildOrderByClause(statements.keySet());
    }

    @Override
    public SqlBuilder limit(int limit) {
        this.limit = limit;
        return this;
    }

    @Override
    public SqlBuilder offset(int offset) {
        this.offset = offset;
        return this;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SqlQueryBuilder)) return false;
        SqlQueryBuilder that = (SqlQueryBuilder) o;
        return Objects.equals(statements, that.statements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(statements);
    }

}
