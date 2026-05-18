package data;

import communication.Message;
import communication.News;
import research.Journal;
import research.ResearchPaper;
import research.Researcher;
import support.ActionLog;
import support.TechSupportRequest;
import users.Student;
import users.User;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DataStore {
    private static final String DEFAULT_FILE = "university.ser";
    private static final DataStore INSTANCE = new DataStore();

    private final List<User> users = new ArrayList<>();
    private final List<ActionLog> actionLogs = new ArrayList<>();
    private final List<News> newsList = new ArrayList<>();
    private final List<Message> messages = new ArrayList<>();
    private final List<Researcher> researchers = new ArrayList<>();
    private final List<TechSupportRequest> techRequests = new ArrayList<>();
    private final List<Journal> journals = new ArrayList<>();
    private List<ResearchPaper> allPapers = new ArrayList<>();

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

    public void logAction(String actorId, String action) {
        String id = "LOG-" + System.currentTimeMillis() + "-" + actorId;
        actionLogs.add(new ActionLog(id, actorId, action));
    }
 
    public List<ActionLog> getActionLogs() {
        return new ArrayList<>(actionLogs);
    }
 
    public List<ActionLog> getActionLogsFor(String actorId) {
        return actionLogs.stream()
                .filter(l -> l.getActorId().equals(actorId))
                .collect(Collectors.toList());
    }

    public void addTechRequest(TechSupportRequest request) {
        techRequests.add(request);
    }
 
    public List<TechSupportRequest> getAllTechRequests() {
        return new ArrayList<>(techRequests);
    }

    public void addResearcher(Researcher researcher) {
        if (!researchers.contains(researcher)) {
            researchers.add(researcher);
        }
    }
 
    public List<Researcher> getAllResearchers() {
        return new ArrayList<>(researchers);
    }
 
    public Optional<Researcher> findResearcherById(String userId) {
        return researchers.stream()
                .filter(r -> r.getId().equals(userId))
                .findFirst();
    }

    public void addPaper(ResearchPaper paper) {
        if (!allPapers.contains(paper)) {
            allPapers.add(paper);
        }
    }
 
    public List<ResearchPaper> getAllPapers() {
        return new ArrayList<>(allPapers);
    }
 
    public List<ResearchPaper> getAllPapersSorted(Comparator<ResearchPaper> comparator) {
        return allPapers.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    public void addMessage(Message message) {
        messages.add(message);
    }
 
    public List<Message> getMessagesFor(String userId) {
        return messages.stream()
                .filter(m -> m.getReceiverId().equals(userId))
                .collect(Collectors.toList());
    }
 
    public List<Message> getMessagesSentBy(String userId) {
        return messages.stream()
                .filter(m -> m.getSenderId().equals(userId))
                .collect(Collectors.toList());
    }
 
    public List<Message> getConversation(String userId1, String userId2) {
        return messages.stream()
                .filter(m -> (m.getSenderId().equals(userId1) && m.getReceiverId().equals(userId2))
                          || (m.getSenderId().equals(userId2) && m.getReceiverId().equals(userId1)))
                .sorted(Comparator.comparing(Message::getSentAt))
                .collect(Collectors.toList());
    }

    public void addNews(News news) {
        newsList.add(news);
    }
 
    public void removeNews(int newsId) {
        newsList.removeIf(n -> n.getId() == newsId);
    }
 
    public List<News> getNewsSorted() {
        return newsList.stream()
                    .sorted(Comparator
                        .comparing(News::isPinned).reversed()
                        .thenComparing(News::getCreatedAt, Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }
 
    public List<News> getAllNews() {
        return new ArrayList<>(newsList);
    }

    public void addJournal(Journal journal) {
        if (!journals.contains(journal)) {
            journals.add(journal);
        }
    }
 
    public Optional<Journal> findJournalByName(String name) {
        return journals.stream()
                .filter(j -> j.getName().equalsIgnoreCase(name))
                .findFirst();
    }
 
    public List<Journal> getAllJournals() {
        return new ArrayList<>(journals);
    }

    public synchronized void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DEFAULT_FILE))) {
            oos.writeObject(this);
            System.out.println("[DataStore] Data saved to " + DEFAULT_FILE);
        } catch (IOException e) {
            System.err.println("[DataStore] Failed to save: " + e.getMessage());
        }
    }
 
    @SuppressWarnings("unused")
    private static DataStore loadFromFile() {
        File file = new File(DEFAULT_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                DataStore loaded = (DataStore) ois.readObject();
                System.out.println("[DataStore] Data loaded from " + DEFAULT_FILE);
                return loaded;
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("[DataStore] Failed to load, starting fresh: " + e.getMessage());
            }
        }
        return new DataStore();
    }
}
