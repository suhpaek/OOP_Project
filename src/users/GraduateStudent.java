package users;

import enums.Degree;

import java.util.ArrayList;
import java.util.List;

public class GraduateStudent extends Student {
    private Degree degree;
    private String researchSupervisorName;
    private final List<String> diplomaProjects = new ArrayList<>();

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

    public void setDegree(Degree degree) {
        this.degree = degree;
    }

    public String getResearchSupervisorName() {
        return researchSupervisorName;
    }

    public void setResearchSupervisorName(String researchSupervisorName) {
        this.researchSupervisorName = researchSupervisorName;
    }

    public List<String> getDiplomaProjects() {
        return diplomaProjects;
    }

    public void addDiplomaProject(String paper) {
        diplomaProjects.add(paper);
    }
}
