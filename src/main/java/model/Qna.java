package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Qna {
    private String id;
    private String userId;
    private String title;
    private String content;
    private String filePath;
    private String createdTime;

    public Qna(Builder builder) {
        this.id = builder.id;
        this.userId = builder.userId;
        this.title = builder.title;
        this.content = builder.content;
        this.filePath = builder.filePath;
        createdTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public static Builder builder(){
        return new Builder();
    }

    public static class Builder{
        private String id;
        private String userId;
        private String title;
        private String content;
        private String filePath;

        private Builder(){}

        public Builder id(String id){
            this.id = id;

            return this;
        }

        public Builder userId(String userId){
            this.userId = userId;

            return this;
        }

        public Builder title(String title){
            this.title = title;

            return this;
        }

        public Builder content(String content){
            this.content = content;

            return this;
        }

        public Builder filePath(String filePath){
            this.filePath = filePath;

            return this;
        }

        public Qna build(){
            return new Qna(this);
        }
    }
}
