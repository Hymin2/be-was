package dto;

public class QnaRequest {
    public static class Register{
        private String title;
        private String content;

        public Register() {}

        public String getTitle() {
            return title;
        }

        public String getContent() {
            return content;
        }
    }
}
