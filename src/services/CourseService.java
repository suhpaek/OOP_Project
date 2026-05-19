package services;

import models.academic.Course;
import models.academic.Lesson;
import models.academic.RegistrationRequest;
import data.DataStore;
import enums.CourseType;
import exceptions.CreditLimitExceededException;
import exceptions.TooManyFailedCoursesException;
import models.users.Student;
import models.users.Teacher;
import models.users.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    public List<Course> getAvailableCourses(Student student) {
        List<Course> result = new ArrayList<>();
        for (Course course : dataStore.getCourses()) {
            if (student == null || !student.getRegisteredCourses().contains(course)) {
                result.add(course);
            }
        }
        return result;
    }

    public List<Course> getRegisteredCourses(Student student) {
        if (student == null) return new ArrayList<>();
        return new ArrayList<>(student.getRegisteredCourses());
    }

    public List<Course> getAssignedCourses(Teacher teacher) {
        if (teacher == null) return new ArrayList<>();
        return new ArrayList<>(teacher.getAssignedCourses());
    }

    public RegistrationRequest createRegistrationRequest(Student student, String courseCode) throws IOException {
        if (!dataStore.isCourseRegistrationOpen()) {
            throw new IllegalStateException("Course registration is closed.");
        }
        Course course = findCourseByCode(courseCode);
        if (course == null) {
            throw new IllegalArgumentException("Course not found.");
        }
        RegistrationRequest request = new RegistrationRequest(student, course);
        dataStore.addRegistrationRequest(request);
        dataStore.save();
        return request;
    }

    public Course findCourseByCode(String code) {
        if (code == null) return null;
        for (Course course : dataStore.getCourses()) {
            if (course.getCode().equalsIgnoreCase(code.trim())) return course;
        }
        return null;
    }

    public String getTeacherInfo(Student student, String courseCode) {
        Course course = findCourseByCode(courseCode);
        if (course == null) throw new IllegalArgumentException("Course not found.");
        if (student != null && !student.getRegisteredCourses().contains(course)) {
            throw new IllegalArgumentException("Student is not registered for this course.");
        }

        return "Course: " + course + "\n"
                + "Lecture teacher: " + formatTeacher(course.getLectureTeacher()) + "\n"
                + "Practice teacher: " + formatTeacher(course.getPracticeTeacher());
    }

    private String formatTeacher(Teacher teacher) {
        if (teacher == null) return "not assigned";
        return teacher.getFullName()
                + " | " + teacher.getEmail()
                + " | rating " + String.format("%.2f", teacher.getAverageRating());
    }

    public void dropStudent(Course course, Student student) { if (student != null) student.dropCourse(course); }
    public void setLectureTeacher(Course course, Teacher teacher) { if (course != null) course.setLectureTeacher(teacher); if (teacher != null) teacher.assignCourse(course); }
    public void setPracticeTeacher(Course course, Teacher teacher) { if (course != null) course.setPracticeTeacher(teacher); if (teacher != null) teacher.assignCourse(course); }

    public void assignTeacher(String courseCode, String teacherUsername, boolean lecture) throws Exception {
        Course course = findCourseByCode(courseCode);
        User user = dataStore.findUserByUsername(teacherUsername);
        if (course == null || !(user instanceof Teacher)) {
            throw new IllegalArgumentException("Course or teacher not found.");
        }
        if (lecture) setLectureTeacher(course, (Teacher) user);
        else setPracticeTeacher(course, (Teacher) user);
        dataStore.save();
    }

    public void addLesson(Course course, Lesson lesson) { if (course != null) course.addLesson(lesson); }
}
