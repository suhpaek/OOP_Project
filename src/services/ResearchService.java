package services;

import models.communication.News;
import data.DataStore;
import enums.NewsType;
import exceptions.NonResearcherJoinProjectException;
import models.research.ResearchPaper;
import models.research.ResearchProject;
import models.research.Researcher;
import models.users.User;
import pattern.strategy.SortStrategy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ResearchService {
    private final DataStore dataStore;
    public ResearchService() { this(DataStore.getInstance()); }
    public ResearchService(DataStore dataStore) { this.dataStore = dataStore; }

    public Researcher getOrCreateResearcher(User user) {
        if (user == null) throw new IllegalArgumentException("User is required.");
        for (Researcher researcher : dataStore.getResearchers()) {
            if (researcher.getUser().equals(user)) {
                return researcher;
            }
        }
        Researcher researcher = new Researcher(user);
        dataStore.addResearcher(researcher);
        saveData();
        return researcher;
    }

    public void publishPaper(User user, ResearchPaper paper) {
        publishPaper(getOrCreateResearcher(user), paper);
    }

    public void publishPaper(Researcher researcher, ResearchPaper paper) {
        if (researcher == null || paper == null) throw new IllegalArgumentException("Researcher and paper are required.");
        researcher.addPaper(paper);
        dataStore.addNews(new News(Math.abs((researcher.getId() + paper.getTitle()).hashCode()),
                "New research paper: " + paper.getTitle(),
                researcher.getName() + " published a paper in " + paper.getJournalName(),
                NewsType.RESEARCH));
        saveData();
    }

    public void joinProject(Researcher researcher, ResearchProject project) throws NonResearcherJoinProjectException {
        if (researcher != null && project != null) researcher.addProject(project);
    }

    public int calculateHIndex(User user) { return getOrCreateResearcher(user).calculateHIndex(); }
    public int calculateHIndex(Researcher researcher) { return researcher == null ? 0 : researcher.calculateHIndex(); }

    public List<ResearchPaper> sortPapers(User user, SortStrategy strategy) {
        List<ResearchPaper> papers = new ArrayList<>(getOrCreateResearcher(user).getPapers());
        strategy.sort(papers);
        return papers;
    }

    public List<ResearchPaper> getPapers(User user) {
        return getOrCreateResearcher(user).getPapers();
    }

    public Researcher getTopCitedResearcher() {
        return Researcher.getTopCitedResearcher(dataStore.getResearchers());
    }

    public List<Researcher> getAllResearchers() {
        return dataStore.getResearchers();
    }

    private void saveData() {
        try {
            dataStore.save();
        } catch (IOException e) {
            throw new IllegalStateException("Could not save research changes.", e);
        }
    }
}
