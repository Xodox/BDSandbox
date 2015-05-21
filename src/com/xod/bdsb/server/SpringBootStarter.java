package com.xod.bdsb.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * Created by protsenkov on 5/21/2015.
 */
@SpringBootApplication
public class SpringBootStarter implements CommandLineRunner{


    @Autowired
    JdbcTemplate jdbcTemplate;

    public static void main(String[] args)

    {
        SpringApplication.run(new Object[]{SpringBootStarter.class}, args);


    }


    @Bean
    @Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
    public WebSecurityConfigurerAdapter getSecurityBean(){
        WebSecurityConfigurerAdapter adapter = new WebSecurityConfigurerAdapter(){
            @Override
            protected void configure(HttpSecurity http) throws Exception {
                http.authorizeRequests().antMatchers("/").permitAll();
            }
        };
        return adapter;
    }


    @Override
    public void run(String... strings) throws Exception {
        System.out.println("===++===");
        System.out.println(jdbcTemplate + "---===---");
        jdbcTemplate.execute("CREATE TABLE WEB_PAGE (ID INT PRIMARY KEY AUTO_INCREMENT, NAME VARCHAR(255),URL VARCHAR(255),DESC VARCHAR(255));");

    }
}
