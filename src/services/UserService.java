package services;

import users.Teacher;
import users.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserService {
    private final Map<String, List<String>> notifications = new HashMap<>();

    public void addNotification(User user, String notification) {
        if (user == null || notification == null || notification.isBlank()) return;
        notifications.computeIfAbsent(user.getId(), ignored -> new ArrayList<>()).add(notification);
    }

    public List<String> getNotifications(User user) {
        if (user == null) return new ArrayList<>();
        return new ArrayList<>(notifications.getOrDefault(user.getId(), new ArrayList<>()));
    }

    public void rateTeacher(Teacher teacher, int rating) { if (teacher != null) teacher.addRatingMark(rating); }
    public double getAverageTeacherRating(Teacher teacher) { return teacher == null ? 0 : teacher.getAverageRating(); }
}
