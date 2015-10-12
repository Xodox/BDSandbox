package com.xod.bdsb.server.utils;

import com.xod.bdsb.server.dao.UrlPageDao;
import com.xod.bdsb.server.dao.UrlPageDaoImpl;
import org.h2.jdbcx.JdbcDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * Created by protsenkov on 5/21/2015.
 */
@Configuration
public class MainSpringConfiguration {

    @Bean
    public UrlPageDao getUrlPageDao(){
        return new UrlPageDaoImpl() ;
    }

    @Bean
    public JdbcTemplate getJdbcTemplate() {
        System.out.println("---init---s");
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:E:/DB/H2/db/BDSB_H21");
        ds.setPassword("");
        ds.setUser("sa");
        return new JdbcTemplate(ds);
    }
}
