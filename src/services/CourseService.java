package services;

import models.academic.Course;
import models.academic.Lesson;
import data.DataStore;
import exceptions.CreditLimitExceededException;
import exceptions.TooManyFailedCoursesException;
import models.users.Student;
import models.users.Teacher;

public class CourseService {
    private final DataStore dataStore;
    public CourseService() { this(DataStore.getInstance()); }
    public CourseService(DataStore dataStore) { this.dataStore = dataStore; }

    public void createCourse(Course course) { dataStore.addCourse(course); }
    public void enrollStudent(Course course, Student student) throws CreditLimitExceededException, TooManyFailedCoursesException {
        if (course != null && student != null) student.registerForCourse(course);
    }
    public void dropStudent(Course course, Student student) { if (student != null) student.dropCourse(course); }
    public void setLectureTeacher(Course course, Teacher teacher) { if (course != null) course.setLectureTeacher(teacher); if (teacher != null) teacher.assignCourse(course); }
    public void setPracticeTeacher(Course course, Teacher teacher) { if (course != null) course.setPracticeTeacher(teacher); if (teacher != null) teacher.assignCourse(course); }
    public void addLesson(Course course, Lesson lesson) { if (course != null) course.addLesson(lesson); }
}
