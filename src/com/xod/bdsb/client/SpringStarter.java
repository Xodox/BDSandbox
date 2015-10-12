package com.xod.bdsb.client;

import com.xod.bdsb.server.dto.SystemInfoDto;
import com.xod.bdsb.server.dto.UrlPageDto;
import org.springframework.http.ResponseEntity;
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
            UrlPageDto page = new UrlPageDto("name", "url", "111desc");
            Map<String, String> params = new HashMap<>();
            params.put("name", "name1");
            params.put("url", "url1");
            params.put("desc", "ddd1");
            List<UrlPageDto> forObject = restTemplate.getForObject("http://localhost:8080/url_page/getAll", List.class);
            System.out.println(forObject.size() + "---");
//            restTemplate.postForObject("http://localhost:8080/url_page/create", page, Integer.class, new Object[0]);
//            restTemplate.postForEntity("http://localhost:8080/url_page/create", page, Void.class,new Object[0]);
            page = restTemplate.postForObject("http://localhost:8080/url_page/create", page, UrlPageDto.class);
            ResponseEntity<UrlPageDto> responseEntity= restTemplate.postForEntity("http://localhost:8080/url_page/create", page, UrlPageDto.class);

            System.out.println("=====11111133333333333331111========= + " + page.getId());
    }
}
