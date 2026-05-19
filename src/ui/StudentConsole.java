package ui;

import data.DataStore;
import models.academic.Course;
import models.users.Student;
import services.CourseService;

import java.util.List;
import java.util.Scanner;

public class StudentConsole {
    private final Scanner scanner;
    private final Student student;
    private final CourseService courseService = new CourseService(DataStore.getInstance());

    public StudentConsole(Scanner scanner, Student student) {
        this.scanner = scanner;
        this.student = student;
    }

    public void start() {
        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    viewAvailableCourses();
                    break;
                case "2":
                    createRegistrationRequest();
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
        System.out.println("===== STUDENT MENU =====");
        System.out.println("Welcome, " + student.getFullName());
        System.out.println("1. View available courses");
        System.out.println("2. Create course registration request");
        System.out.println("0. Logout");
        System.out.print("Choose: ");
    }

    private void viewAvailableCourses() {
        List<Course> courses = courseService.getAvailableCourses(student);
        if (courses.isEmpty()) {
            System.out.println("No available courses.");
            return;
        }

        System.out.printf("%-12s %-35s %-8s %-16s %-6s%n", "Code", "Name", "Credits", "Type", "Year");
        for (Course course : courses) {
            System.out.printf("%-12s %-35s %-8d %-16s %-6d%n",
                    course.getCode(),
                    course.getName(),
                    course.getCredits(),
                    course.getCourseType(),
                    course.getIntendedYearOfStudy());
        }
    }

    private void createRegistrationRequest() {
        try {
            System.out.print("Course code: ");
            String code = scanner.nextLine();
            courseService.createRegistrationRequest(student, code);
            System.out.println("Registration request created. Wait for manager approval.");
        } catch (Exception e) {
            System.out.println("Could not create request: " + e.getMessage());
        }
    }
}
