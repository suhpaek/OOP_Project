package pattern.decorator;

import models.users.User;

public class BaseResearcher implements Researcher {
    protected User user;

    public BaseResearcher(User user) {
        this.user = user;
    }

    @Override
    public void conductResearch() {
        System.out.println(user.getUsername() + " is conducting research.");
    }
}