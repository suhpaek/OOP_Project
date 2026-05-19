package ui;

import data.DataStore;
import models.academic.Course;
import models.users.Student;
import services.CourseService;
import services.GradeService;
import services.NewsService;
import services.TranscriptService;
import services.UserService;

import java.util.List;
import java.util.Scanner;

public class StudentConsole {
    private final Scanner scanner;
    private final Student student;
    private final CourseService courseService = new CourseService(DataStore.getInstance());
    private final GradeService gradeService = new GradeService();
    private final NewsService newsService = new NewsService(DataStore.getInstance());
    private final TranscriptService transcriptService = new TranscriptService();
    private final UserService userService = new UserService();

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
                case "3":
                    viewMyCourses();
                    break;
                case "4":
                    viewMarks();
                    break;
                case "5":
                    viewTranscript();
                    break;
                case "6":
                    getTranscript();
                    break;
                case "7":
                    viewTeacherInfo();
                    break;
                case "8":
                    rateTeacher();
                    break;
                case "9":
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
        System.out.println("===== STUDENT MENU =====");
        System.out.println("Welcome, " + student.getFullName());
        System.out.println("1. View available courses");
        System.out.println("2. Create course registration request");
        System.out.println("3. View my courses");
        System.out.println("4. View marks");
        System.out.println("5. View transcript");
        System.out.println("6. Get transcript");
        System.out.println("7. View teacher info");
        System.out.println("8. Rate teacher");
        System.out.println("9. View news");
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

    private void viewMyCourses() {
        List<Course> courses = courseService.getRegisteredCourses(student);
        if (courses.isEmpty()) {
            System.out.println("You are not registered for any courses.");
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

    private void viewMarks() {
        List<String> marks = gradeService.getStudentMarks(student);
        if (marks.isEmpty()) {
            System.out.println("No marks found.");
            return;
        }
        for (String mark : marks) {
            System.out.println(mark);
        }
    }

    private void viewTranscript() {
        System.out.println(transcriptService.buildStudentTranscript(student));
    }

    private void getTranscript() {
        try {
            String fileName = transcriptService.generateTranscriptFile(student);
            System.out.println("Transcript generated: " + fileName);
        } catch (Exception e) {
            System.out.println("Could not generate transcript: " + e.getMessage());
        }
    }

    private void viewTeacherInfo() {
        try {
            System.out.print("Course code: ");
            String courseCode = scanner.nextLine();
            System.out.println(courseService.getTeacherInfo(student, courseCode));
        } catch (Exception e) {
            System.out.println("Could not show teacher info: " + e.getMessage());
        }
    }

    private void rateTeacher() {
        try {
            System.out.print("Course code: ");
            String courseCode = scanner.nextLine();
            System.out.print("Rate lecture teacher? (yes/no): ");
            boolean lecture = scanner.nextLine().trim().equalsIgnoreCase("yes");
            System.out.print("Rating (1-5): ");
            int rating = Integer.parseInt(scanner.nextLine().trim());
            userService.rateCourseTeacher(student, courseCode, rating, lecture);
            System.out.println("Teacher rated.");
        } catch (Exception e) {
            System.out.println("Could not rate teacher: " + e.getMessage());
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
