package services.users;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import data.UniversityDataStore;
import enums.Degree;
import enums.Gender;
import enums.ManagerType;
import enums.School;
import enums.TeacherType;
import exceptions.InvalidSupervisorException;
import exceptions.UserNotFoundException;
import models.research.Researcher;
import models.support.ActionLog;
import models.users.Admin;
import models.users.Employee;
import models.users.GraduateStudent;
import models.users.Manager;
import models.users.Student;
import models.users.Teacher;
import models.users.User;
import services.research.ResearchService;
import pattern.factory.ConcreteUserFactory;

public class AdminService {

    private final UniversityDataStore dataStore;

    public AdminService() {
        this(UniversityDataStore.getInstance());
    }

    public AdminService(UniversityDataStore dataStore) {
        this.dataStore = dataStore;
    }

    public void addUser(Admin admin, User user) {
        dataStore.addUser(user);
        addLog(user, admin, "Added user");
    }

    public void updateUser(Admin admin, User user) {
        dataStore.updateUser(user);
        addLog(user, admin, "Updated user");
    }

    public User createUser(Admin admin, String role, String username, String password,
            String firstName, String lastName, String email) throws IOException {
        validateRequired(role, username, password, firstName, lastName, email);
        ensureUsernameIsFree(username);
        User user = new ConcreteUserFactory().createUser(role);
        user.updateProfile(username, firstName, lastName, null, null);
        user.changePassword(password);
        user.updateEmail(email);
        if (admin != null) {
            user.selectLanguage(admin.getLanguage());
        }
        addUser(admin, user);
        dataStore.save();
        return user;
    }

    public void updateUserProfile(Admin admin, User user, String username, String firstName, String lastName,
            Gender gender, Date dateOfBirth) {
        user.updateProfile(username, firstName, lastName, gender, dateOfBirth);
        dataStore.updateUser(user);
        addLog(user, admin, "Updated user profile");
    }

    public void updateStudentAcademicInfo(Admin admin, Student student, Degree degree, School school) {
        student.setDegree(degree);
        student.setSchool(school);
        dataStore.updateUser(student);
        // Students with Master or PhD degree are researchers by requirement
        if (degree == Degree.Master || degree == Degree.PhD) {
            new ResearchService(dataStore).getOrCreateResearcher(student);
        }
        addLog(student, admin, "Updated student academic info");
    }

    public void updateEmployeeSalary(Admin admin, Employee employee, double salary) {
        employee.setSalary(salary);
        dataStore.updateUser(employee);
        addLog(employee, admin, "Updated employee salary");
    }

    public void updateTeacherType(Admin admin, Teacher teacher, TeacherType teacherType) {
        teacher.setTeacherType(teacherType);
        dataStore.updateUser(teacher);
        // Professors are always researchers
        if (teacherType == TeacherType.PROFESSOR) {
            new ResearchService(dataStore).getOrCreateResearcher(teacher);
        }
        addLog(teacher, admin, "Updated teacher type");
    }

    public void updateManagerType(Admin admin, Manager manager, ManagerType managerType) {
        manager.setManagerType(managerType);
        dataStore.updateUser(manager);
        addLog(manager, admin, "Updated manager type");
    }

    public void updateTeacherDegree(Admin admin, Teacher teacher, Degree degree) {
        teacher.setDegree(degree);
        dataStore.updateUser(teacher);
        // If teacher has Master or PhD, ensure researcher record (Professors also handled in updateTeacherType)
        if (degree == Degree.Master || degree == Degree.PhD) {
            new ResearchService(dataStore).getOrCreateResearcher(teacher);
        }
        addLog(teacher, admin, "Updated teacher degree");
    }

    public void updateGraduateStudentInfo(Admin admin, GraduateStudent student, Degree degree,
            Researcher supervisor) throws InvalidSupervisorException {
        if (supervisor == null || supervisor.calculateHIndex() < 3) {
            throw new InvalidSupervisorException(supervisor == null ? "null" : supervisor.getName(),
                    supervisor == null ? 0 : supervisor.calculateHIndex());
        }
        student.setGraduateDegree(degree);
        student.setResearchSupervisor(supervisor);
        dataStore.updateUser(student);
        // Graduate students (Master/PhD) are researchers by requirement
        if (degree == Degree.Master || degree == Degree.PhD) {
            new ResearchService(dataStore).getOrCreateResearcher(student);
        }
        addLog(student, admin, "Updated graduate student info");
    }

