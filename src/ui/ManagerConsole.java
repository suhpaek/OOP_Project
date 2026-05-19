package ui;

import models.users.Manager;

import java.util.Scanner;

public class ManagerConsole {
    private final Scanner scanner;
    private final Manager manager;

    public ManagerConsole(Scanner scanner, Manager manager) {
        this.scanner = scanner;
        this.manager = manager;
    }

    public void start() {
        System.out.println();
        System.out.println("===== MANAGER MENU =====");
        System.out.println("Welcome, " + manager.getFullName());
        System.out.println("Manager console is coming next.");
        System.out.println("Press Enter to logout.");
        scanner.nextLine();
    }
}
