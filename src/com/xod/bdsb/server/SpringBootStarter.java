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
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@SpringBootApplication
public class SpringBootStarter {


    public static void main(String[] args)

    {
        SpringApplication.run(new Object[]{SpringBootStarter.class}, args);
    }


    @Bean
    @Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
    public WebSecurityConfigurerAdapter getSecurityBean(){
        WebSecurityConfigurerAdapter adapter = new WebSecurityConfigurerAdapter(){
            @Override
            public void configure(WebSecurity web) throws Exception {
                web.ignoring().antMatchers("/**");
            }
        };
        return adapter;
    }


}
