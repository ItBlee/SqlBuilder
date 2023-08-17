package com.itblee;

import com.itblee.model.*;
import com.itblee.util.StringUtils;
import com.itblee.util.ValidateUtils;

import java.sql.SQLSyntaxErrorException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class QueryBuilder {

    public QueryBuilder() {
        throw new AssertionError();
    }

    public static String buildQuery(Map<SqlQuery, ?> statements) throws SQLSyntaxErrorException {
        ValidateUtils.requireNonNull(statements);
        StringBuilder sql = new StringBuilder();
        Set<SqlQuery> queries = statements.keySet();
        sql.append(buildSelectClause(queries));
        sql.append(buildFromClause(queries));
        sql.append(buildJoinClause(queries));
        sql.append(buildWhereClause(statements));
        sql.append(buildGroupByClause(queries));
        sql.append(buildHavingClause(queries));
        sql.append(buildOrderByClause(queries));
        return sql.toString();
    }

    public static StringBuilder buildSelectClause(Set<SqlQuery> queries) throws SQLSyntaxErrorException {
        ValidateUtils.requireNonNull(queries);
        Set<?> columns = queries.stream()
                .flatMap(query -> query.getSelectColumn().stream())
                .collect(Collectors.toCollection(LinkedHashSet::new));
        Set<String> clauses = new LinkedHashSet<>();
        for (Object col : columns) {
            if (col instanceof String)
                clauses.add((String) col);
            else if (col instanceof SqlQuery)
                clauses.add(SqlBuilder.buildSubQuery((SqlQuery) col, null));
            else throw new SQLSyntaxErrorException();
        }
        if (clauses.isEmpty())
            throw new SQLSyntaxErrorException("Wrong query structure.");
        return SqlBuilder.merge(clauses, ", ").insert(0, " SELECT ");
    }

    public static StringBuilder buildFromClause(Set<SqlQuery> queries) throws SQLSyntaxErrorException {
        ValidateUtils.requireNonNull(queries);
        Set<?> tables = queries.stream()
                .flatMap(query -> query.getFromTable().stream())
                .collect(Collectors.toCollection(LinkedHashSet::new));
        Set<String> clauses = new LinkedHashSet<>();
        for (Object tbl : tables) {
            if (tbl instanceof String)
                clauses.add((String) tbl);
            else if (tbl instanceof SqlQuery)
                clauses.add(SqlBuilder.buildDerivedTable((SqlQuery) tbl, null));
            else
                throw new SQLSyntaxErrorException();
        }
        if (clauses.isEmpty())
            throw new SQLSyntaxErrorException("Wrong query structure.");
        return SqlBuilder.merge(clauses, ", ").insert(0, " FROM ");
    }

    public static StringBuilder buildJoinClause(Set<SqlQuery> queries) throws SQLSyntaxErrorException {
        ValidateUtils.requireNonNull(queries);
        Set<SqlJoin> joins = queries.stream()
                .flatMap(query -> query.getJoin().stream())
                .collect(Collectors.toCollection(LinkedHashSet::new));
        Set<String> clauses = new LinkedHashSet<>();
        for (SqlJoin join : joins) {
            String table;
            Object joinTable = join.getJoinTable();
            if (joinTable instanceof String)
                table = (String) joinTable;
            else if (joinTable instanceof SqlQuery)
                table = SqlBuilder.buildDerivedTable((SqlQuery) joinTable, null);
            else
                throw new SQLSyntaxErrorException();
            String clause = join.getJoinType().getKeyword()
                    + " " + table + " ON " + join.getJoinOn();
            clauses.add(clause);
        }
        if (clauses.isEmpty())
            return new StringBuilder();
        return SqlBuilder.merge(clauses, " ").insert(0, " ");
    }

    public static StringBuilder buildWhereClause(Map<SqlQuery, ?> statements) throws SQLSyntaxErrorException {
        ValidateUtils.requireNonNull(statements);
        Set<String> clauses = new LinkedHashSet<>();
        for (Map.Entry<SqlQuery, ?> statement : statements.entrySet()) {
            Set<Condition> conditions = extractConditions(statement);
            clauses.add(buildConditionClause(conditions).toString());
        }
        StringBuilder finalClause = SqlBuilder.merge(clauses, " " + LogicalOperators.AND + " ");
        if (StringUtils.isNotBlank(finalClause))
            finalClause.insert(0, " WHERE ");
        return finalClause;
    }

    public static StringBuilder buildGroupByClause(Set<SqlQuery> queries) throws SQLSyntaxErrorException {
        ValidateUtils.requireNonNull(queries);
        Set<String> clauses = queries.stream()
                .flatMap(query -> query.getGroupByColumn().stream())
                .collect(Collectors.toCollection(LinkedHashSet::new));
        if (clauses.isEmpty())
            return new StringBuilder();
        return SqlBuilder.merge(clauses, ", ").insert(0, " GROUP BY ");
    }

    public static StringBuilder buildHavingClause(Set<SqlQuery> queries) throws SQLSyntaxErrorException {
        ValidateUtils.requireNonNull(queries);
        StringBuilder finalClause = new StringBuilder();
        Map<String, LogicalOperators> havingClauses = queries.stream()
                .flatMap(query -> query.getHavingClauses().entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        List<String> clauses = new ArrayList<>(havingClauses.keySet());
        List<LogicalOperators> operators = new ArrayList<>(havingClauses.values());
        if (clauses.isEmpty())
            return new StringBuilder();
        finalClause.append(" HAVING ");
        finalClause.append(clauses.get(0));
        for (int i = 1; i < clauses.size(); i++) {
            if (operators.get(i) != null) {
                finalClause.append(operators.get(i)).append(" ");
                finalClause.append(clauses.get(i));
            }
        }
        return finalClause;
    }

    public static StringBuilder buildOrderByClause(Set<SqlQuery> queries) throws SQLSyntaxErrorException {
        ValidateUtils.requireNonNull(queries);
        Map<String, Boolean> orderByColumns = queries.stream()
                .flatMap(query -> query.getOrderByColumn().entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        Set<String> clauses = orderByColumns.entrySet().stream()
                .map(orderBy -> {
                    String clause = orderBy.getKey();
                    boolean isAscendingOrder = orderBy.getValue();
                    if (isAscendingOrder)
                        clause += " ASC";
                    else
                        clause += " DESC";
                    return clause;
                })
                .collect(Collectors.toCollection(LinkedHashSet::new));
        if (clauses.isEmpty())
            return new StringBuilder();
        return SqlBuilder.merge(clauses, ", ").insert(0, " ORDER BY ");
    }

    private static class Condition {
        final Object identifier;
        final Object value;
        final LogicalOperators operator;

        public Condition(Object clause, Object value, LogicalOperators operator) {
            this.identifier = clause;
            this.value = value;
            this.operator = operator;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (!(o instanceof Condition))
                return false;
            Condition condition = (Condition) o;
            return identifier.equals(condition.identifier);
        }

        @Override
        public int hashCode() {
            return Objects.hash(identifier);
        }

    }

    private static Set<Condition> extractConditions(Map.Entry<SqlQuery, ?> statement) throws SQLSyntaxErrorException {
        List<Object> identifiers = new ArrayList<>(statement.getKey().getWhereColumn().keySet());
        List<LogicalOperators> operators = new ArrayList<>(statement.getKey().getWhereColumn().values());
        List<Object> values = new LinkedList<>();
        int singleCondition = 1;
        long numberOfConditionsRequireValue = operators.stream()
                .filter(o -> !o.equals(LogicalOperators.NON_VALUE))
                .count();
        Object val = statement.getValue();
        if (numberOfConditionsRequireValue > singleCondition) {
            if (val.getClass().isArray()) {
                if (numberOfConditionsRequireValue != ((Object[]) val).length)
                    throw new SQLSyntaxErrorException("number of value not match conditions.");
                else values.addAll(Arrays.asList((Object[]) val));
            } else if (val instanceof Collection) {
                if (numberOfConditionsRequireValue != ((Collection<?>) val).size())
                    throw new SQLSyntaxErrorException("number of value not match conditions.");
                else values.addAll((Collection<?>) val);
            } else throw new SQLSyntaxErrorException("number of value not match conditions.");
        } else values.add(val);

        Set<Condition> conditions = new LinkedHashSet<>();
        IntStream.range(0, operators.size()).forEach(i -> {
            if (operators.get(i).equals(LogicalOperators.NON_VALUE)) {
                operators.set(i, LogicalOperators.AND);
                values.add(i, LogicalOperators.NON_VALUE);
            }
            conditions.add(new Condition(identifiers.get(i), values.get(i), operators.get(i)));
        });
        return conditions;
    }

    private static StringBuilder buildConditionClause(Set<Condition> conditions) throws SQLSyntaxErrorException {
        StringBuilder clause = new StringBuilder();
        if (conditions.isEmpty())
            return clause;
        List<Condition> conditionList = new ArrayList<>(conditions);
        clause.append(buildConditionClause(conditionList.get(0))).append(" ");
        if (conditionList.size() > 1) {
            for (int i = 1; i < conditionList.size(); i++) {
                clause.append(conditionList.get(i).operator).append(" ");
                clause.append(buildConditionClause(conditionList.get(i))).append(" ");
            }
            clause = new StringBuilder("( " + clause + " )");
        }
        return clause;
    }

    private static StringBuilder buildConditionClause(Condition condition) throws SQLSyntaxErrorException {
        StringBuilder clause = new StringBuilder();
        Object identifier = condition.identifier;
        Object value = condition.value;

        // add column name (example: building.name)
        if (identifier instanceof String)
            clause.append(identifier).append(" ");
        else if (identifier instanceof SqlQuery)
            return clause.append(LogicalOperators.EXISTS).append(" ")
                    .append(SqlBuilder.buildSubQuery((SqlQuery) identifier, value)).append(" ");
        else throw new SQLSyntaxErrorException("Missing Where columns.");

        if (value == null)
            return new StringBuilder();
        if (value.equals(LogicalOperators.NON_VALUE))
            return clause;

        /*
         * value type
         * subQuery: exist()
         * code: =''
         * string: like'%%'
         * num: =0
         * arr[]: in(,,)
         * Set: ... or ... or ...
         * List/Queue: ... and ... and ...
         * range: between, <=, >=
         */
        if (value.getClass().isArray() && ((Object[]) value).length == 1)
            value = ((Object[]) value)[0];
        if (value instanceof Code) {
            clause.append("= '").append(value).append("'");
        } else if (value instanceof CharSequence) {
            clause.append(LogicalOperators.LIKE).append(" '%").append(value).append("%'");
        } else if (value instanceof Number) {
            clause.append("= ").append(value);
        } else if (value instanceof Collection) {
            if (((Collection<?>) value).isEmpty())
                return new StringBuilder();
            Stream<?> stream;
            if (value instanceof List)
                stream = ((List<?>) value).stream();
            else if (value instanceof Set)
                stream = ((Set<?>) value).stream();
            else stream = ((Queue<?>) value).stream();
            List<String> orClauses = stream.filter(Objects::nonNull)
                    .map(o -> o instanceof CharSequence ?
                            identifier + " " + LogicalOperators.LIKE + " '%" + o + "%'"
                            : identifier + " = " + o)
                    .collect(Collectors.toList());
            LogicalOperators operator = value instanceof Set ? LogicalOperators.OR : LogicalOperators.AND;
            String join = String.join(" " + operator + " ", orClauses);
            clause = new StringBuilder();
            int singleClause = 1;
            if (orClauses.size() != singleClause)
                clause.append("(").append(join).append(")");
            else clause.append(join);
        } else if (value.getClass().isArray()) {
            if (((Object[]) value).length == 0)
                return new StringBuilder();
            String join = Arrays.stream(((Object[]) value))
                    .map(o -> o instanceof CharSequence ? "'" + o + "'" : o.toString())
                    .collect(Collectors.joining(","));
            clause.append(LogicalOperators.IN).append(" (").append(join).append(")");
        } else if (value instanceof Range) {
            Range range = (Range) value;
            if (range.from == null)
                clause.append("<= ").append(range.to);
            else if (range.to == null)
                clause.append(">= ").append(range.from);
            else clause.append(LogicalOperators.BETWEEN).append(" ")
                        .append(range.from)
                        .append(" ").append(LogicalOperators.AND).append(" ")
                        .append(range.to);
        } else throw new IllegalStateException("Value type unsupported.");
        return clause;
    }

}
