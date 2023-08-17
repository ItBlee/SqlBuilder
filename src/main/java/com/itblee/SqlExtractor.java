package com.itblee.repository.sqlbuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface SqlExtractor {
    <T> List<T> extractData(ResultSet resultSet, Class<T> tClass) throws SQLException;
    List<Map<String, Object>> extractRows(ResultSet resultSet) throws SQLException;
}
