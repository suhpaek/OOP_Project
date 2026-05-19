package ui;

import models.users.Student;

import java.util.Scanner;

public class StudentConsole {
    private final Scanner scanner;
    private final Student student;

    public StudentConsole(Scanner scanner, Student student) {
        this.scanner = scanner;
        this.student = student;
    }

    public void start() {
        System.out.println();
        System.out.println("===== STUDENT MENU =====");
        System.out.println("Welcome, " + student.getFullName());
        System.out.println("Student console is coming next.");
        System.out.println("Press Enter to logout.");
        scanner.nextLine();
    }
}
