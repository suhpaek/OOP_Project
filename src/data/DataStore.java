package data;

import support.ActionLog;
import users.Student;
import users.User;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DataStore {
    private static final String DEFAULT_FILE = "university.ser";
    private static final DataStore INSTANCE = new DataStore();

    private final List<User> users = new ArrayList<>();
    private final List<ActionLog> logs = new ArrayList<>();

    private DataStore() {
    }

    public static DataStore getInstance() {
        return INSTANCE;
    }

    public void addUser(User user) {
        users.add(user);
    }

    public void updateUser(User user) {
        removeUser(user.getId());
        users.add(user);
    }

    public void removeUser(String userId) {
        users.removeIf(user -> user.getId().equals(userId));
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    public List<Student> getStudents() {
        List<Student> students = new ArrayList<>();
        for (User user : users) {
            if (user instanceof Student) {
                students.add((Student) user);
            }
        }
        return students;
    }

    public User findUserById(String userId) throws exceptions.UserNotFoundException {
        Optional<User> user = users.stream()
                .filter(candidate -> candidate.getId().equals(userId))
                .findFirst();

        if (user.isEmpty()) {
            throw new exceptions.UserNotFoundException(userId);
        }
        return user.get();
    }

    public User authenticate(String username, String password) throws exceptions.AuthenticationException {
        for (User user : users) {
            if (user.login(username, password)) {
                return user;
            }
        }
        throw new exceptions.AuthenticationException("Invalid username or password");
    }

    public void addLog(ActionLog actionLog) {
        logs.add(actionLog);
    }

    public List<ActionLog> getLogs() {
        return new ArrayList<>(logs);
    }

    public void save() throws IOException {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(DEFAULT_FILE))) {
            outputStream.writeObject(users);
            List<String> serializedLogs = new ArrayList<>();
            for (ActionLog log : logs) {
                serializedLogs.add(log.getCreatedAt() + " | " + log.getActorId() + " | " + log.getAction());
            }
            outputStream.writeObject(serializedLogs);
        }
    }

    @SuppressWarnings("unchecked")
    public void load() throws IOException, ClassNotFoundException {
        File file = new File(DEFAULT_FILE);
        if (!file.exists()) {
            return;
        }

        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file))) {
            users.clear();
            logs.clear();
            users.addAll((List<User>) inputStream.readObject());
            List<String> serializedLogs = (List<String>) inputStream.readObject();
            int index = 0;
            for (String logLine : serializedLogs) {
                logs.add(new ActionLog("log-" + index++, "system", logLine));
            }
        }
    }
}
