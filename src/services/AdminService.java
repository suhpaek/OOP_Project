package services;

import data.DataStore;
import enums.*;
import exceptions.InvalidSupervisorException;
import exceptions.UserNotFoundException;
import models.research.Researcher;
import models.support.ActionLog;
import models.users.*;

import java.io.File;
import java.util.Date;
import java.util.List;

public class AdminService {
    private final DataStore dataStore;

    public AdminService() { this(DataStore.getInstance()); }
    public AdminService(DataStore dataStore) { this.dataStore = dataStore; }

    public void addUser(Admin admin, User user) { dataStore.addUser(user); addLog(user, admin, "Added user"); }
    public void updateUser(Admin admin, User user) { dataStore.updateUser(user); addLog(user, admin, "Updated user"); }

    public void updateUserProfile(Admin admin, User user, String username, String firstName, String lastName,
                                  Gender gender, Date dateOfBirth) {
        user.updateProfile(username, firstName, lastName, gender, dateOfBirth);
        dataStore.updateUser(user);
        addLog(user, admin, "Updated user profile");
    }

    public void updateStudentAcademicInfo(Admin admin, Student student, Degree degree, School school) {
        student.setDegree(degree); student.setSchool(school); dataStore.updateUser(student);
        addLog(student, admin, "Updated student academic info");
    }

    public void updateEmployeeSalary(Admin admin, Employee employee, double salary) {
        employee.setSalary(salary); dataStore.updateUser(employee); addLog(employee, admin, "Updated employee salary");
    }

    public void updateTeacherType(Admin admin, Teacher teacher, TeacherType teacherType) {
        teacher.setTeacherType(teacherType); dataStore.updateUser(teacher); addLog(teacher, admin, "Updated teacher type");
    }

    public void updateManagerType(Admin admin, Manager manager, ManagerType managerType) {
        manager.setManagerType(managerType); dataStore.updateUser(manager); addLog(manager, admin, "Updated manager type");
    }

    public void updateGraduateStudentInfo(Admin admin, GraduateStudent student, Degree degree,
                                          Researcher supervisor) throws InvalidSupervisorException {
        if (supervisor == null || supervisor.calculateHIndex() < 3) {
            throw new InvalidSupervisorException(supervisor == null ? "null" : supervisor.getName(),
                    supervisor == null ? 0 : supervisor.calculateHIndex());
        }
        student.setGraduateDegree(degree); student.setResearchSupervisor(supervisor); dataStore.updateUser(student);
        addLog(student, admin, "Updated graduate student info");
    }

    public void activateUser(Admin admin, User user) { user.setActive(true); dataStore.updateUser(user); addLog(user, admin, "Activated user"); }
    public void deactivateUser(Admin admin, User user) { user.setActive(false); dataStore.updateUser(user); addLog(user, admin, "Deactivated user"); }
    public void deleteUser(Admin admin, User user) { dataStore.removeUser(user.getId()); addLog(user, admin, "Deleted user"); }
    public List<User> viewAllUsers() { return dataStore.getAllUsers(); }
    public User viewUserInfo(User user) throws UserNotFoundException { return dataStore.findUserById(user.getId()); }
    public void processLogFile(Admin admin, File logFile) { dataStore.addLog(new ActionLog(logFile.getName(), admin.getId(), "Processed log file")); }
    public List<ActionLog> readLogs() { return dataStore.getLogs(); }

    private void addLog(User target, Admin admin, String action) {
        if (target != null && admin != null) dataStore.addLog(new ActionLog(target.getId(), admin.getId(), action));
    }
}
