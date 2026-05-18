package data;

import academic.Course;
import communication.Complaint;
import communication.Message;
import communication.News;
import enums.NewsType;
import research.ResearchPaper;
import research.Researcher;
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
    private final List<News> news = new ArrayList<>();
    private final List<Message> messages = new ArrayList<>();
    private final List<Complaint> complaints = new ArrayList<>();
    private final List<Course> courses = new ArrayList<>();

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

    public void addNews(News newsItem) {
        news.add(newsItem);
    }

    public List<News> getNews() {
        return new ArrayList<>(news);
    }

    public void addMessage(Message message) {
        if (message != null) messages.add(message);
    }

    public List<Message> getMessages() {
        return new ArrayList<>(messages);
    }

    public void addComplaint(Complaint complaint) {
        if (complaint != null) complaints.add(complaint);
    }

    public List<Complaint> getComplaints() {
        return new ArrayList<>(complaints);
    }

    public void addCourse(Course course) {
        if (course != null && !courses.contains(course)) courses.add(course);
    }

    public List<Course> getCourses() {
        return new ArrayList<>(courses);
    }

    public void publishResearchPaper(Researcher researcher, ResearchPaper paper) {
        researcher.publishPaper(paper);
    }

    public News createTopCitedResearcherNews(List<Researcher> researchers) {
        Researcher topResearcher = Researcher.getTopCitedResearcher(researchers);
        if (topResearcher == null) return null;
        News newsItem = new News(
                Math.abs(("top-cited-" + topResearcher.getId()).hashCode()),
                "Top cited researcher",
                topResearcher.getName() + " has " + topResearcher.getTotalCitations() + " citations",
                NewsType.RESEARCH
        );
        addNews(newsItem);
        return newsItem;
    }

    public void save() throws IOException {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(DEFAULT_FILE))) {
            outputStream.writeObject(users);
            outputStream.writeObject(logs);
            outputStream.writeObject(news);
            outputStream.writeObject(messages);
            outputStream.writeObject(complaints);
            outputStream.writeObject(courses);
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
            news.clear();
            messages.clear();
            complaints.clear();
            courses.clear();
            users.addAll((List<User>) inputStream.readObject());
            logs.addAll((List<ActionLog>) inputStream.readObject());
            news.addAll((List<News>) inputStream.readObject());
            messages.addAll((List<Message>) inputStream.readObject());
            complaints.addAll((List<Complaint>) inputStream.readObject());
            courses.addAll((List<Course>) inputStream.readObject());
        }
    }
}
