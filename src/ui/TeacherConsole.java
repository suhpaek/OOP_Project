package ui;

import models.users.Teacher;

import java.util.Scanner;

public class TeacherConsole {
    private final Scanner scanner;
    private final Teacher teacher;

    public TeacherConsole(Scanner scanner, Teacher teacher) {
        this.scanner = scanner;
        this.teacher = teacher;
    }

    public void start() {
        System.out.println();
        System.out.println("===== TEACHER MENU =====");
        System.out.println("Welcome, " + teacher.getFullName());
        System.out.println("Teacher console is coming next.");
        System.out.println("Press Enter to logout.");
        scanner.nextLine();
    }
}
