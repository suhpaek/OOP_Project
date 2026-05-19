package services;

import models.academic.Course;
import models.academic.Lesson;
import data.DataStore;
import enums.CourseType;
import exceptions.CreditLimitExceededException;
import exceptions.TooManyFailedCoursesException;
import models.users.Student;
import models.users.Teacher;

import java.io.IOException;

public class CourseService {
    private final DataStore dataStore;
    public CourseService() { this(DataStore.getInstance()); }
    public CourseService(DataStore dataStore) { this.dataStore = dataStore; }

    public void createCourse(Course course) { dataStore.addCourse(course); }

    public Course createCourse(String code, String name, int credits, CourseType type, int yearOfStudy)
            throws IOException {
        if (code == null || code.isBlank() || name == null || name.isBlank()) {
            throw new IllegalArgumentException("Course code and name are required.");
        }
        Course course = new Course(code.trim(), name.trim(), credits, type);
        course.setIntendedYearOfStudy(yearOfStudy);
        dataStore.addCourse(course);
        dataStore.save();
        return course;
    }
    public void enrollStudent(Course course, Student student) throws CreditLimitExceededException, TooManyFailedCoursesException {
        if (course != null && student != null) student.registerForCourse(course);
    }
    public void dropStudent(Course course, Student student) { if (student != null) student.dropCourse(course); }
    public void setLectureTeacher(Course course, Teacher teacher) { if (course != null) course.setLectureTeacher(teacher); if (teacher != null) teacher.assignCourse(course); }
    public void setPracticeTeacher(Course course, Teacher teacher) { if (course != null) course.setPracticeTeacher(teacher); if (teacher != null) teacher.assignCourse(course); }
    public void addLesson(Course course, Lesson lesson) { if (course != null) course.addLesson(lesson); }
}
