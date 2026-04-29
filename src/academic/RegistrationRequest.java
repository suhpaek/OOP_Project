package academic;

import exceptions.CreditLimitExceededException;
import users.Student;

public class RegistrationRequest {
    private Student student;
    private Course course;
    private boolean approved;
    private boolean processed;

    public RegistrationRequest(Student student, Course course) {
        this.student = student;
        this.course = course;
    }

    public Student getStudent() {
        return student;
    }

    public Course getCourse() {
        return course;
    }

    public boolean isApproved() {
        return approved;
    }

    public boolean isProcessed() {
        return processed;
    }

    public void approve() throws CreditLimitExceededException {
        student.registerForCourse(course);
        this.approved = true;
        this.processed = true;
    }

    public void reject() {
        this.approved = false;
        this.processed = true;
    }
}
