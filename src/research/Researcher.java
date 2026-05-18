package research;

import communication.News;
import data.DataStore;
import enums.NewsType;
import exceptions.InvalidSupervisorException;
import exceptions.NonResearcherJoinProjectException;
import users.User;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class Researcher implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final int  MIN_SUPERVISOR_H_INDEX = 3;

    public static final Comparator<ResearchPaper> BY_CITATIONS =
            Comparator.comparingInt(ResearchPaper::getCitations).reversed();

    public static final Comparator<ResearchPaper> BY_DATE =
            Comparator.comparing(ResearchPaper::getPublishDate,
                    Comparator.nullsLast(Comparator.reverseOrder()));

    public static final Comparator<ResearchPaper> BY_LENGTH =
            Comparator.comparingInt(ResearchPaper::getPages).reversed();

    private User user;
    private List<ResearchPaper> papers;
    private List<ResearchProject> projects;

    public Researcher(User user) {
        this.user = Objects.requireNonNull(user, "User must not be null");
        this.papers = new ArrayList<>();
        this.projects = new ArrayList<>();
    }

    public void publishPaper(ResearchPaper paper) {
        if (paper == null) return;

        if (!papers.contains(paper)) {
            papers.add(paper);
        }
        paper.addAuthor(this);

        DataStore ds = DataStore.getInstance();
        ds.addPaper(paper);

        String newsTitle = "New Research Paper: " + paper.getTitle();
        String newsContent = getName() + " published a paper in \"" + paper.getJournalName() + "\"";
        int newsId = Objects.hash(getId(), paper.getTitle(), System.currentTimeMillis());
        ds.addNews(new News(newsId, newsTitle, newsContent, NewsType.RESEARCH));
        ds.logAction(getId(), "Published paper: " + paper.getTitle());
    }

    public void joinProject(ResearchProject project) throws NonResearcherJoinProjectException {
        if (project == null) return;
        project.addParticipant(this);
        if (!projects.contains(project)) {
            projects.add(project);
        }
        DataStore.getInstance().logAction(getId(), "Joined project: " + project.getTopic());
    }

    public int calculateHIndex() {
        List<Integer> sortedCitations = papers.stream()
                .map(ResearchPaper::getCitations)
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());

        int h = 0;
        for (int i = 0; i < sortedCitations.size(); i++) {
            if (sortedCitations.get(i) >= i + 1) {
                h = i + 1;
            } else {
                break;
            }
        }
        return h;
    }

    public void validateAsSupervisor() throws InvalidSupervisorException {
        int h = calculateHIndex();
        if (h < MIN_SUPERVISOR_H_INDEX) {
            throw new InvalidSupervisorException(getName(), h);
        }
    }

    public List<ResearchPaper> printPapers(Comparator<ResearchPaper> comparator) {
        return papers.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    public int getTotalCitations() {
        return papers.stream().mapToInt(ResearchPaper::getCitations).sum();
    }

    public static List<ResearchPaper> printAllPapers(List<ResearchPaper> papers, Comparator<ResearchPaper> comparator) {
        return papers.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    public static Optional<Researcher> getTopCitedResearcher(List<Researcher> researchers) {
        if (researchers == null || researchers.isEmpty()) return Optional.empty();
        return researchers.stream()
                .max(Comparator.comparingInt(Researcher::getTotalCitations));
    }

    public static Optional<Researcher> getTopCitedResearcherOfYear(List<Researcher> researchers, int year) {
        return researchers.stream()
                .max(Comparator.comparingInt(r ->
                        r.getPapers().stream()
                         .filter(p -> p.getPublishDate() != null
                                 && p.getPublishDate().getYear() == year)
                         .mapToInt(ResearchPaper::getCitations)
                         .sum()
                ));
    }

    public static void announceTopCitedResearcher(List<Researcher> researchers) {
        getTopCitedResearcher(researchers).ifPresent(top -> {
            int newsId = Objects.hash("top-cited", top.getId(), System.currentTimeMillis());
            News news = new News(
                    newsId,
                    "Top Cited Researcher: " + top.getName(),
                    top.getName() + " leads the university with "
                            + top.getTotalCitations() + " total citations and h-index "
                            + top.calculateHIndex() + ".",
                    NewsType.RESEARCH
            );
            DataStore.getInstance().addNews(news);
        });
    }

    public String getName(){ 
        return user.getFullName(); 
    }
    public String getId(){ 
        return user.getId(); 
    }
    public User getUser(){ 
        return user; 
    }

    public List<ResearchPaper> getPapers() { 
        return new ArrayList<>(papers); 
    }
    public List<ResearchProject> getProjects() { 
        return new ArrayList<>(projects); 
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
        return String.format("Researcher{name='%s', hIndex=%d, totalCitations=%d, papers=%d}",
                getName(), calculateHIndex(), getTotalCitations(), papers.size());
    }
}