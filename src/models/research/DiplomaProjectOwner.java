package models.research;

import java.util.List;

public interface DiplomaProjectOwner {
    List<ResearchPaper> getDiplomaProjects();

    void addDiplomaProject(ResearchPaper paper);
}
