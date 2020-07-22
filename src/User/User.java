package User;

public class User {

    private String username;
    private String password;
    private int id;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User(String username, String password, int id) {
        this.username = username;
        this.password = password;
        this.id = id;
    }


    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        if (id <= 0) {
            return getUsername();
        } else {
            return getUsername() + "#" + getId();
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
