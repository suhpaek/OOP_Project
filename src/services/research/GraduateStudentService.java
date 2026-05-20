package services.research;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import data.UniversityDataStore;
import models.research.ResearchPaper;
import models.research.Researcher;
import models.users.GraduateStudent;

public class GraduateStudentService {
    private final UniversityDataStore dataStore;
    private final ResearchService researchService;

    public GraduateStudentService() {
        this(UniversityDataStore.getInstance(), new ResearchService());
    }

    public GraduateStudentService(UniversityDataStore dataStore, ResearchService researchService) {
        this.dataStore = dataStore;
        this.researchService = researchService;
    }

    public List<ResearchPaper> getDiplomaProjects(GraduateStudent student) {
        return new ArrayList<>(student.getDiplomaProjects());
    }

    public String getSupervisorInfo(GraduateStudent student) {
        Researcher supervisor = student.getResearchSupervisor();
        if (supervisor == null) {
            return "Supervisor is not assigned.";
        }
        return supervisor.getName() + " | h-index: " + supervisor.calculateHIndex();
    }

    public int syncPublishedPapersAsDiplomaProjects(GraduateStudent student) {
        int added = 0;
        for (ResearchPaper paper : researchService.getPapers(student)) {
            if (!student.getDiplomaProjects().contains(paper)) {
                student.addDiplomaProject(paper);
                added++;
            }
        }
        if (added > 0) {
            saveData();
        }
        return added;
    }

    private void saveData() {
        try {
            dataStore.save();
        } catch (IOException e) {
            throw new IllegalStateException("Could not save graduate student changes.", e);
        }
    }
}
