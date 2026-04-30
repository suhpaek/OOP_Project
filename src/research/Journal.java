package research;

import users.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Journal implements Serializable {
    private String name;
    private List<User> subscribers;
    private List<ResearchPaper> papers;
    private List<String> notifications;

    public Journal(String name) {
        this.name = name;
        this.subscribers = new ArrayList<>();
        this.papers = new ArrayList<>();
        this.notifications = new ArrayList<>();
    }

    public void subscribe(User user) {
        if (user != null && !subscribers.contains(user)) {
            subscribers.add(user);
        }
    }

    public void unsubscribe(User user) {
        subscribers.remove(user);
    }

    public void publishPaper(ResearchPaper paper) {
        papers.add(paper);
        notifySubscribers(paper);
    }

    public void notifySubscribers(ResearchPaper paper) {
        for (User subscriber : subscribers) {
            notifications.add("Notification for " + subscriber.getId() + ": new paper published in "
                    + name + " - " + paper.getTitle());
        }
    }

    public String getName() {
        return name;
    }

    public List<User> getSubscribers() {
        return new ArrayList<>(subscribers);
    }

    public List<ResearchPaper> getPapers() {
        return new ArrayList<>(papers);
    }

    public List<String> getNotifications() {
        return new ArrayList<>(notifications);
    }
}
