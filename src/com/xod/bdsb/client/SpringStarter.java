package com.xod.bdsb.client;

import com.xod.bdsb.server.dto.SystemInfoDto;
import com.xod.bdsb.server.dto.UrlPageDto;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by protsenkov on 5/21/2015.
 */
public class SpringStarter {

        public static void main(String args[]) {
            RestTemplate restTemplate = new RestTemplate();
            UrlPageDto page = new UrlPageDto(11, "name", "url", "111desc");
            Map<String, String> params = new HashMap<>();
            params.put("name", "name1");
            params.put("url", "url1");
            params.put("desc", "ddd1");
//            restTemplate."http://localhost:8080/url_page/create", page);
//            postForEntity("http://localhost:8080/url_page/create", page, Void.class, params);


//            List<UrlPageDto> pages = restTemplate.getForObject("http://localhost:8080/url_page/getAll", List.class);
//            if(pages != null){
//                System.out.println(pages.size());
//            }

            System.out.println("==============");
    }
}
