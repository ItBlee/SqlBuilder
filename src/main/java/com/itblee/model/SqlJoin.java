package com.itblee.model;

import com.itblee.util.StringUtils;
import com.itblee.util.ValidateUtils;

import java.io.Serializable;
import java.util.Objects;

public class SqlJoin implements Serializable {

	private static final long serialVersionUID = -1075257006792061786L;

	public enum Type {
	    EMPTY(""),
        INNER_JOIN("INNER JOIN"),
        LEFT_JOIN("LEFT JOIN"),
        RIGHT_JOIN("RIGHT JOIN"),
        FULL_JOIN("FULL JOIN");

        private final String keyword;

        Type(String keyword) {
            this.keyword = keyword;
        }

        public String getKeyword() {
            return keyword;
        }
    }

    private final Object joinTable;
    private final String joinOn;
    private final Type joinType;

    public static final class Builder {
        private Object joinTable = null;
        private String joinOn = null;
        private Type joinType = null;

        public Builder join(String joinTable) {
            this.joinTable = joinTable;
            return this;
        }

        public Builder join(SqlQuery joinSubQuery) {
            this.joinTable = joinSubQuery;
            return this;
        }

        public Builder on(String joinON) {
            this.joinOn = joinON;
            return this;
        }

        public Builder type(Type joinType) {
            this.joinType = joinType;
            return this;
        }

        public SqlJoin done() {
            ValidateUtils.requireNonNull(joinType);
            StringUtils.requireNonBlank(joinOn);
            if (joinTable == null)
                throw new IllegalArgumentException();
            return new SqlJoin(this);
        }

    }

    public static Builder builder() {
        return new Builder();
    }

    private SqlJoin(final Builder builder) {
        this.joinTable = builder.joinTable;
        this.joinOn = builder.joinOn;
        this.joinType = builder.joinType;
    }

    public Object getJoinTable() {
        return joinTable;
    }

    public String getJoinOn() {
        return joinOn;
    }

    public Type getJoinType() {
        return joinType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SqlJoin)) return false;
        SqlJoin sqlJoin = (SqlJoin) o;
        return getJoinTable().equals(sqlJoin.getJoinTable())
                && getJoinOn().equals(sqlJoin.getJoinOn());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getJoinTable(), getJoinOn());
    }
}
