package com.itblee;

import com.itblee.model.SqlQuery;
import com.itblee.util.StringUtils;
import com.itblee.util.ValidateUtils;

import java.sql.SQLSyntaxErrorException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public interface SqlBuilder {
    String build() throws SQLSyntaxErrorException;
    SqlBuilder limit(int limit);
    SqlBuilder offset(int offset);

    interface Query extends SqlBuilder {
        String buildQuery() throws SQLSyntaxErrorException;
        StringBuilder buildSelectClause() throws SQLSyntaxErrorException;
        StringBuilder buildFromClause() throws SQLSyntaxErrorException;
        StringBuilder buildJoinClause() throws SQLSyntaxErrorException;
        StringBuilder buildWhereClause() throws SQLSyntaxErrorException;
        StringBuilder buildGroupByClause() throws SQLSyntaxErrorException;
        StringBuilder buildHavingClause() throws SQLSyntaxErrorException;
        StringBuilder buildOrderByClause() throws SQLSyntaxErrorException;
    }

    interface Update extends SqlBuilder {}
    interface Delete extends SqlBuilder {}

    static StringBuilder merge(Set<String> clauses, String delim) {
        String format = clauses.stream()
                .map(String::trim)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.joining(delim));
        return new StringBuilder(format);
    }

    static String buildSubQuery(SqlQuery query, Object value) throws SQLSyntaxErrorException {
        ValidateUtils.requireNonNull(query);
        StringBuilder clause = new StringBuilder();
        Map<SqlQuery, ?> statement = Collections.singletonMap(query, value);
        clause.append("(");
        clause.append(QueryBuilder.buildQuery(statement));
        clause.append(")");
        if (StringUtils.isNotBlank(query.getAlias()))
            clause.append(" AS ").append(query.getAlias());
        return clause.toString();
    }

    static String buildDerivedTable(SqlQuery query, Object value) throws SQLSyntaxErrorException {
        ValidateUtils.requireNonNull(query);
        if (StringUtils.isBlank(query.getAlias()))
            throw new SQLSyntaxErrorException();
        return buildSubQuery(query, value);
    }

    static String limitAndOffset(String sql, int limit, int offset) {
        if (limit >= 0)
            sql += " LIMIT " + limit;
        else return sql;
        if (offset >= 0)
            sql += " OFFSET " + offset;
        return sql;
    }

}
