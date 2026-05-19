package services;

import models.communication.News;
import data.DataStore;
import enums.NewsType;
import exceptions.NonResearcherJoinProjectException;
import models.research.ResearchPaper;
import models.research.ResearchProject;
import models.research.Researcher;

import java.util.Comparator;
import java.util.List;

public class ResearchService {
    private final DataStore dataStore;
    public ResearchService() { this(DataStore.getInstance()); }
    public ResearchService(DataStore dataStore) { this.dataStore = dataStore; }

    public void publishPaper(Researcher researcher, ResearchPaper paper) {
        if (researcher == null || paper == null) return;
        researcher.addPaper(paper);
        dataStore.addNews(new News(Math.abs((researcher.getId() + paper.getTitle()).hashCode()),
                "New research paper: " + paper.getTitle(),
                researcher.getName() + " published a paper in " + paper.getJournalName(),
                NewsType.RESEARCH));
    }

    public void joinProject(Researcher researcher, ResearchProject project) throws NonResearcherJoinProjectException {
        if (researcher != null && project != null) researcher.addProject(project);
    }

    public int calculateHIndex(Researcher researcher) { return researcher == null ? 0 : researcher.calculateHIndex(); }
    public List<ResearchPaper> sortPapers(Researcher researcher, Comparator<ResearchPaper> comparator) { return researcher.printPapers(comparator); }
}
