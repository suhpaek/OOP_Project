package ui.features;

import ui.app.ConsoleLanguage;
import ui.app.ConsoleScreen;
import models.research.Journal;
import models.users.User;
import services.research.JournalService;

import java.util.Scanner;

public class JournalConsole implements ConsoleScreen {
    private final Scanner scanner;
    private final User user;
    private final JournalService journalService = new JournalService();

    public JournalConsole(Scanner scanner, User user) {
        this.scanner = scanner;
        this.user = user;
    }

    public void start() {
        boolean running = true;
        while (running) {
            System.out.println();
            System.out.println("===== JOURNALS =====");
            System.out.println("1. View journals");
            System.out.println("2. Subscribe");
            System.out.println("3. Unsubscribe");
            System.out.println("0. Back");
            System.out.print("Choose: ");
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    viewJournals();
                    break;
                case "2":
                    subscribe();
                    break;
                case "3":
                    unsubscribe();
                    break;
                case "0":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private void viewJournals() {
        if (journalService.getJournals().isEmpty()) {
            System.out.println("No journals found.");
            return;
        }
        for (Journal journal : journalService.getJournals()) {
            System.out.printf("%s | subscribers=%d | papers=%d%n",
                    journal.getName(), journal.getSubscribers().size(), journal.getPapers().size());
            for (String notification : journal.getNotifications()) {
                if (notification.contains(user.getId())) {
                    System.out.println("  " + notification);
                }
            }
        }
    }

    private void subscribe() {
        try {
            System.out.print("Journal name: ");
            journalService.subscribe(user, scanner.nextLine().trim());
            System.out.println("Subscribed.");
        } catch (Exception e) {
            System.out.println("Could not subscribe: " + e.getMessage());
        }
    }

    private void unsubscribe() {
        try {
            System.out.print("Journal name: ");
            journalService.unsubscribe(user, scanner.nextLine().trim());
            System.out.println("Unsubscribed.");
        } catch (Exception e) {
            System.out.println("Could not unsubscribe: " + e.getMessage());
        }
    }
}
