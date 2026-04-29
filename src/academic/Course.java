package academic;

import enums.CourseType;
import users.Student;
import users.Teacher;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Course implements Serializable {
    private String code;
    private String name;
    private int credits;
    private CourseType courseType;
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
        return lessons;
    }

    public List<Student> getEnrolledStudents() {
        return enrolledStudents;
    }

    public Map<Student, Mark> getMarks() {
        return marks;
    }

    public void addLesson(Lesson lesson) {
        lessons.add(lesson);
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
        if (enrolledStudents.contains(student)) {
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
}
