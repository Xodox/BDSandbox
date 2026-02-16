package com.xod.bdsb.server.dao;

import com.xod.bdsb.server.dto.CarDto;

import java.util.List;

public interface CarDao {
    List<CarDto> findAll();
    CarDto findById(Integer id);
    List<CarDto> findCarsByDriverId(Integer driverId);

    Integer create(CarDto car);
    CarDto update(CarDto car);
    void delete(Integer id);

    List<Integer> findDriverIdsByCarId(Integer carId);
    void setDriversForCar(Integer carId, List<Integer> driverIds);
}
