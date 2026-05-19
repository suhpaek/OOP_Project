package models.users;

public class Admin extends User {
    public Admin() {
        super();
    }

    public Admin(String username, String password, String firstName, String lastName, String email) {
        super(username, password, firstName, lastName, email);
    }
}
