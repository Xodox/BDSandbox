package com.xod.bdsb.server.dao;

import com.xod.bdsb.server.dto.DriverDto;

import java.util.List;

public interface DriverDao {

    List<DriverDto> findAll();

    DriverDto findById(Integer id);

    List<DriverDto> findByFirstName(String firstName);

    List<DriverDto> findByLastName(String lastName);

    List<DriverDto> findByYearOfBirth(Integer yearOfBirth);

    Integer create(DriverDto driver);

    DriverDto update(DriverDto driver);

    void delete(Integer id);
}
