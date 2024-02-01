package controller;

import service.QnaService;
import webserver.Model;
import webserver.annotation.GetMapping;
import webserver.session.Session;

public class HomeController {
    private final QnaService qnaService = new QnaService();

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

        model.setAttributes("posts", qnaService.getAllPost());
        return "/index.html";
    }
}
