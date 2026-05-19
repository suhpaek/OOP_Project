package services;

import models.communication.Complaint;
import data.DataStore;
import enums.UrgencyLevel;
import models.users.Student;
import models.users.Teacher;

import java.util.List;
import java.util.UUID;

public class ComplaintService {
    private final DataStore dataStore;
    public ComplaintService() { this(DataStore.getInstance()); }
    public ComplaintService(DataStore dataStore) { this.dataStore = dataStore; }

    public Complaint createComplaint(Teacher teacher, Student student, String text, UrgencyLevel urgency) {
        if (teacher == null || student == null || text == null || text.isBlank() || urgency == null) return null;
        Complaint complaint = new Complaint(Math.abs(UUID.randomUUID().hashCode()), student.getFullName() + ": " + text, urgency, teacher.getId());
        dataStore.addComplaint(complaint);
        return complaint;
    }

    public List<Complaint> getAllComplaints() { return dataStore.getComplaints(); }
}
