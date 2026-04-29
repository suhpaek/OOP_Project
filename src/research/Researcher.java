package research;

import exceptions.NonResearcherJoinProjectException;
import users.User;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class Researcher implements Serializable {

    public static final Comparator<ResearchPaper> BY_CITATIONS 
    public static final Comparator<ResearchPaper> BY_DATE 
    public static final Comparator<ResearchPaper> BY_LENGTH 

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
    }

    public void joinProject(ResearchProject project) throws NonResearcherJoinProjectException {

    }

    public int calculateHIndex() {
    }

    public List<ResearchPaper> printPapers(Comparator<ResearchPaper> comparator) {

    public int getTotalCitations() {
    }

    public String getName() {
    }

    public User getUser() {
        return user;
    }

    public List<ResearchPaper> getPapers() {
    }

    public List<ResearchProject> getProjects() {
    }

    public static List<ResearchPaper> printAllPapers(List<ResearchPaper> papers) {
}

    public static Researcher getTopCitedResearcher(List<Researcher> researchers) {
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
}