    public void assignGraduateSupervisor(Admin admin, String graduateUsername, String supervisorUsername)
            throws Exception {
        User graduate = dataStore.findUserByUsername(graduateUsername);
        User supervisorUser = dataStore.findUserByUsername(supervisorUsername);
        if (!(graduate instanceof GraduateStudent)) {
            throw new IllegalArgumentException("User is not a graduate student.");
        }
        Researcher supervisor = new ResearchService(dataStore).getOrCreateResearcher(supervisorUser);
        updateGraduateStudentInfo(admin, (GraduateStudent) graduate, ((GraduateStudent) graduate).getDegree(), supervisor);
        dataStore.save();
    }

    public void activateUser(Admin admin, User user) {
        user.setActive(true);
        dataStore.updateUser(user);
        addLog(user, admin, "Activated user");
    }

    public void deactivateUser(Admin admin, User user) {
        user.setActive(false);
        dataStore.updateUser(user);
        addLog(user, admin, "Deactivated user");
    }

    public void deleteUser(Admin admin, User user) {
        dataStore.removeUser(user.getId());
        addLog(user, admin, "Deleted user");
    }

    public void changeUserStatus(Admin admin, String username, boolean active) throws IOException, UserNotFoundException {
        User user = dataStore.findUserByUsername(username);
        if (active) {
            activateUser(admin, user); 
        }else {
            deactivateUser(admin, user);
        }
        dataStore.save();
    }

    public void deleteUserByUsername(Admin admin, String username) throws IOException, UserNotFoundException {
        User user = dataStore.findUserByUsername(username);
        deleteUser(admin, user);
        dataStore.save();
    }

    public User updateUserByUsername(Admin admin, String currentUsername, String newUsername,
            String firstName, String lastName, String email) throws IOException, UserNotFoundException {
        validateRequired(currentUsername, newUsername, firstName, lastName, email);
        User user = dataStore.findUserByUsername(currentUsername);
        if (!currentUsername.equalsIgnoreCase(newUsername)) {
            ensureUsernameIsFree(newUsername);
        }
        user.updateProfile(newUsername, firstName, lastName, user.getGender(), user.getDateOfBirth());
        user.updateEmail(email);
        updateUser(admin, user);
        dataStore.save();
        return user;
    }

    public List<User> viewAllUsers() {
        return dataStore.getAllUsers();
    }

    public User viewUserInfo(User user) throws UserNotFoundException {
        return dataStore.findUserById(user.getId());
    }

    public void processLogFile(Admin admin, File logFile) {
        dataStore.addLog(new ActionLog(logFile.getName(), admin.getId(), "Processed log file"));
    }

    public List<ActionLog> readLogs() {
        return dataStore.getLogs();
    }

    public String exportLogs(String fileName) throws IOException {
        try (FileWriter writer = new FileWriter(fileName)) {
            for (ActionLog log : dataStore.getLogs()) {
                writer.write(log.getCreatedAt() + " | " + log.getActorId() + " | "
                        + log.getId() + " | " + log.getAction() + System.lineSeparator());
            }
        }
        return fileName;
    }

    private void addLog(User target, Admin admin, String action) {
        if (target != null && admin != null) {
            dataStore.addLog(new ActionLog(target.getId(), admin.getId(), action));
        }
    }

    private void ensureUsernameIsFree(String username) {
        try {
            dataStore.findUserByUsername(username);
            throw new IllegalArgumentException("Username already exists.");
        } catch (UserNotFoundException ignored) {
        }
    }

    private void validateRequired(String... values) {
        for (String value : values) {
            if (value == null || value.isBlank()) {
                throw new IllegalArgumentException("All fields are required.");
            }
        }
    }
}
