package services;

import users.User;

public class AuthService {
    public boolean authenticate(User user, String password) {
        return user != null && password != null && user.login(user.getUsername(), password);
    }

    public void setUserPassword(User user, String newPassword) {
        if (user != null && newPassword != null) user.changePassword(newPassword);
    }
}
