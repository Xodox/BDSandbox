package com.xod.bdsb.server.utils;

import com.xod.bdsb.server.dao.UrlPageDao;
import com.xod.bdsb.server.dao.UrlPageDaoImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * Created by protsenkov on 5/21/2015.
 * Uses the DataSource from environment (application.properties / ConfigMap) so cluster H2 is used.
 */
@Configuration
public class MainSpringConfiguration {

    @Bean
    public UrlPageDao getUrlPageDao() {
        return new UrlPageDaoImpl();
    }

    @Bean
    public JdbcTemplate getJdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
