package com.itblee.repository.sqlbuilder;

import java.util.Set;

public interface SqlStatement {
    Set<?> getIdentifiers();
    Set<?> getTables();
    Object getCondition();
}
