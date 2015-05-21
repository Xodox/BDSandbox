package com.xod.bdsb.server.bean;

import com.xod.bdsb.server.dto.SystemInfoDto;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by protsenkov on 5/21/2015.
 */


@RestController
@RequestMapping("/system")
public class BdEnterBeanImpl implements BdEnterBean {

    @RequestMapping("/info")
    public SystemInfoDto printSystemStatus() {
        return new SystemInfoDto("name", "status1");
    }



}
