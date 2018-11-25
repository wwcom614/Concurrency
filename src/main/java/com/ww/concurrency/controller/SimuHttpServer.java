package com.ww.concurrency.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


//用于模拟HTTP服务端，使用PostMan、ApacheBench(AB)、Jmeter进行HTTP高并发测试
@RestController
@Slf4j
public class SimuHttpServer {

    @GetMapping("/test")
    public String test(){
        return "test";
    }
}
