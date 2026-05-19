package ui;

import data.DataStore;
import models.support.ActionLog;
import models.users.Admin;
import models.users.User;
import services.AdminService;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class AdminConsole {
    private final Scanner scanner;
    private final Admin admin;
    private final DataStore store = DataStore.getInstance();
    private final AdminService adminService = new AdminService(store);

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
                case "0":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private void printMenu() {
        System.out.println();
        System.out.println("===== ADMIN MENU =====");
        System.out.println("1. View all users");
        System.out.println("2. Create user");
        System.out.println("3. View logs");
        System.out.println("4. Activate/deactivate user");
        System.out.println("5. Delete user");
        System.out.println("6. Export logs");
        System.out.println("0. Logout");
        System.out.print("Choose: ");
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
            System.out.println("Roles: Student, Teacher, Manager, Admin");
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

            validateRequired(role, username, password, firstName, lastName, email);
            ensureUsernameIsFree(username);

            User user = adminService.createUser(admin, role, username, password, firstName, lastName, email);
            store.save();
            System.out.println("Created user: " + user);
        } catch (Exception e) {
            System.out.println("Could not create user: " + e.getMessage());
        }
    }

    private void changeUserStatus() {
        try {
            System.out.print("Username: ");
            String username = scanner.nextLine().trim();
            User user = store.findUserByUsername(username);
            System.out.print("Activate user? (yes/no): ");
            boolean active = scanner.nextLine().trim().equalsIgnoreCase("yes");
            if (active) {
                adminService.activateUser(admin, user);
            } else {
                adminService.deactivateUser(admin, user);
            }
            store.save();
            System.out.println("User status updated.");
        } catch (Exception e) {
            System.out.println("Could not update user status: " + e.getMessage());
        }
    }

    private void deleteUser() {
        try {
            System.out.print("Username: ");
            String username = scanner.nextLine().trim();
            User user = store.findUserByUsername(username);
            adminService.deleteUser(admin, user);
            store.save();
            System.out.println("User deleted.");
        } catch (Exception e) {
            System.out.println("Could not delete user: " + e.getMessage());
        }
    }

    private void exportLogs() {
        try {
            System.out.print("File name (default logs.txt): ");
            String fileName = scanner.nextLine().trim();
            if (fileName.isEmpty()) fileName = "logs.txt";
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

    private void ensureUsernameIsFree(String username) throws Exception {
        try {
            store.findUserByUsername(username);
            throw new IllegalArgumentException("Username already exists.");
        } catch (exceptions.UserNotFoundException ignored) {
        }
    }

    private void validateRequired(String... values) {
        for (String value : values) {
            if (value == null || value.isBlank()) {
                throw new IllegalArgumentException("All fields are required.");
            }
        }
    }

    private String safe(String value) {
        return value == null ? "-" : value;
    }
}
