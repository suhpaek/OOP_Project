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
        DataStore.getInstance().addLog(new ActionLog(user.getId(), getId(), "Updated user profile"));
    }

    public void updateStudentAcademicInfo(Student student, Degree degree, School school) {
        student.setDegreeByAdmin(degree);
        student.setSchoolByAdmin(school);
        DataStore.getInstance().updateUser(student);
        DataStore.getInstance().addLog(new ActionLog(student.getId(), getId(), "Updated student academic info"));
    }

    public void updateEmployeeSalary(Employee employee, double salary) {
        employee.setSalaryByAdmin(salary);
        DataStore.getInstance().updateUser(employee);
        DataStore.getInstance().addLog(new ActionLog(employee.getId(), getId(), "Updated employee salary"));
    }

    public void updateTeacherType(Teacher teacher, TeacherType teacherType) {
        teacher.setTeacherTypeByAdmin(teacherType);
        DataStore.getInstance().updateUser(teacher);
        DataStore.getInstance().addLog(new ActionLog(teacher.getId(), getId(), "Updated teacher type"));
    }

    public void updateManagerType(Manager manager, ManagerType managerType) {
        manager.setManagerTypeByAdmin(managerType);
        DataStore.getInstance().updateUser(manager);
        DataStore.getInstance().addLog(new ActionLog(manager.getId(), getId(), "Updated manager type"));
    }

    public void updateGraduateStudentInfo(GraduateStudent student, Degree degree, Researcher researchSupervisor)
            throws InvalidSupervisorException {
        student.setGraduateDegreeByAdmin(degree);
        student.setResearchSupervisorByAdmin(researchSupervisor);
        DataStore.getInstance().updateUser(student);
        DataStore.getInstance().addLog(new ActionLog(student.getId(), getId(), "Updated graduate student info"));
    }

    public void activateUser(User user) {
        user.setActiveByAdmin(true);
        DataStore.getInstance().updateUser(user);
        DataStore.getInstance().addLog(new ActionLog(user.getId(), getId(), "Activated user"));
    }

    public void deactivateUser(User user) {
        user.setActiveByAdmin(false);
        DataStore.getInstance().updateUser(user);
        DataStore.getInstance().addLog(new ActionLog(user.getId(), getId(), "Deactivated user"));
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
        DataStore.getInstance().addLog(new ActionLog(logFile.getName(), getId(), "Processed log file"));
    }

    public List<ActionLog> readLogs() {
        return DataStore.getInstance().getLogs();
    }
}
