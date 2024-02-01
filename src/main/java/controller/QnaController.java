package controller;

import dto.QnaRequest;
import model.Qna;
import service.QnaService;
import webserver.Model;
import webserver.annotation.GetMapping;
import webserver.annotation.PostMapping;
import webserver.annotation.RequestBody;
import webserver.annotation.RequestParam;
import webserver.response.Response;
import webserver.session.Session;

public class QnaController {
    private final QnaService qnaService = new QnaService();

    @GetMapping(path = "/qna/form.html")
    public String qnaRegisterPage(){
        return "/qna/form.html";
    }

    @GetMapping(path = "/qna/show.html")
    public String qnaDetailPage(@RequestParam(name = "id") String id, Model model){
        Qna qna = qnaService.findQnaById(id);

        model.setAttributes("userId", qna.getUserId());
        model.setAttributes("createdTime", qna.getCreatedTime());
        model.setAttributes("title", qna.getTitle());
        model.setAttributes("content", qna.getContent());

        return "/qna/show.html";
    }


    @PostMapping(path = "/qna")
    public Response registerPost(@RequestBody QnaRequest.Register registerInfo, Session session){
        qnaService.save(registerInfo, session);

        return Response.redirect("/index.html");
    }
}
