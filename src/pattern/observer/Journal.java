package pattern.observer;

import java.util.ArrayList;
import java.util.List;

public class Journal implements Subject {
    private List<Observer> subscribers = new ArrayList<>();
    private String name;

    public Journal(String name) {
        this.name = name;
    }

    @Override
    public void subscribe(Observer observer) {
        subscribers.add(observer);
    }

    @Override
    public void unsubscribe(Observer observer) {
        subscribers.remove(observer);
    }

    @Override
    public void notifyObservers(String message) {
        for (Observer observer : subscribers) {
            observer.update("Journal " + name + ": " + message);
        }
    }

    public void publishResearchPaper(String paperTitle) {
        notifyObservers("New research paper published: " + paperTitle);
    }
}