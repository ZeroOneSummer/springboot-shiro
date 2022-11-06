package com.bocsoft.obss.two.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/s2/")
public class HelloController {

    @GetMapping("hello/{msg}")
    public String sayHello(@PathVariable("msg") String msg){
        log.info("s2 received s1 msg: {}", msg);
        return "hi, s1";
    }
}
