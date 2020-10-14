package com.example.springbootmemberServicedemo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
public class TestController {

    @GetMapping("test")
    public String test(@RequestParam(value = "name", defaultValue = "stranger") String name) {
        return "Hello " + name;
    }
}
