package com.xod.bdsb.server.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect
public class UrlPageDto {

    public Integer id;
    public String name;
    public String url;
    public String desc;

    public UrlPageDto() {
    }

    public UrlPageDto(Integer id, String name, String url, String desc) {
        this(name, url, desc);
        this.id = id;

    }

    public UrlPageDto(String name, String url, String desc) {
        this.name = name;
        this.url = url;
        this.desc = desc;
    }


    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getDesc() {
        return desc;
    }
}
