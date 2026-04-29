package users;

import academic.Course;
import academic.RegistrationRequest;
import data.DataStore;
import enums.ManagerType;

import java.util.ArrayList;
import java.util.Comparator;
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

    public void setManagerType(ManagerType managerType) {
        this.managerType = managerType;
    }

    public void addCourseForRegistration(Course course) {
        if (!coursesForRegistration.contains(course)) {
            coursesForRegistration.add(course);
        }
    }

    public List<Course> getCoursesForRegistration() {
        return coursesForRegistration;
    }

    public void assignCourseToTeacher(Course course, Teacher teacher, boolean lecture) {
        teacher.assignCourse(course);
        if (lecture) {
            course.setLectureTeacher(teacher);
        } else {
            course.setPracticeTeacher(teacher);
        }
    }

    public void approveRegistration(RegistrationRequest request) throws Exception {
        request.approve();
    }

    public void rejectRegistration(RegistrationRequest request) {
        request.reject();
    }

    public List<Student> viewStudentsByName() {
        List<Student> students = DataStore.getInstance().getStudents();
        students.sort(Comparator.comparing(Student::getFullName));
        return students;
    }

    public List<Student> viewStudentsByGpa() {
        List<Student> students = DataStore.getInstance().getStudents();
        students.sort(Comparator.comparingDouble((Student student) -> student.getTranscript().calculateGpa()).reversed());
        return students;
    }

    public String createAcademicReport() {
        StringBuilder builder = new StringBuilder("Academic performance report:\n");
        for (Student student : viewStudentsByGpa()) {
            builder.append(student.getFullName())
                    .append(" - GPA: ")
                    .append(String.format("%.2f", student.getTranscript().calculateGpa()))
                    .append('\n');
        }
        return builder.toString();
    }
}
