package research;

import exceptions.NonResearcherJoinProjectException;

import java.util.ArrayList;
import java.util.List;

public class ResearchProject {
    private String topic;
    private List<Researcher> participants;
    private List<ResearchPaper> publishedPapers;

    public ResearchProject(String topic) {
        this.topic = topic;
        this.participants = new ArrayList<>();
        this.publishedPapers = new ArrayList<>();
    }

    public void addParticipant(Object person) throws NonResearcherJoinProjectException {
        if (!(person instanceof Researcher)) {
            throw new NonResearcherJoinProjectException("Only researchers can join project");
        }
        participants.add((Researcher) person);
    }

    public void addPaper(ResearchPaper paper) {
        publishedPapers.add(paper);
    }

    public String getTopic() {
        return topic;
    }

    public List<Researcher> getParticipants() {
        return participants;
    }

    public List<ResearchPaper> getPublishedPapers() {
        return publishedPapers;
    }
}
