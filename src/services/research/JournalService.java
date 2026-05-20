package services.research;

import data.UniversityDataStore;
import models.research.Journal;
import models.research.ResearchPaper;
import models.users.User;

import java.io.IOException;
import java.util.List;

public class JournalService {
    private final UniversityDataStore dataStore;

    public JournalService() {
        this(UniversityDataStore.getInstance());
    }

    public JournalService(UniversityDataStore dataStore) {
        this.dataStore = dataStore;
    }

    public Journal createJournal(String name) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Journal name is required.");
        Journal existing = findJournalOrNull(name);
        if (existing != null) return existing;
        Journal journal = new Journal(name);
        dataStore.addJournal(journal);
        saveData();
        return journal;
    }

    public List<Journal> getJournals() {
        return dataStore.getJournals();
    }

    public void subscribe(User user, String journalName) {
        Journal journal = findJournal(journalName);
        journal.subscribe(user);
        saveData();
    }

    public void unsubscribe(User user, String journalName) {
        Journal journal = findJournal(journalName);
        journal.unsubscribe(user);
        saveData();
    }

    public void publishPaper(String journalName, ResearchPaper paper) {
        Journal journal = findJournal(journalName);
        journal.publishPaper(paper);
        saveData();
    }

    private Journal findJournal(String name) {
        Journal journal = findJournalOrNull(name);
        if (journal == null) throw new IllegalArgumentException("Journal not found.");
        return journal;
    }

    private Journal findJournalOrNull(String name) {
        for (Journal journal : dataStore.getJournals()) {
            if (journal.getName().equalsIgnoreCase(name)) return journal;
        }
        return null;
    }

    private void saveData() {
        try {
            dataStore.save();
        } catch (IOException e) {
            throw new IllegalStateException("Could not save journal changes.", e);
        }
    }
}
