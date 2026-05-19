package services;

import data.DataStore;
import models.organization.StudentOrganization;
import models.users.Student;
import models.users.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OrganizationService {
    private final DataStore dataStore;

    public OrganizationService() {
        this(DataStore.getInstance());
    }

    public OrganizationService(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    public StudentOrganization createOrganization(String name, String description, String headUsername) throws Exception {
        validateName(name);
        StudentOrganization organization = new StudentOrganization(name, description);
        if (headUsername != null && !headUsername.isBlank()) {
            Student head = findStudent(headUsername);
            organization.setHeadStudentId(head.getId());
            head.leadOrganization(name);
            dataStore.updateUser(head);
        }
        dataStore.addStudentOrganization(organization);
        saveData();
        return organization;
    }

    public List<StudentOrganization> getAllOrganizations() {
        return dataStore.getStudentOrganizations();
    }

    public List<StudentOrganization> getOrganizationsForStudent(Student student) {
        List<StudentOrganization> result = new ArrayList<>();
        if (student == null) return result;
        for (StudentOrganization organization : dataStore.getStudentOrganizations()) {
            if (organization.getMemberStudentIds().contains(student.getId())) {
                result.add(organization);
            }
        }
        return result;
    }

    public void joinOrganization(Student student, String organizationName) {
        StudentOrganization organization = findOrganization(organizationName);
        organization.addMember(student.getId());
        student.joinOrganization(organization.getName());
        dataStore.updateUser(student);
        saveData();
    }

    public void leaveOrganization(Student student, String organizationName) {
        StudentOrganization organization = findOrganization(organizationName);
        organization.removeMember(student.getId());
        student.leaveOrganization(organization.getName());
        dataStore.updateUser(student);
        saveData();
    }

    private StudentOrganization findOrganization(String name) {
        for (StudentOrganization organization : dataStore.getStudentOrganizations()) {
            if (organization.getName().equalsIgnoreCase(name)) {
                return organization;
            }
        }
        throw new IllegalArgumentException("Organization not found.");
    }

    private Student findStudent(String username) throws Exception {
        User user = dataStore.findUserByUsername(username);
        if (!(user instanceof Student)) {
            throw new IllegalArgumentException("Head must be a student.");
        }
        return (Student) user;
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Organization name is required.");
        }
        for (StudentOrganization organization : dataStore.getStudentOrganizations()) {
            if (organization.getName().equalsIgnoreCase(name)) {
                throw new IllegalArgumentException("Organization already exists.");
            }
        }
    }

    private void saveData() {
        try {
            dataStore.save();
        } catch (IOException e) {
            throw new IllegalStateException("Could not save organization changes.", e);
        }
    }
}
