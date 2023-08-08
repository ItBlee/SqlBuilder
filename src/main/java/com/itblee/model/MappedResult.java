package com.itblee.model;

import dnl.utils.text.table.TextTable;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class MappedResult {

    private String title;
    private final Set<List<Object>> mappedTable = new LinkedHashSet<>();

    public static class Builder {
        private final MappedResult parent;
        private String fieldType;
        private String entityField;
        private String columnName;
        private Object columnValue;

        public Builder(MappedResult parent) {
            this.parent = parent;
        }

        public Builder setFieldType(String fieldType) {
            this.fieldType = fieldType;
            return this;
        }

        public Builder setEntityField(String entityField) {
            this.entityField = entityField;
            return this;
        }

        public Builder setColumnName(String columnName) {
            this.columnName = columnName;
            return this;
        }

        public Builder setColumnValue(Object columnValue) {
            this.columnValue = columnValue;
            return this;
        }

        public void add() {
            parent.add(this);
        }

    }

    private void add(Builder builder) {
        List<Object> result = new ArrayList<>();
        result.add(builder.fieldType + "  ");
        result.add(builder.entityField + "  ");
        result.add(builder.columnName + "  ");
        result.add(builder.columnValue + "  ");
        mappedTable.add(result);
    }

    public void add(MappedResult results){
        mappedTable.addAll(results.mappedTable);
    }

    public Builder newResult(){
        return new Builder(this);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void print() {
        String[] columnNames = {"FieldType", "EntityField", "ColumnName", "ColumnValue"};
        Object[][] arr = mappedTable.stream()
                .map(List::toArray)
                .toArray(Object[][]::new);
        System.out.println();
        if (title != null)
            System.out.println(title + ": ");
        new TextTable(columnNames, arr).printTable();
        System.out.println();
    }

}
