package controller;

import dto.UserResponse;
import webserver.Model;
import webserver.annotation.GetMapping;
import webserver.session.Session;

import java.util.List;

public class HomeController {
    @GetMapping(path = "/index.html")
    public String indexPage(Model model, Session session){
        if(session != null) {
            model.setAttributes("login", "");
            model.setAttributes("register", "");
            model.setAttributes("userId", session.getUserId() + "님");
        } else{
            model.setAttributes("login", "로그인");
            model.setAttributes("register", "회원가입");
            model.setAttributes("userId", "");
        }

        return "/index.html";
    }
}
