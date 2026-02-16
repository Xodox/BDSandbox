package com.xod.bdsb.server.dao;

import com.xod.bdsb.server.dto.CarDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class CarDaoImpl implements CarDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final RowMapper<CarDto> ROW_MAPPER = (rs, rowNum) -> new CarDto(
        rs.getInt("ID"),
        rs.getString("NAME"),
        rs.getString("MODEL"),
        rs.getInt("MANUFACTURING_YEAR")
    );

    @Override
    public List<CarDto> findAll() {
        return jdbcTemplate.query("SELECT * FROM CARS ORDER BY ID", ROW_MAPPER);
    }

    @Override
    public CarDto findById(Integer id) {
        List<CarDto> list = jdbcTemplate.query("SELECT * FROM CARS WHERE ID = ?", ROW_MAPPER, id);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public List<CarDto> findCarsByDriverId(Integer driverId) {
        return jdbcTemplate.query(
            "SELECT c.ID, c.NAME, c.MODEL, c.MANUFACTURING_YEAR FROM CARS c INNER JOIN DRIVER_CAR dc ON c.ID = dc.CAR_ID WHERE dc.DRIVER_ID = ? ORDER BY c.ID",
            ROW_MAPPER,
            driverId
        );
    }

    @Override
    public Integer create(CarDto car) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO CARS (NAME, MODEL, MANUFACTURING_YEAR) VALUES (?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, car.getName());
            ps.setString(2, car.getModel());
            ps.setObject(3, car.getManufacturingYear());
            return ps;
        }, keyHolder);
        Number key = keyHolder.getKey();
        return key != null ? key.intValue() : null;
    }

    @Override
    public CarDto update(CarDto car) {
        jdbcTemplate.update(
            "UPDATE CARS SET NAME = ?, MODEL = ?, MANUFACTURING_YEAR = ? WHERE ID = ?",
            car.getName(), car.getModel(), car.getManufacturingYear(), car.getId()
        );
        return findById(car.getId());
    }

    @Override
    public void delete(Integer id) {
        jdbcTemplate.update("DELETE FROM DRIVER_CAR WHERE CAR_ID = ?", id);
        jdbcTemplate.update("DELETE FROM CARS WHERE ID = ?", id);
    }

    @Override
    public List<Integer> findDriverIdsByCarId(Integer carId) {
        return jdbcTemplate.query(
            "SELECT DRIVER_ID FROM DRIVER_CAR WHERE CAR_ID = ? ORDER BY DRIVER_ID",
            (rs, rowNum) -> rs.getInt("DRIVER_ID"),
            carId
        );
    }

    @Override
    public void setDriversForCar(Integer carId, List<Integer> driverIds) {
        jdbcTemplate.update("DELETE FROM DRIVER_CAR WHERE CAR_ID = ?", carId);
        if (driverIds != null && !driverIds.isEmpty()) {
            for (Integer driverId : driverIds) {
                jdbcTemplate.update("INSERT INTO DRIVER_CAR (DRIVER_ID, CAR_ID) VALUES (?, ?)", driverId, carId);
            }
        }
    }
}
