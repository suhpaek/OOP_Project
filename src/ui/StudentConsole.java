package ui;

import java.util.List;
import java.util.Scanner;

import models.academic.Course;
import models.organization.StudentOrganization;
import models.users.Student;
import services.CourseService;
import services.GradeService;
import services.NewsService;
import services.OrganizationService;
import services.TranscriptService;
import services.UserService;

public class StudentConsole {

    private final Scanner scanner;
    private final Student student;
    private final CourseService courseService = new CourseService();
    private final GradeService gradeService = new GradeService();
    private final NewsService newsService = new NewsService();
    private final OrganizationService organizationService = new OrganizationService();
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
                case "10":
                    viewSchedule();
                    break;
                case "11":
                    new ResearchConsole(scanner, student).start();
                    break;
                case "12":
                    organizationMenu();
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
        System.out.println("10. View schedule");
        System.out.println("11. Research menu");
        System.out.println("12. Student organizations");
        System.out.println("0. Logout");
        System.out.print("Choose: ");
    }

    private void organizationMenu() {
        boolean running = true;
        while (running) {
            System.out.println();
            System.out.println("===== STUDENT ORGANIZATIONS =====");
            System.out.println("1. View all organizations");
            System.out.println("2. View my organizations");
            System.out.println("3. Join organization");
            System.out.println("4. Leave organization");
            System.out.println("0. Back");
            System.out.print("Choose: ");
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    printOrganizations(organizationService.getAllOrganizations());
                    break;
                case "2":
                    printOrganizations(organizationService.getOrganizationsForStudent(student));
                    break;
                case "3":
                    joinOrganization();
                    break;
                case "4":
                    leaveOrganization();
                    break;
                case "0":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private void joinOrganization() {
        try {
            System.out.print("Organization name: ");
            organizationService.joinOrganization(student, scanner.nextLine().trim());
            System.out.println("Joined organization.");
        } catch (Exception e) {
            System.out.println("Could not join organization: " + e.getMessage());
        }
    }

    private void leaveOrganization() {
        try {
            System.out.print("Organization name: ");
            organizationService.leaveOrganization(student, scanner.nextLine().trim());
            System.out.println("Left organization.");
        } catch (Exception e) {
            System.out.println("Could not leave organization: " + e.getMessage());
        }
    }

    private void printOrganizations(List<StudentOrganization> organizations) {
        if (organizations.isEmpty()) {
            System.out.println("No organizations found.");
            return;
        }
        System.out.printf("%-24s %-8s %-36s %s%n", "Name", "Members", "Head ID", "Description");
        for (StudentOrganization organization : organizations) {
            System.out.printf("%-24s %-8d %-36s %s%n",
                    organization.getName(),
                    organization.getMemberStudentIds().size(),
                    safe(organization.getHeadStudentId()),
                    safe(organization.getDescription()));
        }
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

    private void viewSchedule() {
        try {
            List<String> rows = courseService.getSchedule(student);
            if (rows.isEmpty()) {
                System.out.println("No scheduled lessons.");
                return;
            }
            System.out.printf("%-10s %-8s %-8s %-12s %-12s %-20s%n", "Day", "Time", "Room", "Course", "Type", "Teacher");
            for (String r : rows) {
                String[] parts = r.split(" \\| ");
                System.out.printf("%-10s %-8s %-8s %-12s %-12s %-20s%n",
                        parts.length > 0 ? parts[0] : "",
                        parts.length > 1 ? parts[1] : "",
                        parts.length > 2 ? parts[2] : "",
                        parts.length > 3 ? parts[3] : "",
                        parts.length > 4 ? parts[4] : "",
                        parts.length > 5 ? parts[5] : "");
            }
        } catch (Exception e) {
            System.out.println("Could not show schedule: " + e.getMessage());
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

    private String safe(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }
}
