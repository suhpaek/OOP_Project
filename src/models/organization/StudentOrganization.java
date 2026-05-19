package models.organization;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class StudentOrganization implements Serializable {
    private final String id;
    private String name;
    private String description;
    private String headStudentId;
    private final List<String> memberStudentIds = new ArrayList<>();

    public StudentOrganization(String name, String description) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.description = description;
    }

    public void addMember(String studentId) {
        if (studentId != null && !memberStudentIds.contains(studentId)) {
            memberStudentIds.add(studentId);
        }
    }

    public void removeMember(String studentId) {
        memberStudentIds.remove(studentId);
        if (Objects.equals(headStudentId, studentId)) {
            headStudentId = null;
        }
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getHeadStudentId() { return headStudentId; }
    public List<String> getMemberStudentIds() { return Collections.unmodifiableList(memberStudentIds); }

    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setHeadStudentId(String headStudentId) {
        this.headStudentId = headStudentId;
        addMember(headStudentId);
    }
}
