package services;

import data.DataStore;
import models.academic.Course;
import models.users.Student;
import models.users.Teacher;
import models.users.User;

import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserService {
    private final DataStore dataStore;
    private final CourseService courseService;
    private final Map<String, List<String>> notifications = new HashMap<>();

    public UserService() {
        this(DataStore.getInstance(), new CourseService());
    }

    public UserService(DataStore dataStore, CourseService courseService) {
        this.dataStore = dataStore;
        this.courseService = courseService;
    }

    public void addNotification(User user, String notification) {
        if (user == null || notification == null || notification.isBlank()) return;
        notifications.computeIfAbsent(user.getId(), ignored -> new ArrayList<>()).add(notification);
    }

    public List<String> getNotifications(User user) {
        if (user == null) return new ArrayList<>();
        return new ArrayList<>(notifications.getOrDefault(user.getId(), new ArrayList<>()));
    }

    public void rateTeacher(Teacher teacher, int rating) { if (teacher != null) teacher.addRatingMark(rating); }

    public void rateCourseTeacher(Student student, String courseCode, int rating, boolean lecture)
            throws IOException {
        Course course = courseService.findCourseByCode(courseCode);
        if (course == null || !student.getRegisteredCourses().contains(course)) {
            throw new IllegalArgumentException("Student is not registered for this course.");
        }
        Teacher teacher = lecture ? course.getLectureTeacher() : course.getPracticeTeacher();
        if (teacher == null) throw new IllegalArgumentException("Teacher is not assigned.");
        rateTeacher(teacher, rating);
        dataStore.save();
    }

    public double getAverageTeacherRating(Teacher teacher) { return teacher == null ? 0 : teacher.getAverageRating(); }
}
