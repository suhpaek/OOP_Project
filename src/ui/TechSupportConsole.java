package ui;

import enums.RequestStatus;
import i18n.I18n;
import models.communication.Complaint;
import models.support.TechSupportRequest;
import models.users.TechSupportSpecialist;
import services.ComplaintService;
import services.SupportRequestService;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class TechSupportConsole {
    private final Scanner scanner;
    private final TechSupportSpecialist specialist;
    private final ComplaintService complaintService = new ComplaintService();
    private final SupportRequestService supportRequestService = new SupportRequestService();

    public TechSupportConsole(Scanner scanner, TechSupportSpecialist specialist) {
        this.scanner = scanner;
        this.specialist = specialist;
    }

    public void start() {
        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    viewComplaints();
                    break;
                case "2":
                    viewSupportRequests();
                    break;
                case "3":
                    createSupportRequest();
                    break;
                case "4":
                    updateSupportRequestStatus();
                    break;
                case "5":
                    ConsoleLanguage.changeLanguage(scanner, specialist);
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
        System.out.println("===== " + I18n.t("support.title") + " =====");
        System.out.println(I18n.t("support.welcome", specialist.getFullName()));
        System.out.println("1. " + I18n.t("support.view_complaints"));
        System.out.println("2. " + I18n.t("support.view_requests"));
        System.out.println("3. " + I18n.t("support.create_request"));
        System.out.println("4. " + I18n.t("support.update_request"));
        System.out.println("5. " + I18n.t("menu.change_language"));
        System.out.println("0. " + I18n.t("menu.logout"));
        System.out.print(I18n.t("menu.choice") + ": ");
    }

    private void viewComplaints() {
        List<Complaint> complaints = complaintService.getAllComplaints();
        if (complaints.isEmpty()) {
            System.out.println("No complaints found.");
            return;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        System.out.printf("%-12s %-10s %-18s %-36s %s%n", "ID", "Urgency", "Created", "Teacher ID", "Text");
        for (Complaint complaint : complaints) {
            System.out.printf("%-12d %-10s %-18s %-36s %s%n",
                    complaint.getId(),
                    complaint.getUrgency(),
                    complaint.getCreatedAt().format(formatter),
                    complaint.getTeacherId(),
                    complaint.getText());
        }
    }

    private void viewSupportRequests() {
        List<TechSupportRequest> requests = supportRequestService.getAllRequests();
        if (requests.isEmpty()) {
            System.out.println("No support requests found.");
            return;
        }
        System.out.printf("%-36s %-10s %-24s %s%n", "ID", "Status", "Title", "Description");
        for (TechSupportRequest request : requests) {
            System.out.printf("%-36s %-10s %-24s %s%n",
                    request.getId(),
                    request.getStatus(),
                    request.getTitle(),
                    request.getDescription());
        }
    }

    private void createSupportRequest() {
        try {
            System.out.print("Title: ");
            String title = scanner.nextLine();
            System.out.print("Description: ");
            String description = scanner.nextLine();
            supportRequestService.createRequest(title, description);
            System.out.println("Support request created.");
        } catch (Exception e) {
            System.out.println("Could not create support request: " + e.getMessage());
        }
    }

    private void updateSupportRequestStatus() {
        try {
            System.out.print("Request ID: ");
            String requestId = scanner.nextLine().trim();
            System.out.print("Status (NEW/VIEWED/ACCEPTED/REJECTED/DONE): ");
            RequestStatus status = RequestStatus.valueOf(scanner.nextLine().trim().toUpperCase());
            supportRequestService.updateStatus(requestId, status);
            System.out.println("Support request updated.");
        } catch (Exception e) {
            System.out.println("Could not update support request: " + e.getMessage());
        }
    }
}
