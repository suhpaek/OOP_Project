package research;

import communication.News;
import data.DataStore;
import enums.NewsType;
import exceptions.NonResearcherJoinProjectException;
import users.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class Researcher implements Serializable {
    public static final Comparator<ResearchPaper> BY_CITATIONS =
            Comparator.comparingInt(ResearchPaper::getCitations).reversed();
    public static final Comparator<ResearchPaper> BY_DATE =
            Comparator.comparing(ResearchPaper::getPublishDate, Comparator.nullsLast(Comparator.reverseOrder()));
    public static final Comparator<ResearchPaper> BY_LENGTH =
            Comparator.comparingInt(ResearchPaper::getPages).reversed();

    private User user;
    private List<ResearchPaper> papers;
    private List<ResearchProject> projects;

    public Researcher(User user) {
        if (user == null) throw new IllegalArgumentException("User must not be null");
        this.user = user;
        this.papers = new ArrayList<>();
        this.projects = new ArrayList<>();
    }

    public void publishPaper(ResearchPaper paper) {
        if (paper == null) return;
        if (!papers.contains(paper)) {
            papers.add(paper);
        }
        paper.addAuthor(this);
        DataStore.getInstance().addNews(new News(
                Math.abs((getId() + paper.getTitle()).hashCode()),
                "New research paper: " + paper.getTitle(),
                getName() + " published a paper in " + paper.getJournalName(),
                NewsType.RESEARCH
        ));
    }

    public void joinProject(ResearchProject project) throws NonResearcherJoinProjectException {
        if (project == null) return;
        project.addParticipant(this);
        if (!projects.contains(project)) {
            projects.add(project);
        }
    }

    public int calculateHIndex() {
        List<Integer> citations = new ArrayList<>();
        for (ResearchPaper paper : papers) {
            citations.add(paper.getCitations());
        }
        citations.sort(Collections.reverseOrder());

        int hIndex = 0;
        for (int i = 0; i < citations.size(); i++) {
            if (citations.get(i) >= i + 1) {
                hIndex = i + 1;
            } else {
                break;
            }
        }
        return hIndex;
    }

    public List<ResearchPaper> printPapers(Comparator<ResearchPaper> comparator) {
        List<ResearchPaper> sortedPapers = new ArrayList<>(papers);
        sortedPapers.sort(comparator);
        return sortedPapers;
    }

    public int getTotalCitations() {
        int total = 0;
        for (ResearchPaper paper : papers) {
            total += paper.getCitations();
        }
        return total;
    }

    public String getName() {
        return user.getFullName();
    }

    public String getId() {
        return user.getId();
    }

    public User getUser() {
        return user;
    }

    public List<ResearchPaper> getPapers() {
        return new ArrayList<>(papers);
    }

    public List<ResearchProject> getProjects() {
        return new ArrayList<>(projects);
    }

    public static List<ResearchPaper> printAllPapers(List<ResearchPaper> papers, Comparator<ResearchPaper> comparator) {
        List<ResearchPaper> sortedPapers = new ArrayList<>(papers);
        sortedPapers.sort(comparator);
        return sortedPapers;
    }

    public static Researcher getTopCitedResearcher(List<Researcher> researchers) {
        if (researchers == null || researchers.isEmpty()) return null;
        return Collections.max(researchers, Comparator.comparingInt(Researcher::getTotalCitations));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Researcher)) return false;
        Researcher that = (Researcher) o;
        return Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user);
    }

    @Override
    public String toString() {
        return "Researcher{" +
                "name='" + getName() + '\'' +
                ", hIndex=" + calculateHIndex() +
                ", totalCitations=" + getTotalCitations() +
                '}';
    }
}
