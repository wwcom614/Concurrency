package com.ww.concurrency.ThreadLocal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class ThreadLocalController {

    @GetMapping("/threadlocal/test")
    public Long test(){
        return RequestHolder.getId();
    }

}
