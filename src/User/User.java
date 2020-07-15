package User;

public class User {

    private String username;
    private int id;

    public User(String username, int id) {
        this.username = username;
        this.id = id;
    }


    public String getUsername() {
        return username;
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
