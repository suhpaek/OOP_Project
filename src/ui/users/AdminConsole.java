package ui.users;

import ui.app.ConsoleLanguage;
import ui.app.ConsoleScreen;
import ui.features.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

import enums.Degree;
import enums.School;
import i18n.I18n;
import models.support.ActionLog;
import models.users.Admin;
import models.users.User;
import services.users.AdminService;

public class AdminConsole implements ConsoleScreen {

    private final Scanner scanner;
    private final Admin admin;
    private final AdminService adminService = new AdminService();

    public AdminConsole(Scanner scanner, Admin admin) {
        this.scanner = scanner;
        this.admin = admin;
    }

    public void start() {
        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    viewAllUsers();
                    break;
                case "2":
                    createUser();
                    break;
                case "3":
                    viewLogs();
                    break;
                case "4":
                    changeUserStatus();
                    break;
                case "5":
                    deleteUser();
                    break;
                case "6":
                    exportLogs();
                    break;
                case "7":
                    updateUser();
                    break;
                case "8":
                    assignSupervisor();
                    break;
                case "9":
                    new JournalConsole(scanner, admin).start();
                    break;
                case "10":
                    new SearchConsole(scanner).start();
                    break;
                case "11":
                    ConsoleLanguage.changeLanguage(scanner, admin);
                    break;
                case "0":
                    running = false;
                    break;
                default:
                    System.out.println(I18n.t("app.invalid"));
            }
        }
    }

    private void printMenu() {
        System.out.println();
        System.out.println("===== " + I18n.t("admin.title") + " =====");
        System.out.println(I18n.t("admin.welcome", admin.getFullName()));
        System.out.println("1. " + I18n.t("admin.view_users"));
        System.out.println("2. " + I18n.t("admin.add_user"));
        System.out.println("3. " + I18n.t("admin.view_logs"));
        System.out.println("4. " + I18n.t("admin.change_user_status"));
        System.out.println("5. " + I18n.t("admin.remove_user"));
        System.out.println("6. " + I18n.t("admin.export_logs"));
        System.out.println("7. " + I18n.t("admin.update_user"));
        System.out.println("8. Assign graduate supervisor");
        System.out.println("9. " + I18n.t("journal.title"));
        System.out.println("10. Search");
        System.out.println("11. " + I18n.t("menu.change_language"));
        System.out.println("0. " + I18n.t("menu.logout"));
        System.out.print(I18n.t("menu.choice") + ": ");
    }

    private void viewAllUsers() {
        List<User> users = adminService.viewAllUsers();
        if (users.isEmpty()) {
            System.out.println("No users found.");
            return;
        }

        System.out.printf("%-18s %-16s %-24s %-28s %-8s%n", "Role", "Username", "Name", "Email", "Active");
        for (User user : users) {
            System.out.printf("%-18s %-16s %-24s %-28s %-8s%n",
                    user.getClass().getSimpleName(),
                    safe(user.getUsername()),
                    safe(user.getFullName()),
                    safe(user.getEmail()),
                    user.isActive());
        }
    }

    private void createUser() {
        try {
            System.out.println();
            System.out.println("Roles: Student, GraduateStudent, Teacher, Manager, Admin, TechSupportSpecialist");
            System.out.print("Role: ");
            String role = scanner.nextLine().trim();
            System.out.print("Username: ");
            String username = scanner.nextLine().trim();
            System.out.print("Password: ");
            String password = scanner.nextLine();
            System.out.print("First name: ");
            String firstName = scanner.nextLine().trim();
            System.out.print("Last name: ");
            String lastName = scanner.nextLine().trim();
            System.out.print("Email: ");
            String email = scanner.nextLine().trim();

            User user = adminService.createUser(admin, role, username, password, firstName, lastName, email);

            if (user instanceof models.users.Student) {
                System.out.print("School (SITE/SG/SEOGI/SMSGT/BS/ISE/KMA/SCE/SAM/SNSS): ");
                String schoolInput = scanner.nextLine().trim().toUpperCase();
                School school = null;
                if (!schoolInput.isBlank()) {
                    school = School.valueOf(schoolInput);
                }
                Degree degree = Degree.Bachelor;
                if (user instanceof models.users.GraduateStudent) {
                    System.out.print("Degree (Bachelor/Master/PhD): ");
                    String degInput = scanner.nextLine().trim();
                    if (!degInput.isBlank()) {
                        degree = Degree.valueOf(degInput);
                    }
                }
                adminService.updateStudentAcademicInfo(admin, (models.users.Student) user, degree, school);
            }
            System.out.println("Created user: " + user);
        } catch (Exception e) {
            System.out.println("Could not create user: " + e.getMessage());
        }
    }

    private void changeUserStatus() {
        try {
            System.out.print("Username: ");
            String username = scanner.nextLine().trim();
            System.out.print("Activate user? (yes/no): ");
            boolean active = scanner.nextLine().trim().equalsIgnoreCase("yes");
            adminService.changeUserStatus(admin, username, active);
            System.out.println("User status updated.");
        } catch (Exception e) {
            System.out.println("Could not update user status: " + e.getMessage());
        }
    }

    private void updateUser() {
        try {
            System.out.print("Current username: ");
            String currentUsername = scanner.nextLine().trim();
            System.out.print("New username: ");
            String newUsername = scanner.nextLine().trim();
            System.out.print("First name: ");
            String firstName = scanner.nextLine().trim();
            System.out.print("Last name: ");
            String lastName = scanner.nextLine().trim();
            System.out.print("Email: ");
            String email = scanner.nextLine().trim();
            User user = adminService.updateUserByUsername(admin, currentUsername, newUsername, firstName, lastName, email);
            System.out.println("Updated user: " + user);
        } catch (Exception e) {
            System.out.println("Could not update user: " + e.getMessage());
        }
    }

    private void assignSupervisor() {
        try {
            System.out.print("Graduate student username: ");
            String graduateUsername = scanner.nextLine().trim();
            System.out.print("Supervisor username: ");
            String supervisorUsername = scanner.nextLine().trim();
            adminService.assignGraduateSupervisor(admin, graduateUsername, supervisorUsername);
            System.out.println("Supervisor assigned.");
        } catch (Exception e) {
            System.out.println("Could not assign supervisor: " + e.getMessage());
        }
    }

    private void deleteUser() {
        try {
            System.out.print("Username: ");
            String username = scanner.nextLine().trim();
            adminService.deleteUserByUsername(admin, username);
            System.out.println("User deleted.");
        } catch (Exception e) {
            System.out.println("Could not delete user: " + e.getMessage());
        }
    }

    private void exportLogs() {
        try {
            System.out.print("File name (default logs.txt): ");
            String fileName = scanner.nextLine().trim();
            if (fileName.isEmpty()) {
                fileName = "logs.txt";
            }
            adminService.exportLogs(fileName);
            System.out.println("Logs exported to " + fileName);
        } catch (Exception e) {
            System.out.println("Could not export logs: " + e.getMessage());
        }
    }

    private void viewLogs() {
        List<ActionLog> logs = adminService.readLogs();
        if (logs.isEmpty()) {
            System.out.println("No logs found.");
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        System.out.printf("%-20s %-38s %-38s %s%n", "Time", "Target ID", "Actor ID", "Action");
        for (ActionLog log : logs) {
            System.out.printf("%-20s %-38s %-38s %s%n",
                    log.getCreatedAt().format(formatter),
                    safe(log.getId()),
                    safe(log.getActorId()),
                    safe(log.getAction()));
        }
    }

    private String safe(String value) {
        return value == null ? "-" : value;
    }
}
