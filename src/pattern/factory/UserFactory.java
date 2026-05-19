package pattern.factory;

import models.users.User;

public abstract class UserFactory {
    public abstract User createUser(String userType);
}