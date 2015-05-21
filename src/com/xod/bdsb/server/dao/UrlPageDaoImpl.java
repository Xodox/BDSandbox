package com.xod.bdsb.server.dao;

import com.xod.bdsb.server.dto.UrlPageDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UrlPageDaoImpl implements UrlPageDao {

    @Autowired
    public JdbcTemplate jdbcTemplate;


    @Override
    public List<UrlPageDto> findAllPages() {
        System.out.println("auto = " + jdbcTemplate);
        UrlPageDto page = new UrlPageDto(111, "name", "url", "desc");

        return jdbcTemplate.query("SELECT * FROM WEB_PAGE", new RowMapper<UrlPageDto>() {
            @Override
            public UrlPageDto mapRow(ResultSet resultSet, int i) throws SQLException {
                UrlPageDto dto = new UrlPageDto(111, resultSet.getString("NAME"), resultSet.getString("URL"), resultSet.getString("DESC"));
                return dto;
            }
        });

    }

    @Override
    public Integer createPage(final UrlPageDto page) {
        System.out.println("================");
        SimpleJdbcInsert insertActor = new SimpleJdbcInsert(jdbcTemplate.getDataSource())
                .withTableName("WEB_PAGE")
                .usingGeneratedKeyColumns("ID");
        Map params = new HashMap();
        params.put("name", page.getName());
//        params.put("id", 11);

        params.put("url", page.getUrl());
        params.put("desc", page.getDesc());

        insertActor.execute(params);
        return page.getId();
    }

    @Override
    public UrlPageDto updatePage(UrlPageDto page) {
        return null;
    }

    @Override
    public UrlPageDto deletePage(UrlPageDto page) {
        return null;
    }
}

