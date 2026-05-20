package models.users;

import enums.Degree;
import exceptions.InvalidSupervisorException;
import models.research.DiplomaProjectOwner;
import models.research.ResearchPaper;
import models.research.Researcher;

import java.util.ArrayList;
import java.util.List;

public class GraduateStudent extends Student implements DiplomaProjectOwner {
    private Degree graduateDegree;
    private Researcher researchSupervisor;
    private final List<ResearchPaper> diplomaProjects = new ArrayList<>();

    public GraduateStudent() {
        super();
    }

    public GraduateStudent(String username, String password, String firstName, String lastName, String email, Degree degree) {
        super(username, password, firstName, lastName, email);
        this.graduateDegree = degree;
    }

    public Degree getDegree() {
        return graduateDegree;
    }

    public void setDegree(Degree degree) {
        this.graduateDegree = degree;
    }

    void setGraduateDegreeByAdmin(Degree degree) {
        this.graduateDegree = degree;
    }

    public void setGraduateDegree(Degree degree) {
        setGraduateDegreeByAdmin(degree);
    }

    public Researcher getResearchSupervisor() {
        return researchSupervisor;
    }

    public String getResearchSupervisorName() {
        return researchSupervisor == null ? null : researchSupervisor.getName();
    }

    public void setResearchSupervisorName(String researchSupervisorName) {
        // kept for compatibility with previous model
    }

    public void setResearchSupervisorByAdmin(Researcher researchSupervisor) throws InvalidSupervisorException {
        if (researchSupervisor == null) {
            throw new InvalidSupervisorException("null", 0);
        }
        int hIndex = researchSupervisor.calculateHIndex();
        if (hIndex < 3) {
            throw new InvalidSupervisorException(researchSupervisor.getName(), hIndex);
        }
        this.researchSupervisor = researchSupervisor;
    }

    public void setResearchSupervisor(Researcher researchSupervisor) {
        this.researchSupervisor = researchSupervisor;
    }

    public List<ResearchPaper> getDiplomaProjects() {
        return diplomaProjects;
    }

    public void addDiplomaProject(ResearchPaper paper) {
        if (paper != null) {
            diplomaProjects.add(paper);
        }
    }

    public void addDiplomaProject(String paper) {
        if (paper != null) {
            diplomaProjects.add(new ResearchPaper(paper, "Unknown Journal", 0, null, paper));
        }
    }
}
