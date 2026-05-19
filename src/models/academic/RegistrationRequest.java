package models.academic;

import exceptions.CreditLimitExceededException;
import exceptions.TooManyFailedCoursesException;
import models.users.Manager;
import models.users.Student;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class RegistrationRequest implements Serializable {
    private Student student;
    private Course course;
    private boolean approved;
    private boolean processed;
    private Manager processedBy;
    private LocalDateTime createdAt;

    public RegistrationRequest(Student student, Course course) {
        this.student = student;
        this.course = course;
        this.approved = false;
        this.processed = false;
        this.processedBy = null;
        this.createdAt = LocalDateTime.now();
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

    public Manager getProcessedBy() {
        return processedBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void approve(Manager manager) throws CreditLimitExceededException, TooManyFailedCoursesException {
        student.registerForCourse(course);
        markApproved(manager);
    }

    public void markApproved(Manager manager) {
        this.approved = true;
        this.processed = true;
        this.processedBy = manager;
    }

    public void reject(Manager manager) {
        markRejected(manager);
    }

    public void markRejected(Manager manager) {
        this.approved = false;
        this.processed = true;
        this.processedBy = manager;
    }

    @Override
    public String toString() {
        return "RegistrationRequest{" +
                "student=" + student +
                ", course=" + course +
                ", approved=" + approved +
                ", processed=" + processed +
                ", processedBy=" + processedBy +
                ", createdAt=" + createdAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RegistrationRequest)) return false;
        RegistrationRequest that = (RegistrationRequest) o;
        return Objects.equals(student, that.student)
                && Objects.equals(course, that.course)
                && Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(student, course, createdAt);
    }
}
