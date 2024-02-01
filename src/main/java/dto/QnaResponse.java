package dto;

public class QnaResponse {
    public static class QnaInfo {
        private String id;
        private String userId;
        private String title;
        private String content;
        private String createdTime;

        public QnaInfo(String id, String userId, String title, String content, String createdTime) {
            this.id = id;
            this.userId = userId;
            this.title = title;
            this.content = content;
            this.createdTime = createdTime;
        }

        public String getCreatedTime() {
            return createdTime;
        }
    }
}
