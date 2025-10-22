package com.hcd.ormjsonbconversion.model;

import com.asentinel.common.orm.mappers.Column;
import com.asentinel.common.orm.mappers.PkColumn;
import com.asentinel.common.orm.mappers.SqlParam;
import com.asentinel.common.orm.mappers.Table;

import java.time.LocalDate;

@Table("Articles")
public class Article {

    @PkColumn("id")
    private int id;

    @Column("code")
    private String code;

    @Column(value = "attributes", sqlParam = @SqlParam("jsonb"))
    private Attributes attributes;

    protected Article() {

    }

    public Article(String code, Attributes attributes) {
        this.code = code;
        this.attributes = attributes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Attributes getAttributes() {
        return attributes;
    }

    public void setAttributes(Attributes attributes) {
        this.attributes = attributes;
    }
}
