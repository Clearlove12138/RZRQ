package com.example.a14422.demo1.entity;

import org.litepal.crud.LitePalSupport;

public class ScodeEntity extends LitePalSupport {
    private String scode;
    private String sname;

    public String getScode() {
        return scode;
    }

    public void setScode(String scode) {
        this.scode = scode;
    }

    public String getSname() {
        return sname;
    }

    public void setSname(String sname) {
        this.sname = sname;
    }
}
