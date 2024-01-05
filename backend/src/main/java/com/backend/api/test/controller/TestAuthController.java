package com.backend.api.test.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestAuthController {

    @GetMapping(value = "/auth")
    public String auth() throws Exception {
        return "success";
    }

}
