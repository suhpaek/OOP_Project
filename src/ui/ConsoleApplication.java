package ui;

import data.DataStore;
import models.users.Admin;
import models.users.User;
import services.AuthenticationService;

import java.util.Scanner;

public class ConsoleApplication {
    private final DataStore store = DataStore.getInstance();
    private final Scanner scanner = new Scanner(System.in);
    private final AuthenticationService authenticationService = new AuthenticationService(store);

    public void run() {
        initialize();

        boolean running = true;
        while (running) {
            printMainMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    login();
                    break;
                case "0":
                    running = false;
                    saveData();
                    System.out.println("Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private void initialize() {
        try {
            store.load();
            store.ensureDefaultAdmin();
            store.save();
        } catch (Exception e) {
            System.out.println("Could not initialize storage: " + e.getMessage());
        }
    }

    private void printMainMenu() {
        System.out.println();
        System.out.println("===== UNIVERSITY SYSTEM =====");
        System.out.println("1. Login");
        System.out.println("0. Exit");
        System.out.print("Choose: ");
    }

    private void login() {
        AuthConsole authConsole = new AuthConsole(scanner, authenticationService);
        User user = authConsole.login();
        if (user == null) return;

        if (user instanceof Admin) {
            new AdminConsole(scanner, (Admin) user).start();
            logout(user);
        } else {
            System.out.println("Console menu for " + user.getClass().getSimpleName() + " is not implemented yet.");
            logout(user);
        }
    }

    private void logout(User user) {
        try {
            authenticationService.logout(user);
        } catch (Exception e) {
            System.out.println("Logout failed: " + e.getMessage());
        }
    }

    private void saveData() {
        try {
            store.save();
        } catch (Exception e) {
            System.out.println("Could not save data: " + e.getMessage());
        }
    }
}
