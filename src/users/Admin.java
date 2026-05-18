package users;

import data.DataStore;
import enums.Degree;
import enums.Gender;
import enums.ManagerType;
import enums.School;
import enums.TeacherType;
import exceptions.InvalidSupervisorException;
import exceptions.UserNotFoundException;
import research.Researcher;
import support.ActionLog;

import java.io.File;
import java.util.Date;
import java.util.List;

public class Admin extends User {
    public Admin() {
        super();
    }

    public Admin(String username, String password, String firstName, String lastName, String email) {
        super(username, password, firstName, lastName, email);
    }

    public void addUser(User user) {
        DataStore.getInstance().addUser(user);
    }

    public void updateUser(User user) {
        DataStore.getInstance().updateUser(user);
    }

    public void updateUserProfile(User user, String username, String firstName, String lastName,
                                  Gender gender, Date dateOfBirth) {
        user.updateProfileByAdmin(username, firstName, lastName, gender, dateOfBirth);
        DataStore.getInstance().updateUser(user);
        DataStore.getInstance().logAction(user.getId(), "Updated user profile");
    }

    public void updateStudentAcademicInfo(Student student, Degree degree, School school) {
        student.setDegreeByAdmin(degree);
        student.setSchoolByAdmin(school);
        DataStore.getInstance().updateUser(student);
        DataStore.getInstance().logAction(student.getId(), "Updated student academic info");
    }

    public void updateEmployeeSalary(Employee employee, double salary) {
        employee.setSalaryByAdmin(salary);
        DataStore.getInstance().updateUser(employee);
        DataStore.getInstance().logAction(employee.getId(), "Updated employee salary");
    }

    public void updateTeacherType(Teacher teacher, TeacherType teacherType) {
        teacher.setTeacherTypeByAdmin(teacherType);
        DataStore.getInstance().updateUser(teacher);
        DataStore.getInstance().logAction(teacher.getId(), "Updated teacher type");
    }

    public void updateManagerType(Manager manager, ManagerType managerType) {
        manager.setManagerTypeByAdmin(managerType);
        DataStore.getInstance().updateUser(manager);
        DataStore.getInstance().logAction(manager.getId(), "Updated manager type");
    }

    public void updateGraduateStudentInfo(GraduateStudent student, Degree degree, Researcher researchSupervisor)
            throws InvalidSupervisorException {
        student.setGraduateDegreeByAdmin(degree);
        student.setResearchSupervisorByAdmin(researchSupervisor);
        DataStore.getInstance().updateUser(student);
        DataStore.getInstance().logAction(student.getId(), "Updated graduate student info");
    }

    public void activateUser(User user) {
        user.setActiveByAdmin(true);
        DataStore.getInstance().updateUser(user);
        DataStore.getInstance().logAction(user.getId(), "Activated user");
    }

    public void deactivateUser(User user) {
        user.setActiveByAdmin(false);
        DataStore.getInstance().updateUser(user);
        DataStore.getInstance().logAction(user.getId(), "Deactivated user");
    }

    public void deleteUser(User user) {
        DataStore.getInstance().removeUser(user.getId());
    }

    public List<User> viewAllUsers() {
        return DataStore.getInstance().getAllUsers();
    }

    public User viewUserInfo(User user) throws UserNotFoundException {
        return DataStore.getInstance().findUserById(user.getId());
    }

    public void processLogFile(File logFile) {
        DataStore.getInstance().logAction(getId(), "Processed log file: " + logFile.getName());
    }

    public List<ActionLog> readLogs() {
        return DataStore.getInstance().getActionLogs();
    }
}
