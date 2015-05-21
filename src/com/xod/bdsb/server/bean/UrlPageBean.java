package com.xod.bdsb.server.bean;

import com.xod.bdsb.server.dto.UrlPageDto;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

public interface UrlPageBean {

    public List<UrlPageDto> findAllPage();

    void createPage(UrlPageDto page);

//    public void createPage(String name, String url, String desc);

    public void updatePage(UrlPageDto page);

    public void deletePage(UrlPageDto page);

    public void processPage(UrlPageDto page);

}
