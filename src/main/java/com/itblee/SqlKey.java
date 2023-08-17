package com.itblee;

public interface SqlKey {
    SqlStatement getStatement();
    String getParamName();
    Class<?> getType();
    boolean isScope();
    //boolean isMarker();
}
