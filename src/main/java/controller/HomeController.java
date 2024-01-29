package controller;

import dto.UserResponse;
import webserver.Model;
import webserver.annotation.GetMapping;

import java.util.List;

public class HomeController {
    @GetMapping(path = "/index.html")
    public String indexPage(){
        return "/index.html";
    }
}
