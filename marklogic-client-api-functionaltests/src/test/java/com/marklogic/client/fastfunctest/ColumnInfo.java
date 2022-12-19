package com.marklogic.client.fastfunctest;

public class ColumnInfo {
    private String schema = "";
    private String view = "";
    private String column;
    private String type;
    private boolean hidden;
    private boolean nullable;

    public ColumnInfo(String column, String type) {
        this.column = column;
        this.type = type;
    }

    public ColumnInfo(String schema, String view, String column, String type) {
        this(column, type);
        this.schema = schema;
        this.view = view;
    }

    public ColumnInfo(String schema, String view, String column, String type, boolean hidden) {
        this(schema, view, column, type);
        this.hidden = hidden;
    }

    public ColumnInfo withNullable(boolean nullable) {
        this.nullable = nullable;
        return this;
    }

    public String getExpectedJson() {
        String json = String.format("{\"schema\":\"%s\", \"view\":\"%s\", \"column\":\"%s\", \"type\":\"%s\", ",
            this.schema, this.view, this.column, this.type);
        if (AbstractFunctionalTest.isML11OrHigher) {
            json += String.format("\"hidden\":%s, \"nullable\":%s}", this.hidden, this.nullable);
        } else {
            json += String.format("\"nullable\":%s}", this.nullable);
        }
        return json;
    }
}