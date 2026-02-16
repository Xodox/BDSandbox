package com.xod.bdsb.server.dao;

import com.xod.bdsb.server.dto.DriverDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class DriverDaoImpl implements DriverDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final RowMapper<DriverDto> ROW_MAPPER = new RowMapper<DriverDto>() {
        @Override
        public DriverDto mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new DriverDto(
                rs.getInt("ID"),
                rs.getString("FIRST_NAME"),
                rs.getString("LAST_NAME"),
                rs.getInt("YEAR_OF_BIRTH")
            );
        }
    };

    @Override
    public List<DriverDto> findAll() {
        return jdbcTemplate.query("SELECT * FROM DRIVERS ORDER BY ID", ROW_MAPPER);
    }

    @Override
    public DriverDto findById(Integer id) {
        List<DriverDto> list = jdbcTemplate.query("SELECT * FROM DRIVERS WHERE ID = ?", ROW_MAPPER, id);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public List<DriverDto> findByFirstName(String firstName) {
        return jdbcTemplate.query("SELECT * FROM DRIVERS WHERE FIRST_NAME = ? ORDER BY ID", ROW_MAPPER, firstName);
    }

    @Override
    public List<DriverDto> findByLastName(String lastName) {
        return jdbcTemplate.query("SELECT * FROM DRIVERS WHERE LAST_NAME = ? ORDER BY ID", ROW_MAPPER, lastName);
    }

    @Override
    public List<DriverDto> findByYearOfBirth(Integer yearOfBirth) {
        return jdbcTemplate.query("SELECT * FROM DRIVERS WHERE YEAR_OF_BIRTH = ? ORDER BY ID", ROW_MAPPER, yearOfBirth);
    }

    @Override
    public Integer create(DriverDto driver) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate.getDataSource())
                .withTableName("DRIVERS")
                .usingGeneratedKeyColumns("ID");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("FIRST_NAME", driver.getFirstName());
        params.put("LAST_NAME", driver.getLastName());
        params.put("YEAR_OF_BIRTH", driver.getYearOfBirth());
        Number key = insert.executeAndReturnKey(params);
        return key.intValue();
    }

    @Override
    public DriverDto update(DriverDto driver) {
        jdbcTemplate.update(
            "UPDATE DRIVERS SET FIRST_NAME = ?, LAST_NAME = ?, YEAR_OF_BIRTH = ? WHERE ID = ?",
            driver.getFirstName(),
            driver.getLastName(),
            driver.getYearOfBirth(),
            driver.getId()
        );
        return findById(driver.getId());
    }

    @Override
    public void delete(Integer id) {
        jdbcTemplate.update("DELETE FROM DRIVERS WHERE ID = ?", id);
    }
}
