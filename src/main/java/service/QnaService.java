package service;

import db.QnaDatabase;
import dto.QnaRequest;
import dto.QnaResponse;
import model.Qna;
import webserver.session.Session;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class QnaService {
    public List<QnaResponse.QnaInfo> getAllPost(){
        return QnaDatabase.findAll()
                .stream()
                .map((post) -> new QnaResponse.QnaInfo(post.getId(), post.getUserId(), post.getTitle(), post.getContent(), post.getCreatedTime()))
                .sorted(Comparator.comparing(QnaResponse.QnaInfo::getCreatedTime).reversed())
                .collect(Collectors.toList());
    }

    public Qna findQnaById(String id){
        return QnaDatabase.findQnaById(id);
    }

    public void save(QnaRequest.Register registerInfo, Session session) {
        String userId = session.getUserId();

        QnaDatabase.addQna(
                Qna.builder()
                        .id(String.valueOf(QnaDatabase.getIndex()))
                        .userId(userId)
                        .title(registerInfo.getTitle())
                        .content(registerInfo.getTitle())
                        .filePath(null)
                        .build()
        );
    }
}
