package services.research;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import data.UniversityDataStore;
import enums.NewsType;
import exceptions.NonResearcherJoinProjectException;
import models.communication.News;
import models.research.DiplomaProjectOwner;
import models.research.ResearchPaper;
import models.research.ResearchProject;
import models.research.Researcher;
import models.users.User;
import pattern.strategy.SortStrategy;

public class ResearchService {

    private final UniversityDataStore dataStore;

    public ResearchService() {
        this(UniversityDataStore.getInstance());
    }

    public ResearchService(UniversityDataStore dataStore) {
        this.dataStore = dataStore;
    }

    public Researcher getOrCreateResearcher(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User is required.");
        }
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
        if (user instanceof DiplomaProjectOwner) {
            ((DiplomaProjectOwner) user).addDiplomaProject(paper);
        }
        new JournalService(dataStore).createJournal(paper.getJournalName());
        new JournalService(dataStore).publishPaper(paper.getJournalName(), paper);
        createTopCitedResearcherNews();
        saveData();
    }

    public void publishPaper(Researcher researcher, ResearchPaper paper) {
        if (researcher == null || paper == null) {
            throw new IllegalArgumentException("Researcher and paper are required.");
        }
        researcher.addPaper(paper);
        dataStore.addNews(new News(Math.abs((researcher.getId() + paper.getTitle()).hashCode()),
                "New research paper: " + paper.getTitle(),
                researcher.getName() + " published a paper in " + paper.getJournalName(),
                NewsType.RESEARCH));
        saveData();
    }

    public void joinProject(Researcher researcher, ResearchProject project) throws NonResearcherJoinProjectException {
        if (researcher != null && project != null) {
            researcher.addProject(project);
        }
    }

    public int calculateHIndex(User user) {
        return getOrCreateResearcher(user).calculateHIndex();
    }

    public int calculateHIndex(Researcher researcher) {
        return researcher == null ? 0 : researcher.calculateHIndex();
    }

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

    public News createTopCitedResearcherNews() {
        Researcher top = getTopCitedResearcher();
        if (top == null) {
            return null;
        }
        News newsItem = new News(
                Math.abs(("top-cited-" + top.getId()).hashCode()),
                "Top cited researcher",
                top.getName() + " has " + top.getTotalCitations() + " citations",
                NewsType.RESEARCH
        );
        dataStore.addNews(newsItem);
        saveData();
        return newsItem;
    }

    public Researcher getTopCitedResearcherForYear(int year) {
        Researcher top = null;
        int max = -1;
        for (Researcher r : dataStore.getResearchers()) {
            int total = 0;
            for (ResearchPaper p : r.getPapers()) {
                if (p.getPublishDate() != null && p.getPublishDate().getYear() == year) {
                    total += p.getCitations();
                }
            }
            if (total > max) {
                max = total;
                top = r;
            }
        }
        return top;
    }

    public Researcher getTopCitedResearcherForYearAndSchool(int year, enums.School school) {
        Researcher top = null;
        int max = -1;
        for (Researcher r : dataStore.getResearchers()) {
            // resolve school only for Student users; skip others
            models.users.User u = r.getUser();
            enums.School userSchool = null;
            if (u instanceof models.users.Student) {
                userSchool = ((models.users.Student) u).getSchool();
            }
            if (userSchool == null || userSchool != school) {
                continue;
            }

            int total = 0;
            for (ResearchPaper p : r.getPapers()) {
                if (p.getPublishDate() != null && p.getPublishDate().getYear() == year) {
                    total += p.getCitations();
                }
            }
            if (total > max) {
                max = total;
                top = r;
            }
        }
        return top;
    }

    public News createTopCitedResearcherNewsForYear(int year) {
        Researcher top = getTopCitedResearcherForYear(year);
        if (top == null) {
            return null;
        }
        News newsItem = new News(
                Math.abs(("top-cited-year-" + year + "-" + top.getId()).hashCode()),
                "Top cited researcher of " + year,
                top.getName() + " has " + computeCitationsForYear(top, year) + " citations in " + year,
                NewsType.RESEARCH
        );
        dataStore.addNews(newsItem);
        saveData();
        return newsItem;
    }

    public News createTopCitedResearcherNewsForYearAndSchool(int year, enums.School school) {
        Researcher top = getTopCitedResearcherForYearAndSchool(year, school);
        if (top == null) {
            return null;
        }
        int total = computeCitationsForYear(top, year);
        News newsItem = new News(
                Math.abs(("top-cited-year-school-" + year + "-" + school + "-" + top.getId()).hashCode()),
                "Top cited researcher of " + school + " in " + year,
                top.getName() + " has " + total + " citations in " + year + " for " + school,
                NewsType.RESEARCH
        );
        dataStore.addNews(newsItem);
        saveData();
        return newsItem;
    }

    private int computeCitationsForYear(Researcher r, int year) {
        int total = 0;
        for (ResearchPaper p : r.getPapers()) {
            if (p.getPublishDate() != null && p.getPublishDate().getYear() == year) {
                total += p.getCitations();
            }
        }
        return total;
    }

    public List<Researcher> getAllResearchers() {
        return dataStore.getResearchers();
    }

    public List<ResearchPaper> getAllPapersSorted(SortStrategy strategy) {
        List<ResearchPaper> papers = new ArrayList<>();
        for (Researcher researcher : dataStore.getResearchers()) {
            papers.addAll(researcher.getPapers());
        }
        if (strategy != null) {
            strategy.sort(papers);
        }
        return papers;
    }

    private void saveData() {
        try {
            dataStore.save();
        } catch (IOException e) {
            throw new IllegalStateException("Could not save research changes.", e);
        }
    }
}
