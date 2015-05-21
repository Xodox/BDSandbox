package com.xod.bdsb.server.bean;

import com.xod.bdsb.server.dao.UrlPageDao;
import com.xod.bdsb.server.dto.UrlPageDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;


@RestController
@RequestMapping("/url_page")
public class UrlPageBeanImpl implements UrlPageBean {


    @Autowired
    UrlPageDao urlPageDao;

    @Override
    @RequestMapping("/getAll")
    public List<UrlPageDto> findAllPage() {
        System.out.println("find all pages!");

        return urlPageDao.findAllPages();
    }


    @Override
    @RequestMapping(value = "/create")
    public void createPage(UrlPageDto page) {
        System.out.println("create page===============");
        urlPageDao.createPage(page);
    }



    @Override
    @RequestMapping("/update")
    public void updatePage(UrlPageDto page) {
        System.out.println("update page");
    }

    @Override
    @RequestMapping("/delete")
    public void deletePage(UrlPageDto page) {
        System.out.println("delete page");
    }

    @Override
    @RequestMapping("/process")
    public void processPage(UrlPageDto page) {
        System.out.println("process page");
    }
}
