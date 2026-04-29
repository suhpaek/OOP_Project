package research;

import java.util.ArrayList;
import java.util.List;

public class Journal {
    private String name;
    private List<String> subscribers;
    private List<ResearchPaper> papers;

    public Journal(String name) {
        this.name = name;
        this.subscribers = new ArrayList<>();
        this.papers = new ArrayList<>();
    }

    public void subscribe(String userId) {
        subscribers.add(userId);
    }

    public void unsubscribe(String userId) {
        subscribers.remove(userId);
    }

    public void publishPaper(ResearchPaper paper) {
        papers.add(paper);
        notifySubscribers(paper);
    }

    public void notifySubscribers(ResearchPaper paper) {
    }

    public String getName() {
        return name;
    }

    public List<String> getSubscribers() {
        return subscribers;
    }

    public List<ResearchPaper> getPapers() {
        return papers;
    }
}
