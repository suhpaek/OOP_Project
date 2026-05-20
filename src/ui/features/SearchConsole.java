package ui.features;

import models.academic.Course;
import models.communication.News;
import models.users.User;
import services.search.SearchService;
import ui.app.ConsoleScreen;

import java.util.Scanner;

public class SearchConsole implements ConsoleScreen {
    private final Scanner scanner;
    private final SearchService searchService = new SearchService();

    public SearchConsole(Scanner scanner) {
        this.scanner = scanner;
    }

    public void start() {
        boolean running = true;
        while (running) {
            System.out.println();
            System.out.println("===== SEARCH =====");
            System.out.println("1. Search users");
            System.out.println("2. Search courses");
            System.out.println("3. Search news");
            System.out.println("0. Back");
            System.out.print("Choose: ");
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    searchUsers();
                    break;
                case "2":
                    searchCourses();
                    break;
                case "3":
                    searchNews();
                    break;
                case "0":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private void searchUsers() {
        System.out.print("Regex: ");
        for (User user : searchService.searchUsers(scanner.nextLine())) {
            System.out.printf("%-20s %-18s %-30s %s%n",
                    user.getClass().getSimpleName(), user.getUsername(), user.getFullName(), user.getEmail());
        }
    }

    private void searchCourses() {
        System.out.print("Regex: ");
        for (Course course : searchService.searchCourses(scanner.nextLine())) {
            System.out.printf("%-12s %-40s %-8d %s%n",
                    course.getCode(), course.getName(), course.getCredits(), course.getCourseType());
        }
    }

    private void searchNews() {
        System.out.print("Regex: ");
        for (News news : searchService.searchNews(scanner.nextLine())) {
            System.out.printf("%d | %s | %s%n%s%n",
                    news.getId(), news.getTopic(), news.getTitle(), news.getContent());
        }
    }
}
