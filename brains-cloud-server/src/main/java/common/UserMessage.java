package common;

public class UserMessage extends AbstractMessage {
    private String userName;
    private int password;
    private String answer;

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getAnswer() {
        return answer;
    }

    public void setPassword(String password) {
        this.password = password.hashCode();
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getPassword() {
        return password;
    }

    public String getUserName() {
        return userName;
    }
}
