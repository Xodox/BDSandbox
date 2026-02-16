package com.xod.bdsb.server.dao;

import com.xod.bdsb.server.security.BdsbUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class UserDaoImpl implements UserDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public BdsbUserDetails loadUserByUsername(String username) {
        List<BdsbUserDetails> list = jdbcTemplate.query(
            "SELECT USERNAME, PASSWORD_HASH, ROLE, DRIVER_ID FROM USERS WHERE USERNAME = ?",
            (ResultSet rs, int rowNum) -> {
                Integer driverId = rs.getInt("DRIVER_ID");
                if (rs.wasNull()) driverId = null;
                return new BdsbUserDetails(
                    rs.getString("USERNAME"),
                    rs.getString("PASSWORD_HASH"),
                    rs.getString("ROLE"),
                    driverId
                );
            },
            username
        );
        return list.isEmpty() ? null : list.get(0);
    }
}
