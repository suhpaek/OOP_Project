package ui.users;

import java.util.List;
import java.util.Scanner;

import i18n.I18n;
import enums.LessonType;
import models.academic.Course;
import models.users.Teacher;
import services.academic.AttendanceService;
import services.communication.ComplaintService;
import services.academic.CourseService;
import services.academic.GradeService;
import services.communication.MessageService;
import services.communication.NewsService;
import ui.app.ConsoleLanguage;
import ui.app.ConsoleScreen;
import ui.features.JournalConsole;
import ui.features.NewsConsole;
import ui.features.ResearchConsole;
import ui.features.SearchConsole;

public class TeacherConsole implements ConsoleScreen {

    private final Scanner scanner;
    private final Teacher teacher;
    private final CourseService courseService = new CourseService();
    private final GradeService gradeService = new GradeService();
    private final NewsService newsService = new NewsService();
    private final MessageService messageService = new MessageService();
    private final ComplaintService complaintService = new ComplaintService();
    private final AttendanceService attendanceService = new AttendanceService();

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
                    new NewsConsole(scanner, teacher).start();
                    break;
                case "4":
                    viewStudentsInCourse();
                    break;
                case "5":
                    sendComplaint();
                    break;
                case "6":
                    sendMessage();
                    break;
                case "7":
                    viewInbox();
                    break;
                case "8":
                    new ResearchConsole(scanner, teacher).start();
                    break;
                case "9":
                    new JournalConsole(scanner, teacher).start();
                    break;
                case "10":
                    markAttendance();
                    break;
                case "11":
                    new SearchConsole(scanner).start();
                    break;
                case "12":
                    ConsoleLanguage.changeLanguage(scanner, teacher);
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
        System.out.println("===== " + I18n.t("teacher.title") + " =====");
        System.out.println(I18n.t("teacher.welcome", teacher.getFullName()));
        System.out.println("1. " + I18n.t("teacher.courses"));
        System.out.println("2. " + I18n.t("teacher.put_marks"));
        System.out.println("3. " + I18n.t("menu.news"));
        System.out.println("4. " + I18n.t("teacher.students"));
        System.out.println("5. " + I18n.t("teacher.complaint"));
        System.out.println("6. " + I18n.t("teacher.messages"));
        System.out.println("7. " + I18n.t("menu.inbox"));
        System.out.println("8. " + I18n.t("research.title"));
        System.out.println("9. " + I18n.t("journal.title"));
        System.out.println("10. Mark attendance");
        System.out.println("11. Search");
        System.out.println("12. " + I18n.t("menu.change_language"));
        System.out.println("0. " + I18n.t("menu.logout"));
        System.out.print(I18n.t("menu.choice") + ": ");
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

    private void viewStudentsInCourse() {
        try {
            System.out.print("Course code: ");
            String courseCode = scanner.nextLine();
            List<models.users.Student> students = courseService.getStudentsForTeacherCourse(teacher, courseCode);
            if (students.isEmpty()) {
                System.out.println("No students enrolled for this course.");
                return;
            }
            System.out.printf("%-20s %-30s%n", "Username", "Full name");
            for (models.users.Student s : students) {
                System.out.printf("%-20s %-30s%n", s.getUsername(), s.getFullName());
            }
        } catch (Exception e) {
            System.out.println("Could not retrieve students: " + e.getMessage());
        }
    }

    private void sendComplaint() {
        try {
            System.out.print("Student username: ");
            String studentUsername = scanner.nextLine();
            System.out.print("Complaint text: ");
            String text = scanner.nextLine();
            System.out.print("Urgency (LOW/MEDIUM/HIGH): ");
            enums.UrgencyLevel urgency = enums.UrgencyLevel.valueOf(scanner.nextLine().trim().toUpperCase());
            complaintService.createComplaintByUsername(teacher, studentUsername, text, urgency);
            System.out.println("Complaint sent.");
        } catch (Exception e) {
            System.out.println("Could not send complaint: " + e.getMessage());
        }
    }

    private void sendMessage() {
        try {
            System.out.print("Receiver username: ");
            String receiverUsername = scanner.nextLine();
            System.out.print("Message text: ");
            String text = scanner.nextLine();
            messageService.sendMessageToUsername(teacher, receiverUsername, text);
            System.out.println("Message sent.");
        } catch (Exception e) {
            System.out.println("Could not send message: " + e.getMessage());
        }
    }

    private void viewInbox() {
        try {
            List<String[]> inboxRows = messageService.getInboxRows(teacher);
            if (inboxRows.isEmpty()) {
                System.out.println("Inbox is empty.");
                return;
            }
            System.out.printf("%-36s %-20s %-20s %-40s%n", "ID", "From", "To", "Text");
            for (String[] row : inboxRows) {
                System.out.printf("%-36s %-20s %-20s %-40s%n", row[0], row[1], row[2], row[3]);
            }
        } catch (Exception e) {
            System.out.println("Could not load inbox: " + e.getMessage());
        }
    }

    private void markAttendance() {
        try {
            System.out.print("Student username: ");
            String studentUsername = scanner.nextLine().trim();
            System.out.print("Course code: ");
            String courseCode = scanner.nextLine().trim();
            System.out.print("Lesson type (LECTURE/PRACTICE/LAB): ");
            LessonType lessonType = LessonType.valueOf(scanner.nextLine().trim().toUpperCase());
            System.out.print("Date (yyyy-mm-dd): ");
            java.time.LocalDate date = java.time.LocalDate.parse(scanner.nextLine().trim());
            System.out.print("Present? (yes/no): ");
            boolean present = scanner.nextLine().trim().equalsIgnoreCase("yes");
            attendanceService.markAttendance(teacher, studentUsername, courseCode, lessonType, date, present);
            System.out.println("Attendance saved.");
        } catch (Exception e) {
            System.out.println("Could not mark attendance: " + e.getMessage());
        }
    }
}
