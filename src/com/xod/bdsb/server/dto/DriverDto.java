package com.xod.bdsb.server.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DriverDto {

    public Integer id;
    public String firstName;
    public String lastName;
    public Integer yearOfBirth;

    public DriverDto() {
    }

    public DriverDto(Integer id, String firstName, String lastName, Integer yearOfBirth) {
        this(firstName, lastName, yearOfBirth);
        this.id = id;
    }

    public DriverDto(String firstName, String lastName, Integer yearOfBirth) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.yearOfBirth = yearOfBirth;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public Integer getYearOfBirth() { return yearOfBirth; }
    public void setYearOfBirth(Integer yearOfBirth) { this.yearOfBirth = yearOfBirth; }
}
