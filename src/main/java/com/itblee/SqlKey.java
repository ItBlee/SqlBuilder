package com.itblee.repository.sqlbuilder;

public interface SqlKey {
    SqlStatement getStatement();
    String getParamName();
    Class<?> getType();
    boolean isScope();
    //boolean isMarker();
}
