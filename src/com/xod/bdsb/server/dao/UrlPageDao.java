package com.xod.bdsb.server.dao;

import com.xod.bdsb.server.dto.UrlPageDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;

/**
 * Created by protsenkov on 5/21/2015.
 */
public interface UrlPageDao {



    public List<UrlPageDto> findAllPages();

    public Integer createPage(UrlPageDto page);

    public UrlPageDto updatePage(UrlPageDto page);

    public UrlPageDto deletePage(UrlPageDto page);

    public void createPage(String name, String url, String desc);

}
