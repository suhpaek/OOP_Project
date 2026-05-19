package ui;

import data.DataStore;
import enums.CourseType;
import models.academic.RegistrationRequest;
import models.users.Manager;
import services.CourseService;
import services.ManagerService;

import java.util.List;
import java.util.Scanner;

public class ManagerConsole {
    private final Scanner scanner;
    private final Manager manager;
    private final CourseService courseService = new CourseService(DataStore.getInstance());
    private final ManagerService managerService = new ManagerService(DataStore.getInstance(), courseService);

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
        System.out.println("0. Logout");
        System.out.print("Choose: ");
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
        List<RegistrationRequest> requests = DataStore.getInstance().getRegistrationRequests();
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
}
