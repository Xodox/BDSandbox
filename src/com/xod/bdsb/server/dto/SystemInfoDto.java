package com.xod.bdsb.server.dto;


import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect
public class SystemInfoDto {

    private String name;
    private String status;

    public SystemInfoDto(){}


    public SystemInfoDto(String name, String status) {
        this.name = name;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }
}
