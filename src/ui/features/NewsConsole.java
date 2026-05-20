package ui.features;

import models.communication.Comment;
import models.communication.News;
import models.users.User;
import services.communication.NewsService;
import ui.app.ConsoleScreen;

import java.util.List;
import java.util.Scanner;

public class NewsConsole implements ConsoleScreen {
    private final Scanner scanner;
    private final User user;
    private final NewsService newsService = new NewsService();

    public NewsConsole(Scanner scanner, User user) {
        this.scanner = scanner;
        this.user = user;
    }

    public void start() {
        boolean running = true;
        while (running) {
            System.out.println();
            System.out.println("===== NEWS =====");
            System.out.println("1. View news");
            System.out.println("2. Add comment");
            System.out.println("0. Back");
            System.out.print("Choose: ");
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    viewNews();
                    break;
                case "2":
                    addComment();
                    break;
                case "0":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private void viewNews() {
        List<News> news = newsService.getNews();
        if (news.isEmpty()) {
            System.out.println("No news found.");
            return;
        }
        for (News item : news) {
            System.out.printf("%d | %s%s | %s%n%s%n",
                    item.getId(),
                    item.isPinned() ? "[PINNED] " : "",
                    item.getTopic(),
                    item.getTitle(),
                    item.getContent());
            for (Comment comment : item.getComments()) {
                System.out.println("  comment by " + comment.getAuthorId() + ": " + comment.getText());
            }
            System.out.println();
        }
    }

    private void addComment() {
        try {
            System.out.print("News ID: ");
            int newsId = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Comment: ");
            String text = scanner.nextLine();
            newsService.addComment(newsService.findNewsById(newsId), text, user.getId());
            System.out.println("Comment added.");
        } catch (Exception e) {
            System.out.println("Could not add comment: " + e.getMessage());
        }
    }
}
