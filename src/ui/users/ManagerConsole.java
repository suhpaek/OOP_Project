package ui.users;

import java.util.List;
import java.util.Scanner;

import enums.CourseType;
import enums.LessonType;
import enums.NewsType;
import i18n.I18n;
import models.academic.RegistrationRequest;
import models.organization.StudentOrganization;
import models.users.Manager;
import models.users.Student;
import models.users.Teacher;
import services.academic.CourseService;
import services.academic.ManagerService;
import services.communication.NewsService;
import services.communication.OfficialMessageService;
import services.organization.OrganizationService;
import services.support.SupportRequestService;
import ui.app.ConsoleLanguage;
import ui.app.ConsoleScreen;
import ui.features.JournalConsole;
import ui.features.NewsConsole;
import ui.features.SearchConsole;

public class ManagerConsole implements ConsoleScreen {

    private final Scanner scanner;
    private final Manager manager;
    private final CourseService courseService = new CourseService();
    private final ManagerService managerService = new ManagerService(courseService);
    private final NewsService newsService = new NewsService();
    private final OrganizationService organizationService = new OrganizationService();
    private final SupportRequestService supportRequestService = new SupportRequestService();
    private final OfficialMessageService officialMessageService = new OfficialMessageService();

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
                    new NewsConsole(scanner, manager).start();
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
                case "11":
                    viewTeachers();
                    break;
                case "12":
                    viewEmployeeRequests();
                    break;
                case "13":
                    new JournalConsole(scanner, manager).start();
                    break;
                case "14":
                    new SearchConsole(scanner).start();
                    break;
                case "15":
                    viewOfficialMessages();
                    break;
                case "16":
                    ConsoleLanguage.changeLanguage(scanner, manager);
                    break;
                case "0":
                    running = false;
                    break;
                default:
                    System.out.println(I18n.t("app.invalid"));
            }
        }
    }

    private void printMenu() {
        System.out.println();
        System.out.println("===== " + I18n.t("manager.title") + " =====");
        System.out.println(I18n.t("manager.welcome", manager.getFullName()));
        System.out.println(I18n.t("manager.registration_status") + ": "
                + (managerService.isCourseRegistrationOpen() ? I18n.t("status.open") : I18n.t("status.closed")));
        System.out.println("1. " + I18n.t("manager.add_course"));
        System.out.println("2. " + I18n.t("manager.registration_toggle"));
        System.out.println("3. " + I18n.t("manager.approve_reg"));
        System.out.println("4. " + I18n.t("manager.assign_course"));
        System.out.println("5. " + I18n.t("menu.news"));
        System.out.println("6. " + I18n.t("manager.publish_news"));
        System.out.println("7. " + I18n.t("manager.add_lesson"));
        System.out.println("8. " + I18n.t("manager.view_students"));
        System.out.println("9. " + I18n.t("manager.reports"));
        System.out.println("10. " + I18n.t("student.organizations"));
        System.out.println("11. View teachers alphabetically");
        System.out.println("12. View employee requests");
        System.out.println("13. " + I18n.t("journal.title"));
        System.out.println("14. Search");
        System.out.println("15. View official messages");
        System.out.println("16. " + I18n.t("menu.change_language"));
        System.out.println("0. " + I18n.t("menu.logout"));
        System.out.print(I18n.t("menu.choice") + ": ");
    }

    private void organizationMenu() {
        boolean running = true;
        while (running) {
            System.out.println();
            System.out.println("===== " + I18n.t("org.title") + " =====");
            System.out.println("1. " + I18n.t("org.view_all"));
            System.out.println("2. " + I18n.t("org.create"));
            System.out.println("0. " + I18n.t("menu.back"));
            System.out.print(I18n.t("menu.choice") + ": ");
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
                    System.out.println(I18n.t("app.invalid"));
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
            System.out.print("Intended school (SITE/SG/SEOGI/SMSGT/BS/ISE/KMA/SCE/SAM/SNSS): ");
            String schoolInput = scanner.nextLine().trim().toUpperCase();
            enums.School school = null;
            if (!schoolInput.isBlank()) {
                school = enums.School.valueOf(schoolInput);
            }

            courseService.createCourse(code, name, credits, type, year, school);
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
            if (index < 0) {
                return;
            }
            System.out.print("Approve? (yes/no): ");
            boolean approve = scanner.nextLine().trim().equalsIgnoreCase("yes");
            if (approve) {
                managerService.approveRegistration(manager, requests.get(index)); 
            }else {
                managerService.rejectRegistration(manager, requests.get(index));
            }
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

    private void viewTeachers() {
        List<Teacher> teachers = managerService.viewTeachersAlphabetically(manager);
        if (teachers.isEmpty()) {
            System.out.println("No teachers found.");
            return;
        }
        System.out.printf("%-16s %-28s %-28s %-8s%n", "Username", "Full name", "Email", "Rating");
        for (Teacher teacher : teachers) {
            System.out.printf("%-16s %-28s %-28s %-8.2f%n",
                    teacher.getUsername(),
                    teacher.getFullName(),
                    safe(teacher.getEmail()),
                    teacher.getAverageRating());
        }
    }

    private void createAcademicReport() {
        System.out.println(managerService.createAcademicReport(manager));
    }

    private void viewEmployeeRequests() {
        if (supportRequestService.getAllRequests().isEmpty()) {
            System.out.println("No employee requests found.");
            return;
        }
        System.out.printf("%-36s %-10s %-24s %s%n", "ID", "Status", "Title", "Description");
        for (models.support.TechSupportRequest request : supportRequestService.getAllRequests()) {
            System.out.printf("%-36s %-10s %-24s %s%n",
                    request.getId(), request.getStatus(), request.getTitle(), request.getDescription());
        }
    }

    private void viewOfficialMessages() {
        List<String> messages = officialMessageService.getOfficialMessages();
        if (messages.isEmpty()) {
            System.out.println("No official messages found.");
            return;
        }
        for (String message : messages) {
            System.out.println(message);
        }
    }

    private String safe(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }
}
