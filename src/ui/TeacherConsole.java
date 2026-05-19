package ui;

import data.DataStore;
import models.academic.Course;
import models.users.Teacher;
import services.CourseService;
import services.GradeService;
import services.NewsService;

import java.util.List;
import java.util.Scanner;

public class TeacherConsole {
    private final Scanner scanner;
    private final Teacher teacher;
    private final CourseService courseService = new CourseService(DataStore.getInstance());
    private final GradeService gradeService = new GradeService();
    private final NewsService newsService = new NewsService(DataStore.getInstance());

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
                case "2":
                    putMark();
                    break;
                case "3":
                    viewNews();
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
        System.out.println("2. Put mark");
        System.out.println("3. View news");
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

    private void putMark() {
        try {
            System.out.print("Student username: ");
            String studentUsername = scanner.nextLine();
            System.out.print("Course code: ");
            String courseCode = scanner.nextLine();
            System.out.print("First attestation (0-30): ");
            double first = Double.parseDouble(scanner.nextLine().trim());
            System.out.print("Second attestation (0-30): ");
            double second = Double.parseDouble(scanner.nextLine().trim());
            System.out.print("Final exam (0-40): ");
            double exam = Double.parseDouble(scanner.nextLine().trim());
            gradeService.putMark(teacher, studentUsername, courseCode, first, second, exam);
            System.out.println("Mark saved.");
        } catch (Exception e) {
            System.out.println("Could not put mark: " + e.getMessage());
        }
    }

    private void viewNews() {
        List<String> news = newsService.getFormattedNews();
        if (news.isEmpty()) {
            System.out.println("No news found.");
            return;
        }
        for (String item : news) {
            System.out.println(item);
            System.out.println();
        }
    }
}
