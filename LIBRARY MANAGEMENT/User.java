public class User {
    private int userId;
    private String userName;
    private String role;
    private String password;

    public User(int userId, String userName) {
        this.userId = userId;
        this.userName = userName;
        this.role = "user";
        this.password = "";
    }

    public User(int userId, String userName, String role) {
        this.userId = userId;
        this.userName = userName;
        this.role = role;
        this.password = "";
    }

    public User(int userId, String userName, String role, String password) {
        this.userId = userId;
        this.userName = userName;
        this.role = role;
        this.password = (password != null ? password : "");
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = (password != null ? password : "");
    }

    @Override
    public String toString() {
        return "User ID: " + userId + ", Name: " + userName + ", Role: " + role;
    }
} 