package research;

import users.User;

import java.io.Serializable;
import java.util.*;

public class Journal implements Serializable {

    private static final long serialVersionUID = 1L;

    private String              name;
    private List<User>          subscribers;
    private List<ResearchPaper> papers;
    private List<String>        notificationLog;

    public Journal(String name) {
        this.name            = Objects.requireNonNull(name, "name must not be null");
        this.subscribers     = new ArrayList<>();
        this.papers          = new ArrayList<>();
        this.notificationLog = new ArrayList<>();
    }

    public void subscribe(User user) {
        if (user != null && !subscribers.contains(user)) {
            subscribers.add(user);
        }
    }

    public void unsubscribe(User user) {
        subscribers.remove(user);
    }

    public boolean isSubscribed(User user) {
        return subscribers.contains(user);
    }

    public void publishPaper(ResearchPaper paper) {
        if (paper == null) return;
        if (!papers.contains(paper)) {
            papers.add(paper);
        }
        notifySubscribers(paper);
    }

    private void notifySubscribers(ResearchPaper paper) {
        String msg = String.format("[%s] New paper published: \"%s\" — %s",
                name, paper.getTitle(),
                paper.getPublishDate() != null ? paper.getPublishDate().toString() : "n.d.");

        for (User subscriber : subscribers) {
            notificationLog.add("[→ " + subscriber.getId() + "] " + msg);
        }
    }

    public String              getName()            { return name; }
    public List<User>          getSubscribers()     { return new ArrayList<>(subscribers); }
    public List<ResearchPaper> getPapers()          { return new ArrayList<>(papers); }
    public List<String>        getNotificationLog() { return new ArrayList<>(notificationLog); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Journal)) return false;
        Journal journal = (Journal) o;
        return Objects.equals(name, journal.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return String.format("Journal{name='%s', subscribers=%d, papers=%d}",
                name, subscribers.size(), papers.size());
    }
}