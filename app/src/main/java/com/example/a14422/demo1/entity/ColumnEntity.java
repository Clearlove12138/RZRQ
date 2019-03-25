package com.example.a14422.demo1.entity;

import org.litepal.crud.LitePalSupport;

public class ColumnEntity extends LitePalSupport {
    private String column;

    private  String comment;

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
