package users;

import academic.Course;
import academic.Mark;
import communication.Complaint;
import enums.TeacherType;
import enums.UrgencyLevel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Teacher extends Employee {
    private List<Course> assignedCourses;
    private TeacherType teacherType;
    private List<Integer> ratingMarks;
    private transient final List<Complaint> complaints = new ArrayList<>();

    public Teacher() {
        super();
        this.assignedCourses = new ArrayList<>();
        this.ratingMarks = new ArrayList<>();
    }

    public Teacher(String username, String password, String firstName, String lastName, String email, TeacherType teacherType) {
        super(username, password, firstName, lastName, email);
        this.assignedCourses = new ArrayList<>();
        this.ratingMarks = new ArrayList<>();
        this.teacherType = teacherType;
    }

    public List<Course> getAssignedCourses() {
        return assignedCourses;
    }

    public TeacherType getTeacherType() {
        return teacherType;
    }

    void setTeacherTypeByAdmin(TeacherType teacherType) {
        this.teacherType = teacherType;
    }

    public void addRating(int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Teacher rating must be between 1 and 5");
        }
        ratingMarks.add(rating);
    }

    public double getAverageRating() {
        if (ratingMarks.isEmpty()) return 0;
        int sum = 0;
        for (int rating : ratingMarks) {
            sum += rating;
        }
        return (double) sum / ratingMarks.size();
    }

    public void assignCourse(Course course) {
        if (!assignedCourses.contains(course)) {
            assignedCourses.add(course);
        }
    }

    public void putMark(Student student, Course course, Mark mark) {
        if (!assignedCourses.contains(course)) {
            System.out.println("Teacher is not assigned to this course.");
            return;
        }

        if (!course.isStudentEnrolled(student)) {
            System.out.println("Student is not enrolled in this course.");
            return;
        }

        course.putMark(student, mark);
        student.addMarkToTranscript(course, mark);
    }

    public void viewCourses() {
        for (Course course : assignedCourses) {
            System.out.println(course);
        }
    }

    public void viewStudents(Course course) {
        if (!assignedCourses.contains(course)) {
            System.out.println("Teacher is not assigned to this course.");
            return;
        }

        for (Student student : course.getEnrolledStudents()) {
            System.out.println(student);
        }
    }

    public Complaint sendComplaint(Student student, String text, UrgencyLevel urgencyLevel) {
        Complaint complaint = new Complaint(
                Math.abs(UUID.randomUUID().hashCode()),
                student.getFullName() + ": " + text,
                urgencyLevel,
                getId()
        );
        complaints.add(complaint);
        return complaint;
    }

    public List<Complaint> getComplaints() {
        return complaints;
    }
}
