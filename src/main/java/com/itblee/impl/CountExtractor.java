package com.itblee.impl;

import com.itblee.SqlExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CountExtractor implements SqlExtractor {

    @Override
    public <T> List<T> extractData(ResultSet resultSet, Class<T> tClass) throws SQLException {
        int count = resultSet.getInt("count");
        return (List<T>) Collections.singletonList(count);
    }

    @Override
    public List<Map<String, Object>> extractRows(ResultSet resultSet) throws SQLException {
        throw new UnsupportedOperationException();
    }

}
