
public class User {
    private String userName, password, access;

    public User(String userLine) {
        String[] credentialList = userLine.split(",");
        this.userName = credentialList[0];
        this.password = credentialList[1];
        this.access = credentialList[2];
    }

    public String getUserName() {
        return this.userName;
    }
    public String getAccess() {
        return access;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
