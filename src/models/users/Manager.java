package models.users;

import models.academic.Course;
import enums.ManagerType;

import java.util.ArrayList;
import java.util.List;

public class Manager extends Employee {
    private ManagerType managerType;
    private final List<Course> coursesForRegistration = new ArrayList<>();

    public Manager() {
        super();
    }

    public Manager(String username, String password, String firstName, String lastName, String email, ManagerType managerType) {
        super(username, password, firstName, lastName, email);
        this.managerType = managerType;
    }

    public ManagerType getManagerType() {
        return managerType;
    }

    void setManagerTypeByAdmin(ManagerType managerType) {
        this.managerType = managerType;
    }

    public void setManagerType(ManagerType managerType) {
        setManagerTypeByAdmin(managerType);
    }

    public void addCourseForRegistration(Course course) {
        if (!coursesForRegistration.contains(course)) {
            coursesForRegistration.add(course);
        }
    }

    public List<Course> getCoursesForRegistration() {
        return coursesForRegistration;
    }
}
