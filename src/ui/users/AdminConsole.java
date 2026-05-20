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
        System.out.println("8. " + I18n.t("admin.assign_supervisor"));
        System.out.println("9. " + I18n.t("journal.title"));
        System.out.println("10. " + I18n.t("search.title"));
        System.out.println("11. " + I18n.t("menu.change_language"));
        System.out.println("0. " + I18n.t("menu.logout"));
        System.out.print(I18n.t("menu.choice") + ": ");
    }

    private void viewAllUsers() {
        List<User> users = adminService.viewAllUsers();
        if (users.isEmpty()) {
            System.out.println(I18n.t("admin.no_users"));
            return;
        }

        System.out.printf("%-18s %-16s %-24s %-28s %-8s%n",
                I18n.t("admin.table.role"),
                I18n.t("admin.table.username"),
                I18n.t("admin.table.name"),
                I18n.t("admin.table.email"),
                I18n.t("admin.table.active"));
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
            System.out.println(I18n.t("admin.roles"));
            System.out.print(I18n.t("prompt.role") + ": ");
            String role = scanner.nextLine().trim();
            System.out.print(I18n.t("prompt.username") + ": ");
            String username = scanner.nextLine().trim();
            System.out.print(I18n.t("prompt.password") + ": ");
            String password = scanner.nextLine();
            System.out.print(I18n.t("prompt.first_name") + ": ");
            String firstName = scanner.nextLine().trim();
            System.out.print(I18n.t("prompt.last_name") + ": ");
            String lastName = scanner.nextLine().trim();
            System.out.print(I18n.t("prompt.email") + ": ");
            String email = scanner.nextLine().trim();

            User user = adminService.createUser(admin, role, username, password, firstName, lastName, email);

            if (user instanceof models.users.Student) {
                System.out.print(I18n.t("prompt.school") + ": ");
                String schoolInput = scanner.nextLine().trim().toUpperCase();
                School school = null;
                if (!schoolInput.isBlank()) {
                    school = School.valueOf(schoolInput);
                }
                Degree degree = Degree.Bachelor;
                if (user instanceof models.users.GraduateStudent) {
                    System.out.print(I18n.t("prompt.degree") + ": ");
                    String degInput = scanner.nextLine().trim();
                    if (!degInput.isBlank()) {
                        degree = Degree.valueOf(degInput);
                    }
                }
                adminService.updateStudentAcademicInfo(admin, (models.users.Student) user, degree, school);
            }
            System.out.println(I18n.t("admin.user_created", user));
        } catch (Exception e) {
            System.out.println(I18n.t("admin.create_failed") + ": " + e.getMessage());
        }
    }

    private void changeUserStatus() {
        try {
            System.out.print(I18n.t("prompt.username") + ": ");
            String username = scanner.nextLine().trim();
            System.out.print(I18n.t("admin.activate_prompt") + ": ");
            boolean active = scanner.nextLine().trim().equalsIgnoreCase("yes");
            adminService.changeUserStatus(admin, username, active);
            System.out.println(I18n.t("admin.status_updated"));
        } catch (Exception e) {
            System.out.println(I18n.t("admin.status_update_failed") + ": " + e.getMessage());
        }
    }

    private void updateUser() {
        try {
            System.out.print(I18n.t("admin.current_username") + ": ");
            String currentUsername = scanner.nextLine().trim();
            System.out.print(I18n.t("admin.new_username") + ": ");
            String newUsername = scanner.nextLine().trim();
            System.out.print(I18n.t("prompt.first_name") + ": ");
            String firstName = scanner.nextLine().trim();
            System.out.print(I18n.t("prompt.last_name") + ": ");
            String lastName = scanner.nextLine().trim();
            System.out.print(I18n.t("prompt.email") + ": ");
            String email = scanner.nextLine().trim();
            User user = adminService.updateUserByUsername(admin, currentUsername, newUsername, firstName, lastName, email);
            System.out.println(I18n.t("admin.user_updated", user));
        } catch (Exception e) {
            System.out.println(I18n.t("admin.user_update_failed") + ": " + e.getMessage());
        }
    }

    private void assignSupervisor() {
        try {
            System.out.print(I18n.t("admin.grad_username") + ": ");
            String graduateUsername = scanner.nextLine().trim();
            System.out.print(I18n.t("admin.supervisor_username") + ": ");
            String supervisorUsername = scanner.nextLine().trim();
            adminService.assignGraduateSupervisor(admin, graduateUsername, supervisorUsername);
            System.out.println(I18n.t("manager.supervisor_assigned"));
        } catch (Exception e) {
            System.out.println(I18n.t("admin.assign_supervisor_failed") + ": " + e.getMessage());
        }
    }

    private void deleteUser() {
        try {
            System.out.print(I18n.t("prompt.username") + ": ");
            String username = scanner.nextLine().trim();
            adminService.deleteUserByUsername(admin, username);
            System.out.println(I18n.t("admin.user_deleted"));
        } catch (Exception e) {
            System.out.println(I18n.t("admin.user_delete_failed") + ": " + e.getMessage());
        }
    }

    private void exportLogs() {
        try {
            System.out.print(I18n.t("admin.file_name_prompt") + ": ");
            String fileName = scanner.nextLine().trim();
            if (fileName.isEmpty()) {
                fileName = "logs.txt";
            }
            adminService.exportLogs(fileName);
            System.out.println(I18n.t("admin.logs_exported", fileName));
        } catch (Exception e) {
            System.out.println(I18n.t("admin.logs_export_failed") + ": " + e.getMessage());
        }
    }

    private void viewLogs() {
        List<ActionLog> logs = adminService.readLogs();
        if (logs.isEmpty()) {
            System.out.println(I18n.t("admin.no_logs"));
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        System.out.printf("%-20s %-38s %-38s %s%n",
                I18n.t("admin.table.time"),
                I18n.t("admin.table.target_id"),
                I18n.t("admin.table.actor_id"),
                I18n.t("admin.table.action"));
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
