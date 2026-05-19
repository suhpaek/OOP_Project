package ui;

import java.util.List;
import java.util.Scanner;

import data.DataStore;
import models.academic.Course;
import models.users.Employee;
import models.users.Teacher;
import services.CourseService;
import services.GradeService;
import services.MessageService;
import services.NewsService;

public class TeacherConsole {

    private final Scanner scanner;
    private final Teacher teacher;
    private final CourseService courseService = new CourseService(DataStore.getInstance());
    private final GradeService gradeService = new GradeService();
    private final NewsService newsService = new NewsService(DataStore.getInstance());
    private final MessageService messageService = new MessageService(DataStore.getInstance());

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
        System.out.println("4. View students in course");
        System.out.println("5. Send complaint");
        System.out.println("6. Send message");
        System.out.println("7. View inbox");
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
            services.ComplaintService complaintService = new services.ComplaintService(data.DataStore.getInstance());
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
            try {
                models.users.User user = DataStore.getInstance().findUserByUsername(receiverUsername);
                if (!(user instanceof Employee)) {
                    System.out.println("Receiver is not an employee.");
                    return;
                }
                Employee receiver = (Employee) user;
                messageService.sendMessage(teacher, receiver, text);
                System.out.println("Message sent.");
            } catch (Exception e) {
                System.out.println("Could not find receiver: " + e.getMessage());
            }
        } catch (Exception e) {
            System.out.println("Could not send message: " + e.getMessage());
        }
    }

    private void viewInbox() {
        try {
            List<models.communication.Message> inbox = messageService.getReceivedMessages(teacher);
            if (inbox.isEmpty()) {
                System.out.println("Inbox is empty.");
                return;
            }
            System.out.printf("%-36s %-20s %-20s %-40s%n", "ID", "From", "To", "Text");
            for (models.communication.Message m : inbox) {
                String from = m.getSenderId();
                try {
                    from = DataStore.getInstance().findUserById(m.getSenderId()).getUsername();
                } catch (Exception ignored) {
                }
                String to = m.getReceiverId();
                try {
                    to = DataStore.getInstance().findUserById(m.getReceiverId()).getUsername();
                } catch (Exception ignored) {
                }
                System.out.printf("%-36s %-20s %-20s %-40s%n", m.getId(), from, to, m.getText());
            }
        } catch (Exception e) {
            System.out.println("Could not load inbox: " + e.getMessage());
        }
    }
}
