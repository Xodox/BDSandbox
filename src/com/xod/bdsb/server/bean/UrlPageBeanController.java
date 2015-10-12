package com.xod.bdsb.server.bean;

import com.xod.bdsb.server.dao.UrlPageDao;
import com.xod.bdsb.server.dto.UrlPageDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;


@RestController
//@RequestMapping("/url_page")
public class UrlPageBeanController {


    @Autowired
    UrlPageDao urlPageDao;


    @RequestMapping("/url_page/getAll")
    public List<UrlPageDto> findAllPage() {
        System.out.println("find all pages!");

        return urlPageDao.findAllPages();
    }


    @RequestMapping(value = "/url_page/create", method = RequestMethod.POST)
    public @ResponseBody UrlPageDto createPage(@RequestBody UrlPageDto page) {
        System.out.println("create page===============");
        Integer id = urlPageDao.createPage(page);
        page.setId(id);
        return page;
    }

    @RequestMapping("/update")
    public void updatePage(UrlPageDto page) {
        System.out.println("update page");
    }

    @RequestMapping("/delete")
    public void deletePage(UrlPageDto page) {
        System.out.println("delete page");
    }

    @RequestMapping("/process")
    public void processPage(UrlPageDto page) {
        System.out.println("process page");
    }
}
