package com.cqp.demo4springsecurity.org.cqp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedList;

/**
 * @author cqp
 * @version 1.0.0
 * @ClassName HelloController.java
 * @Description TODO
 * @createTime 2021年07月29日 00:17:00
 */
@RestController
public class HelloController {

    // 登录就能访问
    @GetMapping("/hello")
    public String hello(){
        return "hello security!";
    }


    // 有admin权限才能访问
    @GetMapping("/admin/hello")
    public String admin(){
        return "admin";
    }

    // 有user权限才能访问
    @GetMapping("/user/hello")
    public String user(){
        return "user";
    }

    //回调成功函数
    @RequestMapping("/success")
    public String success(){
        return "success";
    }
}
