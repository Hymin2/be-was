package dto;

public class UserResponse {
    public static class UserInfo{
        private String userId;
        private String name;
        private String email;

        public UserInfo(String userId, String name, String email){
            this.userId = userId;
            this.name = name;
            this.email = email;
        }

        public String getUserId() {
            return userId;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }
    }
}
