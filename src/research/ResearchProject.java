package research;

import exceptions.NonResearcherJoinProjectException;

import java.io.Serializable;
import java.util.*;

public class ResearchProject implements Serializable {

    private static final long serialVersionUID = 1L;

    private String topic;
    private List<Researcher> participants;
    private List<ResearchPaper> publishedPapers;
    private String description;

    public ResearchProject(String topic) {
        this.topic = Objects.requireNonNull(topic, "topic must not be null");
        this.participants = new ArrayList<>();
        this.publishedPapers = new ArrayList<>();
        this.description = "";
    }

    public ResearchProject(String topic, String description) {
        this(topic);
        this.description = description;
    }

    public void addParticipant(Object person) throws NonResearcherJoinProjectException {
        if (!(person instanceof Researcher)) {
            String id = (person != null) ? person.toString() : "null";
            throw new NonResearcherJoinProjectException(id);
        }
        Researcher researcher = (Researcher) person;
        if (!participants.contains(researcher)) {
            participants.add(researcher);
        }
    }

    public void removeParticipant(Researcher researcher) {
        participants.remove(researcher);
    }

    public boolean hasParticipant(Researcher researcher) {
        return participants.contains(researcher);
    }

    public void addPaper(ResearchPaper paper) {
        if (paper != null && !publishedPapers.contains(paper)) {
            publishedPapers.add(paper);
        }
    }

    public String getTopic() { 
        return topic; 
    }
    public String getDescription(){ 
        return description; 
    }
    public List<Researcher> getParticipants(){ 
        return new ArrayList<>(participants); 
    }
    public List<ResearchPaper> getPublishedPapers(){ 
        return new ArrayList<>(publishedPapers); 
    }

    public void setDescription(String description) { 
        this.description = description; 
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ResearchProject)) return false;
        ResearchProject that = (ResearchProject) o;
        return Objects.equals(topic, that.topic);
    }

    @Override
    public int hashCode() {
        return Objects.hash(topic);
    }

    @Override
    public String toString() {
        return String.format("ResearchProject{topic='%s', participants=%d, papers=%d}",
                topic, participants.size(), publishedPapers.size());
    }
}