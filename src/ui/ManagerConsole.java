package ui;

import enums.CourseType;
import enums.LessonType;
import enums.NewsType;
import models.academic.RegistrationRequest;
import models.organization.StudentOrganization;
import models.users.Manager;
import models.users.Student;
import services.CourseService;
import services.ManagerService;
import services.NewsService;
import services.OrganizationService;

import java.util.List;
import java.util.Scanner;

public class ManagerConsole {
    private final Scanner scanner;
    private final Manager manager;
    private final CourseService courseService = new CourseService();
    private final ManagerService managerService = new ManagerService(courseService);
    private final NewsService newsService = new NewsService();
    private final OrganizationService organizationService = new OrganizationService();

    public ManagerConsole(Scanner scanner, Manager manager) {
        this.scanner = scanner;
        this.manager = manager;
    }

    public void start() {
        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    addCourse();
                    break;
                case "2":
                    changeRegistrationStatus();
                    break;
                case "3":
                    processRegistrationRequests();
                    break;
                case "4":
                    assignTeacher();
                    break;
                case "5":
                    viewNews();
                    break;
                case "6":
                    publishNews();
                    break;
                case "7":
                    addLesson();
                    break;
                case "8":
                    viewStudents();
                    break;
                case "9":
                    createAcademicReport();
                    break;
                case "10":
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
        System.out.println("===== MANAGER MENU =====");
        System.out.println("Welcome, " + manager.getFullName());
        System.out.println("Course registration: " + (managerService.isCourseRegistrationOpen() ? "OPEN" : "CLOSED"));
        System.out.println("1. Add course");
        System.out.println("2. Open/close course registration");
        System.out.println("3. View/approve registration requests");
        System.out.println("4. Assign course to teacher");
        System.out.println("5. View news");
        System.out.println("6. Publish news");
        System.out.println("7. Add lesson to course");
        System.out.println("8. View students");
        System.out.println("9. Create academic report");
        System.out.println("10. Student organizations");
        System.out.println("0. Logout");
        System.out.print("Choose: ");
    }

