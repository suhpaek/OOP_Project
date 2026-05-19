package ui;

import data.DataStore;
import models.academic.Course;
import models.users.Teacher;
import services.CourseService;

import java.util.List;
import java.util.Scanner;

public class TeacherConsole {
    private final Scanner scanner;
    private final Teacher teacher;
    private final CourseService courseService = new CourseService(DataStore.getInstance());

    public TeacherConsole(Scanner scanner, Teacher teacher) {
        this.scanner = scanner;
        this.teacher = teacher;
    }

    public void start() {
        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    viewAssignedCourses();
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
        System.out.println("===== TEACHER MENU =====");
        System.out.println("Welcome, " + teacher.getFullName());
        System.out.println("1. View assigned courses");
        System.out.println("0. Logout");
        System.out.print("Choose: ");
    }

    private void viewAssignedCourses() {
        List<Course> courses = courseService.getAssignedCourses(teacher);
        if (courses.isEmpty()) {
            System.out.println("No assigned courses.");
            return;
        }

        System.out.printf("%-12s %-35s %-8s %-16s%n", "Code", "Name", "Credits", "Type");
        for (Course course : courses) {
            System.out.printf("%-12s %-35s %-8d %-16s%n",
                    course.getCode(),
                    course.getName(),
                    course.getCredits(),
                    course.getCourseType());
        }
    }
}
