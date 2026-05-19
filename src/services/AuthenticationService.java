package services;

import data.DataStore;
import enums.Language;
import exceptions.AuthenticationException;
import exceptions.InvalidPasswordException;
import exceptions.UserNotFoundException;
import i18n.I18n;
import models.support.ActionLog;
import models.users.User;

import java.io.IOException;

public class AuthenticationService {
    private final DataStore dataStore;

    public AuthenticationService() {
        this(DataStore.getInstance());
    }

    public AuthenticationService(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    public boolean authenticate(User user, String password) {
        return user != null && password != null && user.login(user.getUsername(), password);
    }

    public User login(String username, String password)
            throws UserNotFoundException, InvalidPasswordException, AuthenticationException {
        return login(username, password, Language.EN);
    }

    public User login(String username, String password, Language language)
            throws UserNotFoundException, InvalidPasswordException, AuthenticationException {
        User user = dataStore.findUserByUsername(username);

        if (!user.isPasswordCorrect(password)) {
            throw new InvalidPasswordException(username);
        }

        if (!user.login(username, password)) {
            throw new AuthenticationException("User is inactive or cannot be logged in.");
        }

        user.selectLanguage(language);
        I18n.setLanguage(user.getLanguage());
        dataStore.updateUser(user);
        dataStore.addLog(new ActionLog(user.getId(), user.getId(), "Logged in"));
        saveData();

        System.out.println(I18n.t("auth.login.success"));
        return user;
    }

    public void logout(User user) throws AuthenticationException {
        if (user == null) return;
        I18n.setLanguage(user.getLanguage());
        user.logout();
        dataStore.updateUser(user);
        dataStore.addLog(new ActionLog(user.getId(), user.getId(), "Logged out"));
        saveData();
        System.out.println(I18n.t("auth.logout.success"));
    }

    public void selectInterfaceLanguage(User user, Language language) throws AuthenticationException {
        if (user == null || language == null) return;
        user.selectLanguage(language);
        I18n.setLanguage(language);
        dataStore.updateUser(user);
        dataStore.addLog(new ActionLog(user.getId(), user.getId(), "Selected interface language: " + language));
        saveData();
        System.out.println(I18n.t("auth.language.selected", language));
    }

    public void setUserPassword(User user, String newPassword) {
        if (user != null && newPassword != null) user.changePassword(newPassword);
    }

    private void saveData() throws AuthenticationException {
        try {
            dataStore.save();
        } catch (IOException e) {
            throw new AuthenticationException("Could not save authentication changes: " + e.getMessage());
        }
    }
}
