package com.xod.bdsb.server.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CarDto {
    public Integer id;
    public String name;
    public String model;
    public Integer manufacturingYear;

    public CarDto() {}
    public CarDto(Integer id, String name, String model, Integer manufacturingYear) {
        this.id = id;
        this.name = name;
        this.model = model;
        this.manufacturingYear = manufacturingYear;
    }
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public Integer getManufacturingYear() { return manufacturingYear; }
    public void setManufacturingYear(Integer manufacturingYear) { this.manufacturingYear = manufacturingYear; }
}
