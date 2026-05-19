package services;

import java.util.List;
import java.util.UUID;

import data.DataStore;
import enums.UrgencyLevel;
import models.communication.Complaint;
import models.users.Student;
import models.users.Teacher;

public class ComplaintService {

    private final DataStore dataStore;

    public ComplaintService() {
        this(DataStore.getInstance());
    }

    public ComplaintService(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    public Complaint createComplaint(Teacher teacher, Student student, String text, UrgencyLevel urgency) {
        if (teacher == null || student == null || text == null || text.isBlank() || urgency == null) {
            return null;
        }
        Complaint complaint = new Complaint(Math.abs(UUID.randomUUID().hashCode()), student.getFullName() + ": " + text, urgency, teacher.getId());
        dataStore.addComplaint(complaint);
        try {
            dataStore.save();
        } catch (Exception ignored) {
        }
        return complaint;
    }

    public Complaint createComplaintByUsername(Teacher teacher, String studentUsername, String text, UrgencyLevel urgency) throws Exception {
        if (teacher == null || studentUsername == null || studentUsername.isBlank()) {
            return null;
        }
        models.users.User user = dataStore.findUserByUsername(studentUsername);
        if (!(user instanceof Student)) {
            throw new IllegalArgumentException("User is not a student.");
        }
        Student student = (Student) user;
        Complaint complaint = teacher.sendComplaint(student, text, urgency);
        dataStore.addComplaint(complaint);
        dataStore.save();
        return complaint;
    }

    public List<Complaint> getAllComplaints() {
        return dataStore.getComplaints();
    }
}
