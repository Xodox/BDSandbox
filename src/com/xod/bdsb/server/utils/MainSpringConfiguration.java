package com.xod.bdsb.server.utils;

import com.xod.bdsb.server.dao.UrlPageDao;
import com.xod.bdsb.server.dao.UrlPageDaoImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

}
