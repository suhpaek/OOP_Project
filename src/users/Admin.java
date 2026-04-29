package users;

import data.DataStore;
import exceptions.UserNotFoundException;
import support.ActionLog;

import java.io.File;
import java.util.List;

public class Admin extends User {
    public Admin() {
        super();
    }

    public Admin(String username, String password, String firstName, String lastName, String email) {
        super(username, password, firstName, lastName, email);
    }

    public void addUser(User user) {
        DataStore.getInstance().addUser(user);
    }

    public void updateUser(User user) {
        DataStore.getInstance().updateUser(user);
    }

    public void deleteUser(User user) {
        DataStore.getInstance().removeUser(user.getId());
    }

    public List<User> viewAllUsers() {
        return DataStore.getInstance().getAllUsers();
    }

    public User viewUserInfo(User user) throws UserNotFoundException {
        return DataStore.getInstance().findUserById(user.getId());
    }

    public void processLogFile(File logFile) {
        DataStore.getInstance().addLog(new ActionLog(logFile.getName(), getId(), "Processed log file"));
    }

    public List<ActionLog> readLogs() {
        return DataStore.getInstance().getLogs();
    }
}
