package data;

import models.academic.Course;
import models.academic.RegistrationRequest;
import models.communication.Complaint;
import models.communication.Message;
import models.communication.News;
import enums.NewsType;
import models.organization.StudentOrganization;
import models.research.ResearchPaper;
import models.research.Researcher;
import models.support.ActionLog;
import models.support.TechSupportRequest;
import models.users.Admin;
import models.users.Student;
import models.users.User;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class DataStore {
    private static final String DEFAULT_FILE = "university.ser";
    private static final DataStore INSTANCE = new DataStore();

    private final List<User> users = new ArrayList<>();
    private final List<ActionLog> logs = new ArrayList<>();
    private final List<News> news = new ArrayList<>();
    private final List<Message> messages = new ArrayList<>();
    private final List<Complaint> complaints = new ArrayList<>();
    private final List<TechSupportRequest> supportRequests = new ArrayList<>();
    private final List<Course> courses = new ArrayList<>();
    private final List<RegistrationRequest> registrationRequests = new ArrayList<>();
    private final List<Researcher> researchers = new ArrayList<>();
    private final List<StudentOrganization> studentOrganizations = new ArrayList<>();
    private boolean courseRegistrationOpen;

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

    public User findUserByUsername(String username) throws exceptions.UserNotFoundException {
        Optional<User> user = users.stream()
                .filter(candidate -> Objects.equals(candidate.getUsername(), username))
                .findFirst();

        if (user.isEmpty()) {
            throw new exceptions.UserNotFoundException(username);
        }
        return user.get();
    }

    public User authenticate(String username, String password)
            throws exceptions.UserNotFoundException, exceptions.AuthenticationException {
        return new services.AuthenticationService(this).login(username, password);
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

    public void addSupportRequest(TechSupportRequest request) {
        if (request != null) supportRequests.add(request);
    }

    public List<TechSupportRequest> getSupportRequests() {
        return new ArrayList<>(supportRequests);
    }

    public void addCourse(Course course) {
        if (course != null && !courses.contains(course)) courses.add(course);
    }

    public List<Course> getCourses() {
        return new ArrayList<>(courses);
    }

    public void addRegistrationRequest(RegistrationRequest request) {
        if (request != null && !registrationRequests.contains(request)) {
            registrationRequests.add(request);
        }
    }

    public List<RegistrationRequest> getRegistrationRequests() {
        return new ArrayList<>(registrationRequests);
    }

    public void addResearcher(Researcher researcher) {
        if (researcher != null && !researchers.contains(researcher)) researchers.add(researcher);
    }

    public List<Researcher> getResearchers() {
        return new ArrayList<>(researchers);
    }

    public void addStudentOrganization(StudentOrganization organization) {
        if (organization != null && !studentOrganizations.contains(organization)) studentOrganizations.add(organization);
    }

    public List<StudentOrganization> getStudentOrganizations() {
        return new ArrayList<>(studentOrganizations);
    }

    public boolean isCourseRegistrationOpen() {
        return courseRegistrationOpen;
    }

    public void setCourseRegistrationOpen(boolean courseRegistrationOpen) {
        this.courseRegistrationOpen = courseRegistrationOpen;
    }

    public void ensureDefaultAdmin() {
        try {
            User user = findUserByUsername("admin");
            user.changePassword("admin");
            user.setActive(true);
            return;
        } catch (exceptions.UserNotFoundException ignored) {
        }

        addUser(new Admin("admin", "admin", "System", "Admin", "admin@university.kz"));
    }

    public void publishResearchPaper(Researcher researcher, ResearchPaper paper) {
        if (researcher != null && paper != null) {
            researcher.addPaper(paper);
        }
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
            outputStream.writeObject(registrationRequests);
            outputStream.writeBoolean(courseRegistrationOpen);
            outputStream.writeObject(supportRequests);
            outputStream.writeObject(researchers);
            outputStream.writeObject(studentOrganizations);
        }
    }

    @SuppressWarnings("unchecked")
    public void load() throws IOException, ClassNotFoundException {
        File file = new File(DEFAULT_FILE);
        if (!file.exists()) {
            return;
        }

        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file))) {
            clearAll();
            users.addAll((List<User>) inputStream.readObject());
            logs.addAll((List<ActionLog>) inputStream.readObject());
            news.addAll((List<News>) inputStream.readObject());
            messages.addAll((List<Message>) inputStream.readObject());
            complaints.addAll((List<Complaint>) inputStream.readObject());
            courses.addAll((List<Course>) inputStream.readObject());
            try {
                registrationRequests.addAll((List<RegistrationRequest>) inputStream.readObject());
                courseRegistrationOpen = inputStream.readBoolean();
                supportRequests.addAll((List<TechSupportRequest>) inputStream.readObject());
                researchers.addAll((List<Researcher>) inputStream.readObject());
                studentOrganizations.addAll((List<StudentOrganization>) inputStream.readObject());
            } catch (EOFException ignored) {
            }
        } catch (InvalidClassException e) {
            clearAll();
        }
    }

    private void clearAll() {
        users.clear();
        logs.clear();
        news.clear();
        messages.clear();
        complaints.clear();
        supportRequests.clear();
        courses.clear();
        registrationRequests.clear();
        researchers.clear();
        studentOrganizations.clear();
        courseRegistrationOpen = false;
    }

}
