package com.hjzgg.example.springboot.controller;

import com.hjzgg.example.springboot.cfgcenter.server.ZKHelper;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author hujunzheng
 * @create 2019-03-31 23:52
 **/
@RestController
@RequestMapping("cfg")
public class CfgCenterController {
    @GetMapping(value = "setdata", produces = MediaType.ALL_VALUE)
    public String setdata(@RequestParam(required = false, defaultValue = "/wmhcfg/projects/wechat/wmhopenapi/a/cfg0") String path
            , @RequestParam String data) {
        ZKHelper.setData(path, data.getBytes());
        return ZKHelper.getData(path);
    }
}