    private void organizationMenu() {
        boolean running = true;
        while (running) {
            System.out.println();
            System.out.println("===== STUDENT ORGANIZATIONS =====");
            System.out.println("1. View organizations");
            System.out.println("2. Create organization");
            System.out.println("0. Back");
            System.out.print("Choose: ");
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    viewOrganizations();
                    break;
                case "2":
                    createOrganization();
                    break;
                case "0":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private void viewOrganizations() {
        List<StudentOrganization> organizations = organizationService.getAllOrganizations();
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

    private void createOrganization() {
        try {
            System.out.print("Name: ");
            String name = scanner.nextLine();
            System.out.print("Description: ");
            String description = scanner.nextLine();
            System.out.print("Head student username (optional): ");
            String headUsername = scanner.nextLine().trim();
            organizationService.createOrganization(name, description, headUsername);
            System.out.println("Organization created.");
        } catch (Exception e) {
            System.out.println("Could not create organization: " + e.getMessage());
        }
    }

    private void addCourse() {
        try {
            System.out.print("Code: ");
            String code = scanner.nextLine();
            System.out.print("Name: ");
            String name = scanner.nextLine();
            System.out.print("Credits: ");
            int credits = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Type (MAJOR/MINOR/FREE_ELECTIVE): ");
            CourseType type = CourseType.valueOf(scanner.nextLine().trim().toUpperCase());
            System.out.print("Intended year of study: ");
            int year = Integer.parseInt(scanner.nextLine().trim());

            courseService.createCourse(code, name, credits, type, year);
            System.out.println("Course added.");
        } catch (Exception e) {
            System.out.println("Could not add course: " + e.getMessage());
        }
    }

    private void changeRegistrationStatus() {
        try {
            System.out.print("Open registration? (yes/no): ");
            String answer = scanner.nextLine().trim().toLowerCase();
            boolean open = answer.equals("yes") || answer.equals("y");
            managerService.setCourseRegistrationOpen(open);
            System.out.println("Course registration is now " + (open ? "OPEN." : "CLOSED."));
        } catch (Exception e) {
            System.out.println("Could not change registration status: " + e.getMessage());
        }
    }

    private void processRegistrationRequests() {
        List<RegistrationRequest> requests = managerService.getRegistrationRequests();
        if (requests.isEmpty()) {
            System.out.println("No registration requests.");
            return;
        }

        for (int i = 0; i < requests.size(); i++) {
            RegistrationRequest request = requests.get(i);
            System.out.printf("%d. %s -> %s | processed=%s approved=%s%n",
                    i + 1,
                    request.getStudent().getUsername(),
                    request.getCourse().getCode(),
                    request.isProcessed(),
                    request.isApproved());
        }

        try {
            System.out.print("Request number (0 to cancel): ");
            int index = Integer.parseInt(scanner.nextLine().trim()) - 1;
            if (index < 0) return;
            System.out.print("Approve? (yes/no): ");
            boolean approve = scanner.nextLine().trim().equalsIgnoreCase("yes");
            if (approve) managerService.approveRegistration(manager, requests.get(index));
            else managerService.rejectRegistration(manager, requests.get(index));
            System.out.println("Request processed.");
        } catch (Exception e) {
            System.out.println("Could not process request: " + e.getMessage());
        }
    }

    private void assignTeacher() {
        try {
            System.out.print("Course code: ");
            String courseCode = scanner.nextLine();
            System.out.print("Teacher username: ");
            String teacherUsername = scanner.nextLine();
            System.out.print("Lecture teacher? (yes/no): ");
            boolean lecture = scanner.nextLine().trim().equalsIgnoreCase("yes");
            courseService.assignTeacher(courseCode, teacherUsername, lecture);
            System.out.println("Teacher assigned.");
        } catch (Exception e) {
            System.out.println("Could not assign teacher: " + e.getMessage());
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

    private void publishNews() {
        try {
            System.out.print("Title: ");
            String title = scanner.nextLine();
            System.out.print("Content: ");
            String content = scanner.nextLine();
            System.out.print("Type (GENERAL/RESEARCH): ");
            NewsType type = NewsType.valueOf(scanner.nextLine().trim().toUpperCase());
            newsService.publish(title, content, type);
            System.out.println("News published.");
        } catch (Exception e) {
            System.out.println("Could not publish news: " + e.getMessage());
        }
    }

    private void addLesson() {
        try {
            System.out.print("Course code: ");
            String courseCode = scanner.nextLine();
            System.out.print("Type (LECTURE/PRACTICE/LAB): ");
            LessonType type = LessonType.valueOf(scanner.nextLine().trim().toUpperCase());
            System.out.print("Day: ");
            String day = scanner.nextLine();
            System.out.print("Time: ");
            String time = scanner.nextLine();
            System.out.print("Room: ");
            String room = scanner.nextLine();
            System.out.print("Teacher username: ");
            String teacherUsername = scanner.nextLine();
            courseService.addLesson(courseCode, type, day, time, room, teacherUsername);
            System.out.println("Lesson added.");
        } catch (Exception e) {
            System.out.println("Could not add lesson: " + e.getMessage());
        }
    }

    private void viewStudents() {
        System.out.print("Sort by GPA? (yes/no): ");
        boolean byGpa = scanner.nextLine().trim().equalsIgnoreCase("yes");
        List<Student> students = byGpa
                ? managerService.viewStudentsByGpa(manager)
                : managerService.viewStudentsByName(manager);
        if (students.isEmpty()) {
            System.out.println("No students found.");
            return;
        }
        System.out.printf("%-16s %-28s %-8s%n", "Username", "Full name", "GPA");
        for (Student student : students) {
            System.out.printf("%-16s %-28s %-8.2f%n",
                    student.getUsername(),
                    student.getFullName(),
                    student.getTranscript().calculateGpa());
        }
    }

    private void createAcademicReport() {
        System.out.println(managerService.createAcademicReport(manager));
    }

    private String safe(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }
}
