package services;

import users.User;

public class AuthenticationService extends AuthService {
    public boolean login(User user, String password) {
        return authenticate(user, password);
    }
}
