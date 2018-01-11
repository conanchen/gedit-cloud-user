package com.github.conanchen.gedit.user.controller;

import com.google.gson.Gson;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.DateFormat;
import java.util.Date;

@RestController
@EnableAutoConfiguration
public class HelloController {
    @RequestMapping(value = "/hello")
    public String hello() {
        return "hello@" + DateFormat.getInstance().format(new Date()) + ", HelloController Spring Boot ";
    }
    @RequestMapping(value = "/")
    public Date index() {
        return new Date();
    }
}