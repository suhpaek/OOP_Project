package models.users;

import models.academic.Course;
import enums.TeacherType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Teacher extends Employee {
    private List<Course> assignedCourses;
    private TeacherType teacherType;
    private List<Integer> ratingMarks;

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
        return Collections.unmodifiableList(assignedCourses);
    }

    public TeacherType getTeacherType() {
        return teacherType;
    }

    void setTeacherTypeByAdmin(TeacherType teacherType) {
        this.teacherType = teacherType;
    }

    public void setTeacherType(TeacherType teacherType) {
        setTeacherTypeByAdmin(teacherType);
    }

    public void addRating(int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Teacher rating must be between 1 and 5");
        }
        ratingMarks.add(rating);
    }

    public void addRatingMark(int rating) {
        addRating(rating);
    }

    public List<Integer> getRatingMarks() {
        return Collections.unmodifiableList(ratingMarks);
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
}
