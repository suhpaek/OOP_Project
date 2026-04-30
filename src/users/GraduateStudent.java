package users;

import enums.Degree;
import exceptions.InvalidSupervisorException;
import research.ResearchPaper;
import research.Researcher;

import java.util.ArrayList;
import java.util.List;

public class GraduateStudent extends Student {
    private Degree degree;
    private Researcher researchSupervisor;
    private final List<ResearchPaper> diplomaProjects = new ArrayList<>();

    public GraduateStudent() {
        super();
    }

    public GraduateStudent(String username, String password, String firstName, String lastName, String email, Degree degree) {
        super(username, password, firstName, lastName, email);
        this.degree = degree;
    }

    public Degree getDegree() {
        return degree;
    }

    void setGraduateDegreeByAdmin(Degree degree) {
        this.degree = degree;
    }

    public String getResearchSupervisorName() {
        return researchSupervisor == null ? null : researchSupervisor.getName();
    }

    public Researcher getResearchSupervisor() {
        return researchSupervisor;
    }

    void setResearchSupervisorByAdmin(Researcher researchSupervisor) throws InvalidSupervisorException {
        if (researchSupervisor != null && researchSupervisor.calculateHIndex() < 3) {
            throw new InvalidSupervisorException(researchSupervisor.getName(), researchSupervisor.calculateHIndex());
        }
        this.researchSupervisor = researchSupervisor;
    }

    public List<ResearchPaper> getDiplomaProjects() {
        return new ArrayList<>(diplomaProjects);
    }

    public void addDiplomaProject(ResearchPaper paper) {
        diplomaProjects.add(paper);
    }
}
