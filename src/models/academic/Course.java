package models.academic;

import enums.CourseType;
import enums.School;
import models.users.Student;
import models.users.Teacher;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Course implements Serializable, Comparable<Course> {
    private String code;
    private String name;
    private int credits;
    private CourseType courseType;
    private School intendedSchool;
    private int intendedYearOfStudy;
    private Teacher lectureTeacher;
    private Teacher practiceTeacher;
    private List<Lesson> lessons;
    private List<Student> enrolledStudents;
    private Map<Student, Mark> marks;

    public Course() {
        this.lessons = new ArrayList<>();
        this.enrolledStudents = new ArrayList<>();
        this.marks = new HashMap<>();
    }

    public Course(String code, String name, int credits, CourseType courseType) {
        this();
        this.code = code;
        this.name = name;
        this.credits = credits;
        this.courseType = courseType;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public int getCredits() {
        return credits;
    }

    public CourseType getCourseType() {
        return courseType;
    }

    public School getIntendedSchool() {
        return intendedSchool;
    }

    public void setIntendedSchool(School intendedSchool) {
        this.intendedSchool = intendedSchool;
    }

    public int getIntendedYearOfStudy() {
        return intendedYearOfStudy;
    }

    public void setIntendedYearOfStudy(int intendedYearOfStudy) {
        if (intendedYearOfStudy < 1) {
            throw new IllegalArgumentException("Intended year of study must be positive");
        }
        this.intendedYearOfStudy = intendedYearOfStudy;
    }

    public Teacher getLectureTeacher() {
        return lectureTeacher;
    }

    public void setLectureTeacher(Teacher lectureTeacher) {
        this.lectureTeacher = lectureTeacher;
    }

    public Teacher getPracticeTeacher() {
        return practiceTeacher;
    }

    public void setPracticeTeacher(Teacher practiceTeacher) {
        this.practiceTeacher = practiceTeacher;
    }

    public List<Lesson> getLessons() {
        return Collections.unmodifiableList(lessons);
    }

    public List<Student> getEnrolledStudents() {
        return Collections.unmodifiableList(enrolledStudents);
    }

    public Map<Student, Mark> getMarks() {
        return Collections.unmodifiableMap(marks);
    }

    public void addLesson(Lesson lesson) {
        if (lesson != null) {
            lessons.add(lesson);
        }
    }

    public boolean isStudentEnrolled(Student student) {
        return enrolledStudents.contains(student);
    }

    public void enrollStudent(Student student) {
        if (!enrolledStudents.contains(student)) {
            enrolledStudents.add(student);
        }
    }

    public void dropStudent(Student student) {
        enrolledStudents.remove(student);
        marks.remove(student);
    }

    public void putMark(Student student, Mark mark) {
        if (student != null && mark != null && enrolledStudents.contains(student)) {
            marks.put(student, mark);
        }
    }

    public Mark getMark(Student student) {
        return marks.get(student);
    }

    @Override
    public String toString() {
        return code + " - " + name + " (" + credits + " credits, " + courseType + ")";
    }

    @Override
    public int compareTo(Course other) {
        return code.compareToIgnoreCase(other.code);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Course)) return false;
        Course course = (Course) o;
        return Objects.equals(code, course.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }
}
