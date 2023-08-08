package com.itblee.model;

import com.itblee.SqlStatement;
import com.itblee.util.ValidateUtils;

import java.io.Serializable;
import java.util.*;

public class SqlQuery implements Serializable, SqlStatement {
	
	private static final long serialVersionUID = -4006437901874345613L;
	
	private final Set<?> selectColumn;
    private final Set<?> fromTable;
    private final Set<SqlJoin> join;
    private final Map<?, LogicalOperators> whereColumn;
    private final Set<String> groupByColumn;
    private final Map<String, LogicalOperators> havingClauses;
    private final Map<String, Boolean> orderByColumn;
    private final String alias;

    public static final class Builder {
        private Set<Object> selectColumn = new LinkedHashSet<>();
        private Set<Object> fromTable = new LinkedHashSet<>();
        private Set<SqlJoin> join = new LinkedHashSet<>();
        private Map<Object, LogicalOperators> whereColumn = new LinkedHashMap<>();
        private Set<String> groupByColumn = new LinkedHashSet<>();
        private Map<String, LogicalOperators> havingClauses = new LinkedHashMap<>();
        private Map<String, Boolean> orderByColumn = new LinkedHashMap<>();
        private String alias = null;

        public Builder select(Object... selectColumn) {
            for (Object o : selectColumn) {
                if (o instanceof String || o instanceof SqlQuery)
                    this.selectColumn.add(o);
                else throw new IllegalArgumentException();
            }
            return this;
        }

        public Builder from(Object... fromTable) {
            for (Object o : fromTable) {
                if (o instanceof String || o instanceof SqlQuery)
                    this.fromTable.add(o);
                else throw new IllegalArgumentException();
            }
            return this;
        }

        public Builder joinWith(SqlJoin... join) {
            this.join.addAll(Arrays.asList(join));
            return this;
        }

        public Builder where(Object... whereColumn) {
            for (Object o : whereColumn) {
                if (o instanceof String || o instanceof SqlQuery)
                    this.whereColumn.putIfAbsent(o, LogicalOperators.AND);
                else throw new IllegalArgumentException();
            }
            return this;
        }

        public Builder orWhere(Object whereColumn) {
            if (whereColumn instanceof String || whereColumn instanceof SqlQuery)
                this.whereColumn.putIfAbsent(whereColumn, LogicalOperators.OR);
            else throw new IllegalArgumentException();
            return this;
        }

        public Builder whereWithoutValue(Object... whereColumn) {
            for (Object o : whereColumn) {
                if (o instanceof String || o instanceof SqlQuery)
                    this.whereColumn.putIfAbsent(o, LogicalOperators.NON_VALUE);
                else throw new IllegalArgumentException();
            }
            return this;
        }

        public Builder groupBy(String... groupColumn) {
            this.groupByColumn.addAll(Arrays.asList(groupColumn));
            return this;
        }

        public Builder having(String... havingClauses) {
            for (String s : havingClauses)
                this.havingClauses.putIfAbsent(s, LogicalOperators.AND);
            return this;
        }

        public Builder having(String havingCondition, LogicalOperators operators) {
            ValidateUtils.requireNonNull(operators);
            this.havingClauses.putIfAbsent(havingCondition, operators);
            return this;
        }

        public Builder orderBy(String orderColumn) {
            this.orderByColumn.putIfAbsent(orderColumn, true);
            return this;
        }

        public Builder orderBy(String orderColumn, boolean isAscendingSort) {
            this.orderByColumn.putIfAbsent(orderColumn, isAscendingSort);
            return this;
        }

        public Builder as(String alias) {
            this.alias = alias;
            return this;
        }

        public SqlQuery build() {
            if (fromTable.isEmpty())
                throw new IllegalStateException();
            this.selectColumn = Collections.unmodifiableSet(this.selectColumn);
            this.fromTable = Collections.unmodifiableSet(this.fromTable);
            this.join = Collections.unmodifiableSet(this.join);
            this.whereColumn = Collections.unmodifiableMap(this.whereColumn);
            this.groupByColumn = Collections.unmodifiableSet(this.groupByColumn);
            this.havingClauses = Collections.unmodifiableMap(this.havingClauses);
            this.orderByColumn = Collections.unmodifiableMap(this.orderByColumn);
            return new SqlQuery(this);
        }

    }

    public static Builder builder() {
        return new Builder();
    }

    private SqlQuery(Builder builder) {
        this.selectColumn = builder.selectColumn;
        this.fromTable = builder.fromTable;
        this.join = builder.join;
        this.whereColumn = builder.whereColumn;
        this.groupByColumn = builder.groupByColumn;
        this.havingClauses = builder.havingClauses;
        this.orderByColumn = builder.orderByColumn;
        this.alias = builder.alias;
    }

    public Set<?> getSelectColumn() {
        return selectColumn;
    }

    public Set<?> getFromTable() {
        return fromTable;
    }

    public Set<SqlJoin> getJoin() {
        return join;
    }

    public Map<?, LogicalOperators> getWhereColumn() {
        return whereColumn;
    }

    public Set<String> getGroupByColumn() {
        return groupByColumn;
    }

    public Map<String, LogicalOperators> getHavingClauses() {
        return havingClauses;
    }

    public Map<String, Boolean> getOrderByColumn() {
        return orderByColumn;
    }

    public String getAlias() {
        return alias;
    }

    @Override
    public Set<?> getIdentifiers() {
        return getSelectColumn();
    }

    @Override
    public Set<?> getTables() {
        return getFromTable();
    }

    @Override
    public Object getCondition() {
        return getWhereColumn();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SqlQuery)) return false;
        SqlQuery query = (SqlQuery) o;
        return getSelectColumn().equals(query.getSelectColumn())
                && getFromTable().equals(query.getFromTable())
                && Objects.equals(getJoin(), query.getJoin())
                && Objects.equals(getWhereColumn(), query.getWhereColumn())
                && Objects.equals(getGroupByColumn(), query.getGroupByColumn())
                && Objects.equals(getHavingClauses(), query.getHavingClauses())
                && Objects.equals(getOrderByColumn(), query.getOrderByColumn());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSelectColumn(), getFromTable(), getJoin(), getWhereColumn(), getGroupByColumn(), getHavingClauses(), getOrderByColumn());
    }
}
